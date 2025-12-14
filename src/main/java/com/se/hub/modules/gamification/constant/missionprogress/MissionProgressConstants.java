package com.se.hub.modules.gamification.constant.missionprogress;

public final class MissionProgressConstants {
    private MissionProgressConstants() {}

    // ===== TABLE NAME =====
    public static final String TABLE_MISSION_PROGRESS = "mission_progress";

    // ===== COLUMN NAMES =====
    public static final String START_AT = "startAt";
    public static final String END_AT = "endAt";
    public static final String CURRENT_VALUE = "currentValue";
    public static final String STATUS = "status";
    public static final String REWARD_STATUS = "rewardStatus";
    public static final String MISSION_ID = "mission_id";
    public static final String GAMIFICATION_PROFILE_ID = "gamification_profile_id";

    // ===== COLUMN DEFINITIONS =====
    public static final String TIME_DEFINITION = "DATE";
    public static final String CURRENT_VALUE_DEFINITION = "BIGINT";
    public static final String STATUS_DEFINITION = "VARCHAR(50)";
    public static final String REWARD_STATUS_DEFINITION = "VARCHAR(50)";
}


