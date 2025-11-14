package com.se.hub.modules.interaction.constant;

public class InteractionErrorCodeConstants {
    // Comment Error Codes
    public static final String COMMENT_NOT_FOUND = "COMMENT_NOT_FOUND";
    public static final String COMMENT_AUTHOR_INVALID = "COMMENT_AUTHOR_INVALID";
    public static final String COMMENT_CONTENT_INVALID = "COMMENT_CONTENT_INVALID";
    public static final String COMMENT_TARGET_TYPE_INVALID = "COMMENT_TARGET_TYPE_INVALID";
    public static final String COMMENT_TARGET_ID_INVALID = "COMMENT_TARGET_ID_INVALID";
    public static final String COMMENT_ID_REQUIRED = "COMMENT_ID_REQUIRED";

    // Reaction Error Codes
    public static final String REACTION_ERROR = "REACTION_ERROR";
    public static final String REACTION_NOT_FOUND = "REACTION_NOT_FOUND";

    // Report Error Codes
    public static final String REPORT_NOT_FOUND = "REPORT_NOT_FOUND";
    public static final String REPORT_ERROR = "REPORT_ERROR";
    public static final String REPORT_ALREADY_EXISTS = "REPORT_ALREADY_EXISTS";
    public static final String REPORT_DELETE_FORBIDDEN = "REPORT_DELETE_FORBIDDEN";

    // Common Error Codes
    public static final String FORBIDDEN_OPERATION = "FORBIDDEN_OPERATION";
    public static final String INVALID_REQUEST = "INVALID_REQUEST";

    private InteractionErrorCodeConstants() {}
}

