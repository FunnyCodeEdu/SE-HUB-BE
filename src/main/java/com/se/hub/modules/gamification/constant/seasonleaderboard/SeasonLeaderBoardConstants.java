package com.se.hub.modules.gamification.constant.seasonleaderboard;

public final class SeasonLeaderBoardConstants {
    private SeasonLeaderBoardConstants() {}

    // ===== TABLE NAME =====
    public static final String TABLE_SEASON_LEADERBOARD = "season_leaderboard";

    // ===== COLUMN NAMES =====
    public static final String SEASON_XP = "seasonXp";
    public static final String FINAL_RANK = "finalRank";
    public static final String REWARD_STATUS = "rewardStatus";
    public static final String SEASON_ID = "season_id";
    public static final String GAMIFICATION_PROFILE_ID = "gamification_profile_id";

    // ===== COLUMN DEFINITIONS =====
    public static final String SEASON_XP_DEFINITION = "BIGINT";
    public static final String FINAL_RANK_DEFINITION = "INT";
    public static final String REWARD_STATUS_DEFINITION = "VARCHAR(50)";
}


