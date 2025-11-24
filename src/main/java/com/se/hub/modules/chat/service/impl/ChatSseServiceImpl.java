package com.se.hub.modules.chat.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.hub.modules.chat.service.api.ChatSseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SSE service implementation for real-time chat message delivery
 * Handles SSE connections and pushes chat messages to connected clients
 * Subscribes to Redis Pub/Sub channel for distributed chat messages
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatSseServiceImpl implements ChatSseService {
    RedisMessageListenerContainer redisMessageListenerContainer;
    ObjectMapper objectMapper;

    // Map to store user ID to List of SseEmitters (multi-device support)
    Map<String, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    // Timeout for SSE connections: 0L means infinite (rely on cleanup events)
    static final long SSE_TIMEOUT = 0L;
    
    // Keep-alive interval in milliseconds (30 seconds)
    static final long KEEP_ALIVE_INTERVAL = 30000L;

    @PostConstruct
    public void init() {
        setupRedisSubscription();
    }

    @PreDestroy
    public void cleanup() {
        // Close all emitters
        userEmitters.values().forEach(emitters -> 
            emitters.forEach(emitter -> {
                try {
                    emitter.complete();
                } catch (Exception e) {
                    log.warn("ChatSseService_cleanup_Error completing emitter", e);
                }
            })
        );
        userEmitters.clear();
    }

    @Override
    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        
        // Add emitter to user's list
        userEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        
        // Setup cleanup callbacks to prevent memory leaks
        emitter.onCompletion(() -> {
            removeEmitter(userId, emitter);
            log.debug("ChatSseService_subscribe_Emitter completed for user: {}", userId);
        });
        
        emitter.onTimeout(() -> {
            removeEmitter(userId, emitter);
            log.debug("ChatSseService_subscribe_Emitter timeout for user: {}", userId);
        });
        
        emitter.onError((e) -> {
            removeEmitter(userId, emitter);
            log.warn("ChatSseService_subscribe_Emitter error for user: {}", userId, e);
        });
        
        log.info("ChatSseService_subscribe_User {} subscribed to chat SSE, total connections: {}", 
                userId, userEmitters.get(userId).size());
        
        // Send initial connection event
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("{\"message\":\"Connected to chat stream\"}"));
        } catch (IOException e) {
            log.warn("ChatSseService_subscribe_Error sending initial event to user: {}", userId, e);
            removeEmitter(userId, emitter);
        }
        
        return emitter;
    }

    @Override
    public void sendMessageToUser(String userId, Object message) {
        List<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters != null && !emitters.isEmpty()) {
            int successCount = 0;
            int failCount = 0;
            
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("chat_message")
                            .data(message));
                    successCount++;
                } catch (IOException e) {
                    log.warn("ChatSseService_sendMessageToUser_Error sending to emitter for user: {}", userId, e);
                    removeEmitter(userId, emitter);
                    failCount++;
                }
            }
            
            log.debug("ChatSseService_sendMessageToUser_Message sent to user: {}, success: {}, failed: {}", 
                    userId, successCount, failCount);
        } else {
            // User not connected - message is already stored in DB
            log.debug("ChatSseService_sendMessageToUser_User {} not connected, message stored in DB", userId);
        }
    }

    @Override
    public void removeEmitter(String userId, SseEmitter emitter) {
        List<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                userEmitters.remove(userId);
                log.debug("ChatSseService_removeEmitter_All emitters removed for user: {}", userId);
            }
        }
    }

    /**
     * Setup Redis Pub/Sub subscription for chat messages
     * Uses a single channel "chat_messages" and filters by userId
     */
    private void setupRedisSubscription() {
        // Subscribe to the chat_messages channel
        String channel = "chat_messages";
        MessageListener listener = new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                try {
                    String messageBody = new String(message.getBody());
                    // Parse message: expected format {"userId": "...", "payload": ...}
                    @SuppressWarnings("unchecked")
                    Map<String, Object> messageData = objectMapper.readValue(messageBody, Map.class);
                    String userId = (String) messageData.get("userId");
                    Object payload = messageData.get("payload");
                    
                    if (userId != null && payload != null) {
                        sendMessageToUser(userId, payload);
                    }
                } catch (Exception e) {
                    log.error("ChatSseService_setupRedisSubscription_Error processing Redis message", e);
                }
            }
        };
        
        ChannelTopic topic = new ChannelTopic(channel);
        redisMessageListenerContainer.addMessageListener(listener, topic);
        
        log.info("ChatSseService_setupRedisSubscription_Subscribed to Redis channel: {}", channel);
    }

    /**
     * Send keep-alive comments to all connected clients every 30 seconds
     * This prevents connection drops due to inactivity
     */
    @Scheduled(fixedRate = KEEP_ALIVE_INTERVAL)
    public void sendKeepAlive() {
        userEmitters.forEach((userId, emitters) -> {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .comment("keep-alive"));
                } catch (IOException e) {
                    log.debug("ChatSseService_sendKeepAlive_Error sending keep-alive to user: {}", userId);
                    removeEmitter(userId, emitter);
                }
            }
        });
    }
}

