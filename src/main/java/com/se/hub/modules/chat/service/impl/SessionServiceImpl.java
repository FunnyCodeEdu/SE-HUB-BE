package com.se.hub.modules.chat.service.impl;

import com.se.hub.modules.chat.constant.RedisKeys;
import com.se.hub.modules.chat.service.api.SessionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

/**
 * Session Service Implementation
 * Manages user sessions for Socket.IO connections with heartbeat mechanism
 * Virtual Thread Best Practice:
 * - This service uses synchronous blocking I/O operations (Redis operations)
 * - Virtual threads automatically handle blocking operations efficiently
 * - Scheduled cleanup task runs on virtual threads
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionServiceImpl implements SessionService {
    
    StringRedisTemplate redisTemplate;
    
    private static final Duration SESSION_TTL = Duration.ofHours(24);
    private static final Duration HEARTBEAT_INTERVAL = Duration.ofMinutes(5);
    
    /**
     * Save session for user
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during Redis operations, enabling high concurrency.
     */
    @Override
    public void saveSession(String userId, String sessionId) {
        String key = RedisKeys.USER_SESSION_KEY + userId;
        redisTemplate.opsForSet().add(key, sessionId);
        redisTemplate.expire(key, SESSION_TTL);
        
        // Save heartbeat timestamp
        String heartbeatKey = RedisKeys.USER_SESSION_HEARTBEAT_KEY + userId + ":" + sessionId;
        redisTemplate.opsForValue().set(heartbeatKey, String.valueOf(Instant.now().toEpochMilli()), HEARTBEAT_INTERVAL);
        
        log.debug("SessionServiceImpl_saveSession_Saved session {} for user {}", sessionId, userId);
    }
    
    /**
     * Update heartbeat for session
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during Redis operations, enabling high concurrency.
     */
    @Override
    public void updateHeartbeat(String userId, String sessionId) {
        String heartbeatKey = RedisKeys.USER_SESSION_HEARTBEAT_KEY + userId + ":" + sessionId;
        redisTemplate.opsForValue().set(heartbeatKey, String.valueOf(Instant.now().toEpochMilli()), HEARTBEAT_INTERVAL);
    }
    
    /**
     * Get all sessions for user
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during Redis operations, enabling high concurrency.
     */
    @Override
    public Set<String> getSessions(String userId) {
        String key = RedisKeys.USER_SESSION_KEY + userId;
        return redisTemplate.opsForSet().members(key);
    }
    
    /**
     * Remove session for user
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during Redis operations, enabling high concurrency.
     */
    @Override
    public void removeSession(String userId, String sessionId) {
        String key = RedisKeys.USER_SESSION_KEY + userId;
        redisTemplate.opsForSet().remove(key, sessionId);
        
        // Remove heartbeat
        String heartbeatKey = RedisKeys.USER_SESSION_HEARTBEAT_KEY + userId + ":" + sessionId;
        redisTemplate.delete(heartbeatKey);
        
        log.debug("SessionServiceImpl_removeSession_Removed session {} for user {}", sessionId, userId);
    }
    
    /**
     * Cleanup stale sessions without heartbeat
     * Virtual Thread Best Practice: Scheduled task runs on virtual threads.
     * Virtual threads yield during Redis operations, enabling high concurrency.
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void cleanupStaleSessions() {
        log.debug("SessionServiceImpl_cleanupStaleSessions_Starting cleanup of stale sessions");
        // Note: Full implementation would scan all heartbeat keys and remove expired ones
        // For now, Redis TTL handles expiration automatically
        // This method can be enhanced to actively clean up sessions without recent heartbeats
    }
}

