package com.se.hub.modules.notification.constant;

public class NotificationMessageConstants {
    // Error Messages
    public static final String NOTIFICATION_NOT_FOUND_MESSAGE = "Notification not found";
    public static final String USER_NOTIFICATION_NOT_FOUND_MESSAGE = "User notification not found";
    public static final String NOTIFICATION_TYPE_INVALID_MESSAGE = "Notification type is invalid";
    public static final String NOTIFICATION_STATUS_INVALID_MESSAGE = "Notification status is invalid";
    public static final String NOTIFICATION_ID_REQUIRED_MESSAGE = "Notification ID is required";
    public static final String USER_ID_REQUIRED_MESSAGE = "User ID is required";
    public static final String TEMPLATE_NOT_FOUND_MESSAGE = "Notification template not found";

    // API Response Messages
    public static final String API_NOTIFICATION_CREATED_SUCCESS = "Notification created successfully";
    public static final String API_NOTIFICATION_RETRIEVED_SUCCESS = "Retrieved notifications successfully";
    public static final String API_NOTIFICATION_RETRIEVED_BY_ID_SUCCESS = "Retrieved notification by ID successfully";
    public static final String API_NOTIFICATION_MARKED_READ_SUCCESS = "Notification marked as read successfully";
    public static final String API_NOTIFICATION_MARKED_ALL_READ_SUCCESS = "All notifications marked as read successfully";
    public static final String API_NOTIFICATION_DELETED_SUCCESS = "Notification deleted successfully";
    public static final String API_UNREAD_COUNT_RETRIEVED_SUCCESS = "Retrieved unread count successfully";
    public static final String API_SETTINGS_RETRIEVED_SUCCESS = "Retrieved notification settings successfully";
    public static final String API_SETTINGS_UPDATED_SUCCESS = "Notification settings updated successfully";

    private NotificationMessageConstants() {
        // Prevent instantiation
    }
}

