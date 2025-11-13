package com.se.hub.common.constant;

/**
 * HTTP Response Code Constants
 * Contains HTTP status codes for API responses
 */
public class ResponseCode {
    public static final String OK_200 = "200";
    public static final String BAD_REQUEST_400 = "400";
    public static final String NOT_FOUND_404 = "404";
    public static final String INTERNAL_ERROR_500 = "500";

    private ResponseCode() {
        // Prevent instantiation
    }
}

