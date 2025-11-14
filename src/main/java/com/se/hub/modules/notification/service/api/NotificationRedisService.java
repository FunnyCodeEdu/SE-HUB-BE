package com.se.hub.modules.notification.service.api;

import com.se.hub.modules.notification.dto.response.NotificationResponse;

import java.util.List;

/**
 * Service for managing notification data in Redis
 * Handles caching of unread count, recent notifications, and aggregation data
 */
public interface NotificationRedisService {
    /**
     * Increment unread count for a user
     * @param userId user ID
     */
    void incrementUnreadCount(String userId);

    /**
     * Decrement unread count for a user
     * @param userId user ID
     */
    void decrementUnreadCount(String userId);

    /**
     * Get unread count for a user (from Redis cache)
     * @param userId user ID
     * @return unread count, or null if not cached
     */
    Long getUnreadCount(String userId);

    /**
     * Set unread count for a user
     * @param userId user ID
     * @param count unread count
     */
    void setUnreadCount(String userId, long count);

    /**
     * Add notification to recent list for a user
     * @param userId user ID
     * @param notification notification response
     */
    void addToRecentList(String userId, NotificationResponse notification);

    /**
     * Get recent notifications for a user (from Redis cache)
     * @param userId user ID
     * @param limit maximum number of notifications to return
     * @return list of recent notifications
     */
    List<NotificationResponse> getRecentList(String userId, int limit);

    /**
     * Clear recent list for a user
     * @param userId user ID
     */
    void clearRecentList(String userId);

    /**
     * Publish notification to Redis Pub/Sub channel
     * @param userId user ID
     * @param notification notification response
     */
    void publishNotification(String userId, NotificationResponse notification);

    /**
     * Add event to aggregation set
     * @param aggregationKey aggregation key (e.g., "post_liked:blogId:userId")
     * @param eventData event data as JSON string
     */
    void addToAggregation(String aggregationKey, String eventData);

    /**
     * Get aggregation events
     * @param aggregationKey aggregation key
     * @return list of event data
     */
    List<String> getAggregationEvents(String aggregationKey);

    /**
     * Remove aggregation key
     * @param aggregationKey aggregation key
     */
    void removeAggregation(String aggregationKey);

    /**
     * Batch increment unread count for multiple users
     * @param userIds list of user IDs
     */
    void batchIncrementUnreadCount(List<String> userIds);

    /**
     * Batch publish notifications to multiple users
     * @param notifications map of userId to NotificationResponse
     */
    void batchPublishNotifications(java.util.Map<String, NotificationResponse> notifications);
}

