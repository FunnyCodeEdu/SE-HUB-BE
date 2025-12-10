package com.se.hub.modules.gamification.constant.eventlog;

public final class GamificationEventLogConstants {
    private GamificationEventLogConstants() {}

    // ===== TABLE NAME =====
    public static final String TABLE_GAMIFICATION_EVENT_LOG = "gamification_event_log";

    // ===== COLUMN NAMES =====
    public static final String ACTION_TYPE = "actionType";
    public static final String XP_DELTA = "xpDelta";
    public static final String TOKEN_DELTA = "tokenDelta";
    public static final String GAMIFICATION_PROFILE_ID = "gamification_profile_id";

    // ===== COLUMN DEFINITIONS =====
    public static final String ACTION_TYPE_DEFINITION = "VARCHAR(50)";
    public static final String XP_DELTA_DEFINITION = "BIGINT";
    public static final String TOKEN_DELTA_DEFINITION = "BIGINT";
}


