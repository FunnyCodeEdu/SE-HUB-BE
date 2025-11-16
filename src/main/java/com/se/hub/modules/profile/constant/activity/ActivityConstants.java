package com.se.hub.modules.profile.constant.activity;

public class ActivityConstants {
    //===== TABLE NAME ======
    public static final String TABLE_ACTIVITY = "activity";

    //===== COLUMN NAME ======
    public static final String COL_PROFILE_ID = "profile_id";
    public static final String COL_ACTIVITY_DATE = "activity_date";
    public static final String COL_COUNT = "count";

    //===== COLUMN DEFINITIONS ======
    public static final String COUNT_DEFINITION = "INTEGER NOT NULL DEFAULT 1";

    //===== VALIDATION LIMITS ======
    public static final int COUNT_MIN = 0;
    public static final int COUNT_DEFAULT = 1;

    //===== INDEXES ======
    public static final String INDEX_PROFILE_DATE = "idx_activity_profile_date";
    public static final String UNIQUE_CONSTRAINT_PROFILE_DATE = "uk_activity_profile_date";

    //===== COLOR LEVELS (GitHub Contribution Graph Style) ======
    public static final String COLOR_LEVEL_0 = "#ebedf0";  // No contributions
    public static final String COLOR_LEVEL_1 = "#9be9a8";  // 1-3 contributions
    public static final String COLOR_LEVEL_2 = "#40c463";  // 4-7 contributions
    public static final String COLOR_LEVEL_3 = "#30a14e";  // 8-14 contributions
    public static final String COLOR_LEVEL_4 = "#216e39";  // 15+ contributions

    //===== TIMEZONE ======
    public static final String DEFAULT_TIMEZONE = "Asia/Ho_Chi_Minh";

    private ActivityConstants() {}
}

