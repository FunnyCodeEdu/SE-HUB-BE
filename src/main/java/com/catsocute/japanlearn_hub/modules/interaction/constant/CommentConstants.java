package com.catsocute.japanlearn_hub.modules.interaction.constant;

public class CommentConstants {
    //===== TABLE NAME ======
    public static final String TABLE_COMMENT = "comment";

    //===== COLUMN NAME ======
    public static final String COL_AUTHOR_ID = "authorId";
    public static final String COL_TARGET_TYPE = "targetType";
    public static final String COL_TARGET_ID = "targetId";
    public static final String COL_CONTENT = "content";
    public static final String COL_PARENT_COMMENT = "parentCommentId";

    //===== COLUMN DEFINITIONS ======
    public static final String TARGET_TYPE_DEFINITION = "VARCHAR(20)";
    public static final String TARGET_ID_DEFINITION = "VARCHAR(36)";
    public static final String CONTENT_DEFINITION = "TEXT";

    //===== VALIDATION VALUE LIMITS ======
    public static final int CONTENT_MIN_LENGTH = 1;
    public static final int CONTENT_MAX_LENGTH = 5000;
    public static final int TARGET_ID_MAX_LENGTH = 36;

    private CommentConstants() {}
}

