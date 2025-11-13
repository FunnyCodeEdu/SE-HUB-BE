package com.se.hub.modules.blog.constant;

/**
 * Blog Message Codes
 * Contains HTTP response codes for Blog module
 */
public class BlogMessageCodes {
    // Success Codes
    public static final String SUCCESS = "M001";
    public static final String CREATED = "M002";
    public static final String UPDATED = "M003";
    public static final String DELETED = "M004";
    public static final String RETRIEVED = "M005";

    // Error Codes
    public static final String VALIDATION_ERROR = "E001";
    public static final String NOT_FOUND = "E002";
    public static final String UNAUTHORIZED = "E003";
    public static final String FORBIDDEN = "E004";
    public static final String INTERNAL_ERROR = "E005";

    private BlogMessageCodes() {
        // Prevent instantiation
    }
}

