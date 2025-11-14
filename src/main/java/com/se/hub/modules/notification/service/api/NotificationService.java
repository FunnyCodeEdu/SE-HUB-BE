package com.se.hub.modules.notification.service.api;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.notification.dto.request.UpdateNotificationSettingRequest;
import com.se.hub.modules.notification.dto.response.NotificationResponse;
import com.se.hub.modules.notification.dto.response.NotificationSettingResponse;
import com.se.hub.modules.notification.dto.response.UnreadCountResponse;

public interface NotificationService {
    /**
     * Get notifications for current user with paging
     * @param request paging request
     * @return paging response with notifications
     */
    PagingResponse<NotificationResponse> getNotifications(PagingRequest request);

    /**
     * Get notification by ID for current user
     * @param notificationId notification ID
     * @return notification response
     */
    NotificationResponse getNotificationById(String notificationId);

    /**
     * Mark notification as read
     * @param notificationId notification ID
     */
    void markAsRead(String notificationId);

    /**
     * Mark all notifications as read for current user
     */
    void markAllAsRead();

    /**
     * Get unread count for current user
     * @return unread count response
     */
    UnreadCountResponse getUnreadCount();

    /**
     * Delete notification by ID for current user
     * @param notificationId notification ID
     */
    void deleteNotification(String notificationId);

    /**
     * Get notification settings for current user
     * @return notification settings response
     */
    NotificationSettingResponse getSettings();

    /**
     * Update notification settings for current user
     * @param request update settings request
     * @return updated notification settings response
     */
    NotificationSettingResponse updateSettings(UpdateNotificationSettingRequest request);
}

