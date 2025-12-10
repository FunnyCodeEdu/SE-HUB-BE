package com.se.hub.modules.chat.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.chat.dto.request.CreateChatMessageRequest;
import com.se.hub.modules.chat.dto.request.GetMessagesRequest;
import com.se.hub.modules.chat.dto.response.ChatMessageResponse;
import com.se.hub.modules.chat.entity.ChatMessage;
import com.se.hub.modules.chat.entity.Conversation;
import com.se.hub.modules.chat.entity.ParticipantInfo;
import com.se.hub.modules.chat.event.NewChatMessageEvent;
import com.se.hub.modules.chat.exception.ChatErrorCode;
import com.se.hub.modules.chat.mapper.ChatMessageMapper;
import com.se.hub.modules.chat.repository.ChatMessageRepository;
import com.se.hub.modules.chat.repository.ConversationRepository;
import com.se.hub.modules.chat.service.api.ChatMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Chat Message Service Implementation
 * Virtual Thread Best Practice:
 * - This service uses synchronous blocking I/O operations (MongoDB repository calls)
 * - Virtual threads automatically handle blocking operations efficiently
 * - No need to use CompletableFuture or reactive APIs
 * - Each method call will run on a virtual thread, allowing high concurrency
 * - Database operations are blocking but virtual threads handle them efficiently
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageServiceImpl implements ChatMessageService {
    
    ConversationRepository conversationRepository;
    ChatMessageRepository chatMessageRepository;
    ChatMessageMapper chatMessageMapper;
    ApplicationEventPublisher eventPublisher;
    
    /**
     * Create a new chat message.
     * Virtual Thread Best Practice: Uses @Transactional with synchronous blocking I/O operations.
     * Virtual threads yield during each database operation, enabling high concurrency.
     */
    @Override
    @Transactional
    public ChatMessageResponse createMessage(CreateChatMessageRequest request) {
        log.debug("ChatMessageServiceImpl_createMessage_Creating message for conversation: {}", request.getConversationId());
        
        String currentUserId = AuthUtils.getCurrentUserId();
        
        // Validate participant
        Conversation conversation = validateParticipant(request.getConversationId(), currentUserId);
        
        // Create message
        ChatMessage message = ChatMessage.builder()
            .conversationId(request.getConversationId())
            .senderId(currentUserId)
            .message(request.getMessage())
            .createDate(Instant.now())
            .build();
        
        message = chatMessageRepository.save(message);
        
        // Update conversation modified date
        conversation.setModifiedDate(Instant.now());
        conversationRepository.save(conversation);
        
        // Map to response (Profile fetch is handled in mapper)
        ChatMessageResponse response = chatMessageMapper.toChatMessageResponse(message);
        response.setIsMe(true);
        
        // Publish event to send message via SSE and create notifications
        List<String> recipientUserIds = conversation.getParticipants().stream()
                .map(ParticipantInfo::getUserId)
                .filter(id -> !id.equals(currentUserId))
                .toList();

        eventPublisher.publishEvent(new NewChatMessageEvent(
                this, 
                request.getConversationId(), 
                currentUserId, 
                recipientUserIds, 
                response
        ));
        
        return response;
    }
    
    /**
     * Get messages for a conversation with pagination.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public PagingResponse<ChatMessageResponse> getMessages(GetMessagesRequest request) {
        log.debug("ChatMessageServiceImpl_getMessages_Fetching messages for conversation: {}", request.getConversationId());
        
        String currentUserId = AuthUtils.getCurrentUserId();
        
        // Validate participant
        validateParticipant(request.getConversationId(), currentUserId);
        
        // Pagination
        Pageable pageable = PageRequest.of(
            request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
            request.getPageSize(),
            Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortField())
        );
        
        // Cursor-based pagination if beforeDate provided
        Page<ChatMessage> messages;
        if (request.getBeforeDate() != null) {
            List<ChatMessage> messageList = chatMessageRepository.findByConversationIdAndCreateDateBeforeOrderByCreateDateDesc(
                request.getConversationId(), request.getBeforeDate(), pageable);
            // Convert List to Page for consistency
            messages = new org.springframework.data.domain.PageImpl<>(messageList, pageable, messageList.size());
        } else {
            messages = chatMessageRepository.findByConversationIdOrderByCreateDateDesc(
                request.getConversationId(), pageable);
        }
        
        // Map to response (Profile fetch is handled in mapper)
        List<ChatMessageResponse> responseList = messages.getContent().stream()
            .map(msg -> {
                ChatMessageResponse response = chatMessageMapper.toChatMessageResponse(msg);
                response.setIsMe(currentUserId.equals(msg.getSenderId()));
                return response;
            })
            .toList();
        
        return PagingResponse.<ChatMessageResponse>builder()
            .currentPage(messages.getNumber())
            .totalPages(messages.getTotalPages())
            .pageSize(messages.getSize())
            .totalElement(messages.getTotalElements())
                .data(responseList)
                .build();
    }
    
    /**
     * Validate that current user is a participant of the conversation
     */
    private Conversation validateParticipant(String conversationId, String userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> {
                log.error("ChatMessageServiceImpl_validateParticipant_Conversation not found: {}", conversationId);
                return ChatErrorCode.CONVERSATION_NOT_FOUND.toException();
            });
        
        boolean isParticipant = conversation.getParticipants().stream()
            .anyMatch(p -> p.getUserId().equals(userId));
        
        if (!isParticipant) {
            log.warn("ChatMessageServiceImpl_validateParticipant_User {} is not a participant of conversation {}", userId, conversationId);
            throw ChatErrorCode.NOT_PARTICIPANT.toException();
        }
        
        return conversation;
    }
}

