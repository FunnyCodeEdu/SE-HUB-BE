package com.se.hub.modules.chat.constant;

/**
 * Chat Constants
 * Contains MongoDB collection names, field definitions, and validation constants
 */
public class ChatConstants {
    // MongoDB Collections
    public static final String COLLECTION_CONVERSATION = "conversation";
    public static final String COLLECTION_CHAT_MESSAGE = "chat-message";
    
    // Field Definitions
    public static final String CONVERSATION_TYPE_DEFINITION = "VARCHAR(20)";
    public static final String PARTICIPANTS_HASH_DEFINITION = "VARCHAR(64)";
    public static final String MESSAGE_DEFINITION = "TEXT";
    
    // Validation
    public static final int MIN_PARTICIPANTS = 2;
    public static final int MAX_PARTICIPANTS = 100; // For group chat
    public static final int MESSAGE_MAX_LENGTH = 5000;
    
    private ChatConstants() {
        // Prevent instantiation
    }
}

