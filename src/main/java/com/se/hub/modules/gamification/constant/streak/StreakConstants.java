package com.se.hub.modules.gamification.constant.streak;

public final class StreakConstants {
    private StreakConstants() {}

    // ===== TABLE NAME =====
    public static final String TABLE_STREAK = "streak";

    // ===== COLUMN NAMES =====
    public static final String CURRENT_STREAK = "currentStreak";
    public static final String MAX_STREAK = "maxStreak";
    public static final String LAST_ACTIVE_AT = "lastActiveAt";
    public static final String FREEZE_USED_TODAY = "freezeUsedToday";
    public static final String REWARD_ID = "reward_id";
    public static final String GAMIFICATION_PROFILE_ID = "gamification_profile_id";
    public static final String STREAK_ID = "streak_id";
    public static final String TABLE_STREAK_REWARD = "streak_reward";

    // ===== COLUMN DEFINITIONS =====
    public static final String STREAK_INT_DEFINITION = "INT";
    public static final String TIME_DEFINITION = "TIMESTAMP";
    public static final String BOOLEAN_DEFINITION = "BOOLEAN";
}

