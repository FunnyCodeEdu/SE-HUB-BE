package com.se.hub.modules.gamification.constant.gamificationprofile;

public final class GamificationProfileConstants {
    private GamificationProfileConstants() {}

    // ===== TABLE NAME =====
    public static final String TABLE_GAMIFICATION_PROFILE = "gamification_profile";

    // ===== COLUMN NAMES =====
    public static final String PROFILE_ID = "profileId";
    public static final String TOTAL_XP = "totalXp";
    public static final String SEASON_XP = "seasonXp";
    public static final String FREEZE_COUNT = "freezeCount";
    public static final String REPAIR_COUNT = "repairCount";

    // ===== COLUMN DEFINITIONS =====
    public static final String TOTAL_XP_DEFINITION = "BIGINT";
    public static final String SEASON_XP_DEFINITION = "BIGINT";
    public static final String FREEZE_COUNT_DEFINITION = "INT";
    public static final String REPAIR_COUNT_DEFINITION = "INT";

    // ===== DEFAULT VALUES =====
    public static final long DEFAULT_XP = 0L;
    public static final int DEFAULT_FREEZE_COUNT = 0;
    public static final int DEFAULT_REPAIR_COUNT = 0;
}

