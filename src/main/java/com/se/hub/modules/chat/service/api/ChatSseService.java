package com.se.hub.modules.chat.service.api;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Service for managing SSE (Server-Sent Events) connections for chat real-time messaging
 */
public interface ChatSseService {
    /**
     * Subscribe a user to chat SSE events
     * @param userId User ID to subscribe
     * @return SseEmitter for the connection
     */
    SseEmitter subscribe(String userId);

    /**
     * Send chat message to a specific user via SSE
     * @param userId User ID to send message to
     * @param message Chat message data (will be serialized to JSON)
     */
    void sendMessageToUser(String userId, Object message);

    /**
     * Remove a specific emitter for a user
     * @param userId User ID
     * @param emitter Emitter to remove
     */
    void removeEmitter(String userId, SseEmitter emitter);
}

