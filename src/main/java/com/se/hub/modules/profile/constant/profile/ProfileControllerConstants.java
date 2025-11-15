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
    public static final String FOLLOW_USER_OPERATION_SUMMARY = "Follow a user";
    public static final String UNFOLLOW_USER_OPERATION_SUMMARY = "Unfollow a user";
    public static final String CHECK_FOLLOW_OPERATION_SUMMARY = "Check if following a user";
    public static final String GET_FOLLOWING_OPERATION_SUMMARY = "Get following list";
    public static final String GET_FOLLOWERS_OPERATION_SUMMARY = "Get followers list";
    
    // Operation descriptions
    public static final String CREATE_DEFAULT_OPERATION_DESCRIPTION = "Create a default profile for a user";
    public static final String UPDATE_OPERATION_DESCRIPTION = "Update user profile information";
    public static final String GET_BY_USER_ID_OPERATION_DESCRIPTION = "Retrieve profile by user ID";
    public static final String GET_BY_PROFILE_ID_OPERATION_DESCRIPTION = "Retrieve profile by profile ID";
    public static final String GET_ALL_OPERATION_DESCRIPTION = "Retrieve all profiles with pagination support";
    public static final String GET_MY_PROFILE_OPERATION_DESCRIPTION = "Retrieve current user's profile";
    public static final String FOLLOW_USER_OPERATION_DESCRIPTION = "Follow another user";
    public static final String UNFOLLOW_USER_OPERATION_DESCRIPTION = "Unfollow a user";
    public static final String CHECK_FOLLOW_OPERATION_DESCRIPTION = "Check if current user is following a specific user";
    public static final String GET_FOLLOWING_OPERATION_DESCRIPTION = "Get list of users that current user is following";
    public static final String GET_FOLLOWERS_OPERATION_DESCRIPTION = "Get list of users that follow current user";
    
    // Response descriptions
    public static final String CREATE_DEFAULT_SUCCESS_RESPONSE = "Default profile created successfully";
    public static final String UPDATE_SUCCESS_RESPONSE = "Profile updated successfully";
    public static final String GET_BY_USER_ID_SUCCESS_RESPONSE = "Profile retrieved successfully";
    public static final String GET_BY_PROFILE_ID_SUCCESS_RESPONSE = "Profile retrieved successfully";
    public static final String GET_ALL_SUCCESS_RESPONSE = "Profiles retrieved successfully";
    public static final String GET_MY_PROFILE_SUCCESS_RESPONSE = "Current user's profile retrieved successfully";
    public static final String FOLLOW_USER_SUCCESS_RESPONSE = "User followed successfully";
    public static final String UNFOLLOW_USER_SUCCESS_RESPONSE = "User unfollowed successfully";
    public static final String CHECK_FOLLOW_SUCCESS_RESPONSE = "Follow status retrieved successfully";
    public static final String GET_FOLLOWING_SUCCESS_RESPONSE = "Following list retrieved successfully";
    public static final String GET_FOLLOWERS_SUCCESS_RESPONSE = "Followers list retrieved successfully";
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
