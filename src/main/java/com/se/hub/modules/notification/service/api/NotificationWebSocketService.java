package com.se.hub.modules.notification.service.api;

/**
 * Service for managing WebSocket connections and real-time notification delivery
 * Uses Socket.IO (Netty) for WebSocket communication
 */
public interface NotificationWebSocketService {
    /**
     * Start the WebSocket server and subscribe to Redis Pub/Sub
     */
    void start();

    /**
     * Stop the WebSocket server
     */
    void stop();

    /**
     * Send notification to a specific user via WebSocket
     * @param userId user ID
     * @param notification notification data as JSON string
     */
    void sendNotificationToUser(String userId, String notification);
}



