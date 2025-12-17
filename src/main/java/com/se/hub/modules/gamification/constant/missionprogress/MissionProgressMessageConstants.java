package com.se.hub.modules.gamification.constant.missionprogress;

public final class MissionProgressMessageConstants {
    private MissionProgressMessageConstants() {}

    public static final String START_AT_REQUIRED = "Mission progress start time is required";
    public static final String END_AT_REQUIRED = "Mission progress end time is required";
    public static final String CURRENT_VALUE_REQUIRED = "Current value is required";
    public static final String CURRENT_VALUE_MIN = "Current value must be greater than or equal to 0";
    public static final String STATUS_REQUIRED = "Mission progress status is required";
    public static final String REWARD_STATUS_REQUIRED = "Reward status is required";
    public static final String MISSION_REQUIRED = "Mission reference is required";
    public static final String PROFILE_REQUIRED = "Gamification profile reference is required";

    // ===== API MESSAGES =====
    public static final String API_DAILY_PROGRESS_RETRIEVED_SUCCESS = "Daily mission progress retrieved successfully";
    public static final String API_BAD_REQUEST = "Invalid request data";
    public static final String API_INTERNAL_ERROR = "Internal server error";
}

