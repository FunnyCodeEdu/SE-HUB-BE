package com.se.hub.modules.interaction.constant;

public class ReactionConstants {
    //===== TABLE NAME ======
    public static final String TABLE_REACTION = "reaction";

    //===== COLUMN NAME ======
    public static final String COL_USER_ID = "userId";
    public static final String COL_TARGET_TYPE = "targetType";
    public static final String COL_TARGET_ID = "targetId";
    public static final String COL_REACTION_TYPE = "reactionType";

    //===== COLUMN DEFINITIONS ======
    public static final String TARGET_TYPE_DEFINITION = "VARCHAR(20)";
    public static final String TARGET_ID_DEFINITION = "VARCHAR(36)";
    public static final String REACTION_TYPE_DEFINITION = "VARCHAR(20)";

    //===== VALIDATION VALUE LIMITS ======
    public static final int TARGET_ID_MAX_LENGTH = 36;

    private ReactionConstants() {}
}

