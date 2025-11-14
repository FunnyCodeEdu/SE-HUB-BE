package com.se.hub.modules.chat.constant;

/**
 * Chat Error Code Constants
 * Contains error code string constants for Chat module
 */
public class ChatErrorCodeConstants {
    // Conversation Error Codes
    public static final String CONVERSATION_NOT_FOUND = "CONVERSATION_NOT_FOUND";
    public static final String NOT_PARTICIPANT = "NOT_PARTICIPANT";
    public static final String CONVERSATION_ALREADY_EXISTS = "CONVERSATION_ALREADY_EXISTS";
    public static final String INVALID_PARTICIPANT_COUNT = "INVALID_PARTICIPANT_COUNT";
    
    // Message Error Codes
    public static final String MESSAGE_TOO_LONG = "MESSAGE_TOO_LONG";
    
    // Session Error Codes
    public static final String SESSION_NOT_FOUND = "SESSION_NOT_FOUND";
    
    // Common Error Codes
    public static final String GENERATION_ERROR = "GENERATION_ERROR";
    
    private ChatErrorCodeConstants() {
        // Prevent instantiation
    }
}

