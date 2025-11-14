package com.se.hub.modules.notification.constant;

public class NotificationTemplateConstants {
    // Template Titles
    public static final String TEMPLATE_TITLE_MENTION = "{0} đã nhắc đến bạn";
    public static final String TEMPLATE_TITLE_POST_LIKED = "{0} đã thích bài viết của bạn";
    public static final String TEMPLATE_TITLE_POST_COMMENTED = "{0} đã bình luận bài viết của bạn";
    public static final String TEMPLATE_TITLE_BLOG_APPROVED = "Blog của bạn đã được duyệt";
    public static final String TEMPLATE_TITLE_BLOG_REJECTED = "Blog của bạn đã bị từ chối";
    public static final String TEMPLATE_TITLE_ACHIEVEMENT_UNLOCKED = "Bạn đã mở khóa thành tựu: {0}";
    public static final String TEMPLATE_TITLE_FOLLOWED_YOU = "{0} đã theo dõi bạn";
    public static final String TEMPLATE_TITLE_SYSTEM_ANNOUNCEMENT = "Thông báo hệ thống";

    // Template Contents
    public static final String TEMPLATE_CONTENT_MENTION = "{0} đã nhắc đến bạn trong bình luận: \"{1}\"";
    public static final String TEMPLATE_CONTENT_POST_LIKED = "{0} đã thích bài viết \"{1}\" của bạn";
    public static final String TEMPLATE_CONTENT_POST_COMMENTED = "{0} đã bình luận: \"{1}\" trên bài viết của bạn";
    public static final String TEMPLATE_CONTENT_BLOG_APPROVED = "Blog \"{0}\" của bạn đã được duyệt và đã được xuất bản";
    public static final String TEMPLATE_CONTENT_BLOG_REJECTED = "Blog \"{0}\" của bạn đã bị từ chối. Lý do: {1}";
    public static final String TEMPLATE_CONTENT_ACHIEVEMENT_UNLOCKED = "Chúc mừng! Bạn đã mở khóa thành tựu \"{0}\"";
    public static final String TEMPLATE_CONTENT_FOLLOWED_YOU = "{0} đã bắt đầu theo dõi bạn";
    public static final String TEMPLATE_CONTENT_SYSTEM_ANNOUNCEMENT = "{0}";

    private NotificationTemplateConstants() {
        // Prevent instantiation
    }
}


