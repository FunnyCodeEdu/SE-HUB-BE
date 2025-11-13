package com.se.hub.modules.profile.constant.profile;

public class ProfileControllerConstants {
    
    // Tag constants
    public static final String TAG_NAME = "Profile Management";
    public static final String TAG_DESCRIPTION = "APIs for managing user profiles";
    
    // Operation summaries
    public static final String CREATE_DEFAULT_OPERATION_SUMMARY = "Create default profile";
    public static final String UPDATE_OPERATION_SUMMARY = "Update profile";
    public static final String GET_BY_USER_ID_OPERATION_SUMMARY = "Get profile by user ID";
    public static final String GET_BY_PROFILE_ID_OPERATION_SUMMARY = "Get profile by profile ID";
    public static final String GET_ALL_OPERATION_SUMMARY = "Get all profiles with pagination";
    public static final String GET_MY_PROFILE_OPERATION_SUMMARY = "Get current user's profile";
    
    // Operation descriptions
    public static final String CREATE_DEFAULT_OPERATION_DESCRIPTION = "Create a default profile for a user";
    public static final String UPDATE_OPERATION_DESCRIPTION = "Update user profile information";
    public static final String GET_BY_USER_ID_OPERATION_DESCRIPTION = "Retrieve profile by user ID";
    public static final String GET_BY_PROFILE_ID_OPERATION_DESCRIPTION = "Retrieve profile by profile ID";
    public static final String GET_ALL_OPERATION_DESCRIPTION = "Retrieve all profiles with pagination support";
    public static final String GET_MY_PROFILE_OPERATION_DESCRIPTION = "Retrieve current user's profile";
    
    // Response descriptions
    public static final String CREATE_DEFAULT_SUCCESS_RESPONSE = "Default profile created successfully";
    public static final String UPDATE_SUCCESS_RESPONSE = "Profile updated successfully";
    public static final String GET_BY_USER_ID_SUCCESS_RESPONSE = "Profile retrieved successfully";
    public static final String GET_BY_PROFILE_ID_SUCCESS_RESPONSE = "Profile retrieved successfully";
    public static final String GET_ALL_SUCCESS_RESPONSE = "Profiles retrieved successfully";
    public static final String GET_MY_PROFILE_SUCCESS_RESPONSE = "Current user's profile retrieved successfully";
    public static final String BAD_REQUEST_RESPONSE = "Invalid request data";
    public static final String NOT_FOUND_RESPONSE = "Profile not found";
    public static final String INTERNAL_ERROR_RESPONSE = "Internal server error";
    
    // Parameter descriptions
    public static final String USER_ID_PARAM_DESCRIPTION = "Unique identifier of the user";
    public static final String PROFILE_ID_PARAM_DESCRIPTION = "Unique identifier of the profile";
    
    private ProfileControllerConstants() {
        // Utility class
    }
}
