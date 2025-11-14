package com.se.hub.modules.chat.service.api;

import java.util.Set;

/**
 * Session Service Interface
 * Manages user sessions for Socket.IO connections
 */
public interface SessionService {
    
    /**
     * Save session for user
     */
    void saveSession(String userId, String sessionId);
    
    /**
     * Update heartbeat for session
     */
    void updateHeartbeat(String userId, String sessionId);
    
    /**
     * Get all sessions for user
     */
    Set<String> getSessions(String userId);
    
    /**
     * Remove session for user
     */
    void removeSession(String userId, String sessionId);
}

