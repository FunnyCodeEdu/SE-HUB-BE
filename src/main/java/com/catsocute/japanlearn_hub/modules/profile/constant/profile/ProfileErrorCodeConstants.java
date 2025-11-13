package com.catsocute.japanlearn_hub.modules.profile.constant.profile;

public class ProfileErrorCodeConstants {
    
    // ===== VALIDATION ERROR MESSAGES =====
    public static final String FULL_NAME_NOT_BLANK = "PROFILE_FULL_NAME_NOT_BLANK";
    public static final String FULL_NAME_SIZE_INVALID = "PROFILE_FULL_NAME_SIZE_INVALID";
    public static final String PHONE_NUMBER_PATTERN_INVALID = "PROFILE_PHONE_NUMBER_PATTERN_INVALID";
    public static final String EMAIL_INVALID_FORMAT = "PROFILE_EMAIL_INVALID_FORMAT";
    public static final String EMAIL_SIZE_INVALID = "PROFILE_EMAIL_SIZE_INVALID";
    public static final String AVATAR_URL_SIZE_INVALID = "PROFILE_AVATAR_URL_SIZE_INVALID";
    public static final String GENDER_NOT_NULL = "PROFILE_GENDER_NOT_NULL";
    public static final String LEVEL_NOT_NULL = "PROFILE_LEVEL_NOT_NULL";
    public static final String USER_NOT_NULL = "PROFILE_USER_NOT_NULL";
    
    // ===== BUSINESS LOGIC ERROR CODES =====
    public static final String PROFILE_NOT_NULL = "PROFILE_NOT_NULL";
    public static final String PROFILE_NOT_FOUND = "PROFILE_NOT_FOUND";
    public static final String PROFILE_NOT_EXISTED = "PROFILE_NOT_EXISTED";
    public static final String PROFILE_EMAIL_EXISTED = "PROFILE_EMAIL_EXISTED";
    public static final String PROFILE_PHONE_EXISTED = "PROFILE_PHONE_EXISTED";
    public static final String PROFILE_ALREADY_EXISTS = "PROFILE_ALREADY_EXISTS";
    public static final String PROFILE_ALREADY_VERIFIED = "PROFILE_ALREADY_VERIFIED";
    public static final String PROFILE_ALREADY_BLOCKED = "PROFILE_ALREADY_BLOCKED";
    public static final String PROFILE_NOT_VERIFIED = "PROFILE_NOT_VERIFIED";
    public static final String PROFILE_NOT_ACTIVE = "PROFILE_NOT_ACTIVE";
    public static final String PROFILE_LEVEL_INVALID = "PROFILE_LEVEL_INVALID";
    public static final String PROFILE_UPDATE_FAILED = "PROFILE_UPDATE_FAILED";
    public static final String PROFILE_DELETE_FAILED = "PROFILE_DELETE_FAILED";
    public static final String PROFILE_VERIFICATION_FAILED = "PROFILE_VERIFICATION_FAILED";
    public static final String PROFILE_BLOCK_FAILED = "PROFILE_BLOCK_FAILED";
    public static final String PROFILE_UNBLOCK_FAILED = "PROFILE_UNBLOCK_FAILED";
    
    private ProfileErrorCodeConstants() {}
}
