package com.se.hub.modules.blog.constant;

public class BlogConstants {
    public static final String TABLE_BLOG = "blogs";

    public static final String COL_AUTHOR_ID = "author_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_COVER_IMAGE_URL = "cover_image_url";
    public static final String COL_COMMENT_COUNT = "cmt_count";
    public static final String COL_REACTION_COUNT = "reaction_count";
    public static final String COL_ALLOW_COMMENTS = "allow_comments";

    public static final int CONTENT_MAX_LENGTH = 50000;
    public static final String CONTENT_DEFINITION = "TEXT";
    public static final String IMG_URL_DEFINITION = "VARCHAR(500)";
}
