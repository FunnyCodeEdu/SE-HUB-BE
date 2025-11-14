package com.se.hub.modules.notification.service.impl;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.se.hub.modules.auth.constant.JwtClaimSetConstant;
import com.se.hub.modules.notification.constant.NotificationConstants;
import com.se.hub.modules.notification.service.api.NotificationWebSocketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket service implementation using Socket.IO (Netty)
 * Handles real-time notification delivery via WebSocket connections
 * Subscribes to Redis Pub/Sub channels and pushes notifications to connected clients
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationWebSocketServiceImpl implements NotificationWebSocketService {
    final SocketIOServer socketIOServer;
    final RedisMessageListenerContainer redisMessageListenerContainer;
    final JwtDecoder jwtDecoder;

    @Value("${websocket.retry.max-attempts:3}")
    int maxRetryAttempts;

    @Value("${websocket.retry.initial-delay-ms:100}")
    long initialRetryDelayMs;

    // Map to store user ID to Set of Socket.IO client connections (multi-device support)
    Map<String, Set<SocketIOClient>> userConnections = new ConcurrentHashMap<>();
    
    // Map to store user ID to their Redis channel subscriptions
    Map<String, MessageListener> userSubscriptions = new ConcurrentHashMap<>();

    // Scheduled executor for retry mechanism and health checks
    ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(2);

    @PostConstruct
    public void init() {
        start();
    }

    @PreDestroy
    public void cleanup() {
        stop();
        retryExecutor.shutdown();
        try {
            if (!retryExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                retryExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            retryExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void start() {
        // Register namespace for notifications
        com.corundumstudio.socketio.SocketIONamespace notificationNamespace = 
                socketIOServer.addNamespace("/notification");
        
        // Handle client connections to /notification namespace
        notificationNamespace.addConnectListener(client -> {
            try {
                // Get user ID from JWT token or query params
                String userId = getUserIdFromClient(client);
                if (userId != null && !userId.isEmpty()) {
                    // Store connection with user ID as key (multi-device support)
                    userConnections.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(client);
                    
                    // Join user to their notification room (Socket.IO rooms)
                    String roomName = "notif:user:" + userId;
                    client.joinRoom(roomName);
                    
                    // Subscribe to user's Redis Pub/Sub channel (only once per user)
                    subscribeToUserChannel(userId);
                    
                    log.debug("NotificationWebSocketService_start_User {} connected to /notification namespace, joined room: {}, total connections: {}", 
                            userId, roomName, userConnections.get(userId).size());
                } else {
                    log.warn("NotificationWebSocketService_start_Client connected to /notification without valid authentication, disconnecting");
                    client.disconnect();
                }
            } catch (Exception e) {
                log.error("NotificationWebSocketService_start_Error handling client connection to /notification", e);
                client.disconnect();
            }
        });

        // Handle client disconnections from /notification namespace
        notificationNamespace.addDisconnectListener(client -> {
            String userId = getUserIdFromClient(client);
            if (userId != null) {
                Set<SocketIOClient> connections = userConnections.get(userId);
                if (connections != null) {
                    connections.remove(client);
                    // If no more connections for this user, remove from map and unsubscribe
                    if (connections.isEmpty()) {
                        userConnections.remove(userId);
                        unsubscribeFromUserChannel(userId);
                        log.debug("NotificationWebSocketService_start_User {} disconnected from /notification namespace, no more connections", userId);
                    } else {
                        log.debug("NotificationWebSocketService_start_User {} disconnected from /notification namespace, remaining connections: {}", 
                                userId, connections.size());
                    }
                }
            }
        });

        // Subscribe to Redis Pub/Sub channels
        setupRedisSubscriptions();
        
        // Start server after all namespaces are registered
        // This ensures both /chat and /notification namespaces are ready
        // Server will only start once, even if this method is called multiple times
        synchronized (socketIOServer) {
            try {
                socketIOServer.start();
                log.info("NotificationWebSocketService_start_Unified WebSocket Gateway started on port: {}", 
                        socketIOServer.getConfiguration().getPort());
            } catch (IllegalStateException e) {
                // Server already started - this is fine
                log.debug("NotificationWebSocketService_start_Server already started");
            } catch (Exception e) {
                log.error("NotificationWebSocketService_start_Error starting server", e);
            }
        }
        
        log.info("NotificationWebSocketService_start_/notification namespace registered successfully");
    }

    @Override
    public void stop() {
        // Server will be stopped by UnifiedWebSocketConfig or application shutdown
        log.info("NotificationWebSocketService_stop_/notification namespace stopped");
    }

    @Override
    public void sendNotificationToUser(String userId, String notification) {
        Set<SocketIOClient> connections = userConnections.get(userId);
        if (connections != null && !connections.isEmpty()) {
            // Send to all connected devices for this user
            int successCount = 0;
            int failCount = 0;
            
            for (SocketIOClient client : connections) {
                if (client != null && client.isChannelOpen()) {
                    try {
                        client.sendEvent("notification", notification);
                        successCount++;
                    } catch (Exception e) {
                        log.warn("NotificationWebSocketService_sendNotificationToUser_Error sending to client for user: {}", userId, e);
                        failCount++;
                        // Remove invalid connection
                        connections.remove(client);
                        // Retry sending with exponential backoff
                        retrySendNotification(userId, notification, client, 1);
                    }
                } else {
                    // Remove invalid connection
                    connections.remove(client);
                    failCount++;
                }
            }
            
            log.debug("NotificationWebSocketService_sendNotificationToUser_Notification sent to user: {}, success: {}, failed: {}", 
                    userId, successCount, failCount);
        } else {
            // User not connected - notification is already stored in DB and Redis
            // Will be delivered when user connects and fetches notifications
            log.debug("NotificationWebSocketService_sendNotificationToUser_User {} not connected, notification stored in DB/Redis", userId);
        }
        
        // Also send to room via /notification namespace (backup mechanism for multiple connections)
        try {
            String roomName = "notif:user:" + userId;
            com.corundumstudio.socketio.SocketIONamespace notificationNamespace = socketIOServer.getNamespace("/notification");
            if (notificationNamespace != null) {
                notificationNamespace.getRoomOperations(roomName).sendEvent("notification", notification);
            }
        } catch (Exception e) {
            log.warn("NotificationWebSocketService_sendNotificationToUser_Error sending to room for user: {}", userId, e);
        }
    }

    /**
     * Retry sending notification with exponential backoff
     */
    private void retrySendNotification(String userId, String notification, SocketIOClient client, int attempt) {
        if (attempt > maxRetryAttempts) {
            log.warn("NotificationWebSocketService_retrySendNotification_Max retry attempts reached for user: {}", userId);
            return;
        }

        long delay = initialRetryDelayMs * (1L << (attempt - 1)); // Exponential backoff: 100ms, 200ms, 400ms
        retryExecutor.schedule(() -> {
            try {
                if (client != null && client.isChannelOpen()) {
                    client.sendEvent("notification", notification);
                    log.debug("NotificationWebSocketService_retrySendNotification_Retry successful for user: {}, attempt: {}", userId, attempt);
                } else {
                    log.debug("NotificationWebSocketService_retrySendNotification_Client no longer valid for user: {}", userId);
                }
            } catch (Exception e) {
                log.warn("NotificationWebSocketService_retrySendNotification_Retry failed for user: {}, attempt: {}", userId, attempt, e);
                retrySendNotification(userId, notification, client, attempt + 1);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Setup Redis Pub/Sub subscriptions
     * This method is called once at startup
     * Individual user channels are subscribed dynamically when users connect
     */
    private void setupRedisSubscriptions() {
        log.info("NotificationWebSocketService_setupRedisSubscriptions_Redis Pub/Sub setup completed. User channels will be subscribed dynamically.");
    }

    /**
     * Subscribe to a specific user's Redis Pub/Sub channel
     * Called when a user connects to WebSocket
     */
    private void subscribeToUserChannel(String userId) {
        if (userSubscriptions.containsKey(userId)) {
            // Already subscribed
            return;
        }

        String channel = NotificationConstants.REDIS_KEY_CHANNEL_PREFIX + userId;
        MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                try {
                    String notificationJson = new String(message.getBody());
                    sendNotificationToUser(userId, notificationJson);
                } catch (Exception e) {
                    log.error("NotificationWebSocketService_subscribeToUserChannel_Error processing Redis message for user: {}", userId, e);
                }
            }
        };

        ChannelTopic topic = new ChannelTopic(channel);
        redisMessageListenerContainer.addMessageListener(messageListener, topic);
        userSubscriptions.put(userId, messageListener);
        
        log.debug("NotificationWebSocketService_subscribeToUserChannel_Subscribed to Redis channel: {} for user: {}", channel, userId);
    }

    /**
     * Unsubscribe from a specific user's Redis Pub/Sub channel
     * Called when a user disconnects from WebSocket
     */
    private void unsubscribeFromUserChannel(String userId) {
        MessageListener listener = userSubscriptions.remove(userId);
        if (listener != null) {
            String channel = NotificationConstants.REDIS_KEY_CHANNEL_PREFIX + userId;
            ChannelTopic topic = new ChannelTopic(channel);
            redisMessageListenerContainer.removeMessageListener(listener, topic);
            log.debug("NotificationWebSocketService_unsubscribeFromUserChannel_Unsubscribed from Redis channel: {} for user: {}", channel, userId);
        }
    }

    /**
     * Extract user ID from Socket.IO client
     * Priority: JWT token > query param userId > header userId
     */
    private String getUserIdFromClient(SocketIOClient client) {
        try {
            // First, try to get from JWT token (most secure)
            String token = client.getHandshakeData().getSingleUrlParam("token");
            if (token == null || token.isEmpty()) {
                // Try from Authorization header
                String authHeader = client.getHandshakeData().getHttpHeaders().get("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }
            
            if (token != null && !token.isEmpty()) {
                try {
                    Jwt jwt = jwtDecoder.decode(token);
                    // Try different claim names for userId
                    Object userIdClaim = jwt.getClaims().get(JwtClaimSetConstant.CLAIM_USER_ID);
                    if (userIdClaim == null) {
                        userIdClaim = jwt.getClaims().get("userId");
                    }
                    if (userIdClaim == null) {
                        userIdClaim = jwt.getClaims().get("sub"); // Subject claim
                    }
                    if (userIdClaim != null) {
                        return userIdClaim.toString();
                    }
                } catch (JwtException e) {
                    log.warn("NotificationWebSocketService_getUserIdFromClient_Invalid JWT token", e);
                }
            }

            // Fallback: Try to get from query params (less secure, for development)
            String userIdParam = client.getHandshakeData().getSingleUrlParam("userId");
            if (userIdParam != null && !userIdParam.isEmpty()) {
                log.debug("NotificationWebSocketService_getUserIdFromClient_Using userId from query param (less secure)");
                return userIdParam;
            }

            // Last resort: Try to get from handshake data headers
            String userId = client.getHandshakeData().getHttpHeaders().get("userId");
            if (userId != null && !userId.isEmpty()) {
                log.debug("NotificationWebSocketService_getUserIdFromClient_Using userId from header (less secure)");
                return userId;
            }

            return null;
        } catch (Exception e) {
            log.warn("NotificationWebSocketService_getUserIdFromClient_Error extracting user ID", e);
            return null;
        }
    }
}

