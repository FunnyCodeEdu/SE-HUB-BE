package com.se.hub.modules.chat.constant;

/**
 * Chat Error Code Constants
 * Contains error code string constants for Chat module
 */
public class ChatErrorCodeConstants {
    // Conversation Error Codes
    public static final String CONVERSATION_NOT_FOUND = "CONVERSATION_NOT_FOUND";
    public static final String CONVERSATION_ID_IS_REQUIRED = "CONVERSATION_IS_REQUIRED";
    public static final String CONVERSATION_TYPE_IS_REQUIRED = "CONVERSATION_TYPE_IS_REQUIRED";
    public static final String NOT_PARTICIPANT = "NOT_PARTICIPANT";
    public static final String CONVERSATION_ALREADY_EXISTS = "CONVERSATION_ALREADY_EXISTS";
    public static final String INVALID_PARTICIPANT_COUNT = "INVALID_PARTICIPANT_COUNT";
    
    // Message Error Codes
    public static final String MESSAGE_TOO_LONG = "MESSAGE_TOO_LONG";
    public static final String MESSAGE_NOT_BLANK = "MESSAGE_NOT_BLANK";
    
    // Session Error Codes
    public static final String SESSION_NOT_FOUND = "SESSION_NOT_FOUND";

    //Participant Error codes
    public static final String PARTICIPANT_ID_ARE_REQUIRED = "PARTICIPANT_ID_ARE_REQUIRED";

    // Common Error Codes
    public static final String GENERATION_ERROR = "GENERATION_ERROR";
    
    private ChatErrorCodeConstants() {
        // Prevent instantiation
    }
}

