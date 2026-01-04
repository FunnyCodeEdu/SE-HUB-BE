package com.se.hub.modules.profile.constant.userlevel;

import com.se.hub.modules.profile.constant.userstats.UserStatsConstants;

public class UserLevelConstants {
    //===== VALIDATION LIMITS ======
    public static final int MIN_POINTS_MIN = UserStatsConstants.POINTS_MIN;
    public static final int MAX_POINTS_MIN = UserStatsConstants.POINTS_MIN;

    //===== VALUES ======
    public static final int COPPER_MIN = UserStatsConstants.POINTS_MIN;
    public static final int COPPER_MAX = 99;
    public static final int SILVER_MIN = COPPER_MAX + 1;
    public static final int SILVER_MAX = 199;
    public static final int GOLD_MIN = SILVER_MAX + 1;
    public static final int GOLD_MAX = 299;
    public static final int PLATINUM_MIN = GOLD_MAX + 1;
    public static final int PLATINUM_MAX = 399;
    public static final int DIAMOND_MIN = PLATINUM_MAX + 1;
    public static final int DIAMOND_MAX = 599;

    private UserLevelConstants() {}
}

