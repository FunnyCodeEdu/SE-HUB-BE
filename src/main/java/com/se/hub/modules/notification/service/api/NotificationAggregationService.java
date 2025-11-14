package com.se.hub.modules.notification.service.api;

/**
 * Service for aggregating multiple similar events into single notifications
 * Prevents notification spam (e.g., 10 likes in 1 minute → 1 aggregated notification)
 */
public interface NotificationAggregationService {
    /**
     * Add event to aggregation pool
     * @param eventType event type (e.g., "POST_LIKED")
     * @param targetId target ID (e.g., blog ID)
     * @param userId user ID who performed the action
     * @param eventData event data as JSON string
     */
    void addEventToAggregation(String eventType, String targetId, String userId, String eventData);

    /**
     * Process aggregation and create notifications
     * Should be called by cron job periodically (30-60s)
     */
    void processAggregations();
}



