package com.se.hub.modules.chat.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.hub.modules.chat.dto.response.ChatMessageResponse;
import com.se.hub.modules.chat.event.NewChatMessageEvent;
import com.se.hub.modules.chat.service.api.ChatEventHandler;
import com.se.hub.modules.notification.dto.response.NotificationResponse;
import com.se.hub.modules.notification.enums.NotificationType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Chat Event Handler Implementation

 * Handles all chat-related domain events
 * Uses @Async to process events asynchronously with virtual threads
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatEventHandlerImpl implements ChatEventHandler {
    
    StringRedisTemplate redisTemplate;
    ObjectMapper objectMapper;

    @Override
    @Async
    @EventListener
    public void handleNewChatMessage(NewChatMessageEvent event) {
        log.debug("ChatEventHandler_handleNewChatMessage_Processing new message event for conversation: {}", 
                event.getConversationId());
        
        try {
            // 1. Send chat message to all recipients via SSE
            sendChatMessageViaSSE(event);
            
            // 2. Send notification to all recipients
            sendChatNotification(event);
            
            log.debug("ChatEventHandler_handleNewChatMessage_Event processed successfully");
        } catch (Exception e) {
            log.error("ChatEventHandler_handleNewChatMessage_Error processing new chat message event", e);
        }
    }

    /**
     * Send chat message to all recipients via SSE using Redis Pub/Sub
     */
    private void sendChatMessageViaSSE(NewChatMessageEvent event) {
        try {
            ChatMessageResponse message = event.getMessage();
            
            // Send to each recipient
            for (String recipientUserId : event.getRecipientUserIds()) {
                // Set isMe flag for each recipient
                ChatMessageResponse recipientMessage = ChatMessageResponse.builder()
                        .messageId(message.getMessageId())
                        .conversationId(message.getConversationId())
                        .isMe(false) // Recipients always get isMe=false
                        .message(message.getMessage())
                        .sender(message.getSender())
                        .createDate(message.getCreateDate())
                        .build();
                
                // Publish to Redis channel for SSE delivery
                Map<String, Object> redisMessage = new HashMap<>();
                redisMessage.put("userId", recipientUserId);
                redisMessage.put("payload", recipientMessage);
                
                String messageJson = objectMapper.writeValueAsString(redisMessage);
                redisTemplate.convertAndSend("chat_messages", messageJson);
                
                log.debug("ChatEventHandler_sendChatMessageViaSSE_Message sent to Redis for user: {}", recipientUserId);
            }
        } catch (Exception e) {
            log.error("ChatEventHandler_sendChatMessageViaSSE_Error sending chat message via SSE", e);
        }
    }

    /**
     * Send notification to all recipients about new chat message
     */
    private void sendChatNotification(NewChatMessageEvent event) {
        try {
            ChatMessageResponse message = event.getMessage();

            String senderName;

            if (message.getSender() == null) {
                senderName = "Someone";
            } else if (message.getSender().getFullName() != null) {
                senderName = message.getSender().getFullName();
            } else {
                senderName = message.getSender().getUsername();
            }


            // Create notification response for each recipient
            for (String recipientUserId : event.getRecipientUserIds()) {
                NotificationResponse notification = NotificationResponse.builder()
                        .id("chat_" + message.getMessageId())
                        .notificationType(NotificationType.MESSAGE_RECEIVED)
                        .title(senderName)
                        .content(truncateMessage(message.getMessage()))
                        .targetType("CONVERSATION")
                        .targetId(message.getConversationId())
                        .createDate(Instant.now())
                        .build();
                
                // Publish to Redis notifications channel for SSE delivery
                Map<String, Object> redisMessage = new HashMap<>();
                redisMessage.put("userId", recipientUserId);
                redisMessage.put("payload", notification);
                
                String messageJson = objectMapper.writeValueAsString(redisMessage);
                redisTemplate.convertAndSend("notifications", messageJson);
                
                log.debug("ChatEventHandler_sendChatNotification_Notification sent to Redis for user: {}", recipientUserId);
            }
        } catch (Exception e) {
            log.error("ChatEventHandler_sendChatNotification_Error sending chat notification", e);
        }
    }

    /**
     * Truncate message to 100 characters for notification
     */
    private String truncateMessage(String message) {
        if (message == null) {
            return "";
        }
        return message.length() > 100 ? message.substring(0, 100) + "..." : message;
    }
}

