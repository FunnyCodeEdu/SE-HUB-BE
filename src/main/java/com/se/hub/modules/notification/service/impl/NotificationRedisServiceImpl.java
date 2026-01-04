package com.se.hub.modules.notification.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.hub.modules.notification.constant.NotificationConstants;
import com.se.hub.modules.notification.dto.response.NotificationResponse;
import com.se.hub.modules.notification.service.api.NotificationRedisService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis service implementation for notification caching and pub/sub
 * Uses Redis for:
 * - Unread count caching
 * - Recent notifications list
 * - Pub/Sub for real-time delivery
 * - Aggregation data
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationRedisServiceImpl implements NotificationRedisService {
    StringRedisTemplate stringRedisTemplate;
    ObjectMapper objectMapper;

    @Override
    public void incrementUnreadCount(String userId) {
        String key = NotificationConstants.REDIS_KEY_UNREAD_PREFIX + userId;
        stringRedisTemplate.opsForValue().increment(key);
        // Set TTL if key doesn't exist or doesn't have TTL
        stringRedisTemplate.expire(key, Duration.ofSeconds(NotificationConstants.REDIS_TTL_UNREAD_COUNT_SECONDS));
        log.debug("NotificationRedisService_incrementUnreadCount_Incremented unread count for user: {}", userId);
    }

    @Override
    public void decrementUnreadCount(String userId) {
        String key = NotificationConstants.REDIS_KEY_UNREAD_PREFIX + userId;
        Long count = stringRedisTemplate.opsForValue().decrement(key);
        if (count != null && count < 0) {
            // Ensure count doesn't go below 0
            stringRedisTemplate.opsForValue().set(key, "0");
        }
        log.debug("NotificationRedisService_decrementUnreadCount_Decremented unread count for user: {}", userId);
    }

    @Override
    public Long getUnreadCount(String userId) {
        String key = NotificationConstants.REDIS_KEY_UNREAD_PREFIX + userId;
        String countStr = stringRedisTemplate.opsForValue().get(key);
        if (countStr == null) {
            return null;
        }
        try {
            return Long.parseLong(countStr);
        } catch (NumberFormatException e) {
            log.warn("NotificationRedisService_getUnreadCount_Invalid unread count format for user: {}", userId);
            return null;
        }
    }

    @Override
    public void setUnreadCount(String userId, long count) {
        String key = NotificationConstants.REDIS_KEY_UNREAD_PREFIX + userId;
        stringRedisTemplate.opsForValue().set(key, String.valueOf(count), 
                Duration.ofSeconds(NotificationConstants.REDIS_TTL_UNREAD_COUNT_SECONDS));
        log.debug("NotificationRedisService_setUnreadCount_Set unread count to {} for user: {}", count, userId);
    }

    @Override
    public void addToRecentList(String userId, NotificationResponse notification) {
        String key = NotificationConstants.REDIS_KEY_RECENT_PREFIX + userId;
        try {
            String notificationJson = objectMapper.writeValueAsString(notification);
            // Use list, add to left (most recent first)
            stringRedisTemplate.opsForList().leftPush(key, notificationJson);
            // Trim list to keep only recent items
            stringRedisTemplate.opsForList().trim(key, 0, (long)NotificationConstants.RECENT_LIST_MAX_SIZE - 1);
            // Set TTL
            stringRedisTemplate.expire(key, Duration.ofSeconds(NotificationConstants.REDIS_TTL_RECENT_LIST_SECONDS));
            log.debug("NotificationRedisService_addToRecentList_Added notification to recent list for user: {}", userId);
        } catch (Exception e) {
            log.error("NotificationRedisService_addToRecentList_Error adding notification to recent list", e);
        }
    }

    @Override
    public List<NotificationResponse> getRecentList(String userId, int limit) {
        String key = NotificationConstants.REDIS_KEY_RECENT_PREFIX + userId;
        List<String> notificationJsonList = stringRedisTemplate.opsForList().range(key, 0, (long)limit - 1);
        
        if (notificationJsonList == null || notificationJsonList.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<NotificationResponse> notifications = new ArrayList<>();
        for (String json : notificationJsonList) {
            try {
                NotificationResponse notification = objectMapper.readValue(json, NotificationResponse.class);
                notifications.add(notification);
            } catch (Exception e) {
                log.warn("NotificationRedisService_getRecentList_Error parsing notification JSON", e);
            }
        }
        
        return notifications;
    }

    @Override
    public void clearRecentList(String userId) {
        String key = NotificationConstants.REDIS_KEY_RECENT_PREFIX + userId;
        stringRedisTemplate.delete(key);
        log.debug("NotificationRedisService_clearRecentList_Cleared recent list for user: {}", userId);
    }

    @Override
    public void publishNotification(String userId, NotificationResponse notification) {
        // Use single channel "notifications" for SSE compatibility
        String channel = "notifications";
        try {
            // Create message with userId and payload
            Map<String, Object> message = Map.of(
                "userId", userId,
                "payload", notification
            );
            String messageJson = objectMapper.writeValueAsString(message);
            stringRedisTemplate.convertAndSend(channel, messageJson);
            log.debug("NotificationRedisService_publishNotification_Published notification to channel: {} for user: {}", channel, userId);
        } catch (Exception e) {
            log.error("NotificationRedisService_publishNotification_Error publishing notification", e);
        }
    }

    @Override
    public void addToAggregation(String aggregationKey, String eventData) {
        String key = NotificationConstants.REDIS_KEY_AGG_PREFIX + aggregationKey;
        stringRedisTemplate.opsForSet().add(key, eventData);
        stringRedisTemplate.expire(key, Duration.ofSeconds(NotificationConstants.REDIS_TTL_AGGREGATION_SECONDS));
        log.debug("NotificationRedisService_addToAggregation_Added event to aggregation: {}", aggregationKey);
    }

    @Override
    public List<String> getAggregationEvents(String aggregationKey) {
        String key = NotificationConstants.REDIS_KEY_AGG_PREFIX + aggregationKey;
        Set<String> events = stringRedisTemplate.opsForSet().members(key);
        return events != null ? new ArrayList<>(events) : new ArrayList<>();
    }

    @Override
    public void removeAggregation(String aggregationKey) {
        String key = NotificationConstants.REDIS_KEY_AGG_PREFIX + aggregationKey;
        stringRedisTemplate.delete(key);
        log.debug("NotificationRedisService_removeAggregation_Removed aggregation: {}", aggregationKey);
    }

    @Override
    public void batchIncrementUnreadCount(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        // Batch increment with TTL - process in parallel using virtual threads
        userIds.parallelStream().forEach(userId -> {
            try {
                incrementUnreadCount(userId);
            } catch (Exception e) {
                log.warn("NotificationRedisService_batchIncrementUnreadCount_Error incrementing for user: {}", userId, e);
            }
        });

        log.debug("NotificationRedisService_batchIncrementUnreadCount_Batch incremented unread count for {} users", userIds.size());
    }

    @Override
    public void batchPublishNotifications(Map<String, NotificationResponse> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return;
        }

        // Batch publish - process in parallel using virtual threads
        notifications.entrySet().parallelStream().forEach(entry -> {
            try {
                publishNotification(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                log.warn("NotificationRedisService_batchPublishNotifications_Error publishing notification for user: {}", 
                        entry.getKey(), e);
            }
        });

        log.debug("NotificationRedisService_batchPublishNotifications_Batch published notifications for {} users", notifications.size());
    }
}

