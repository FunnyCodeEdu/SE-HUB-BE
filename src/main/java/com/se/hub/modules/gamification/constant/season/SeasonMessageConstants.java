package com.se.hub.modules.gamification.constant.season;

public final class SeasonMessageConstants {
    private SeasonMessageConstants() {}

    public static final String NAME_REQUIRED = "Season name is required";
    public static final String NAME_MAX = "Season name exceeds maximum length";
    public static final String START_AT_REQUIRED = "Season start time is required";
    public static final String END_AT_REQUIRED = "Season end time is required";
    public static final String STATUS_REQUIRED = "Season status is required";
    public static final String REWARDS_REQUIRED = "Season rewards list is required";

    // API Response Messages
    public static final String API_SEASON_CREATED_SUCCESS = "Season created successfully";
    public static final String API_SEASON_RETRIEVED_ALL_SUCCESS = "Retrieved all seasons successfully";
    public static final String API_SEASON_RETRIEVED_BY_ID_SUCCESS = "Retrieved season by ID successfully";
    public static final String API_SEASON_UPDATED_SUCCESS = "Season updated successfully";
    public static final String API_SEASON_DELETED_SUCCESS = "Season deleted successfully";
    public static final String API_BAD_REQUEST = "Bad request";
    public static final String API_INTERNAL_ERROR = "Internal server error";
}

