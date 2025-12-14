package com.se.hub.modules.gamification.constant.mission;

public final class MissionMessageConstants {
    private MissionMessageConstants() {}

    public static final String TYPE_REQUIRED = "Mission type is required";
    public static final String TARGET_TYPE_REQUIRED = "Mission target type is required";
    public static final String TOTAL_COUNT_REQUIRED = "Mission total count is required";
    public static final String TOTAL_COUNT_MIN = "Mission total count must be greater than or equal to 1";
    public static final String DESCRIPTION_REQUIRED = "Mission description is required";
    public static final String DESCRIPTION_MAX = "Mission description exceeds maximum length";

    // API Response Messages
    public static final String API_MISSION_CREATED_SUCCESS = "Mission created successfully";
    public static final String API_MISSION_RETRIEVED_ALL_SUCCESS = "Retrieved all missions successfully";
    public static final String API_MISSION_RETRIEVED_BY_ID_SUCCESS = "Retrieved mission by ID successfully";
    public static final String API_MISSION_UPDATED_SUCCESS = "Mission updated successfully";
    public static final String API_MISSION_DELETED_SUCCESS = "Mission deleted successfully";
    public static final String API_BAD_REQUEST = "Bad request";
    public static final String API_INTERNAL_ERROR = "Internal server error";
}

