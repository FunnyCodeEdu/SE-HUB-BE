package com.catsocute.japanlearn_hub.common.constant;

public class ErrorCodeConstant {
    public static final String SERVER_UNCATEGORIZED_EXCEPTION = "SERVER_UNCATEGORIZED_EXCEPTION";

    // ==== AUTHENTICATION ERRORS ====
    public static final String AUTH_UNAUTHENTICATED =  "AUTH_UNAUTHENTICATED";
    public static final String AUTH_MISSING_TOKEN =  "AUTH_MISSING_TOKEN";

    // ==== AUTHORIZATION ERRORS ====
    public static final String PERM_UNAUTHORIZED = "PERM_UNAUTHORIZED";

    // ===== PAGINATION ERRORS =====
    public static final String PAGE_NUMBER_INVALID = "PAGE_NUMBER_INVALID";
    public static final String PAGE_SIZE_INVALID = "PAGE_SIZE_INVALID";

    // ==== ROLE ====
    public static final String ROLE_NOT_FOUND = "ROLE_NOT_FOUND";
}
