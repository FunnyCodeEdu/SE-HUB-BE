package com.se.hub.modules.chat.constant;

/**
 * Redis Keys Constants
 * Contains Redis key patterns for chat module
 */
public class RedisKeys {
    public static final String USER_SESSION_KEY = "user:session:";
    public static final String USER_SESSION_HEARTBEAT_KEY = "user:session:heartbeat:";
    public static final String CONVERSATION_ROOM_KEY = "conversation:room:";
    
    private RedisKeys() {
        // Prevent instantiation
    }
}

