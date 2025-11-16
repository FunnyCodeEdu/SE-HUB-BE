package com.se.hub.modules.profile.constant.activity;

public class ActivityControllerConstants {
    
    // Operation summaries
    public static final String GET_ACTIVITY_OPERATION_SUMMARY = "Get activity by date";
    
    // Operation descriptions
    public static final String GET_ACTIVITY_OPERATION_DESCRIPTION = "Get activity count for a specific profile on a specific date";
    
    // Response descriptions
    public static final String GET_ACTIVITY_SUCCESS_RESPONSE = "Activity retrieved successfully";
    
    // Parameter descriptions
    public static final String PROFILE_ID_PARAM_DESCRIPTION = "Unique identifier of the profile";
    public static final String DATE_PARAM_DESCRIPTION = "Date to get activity for (format: YYYY-MM-DD). If not provided, uses current date";
    
    private ActivityControllerConstants() {
        // Utility class
    }
}

