package com.se.hub.modules.blog.constant;

public class BlogConstants {
    public static final String TABLE_BLOG = "blogs";
    public static final String TABLE_BLOG_REACTION = "blog_reactions";

    public static final String COL_AUTHOR_ID = "author_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_COVER_IMAGE_URL = "cover_image_url";
    public static final String COL_COMMENT_COUNT = "cmt_count";
    public static final String COL_REACTION_COUNT = "reaction_count";
    public static final String COL_VIEW_COUNT = "view_count";
    public static final String COL_ALLOW_COMMENTS = "allow_comments";
    public static final String COL_IS_APPROVED = "is_approved";
    
    public static final String COL_BLOG_ID = "blog_id";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_IS_LIKE = "is_like";
    
    public static final String UNIQUE_BLOG_REACTION_USER_BLOG = "uk_blog_reaction_user_blog";

    public static final int CONTENT_MAX_LENGTH = 50000;
    public static final String CONTENT_DEFINITION = "TEXT";
    public static final String IMG_URL_DEFINITION = "VARCHAR(500)";
    
    private BlogConstants() {
        // Prevent instantiation
    }
}
