package com.se.hub.modules.gamification.constant.mission;

public final class MissionConstants {
    private MissionConstants() {}

    // ===== TABLE NAME =====
    public static final String TABLE_MISSION = "mission";

    // ===== COLUMN NAMES =====
    public static final String TYPE = "type";
    public static final String IS_ACTIVE = "isActive";
    public static final String TARGET_TYPE = "targetType";
    public static final String TOTAL_COUNT = "totalCount";
    public static final String DESCRIPTION = "description";
    public static final String REWARD_ID = "reward_id";
    public static final String MISSION_ID = "mission_id";
    public static final String TABLE_MISSION_REWARD = "mission_reward";

    // ===== COLUMN DEFINITIONS =====
    public static final String TYPE_DEFINITION = "VARCHAR(50)";
    public static final String TARGET_TYPE_DEFINITION = "VARCHAR(50)";
    public static final String DESCRIPTION_DEFINITION = "VARCHAR(500)";
    public static final String TOTAL_COUNT_DEFINITION = "INT";
}

