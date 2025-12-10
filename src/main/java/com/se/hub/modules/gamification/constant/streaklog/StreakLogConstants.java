package com.se.hub.modules.gamification.constant.streaklog;

public final class StreakLogConstants {
    private StreakLogConstants() {}

    // ===== TABLE NAME =====
    public static final String TABLE_STREAK_LOG = "streak_log";

    // ===== COLUMN NAMES =====
    public static final String DATE = "date";
    public static final String STATUS = "status";
    public static final String GAMIFICATION_PROFILE_ID = "gamification_profile_id";

    // ===== COLUMN DEFINITIONS =====
    public static final String TIME_DEFINITION = "TIMESTAMP";
    public static final String STATUS_DEFINITION = "VARCHAR(50)";
}


