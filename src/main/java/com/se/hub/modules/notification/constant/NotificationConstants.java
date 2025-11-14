package com.se.hub.modules.notification.constant;

public class NotificationConstants {
    //===== TABLE NAME ======
    public static final String TABLE_NOTIFICATION = "notifications";
    public static final String TABLE_USER_NOTIFICATION = "user_notifications";
    public static final String TABLE_NOTIFICATION_TEMPLATE = "notification_templates";
    public static final String TABLE_NOTIFICATION_SETTING = "notification_settings";

    //===== NOTIFICATION COLUMN NAME ======
    public static final String COL_NOTIFICATION_TYPE = "notification_type";
    public static final String COL_TITLE = "title";
    public static final String COL_CONTENT = "content";
    public static final String COL_METADATA = "metadata";
    public static final String COL_TARGET_TYPE = "target_type";
    public static final String COL_TARGET_ID = "target_id";

    //===== USER_NOTIFICATION COLUMN NAME ======
    public static final String COL_USER_ID = "user_id";
    public static final String COL_NOTIFICATION_ID = "notification_id";
    public static final String COL_STATUS = "status";
    public static final String COL_READ_AT = "read_at";

    //===== NOTIFICATION_TEMPLATE COLUMN NAME ======
    public static final String COL_TEMPLATE_TYPE = "template_type";
    public static final String COL_TEMPLATE_TITLE = "template_title";
    public static final String COL_TEMPLATE_CONTENT = "template_content";

    //===== NOTIFICATION_SETTING COLUMN NAME ======
    public static final String COL_SETTING_USER_ID = "user_id";
    public static final String COL_EMAIL_ENABLED = "email_enabled";
    public static final String COL_PUSH_ENABLED = "push_enabled";
    public static final String COL_MENTION_ENABLED = "mention_enabled";
    public static final String COL_LIKE_ENABLED = "like_enabled";
    public static final String COL_COMMENT_ENABLED = "comment_enabled";
    public static final String COL_BLOG_ENABLED = "blog_enabled";
    public static final String COL_ACHIEVEMENT_ENABLED = "achievement_enabled";
    public static final String COL_FOLLOW_ENABLED = "follow_enabled";
    public static final String COL_SYSTEM_ENABLED = "system_enabled";

    //===== COLUMN DEFINITIONS ======
    public static final String TITLE_DEFINITION = "VARCHAR(255)";
    public static final String CONTENT_DEFINITION = "TEXT";
    public static final String METADATA_DEFINITION = "JSONB";
    public static final String TARGET_TYPE_DEFINITION = "VARCHAR(50)";
    public static final String TARGET_ID_DEFINITION = "VARCHAR(255)";
    public static final String TEMPLATE_TITLE_DEFINITION = "VARCHAR(255)";
    public static final String TEMPLATE_CONTENT_DEFINITION = "TEXT";

    //===== VALIDATION VALUE LIMITS ======
    public static final int TITLE_MIN_LENGTH = 1;
    public static final int TITLE_MAX_LENGTH = 255;
    public static final int CONTENT_MAX_LENGTH = 5000;
    public static final int TARGET_ID_MAX_LENGTH = 255;
    public static final int RECENT_LIST_MAX_SIZE = 100;
    public static final int RECENT_LIST_DEFAULT_SIZE = 50;

    //===== REDIS KEY PATTERNS ======
    public static final String REDIS_KEY_UNREAD_PREFIX = "notif:unread:user:";
    public static final String REDIS_KEY_RECENT_PREFIX = "notif:recent:user:";
    public static final String REDIS_KEY_CHANNEL_PREFIX = "notif:channel:user:";
    public static final String REDIS_KEY_AGG_PREFIX = "notif:agg:";

    //===== REDIS TTL ======
    public static final long REDIS_TTL_RECENT_LIST_SECONDS = 86400; // 24 hours
    public static final long REDIS_TTL_AGGREGATION_SECONDS = 60; // 1 minute
    public static final long REDIS_TTL_UNREAD_COUNT_SECONDS = 604800; // 7 days

    //===== BATCH PROCESSING ======
    public static final int BATCH_SIZE = 1000; // Batch size for database operations
    public static final int SYSTEM_ANNOUNCEMENT_PAGE_SIZE = 500; // Page size for system announcement pagination

    private NotificationConstants() {
        // Prevent instantiation
    }
}

