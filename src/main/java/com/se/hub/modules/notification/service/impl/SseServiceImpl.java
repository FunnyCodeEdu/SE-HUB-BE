package com.se.hub.modules.notification.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.hub.modules.notification.constant.NotificationConstants;
import com.se.hub.modules.notification.service.api.SseService;
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
 * SSE service implementation for real-time notification delivery
 * Handles SSE connections and pushes notifications to connected clients
 * Subscribes to Redis Pub/Sub channel for distributed notifications
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SseServiceImpl implements SseService {
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
                    log.warn("SseService_cleanup_Error completing emitter", e);
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
            log.debug("SseService_subscribe_Emitter completed for user: {}", userId);
        });
        
        emitter.onTimeout(() -> {
            removeEmitter(userId, emitter);
            log.debug("SseService_subscribe_Emitter timeout for user: {}", userId);
        });
        
        emitter.onError((e) -> {
            removeEmitter(userId, emitter);
            // Only log if it's not a client disconnect (Broken pipe is expected when client closes connection)
            if (!(e instanceof IOException) && 
                !(e instanceof org.springframework.web.context.request.async.AsyncRequestNotUsableException)) {
                log.warn("SseService_subscribe_Emitter error for user: {}", userId, e);
            } else {
                log.debug("SseService_subscribe_Client disconnected for user: {}", userId);
            }
        });
        
        log.info("SseService_subscribe_User {} subscribed to SSE, total connections: {}", 
                userId, userEmitters.get(userId).size());
        
        // Send initial connection event
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("{\"message\":\"Connected to notification stream\"}"));
        } catch (IOException e) {
            log.warn("SseService_subscribe_Error sending initial event to user: {}", userId, e);
            removeEmitter(userId, emitter);
        }
        
        return emitter;
    }

    @Override
    public void sendNotificationToUser(String userId, Object notification) {
        List<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters != null && !emitters.isEmpty()) {
            int successCount = 0;
            int failCount = 0;
            
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(notification));
                    successCount++;
                } catch (IOException e) {
                    // Client disconnected - remove emitter silently
                    log.debug("SseService_sendNotificationToUser_Client disconnected for user: {}", userId);
                    removeEmitter(userId, emitter);
                    failCount++;
                } catch (Exception e) {
                    log.warn("SseService_sendNotificationToUser_Unexpected error sending to emitter for user: {}", userId, e);
                    removeEmitter(userId, emitter);
                    failCount++;
                }
            }
            
            log.debug("SseService_sendNotificationToUser_Notification sent to user: {}, success: {}, failed: {}", 
                    userId, successCount, failCount);
        } else {
            // User not connected - notification is already stored in DB
            log.debug("SseService_sendNotificationToUser_User {} not connected, notification stored in DB", userId);
        }
    }

    @Override
    public void removeEmitter(String userId, SseEmitter emitter) {
        List<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                userEmitters.remove(userId);
                log.debug("SseService_removeEmitter_All emitters removed for user: {}", userId);
            }
        }
    }

    /**
     * Setup Redis Pub/Sub subscription for notifications
     * Uses a single channel "notifications" and filters by userId
     */
    private void setupRedisSubscription() {
        // Subscribe to the notifications channel
        String channel = "notifications";
        MessageListener listener = new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                try {
                    String messageBody = new String(message.getBody());
                    // Parse message: expected format {"userId": "...", "payload": ...}
                    Map<String, Object> messageData = objectMapper.readValue(messageBody, Map.class);
                    String userId = (String) messageData.get("userId");
                    Object payload = messageData.get("payload");
                    
                    if (userId != null && payload != null) {
                        sendNotificationToUser(userId, payload);
                    }
                } catch (Exception e) {
                    log.error("SseService_setupRedisSubscription_Error processing Redis message", e);
                }
            }
        };
        
        ChannelTopic topic = new ChannelTopic(channel);
        redisMessageListenerContainer.addMessageListener(listener, topic);
        
        log.info("SseService_setupRedisSubscription_Subscribed to Redis channel: {}", channel);
    }

    /**
     * Send keep-alive comments to all connected clients every 30 seconds
     * This prevents connection drops due to inactivity
     */
    @Scheduled(fixedRate = KEEP_ALIVE_INTERVAL)
    public void sendKeepAlive() {
        userEmitters.forEach((userId, emitters) -> {
            // Use iterator to safely remove while iterating
            List<SseEmitter> toRemove = new CopyOnWriteArrayList<>();
            
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .comment("keep-alive"));
                } catch (IOException e) {
                    // Client disconnected - remove emitter silently
                    log.debug("SseService_sendKeepAlive_Client disconnected for user: {}", userId);
                    toRemove.add(emitter);
                } catch (Exception e) {
                    // Other unexpected errors
                    log.warn("SseService_sendKeepAlive_Unexpected error sending keep-alive to user: {}", userId, e);
                    toRemove.add(emitter);
                }
            }
            
            // Remove disconnected emitters
            toRemove.forEach(emitter -> removeEmitter(userId, emitter));
        });
    }
}
