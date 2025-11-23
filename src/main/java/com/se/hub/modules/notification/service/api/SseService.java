package com.se.hub.modules.notification.service.api;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Service for managing SSE (Server-Sent Events) connections and real-time notification delivery
 */
public interface SseService {
    /**
     * Subscribe a user to SSE notifications
     * @param userId User ID to subscribe
     * @return SseEmitter for the connection
     */
    SseEmitter subscribe(String userId);

    /**
     * Send notification to a specific user via SSE
     * @param userId User ID to send notification to
     * @param notification Notification data (will be serialized to JSON)
     */
    void sendNotificationToUser(String userId, Object notification);

    /**
     * Remove a specific emitter for a user
     * @param userId User ID
     * @param emitter Emitter to remove
     */
    void removeEmitter(String userId, SseEmitter emitter);
}
