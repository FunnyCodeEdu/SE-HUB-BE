package com.se.hub.modules.gamification.constant.rewardstreak;

public final class RewardStreakMessageConstants {
    private RewardStreakMessageConstants() {}

    public static final String STREAK_TARGET_REQUIRED = "Streak target is required";
    public static final String STREAK_TARGET_MIN = "Streak target must be greater than or equal to 0";
    public static final String DESCRIPTION_REQUIRED = "Description is required";
    public static final String DESCRIPTION_MAX = "Description exceeds maximum length";

    // API Response Messages
    public static final String API_STREAK_REWARD_CREATED_SUCCESS = "Streak reward created successfully";
    public static final String API_STREAK_REWARD_RETRIEVED_ALL_SUCCESS = "Retrieved all streak rewards successfully";
    public static final String API_STREAK_REWARD_RETRIEVED_BY_ID_SUCCESS = "Retrieved streak reward by ID successfully";
    public static final String API_STREAK_REWARD_UPDATED_SUCCESS = "Streak reward updated successfully";
    public static final String API_STREAK_REWARD_DELETED_SUCCESS = "Streak reward deleted successfully";
    public static final String API_BAD_REQUEST = "Bad request";
    public static final String API_INTERNAL_ERROR = "Internal server error";
}

