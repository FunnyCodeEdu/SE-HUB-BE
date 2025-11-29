package com.se.hub.modules.chat.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.chat.constant.ChatConstants;
import com.se.hub.modules.chat.dto.request.CreateConversationRequest;
import com.se.hub.modules.chat.dto.response.ChatMessageResponse;
import com.se.hub.modules.chat.dto.response.ConversationResponse;
import com.se.hub.modules.chat.entity.ChatMessage;
import com.se.hub.modules.chat.entity.Conversation;
import com.se.hub.modules.chat.entity.ParticipantInfo;
import com.se.hub.modules.chat.enums.ConversationType;
import com.se.hub.modules.chat.exception.ChatErrorCode;
import com.se.hub.modules.chat.mapper.ChatMessageMapper;
import com.se.hub.modules.chat.mapper.ConversationMapper;
import com.se.hub.modules.chat.repository.ChatMessageRepository;
import com.se.hub.modules.chat.repository.ConversationRepository;
import com.se.hub.modules.chat.service.api.ConversationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Conversation Service Implementation
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
public class ConversationServiceImpl implements ConversationService {
    
    ConversationRepository conversationRepository;
    ConversationMapper conversationMapper;
    ChatMessageRepository chatMessageRepository;
    ChatMessageMapper chatMessageMapper;
    
    /**
     * Create a new conversation.
     * Virtual Thread Best Practice: Uses @Transactional with synchronous blocking I/O operations.
     * Virtual threads yield during each database operation, enabling high concurrency.
     */
    @Override
    @Transactional
    public ConversationResponse createConversation(CreateConversationRequest request) {
        log.debug("ConversationServiceImpl_createConversation_Creating conversation for user: {}", AuthUtils.getCurrentUserId());
        
        String currentUserId = AuthUtils.getCurrentUserId();
        
        // Validate participant count
        List<String> participantIds = new ArrayList<>(request.getParticipantIds());
        if (!participantIds.contains(currentUserId)) {
            participantIds.add(currentUserId);
        }
        
        if (participantIds.size() < ChatConstants.MIN_PARTICIPANTS) {
            log.warn("ConversationServiceImpl_createConversation_Invalid participant count: {}", participantIds.size());
            throw ChatErrorCode.INVALID_PARTICIPANT_COUNT.toException();
        }
        
        // Prevent self-conversation for DIRECT type
        if (request.getType() == ConversationType.DIRECT && participantIds.size() != 2) {
            log.warn("ConversationServiceImpl_createConversation_DIRECT conversation must have exactly 2 participants, got: {}", participantIds.size());
            throw ChatErrorCode.INVALID_PARTICIPANT_COUNT.toException();
        }
        
        // Generate hash
        String participantsHash = generateParticipantHash(participantIds);
        
        // Try to find existing conversation with retry mechanism
        Conversation conversation = findOrCreateConversation(participantsHash, request, participantIds, currentUserId);
        
        return conversationMapper.toConversationResponse(conversation, currentUserId);
    }
    
    /**
     * Find or create conversation with retry mechanism for concurrent creation
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database operations, enabling high concurrency.
     */
    private Conversation findOrCreateConversation(String participantsHash, 
                                                  CreateConversationRequest request,
                                                  List<String> participantIds,
                                                  String currentUserId) {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                // Try to find existing
                Optional<Conversation> existing = conversationRepository.findByParticipantsHash(participantsHash);
                if (existing.isPresent()) {
                    log.debug("ConversationServiceImpl_findOrCreateConversation_Found existing conversation: {}", existing.get().getConversationId());
                    return existing.get();
                }
                
                // Create new conversation
                List<ParticipantInfo> participants = participantIds.stream()
                    .map(userId -> ParticipantInfo.builder().userId(userId).build())
                    .toList();
                
                Conversation newConversation = Conversation.builder()
                    .type(request.getType())
                    .participantsHash(participantsHash)
                    .participants(participants)
                    .createdDate(Instant.now())
                    .modifiedDate(Instant.now())
                    .build();
                
                Conversation saved = conversationRepository.save(newConversation);
                log.debug("ConversationServiceImpl_findOrCreateConversation_Created new conversation: {}", saved.getConversationId());
                return saved;
                
            } catch (DuplicateKeyException e) {
                // Retry if duplicate key (concurrent creation)
                retryCount++;
                log.warn("ConversationServiceImpl_findOrCreateConversation_Duplicate key detected, retry {}/{}", retryCount, maxRetries);
                
                if (retryCount >= maxRetries) {
                    log.error("ConversationServiceImpl_findOrCreateConversation_Failed after {} retries", maxRetries);
                    throw ChatErrorCode.CONVERSATION_ALREADY_EXISTS.toException();
                }
                
                // Try to find the conversation that was created by another thread
                Optional<Conversation> existing = conversationRepository.findByParticipantsHash(participantsHash);
                if (existing.isPresent()) {
                    log.debug("ConversationServiceImpl_findOrCreateConversation_Found conversation after retry: {}", existing.get().getConversationId());
                    return existing.get();
                }
                
                try {
                    Thread.sleep(100L * retryCount); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("ConversationServiceImpl_findOrCreateConversation_Thread interrupted during retry");
                    throw ChatErrorCode.CONVERSATION_ALREADY_EXISTS.toException();
                }
            }
        }
        
        throw ChatErrorCode.CONVERSATION_ALREADY_EXISTS.toException();
    }
    
    /**
     * Get all conversations for current user with pagination.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public PagingResponse<ConversationResponse> getConversations(PagingRequest request) {
        log.debug("ConversationServiceImpl_getConversations_Fetching conversations for user: {}", AuthUtils.getCurrentUserId());
        String currentUserId = AuthUtils.getCurrentUserId();
        
        Pageable pageable = PageRequest.of(
            request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
            request.getPageSize(),
            PagingUtil.createSort(request)
        );
        
        Page<Conversation> conversations = conversationRepository.findByParticipantIdsContains(currentUserId, pageable);
        
        return PagingResponse.<ConversationResponse>builder()
            .currentPage(conversations.getNumber())
            .totalPages(conversations.getTotalPages())
            .pageSize(conversations.getSize())
            .totalElement(conversations.getTotalElements())
            .data(conversations.getContent().stream()
                .map(conv -> {
                    ConversationResponse response = conversationMapper.toConversationResponse(conv, currentUserId);
                    // Fetch and set last message
                    ChatMessage lastMessage = chatMessageRepository.findTopByConversationIdOrderByCreateDateDesc(conv.getConversationId());
                    if (lastMessage != null) {
                        ChatMessageResponse lastMessageResponse = chatMessageMapper.toChatMessageResponse(lastMessage);
                        lastMessageResponse.setIsMe(currentUserId.equals(lastMessage.getSenderId()));
                        response.setLastMessage(lastMessageResponse);
                    }
                    return response;
                })
                .toList())
            .build();
    }
    
    /**
     * Generate participant hash for conversation uniqueness
     */
    private String generateParticipantHash(List<String> userIds) {
        // Sort to ensure consistent hash
        List<String> sorted = new ArrayList<>(userIds);
        Collections.sort(sorted);
        
        String raw = String.join("_", sorted);
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            
            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (Exception e) {
            log.error("ConversationServiceImpl_generateParticipantHash_Error generating hash", e);
            throw ChatErrorCode.GENERATION_ERROR.toException();
        }
    }
}

