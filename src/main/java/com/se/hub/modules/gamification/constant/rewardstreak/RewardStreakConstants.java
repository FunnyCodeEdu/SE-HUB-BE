package com.se.hub.modules.gamification.constant.rewardstreak;

public final class RewardStreakConstants {
    private RewardStreakConstants() {}

    // ===== TABLE NAME =====
    public static final String TABLE_REWARD_STREAK = "streak_reward";
    public static final String TABLE_REWARD_STREAK_REWARD = "reward_streak_reward";

    // ===== COLUMN NAMES =====
    public static final String STREAK_TARGET = "streakTarget";
    public static final String DESCRIPTION = "description";
    public static final String IS_ACTIVE = "active";
    public static final String REWARD_STREAK_ID = "reward_streak_id";
    public static final String REWARD_ID = "reward_id";

    // ===== COLUMN DEFINITIONS =====
    public static final String STREAK_TARGET_DEFINITION = "INT";
    public static final String DESCRIPTION_DEFINITION = "VARCHAR(500)";
    public static final String BOOLEAN_DEFINITION = "BOOLEAN";

    // ===== VALIDATION LIMITS =====
    public static final int DESCRIPTION_MAX = 500;
}


