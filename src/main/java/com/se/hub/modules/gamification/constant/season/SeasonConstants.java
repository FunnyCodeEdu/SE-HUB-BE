package com.se.hub.modules.gamification.constant.season;

public final class SeasonConstants {
    private SeasonConstants() {}

    // ===== TABLE NAME =====
    public static final String TABLE_SEASON = "season";
    public static final String TABLE_SEASON_REWARD = "season_reward";

    // ===== COLUMN NAMES =====
    public static final String NAME = "name";
    public static final String START_AT = "startAt";
    public static final String END_AT = "endAt";
    public static final String STATUS = "status";
    public static final String SEASON_ID = "season_id";
    public static final String REWARD_ID = "reward_id";

    // ===== COLUMN DEFINITIONS =====
    public static final String NAME_DEFINITION = "VARCHAR(255)";
    public static final String STATUS_DEFINITION = "VARCHAR(50)";
    public static final String TIME_DEFINITION = "TIMESTAMP";

    // ===== VALIDATION LIMITS =====
    public static final int NAME_MAX = 255;
}

