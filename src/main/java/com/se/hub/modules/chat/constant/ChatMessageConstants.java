package com.se.hub.modules.chat.constant;

/**
 * Chat Message Constants
 * Contains all message strings for Chat module
 */
public class ChatMessageConstants {
    // API Response Messages
    public static final String API_MESSAGE_CREATED_SUCCESS = "Message created successfully";
    public static final String API_MESSAGES_RETRIEVED_SUCCESS = "Messages retrieved successfully";
    public static final String API_CONVERSATION_CREATED_SUCCESS = "Conversation created successfully";
    public static final String API_CONVERSATIONS_RETRIEVED_SUCCESS = "Conversations retrieved successfully";
    
    // Error Messages
    public static final String CONVERSATION_NOT_FOUND_MESSAGE = "Conversation not found";
    public static final String NOT_PARTICIPANT_MESSAGE = "You are not a participant of this conversation";
    public static final String CONVERSATION_ALREADY_EXISTS_MESSAGE = "Conversation already exists";
    public static final String INVALID_PARTICIPANT_COUNT_MESSAGE = "Conversation must have at least 2 participants";
    public static final String MESSAGE_TOO_LONG_MESSAGE = "Message exceeds maximum length";
    public static final String SESSION_NOT_FOUND_MESSAGE = "Session not found";
    public static final String GENERATION_ERROR_MESSAGE = "Error generating hash";
    
    // Common API Response Messages
    public static final String API_BAD_REQUEST = "Bad request";
    public static final String API_INTERNAL_ERROR = "Internal server error";
    
    private ChatMessageConstants() {
        // Prevent instantiation
    }
}

