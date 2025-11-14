package com.se.hub.modules.notification.service.impl;

import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.notification.constant.NotificationConstants;
import com.se.hub.modules.notification.dto.response.NotificationResponse;
import com.se.hub.modules.notification.entity.Notification;
import com.se.hub.modules.notification.entity.UserNotification;
import com.se.hub.modules.notification.enums.NotificationStatus;
import com.se.hub.modules.notification.enums.NotificationType;
import com.se.hub.modules.notification.mapper.NotificationMapper;
import com.se.hub.modules.notification.repository.NotificationRepository;
import com.se.hub.modules.notification.repository.UserNotificationRepository;
import com.se.hub.modules.notification.service.api.NotificationAggregationService;
import com.se.hub.modules.notification.service.api.NotificationRedisService;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Aggregation service implementation
 * Aggregates multiple similar events (e.g., likes) into single notifications
 * Prevents notification spam
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationAggregationServiceImpl implements NotificationAggregationService {
    NotificationRedisService notificationRedisService;
    StringRedisTemplate stringRedisTemplate;
    NotificationRepository notificationRepository;
    UserNotificationRepository userNotificationRepository;
    ProfileRepository profileRepository;
    NotificationMapper notificationMapper;

    private static final int MIN_EVENTS_FOR_AGGREGATION = 2;
    private static final String AGGREGATION_KEY_SEPARATOR = ":";

    @Override
    public void addEventToAggregation(String eventType, String targetId, String userId, String eventData) {
        String aggregationKey = buildAggregationKey(eventType, targetId, userId);
        notificationRedisService.addToAggregation(aggregationKey, eventData);
        log.debug("NotificationAggregationService_addEventToAggregation_Added event to aggregation: {}", aggregationKey);
    }

    @Override
    @Scheduled(fixedDelay = 60000) // Run every 60 seconds
    public void processAggregations() {
        log.debug("NotificationAggregationService_processAggregations_Processing aggregations");
        
        // Get all aggregation keys from Redis
        Set<String> keys = stringRedisTemplate.keys(NotificationConstants.REDIS_KEY_AGG_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            try {
                String aggregationKey = key.substring(NotificationConstants.REDIS_KEY_AGG_PREFIX.length());
                List<String> events = notificationRedisService.getAggregationEvents(aggregationKey);
                
                if (events.size() >= MIN_EVENTS_FOR_AGGREGATION) {
                    // Parse aggregation key to get event type, target ID, and user ID
                    String[] parts = aggregationKey.split(AGGREGATION_KEY_SEPARATOR);
                    if (parts.length >= 3) {
                        String eventType = parts[0];
                        String targetId = parts[1];
                        String userId = parts[2];
                        
                        // Create aggregated notification
                        createAggregatedNotification(eventType, targetId, userId, events);
                        
                        // Remove aggregation key
                        notificationRedisService.removeAggregation(aggregationKey);
                    }
                }
            } catch (Exception e) {
                log.error("NotificationAggregationService_processAggregations_Error processing aggregation key: {}", key, e);
            }
        }
        
        log.debug("NotificationAggregationService_processAggregations_Processed {} aggregation keys", keys.size());
    }

    /**
     * Build aggregation key from event type, target ID, and user ID
     */
    private String buildAggregationKey(String eventType, String targetId, String userId) {
        return eventType + AGGREGATION_KEY_SEPARATOR + targetId + AGGREGATION_KEY_SEPARATOR + userId;
    }

    /**
     * Create aggregated notification from multiple events
     */
    @Transactional
    private void createAggregatedNotification(String eventType, String targetId, String userId, List<String> events) {
        log.info("NotificationAggregationService_createAggregatedNotification_Creating aggregated notification for {} events of type {} for user {}", 
                events.size(), eventType, userId);
        
        try {
            // Get target user profile
            Profile targetUser = profileRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
            
            // Determine notification type based on event type
            NotificationType notificationType = mapEventTypeToNotificationType(eventType);
            if (notificationType == null) {
                log.warn("NotificationAggregationService_createAggregatedNotification_Unknown event type: {}", eventType);
                return;
            }
            
            // Create aggregated title and content
            String title = createAggregatedTitle(eventType, events.size());
            String content = createAggregatedContent(eventType, targetId, events.size());
            
            // Create notification entity
            Notification notification = Notification.builder()
                    .notificationType(notificationType)
                    .title(title)
                    .content(content)
                    .targetType(extractTargetType(eventType))
                    .targetId(targetId)
                    .build();
            notification.setCreatedBy("SYSTEM");
            notification.setUpdateBy("SYSTEM");
            
            Notification savedNotification = notificationRepository.save(notification);
            
            // Create user notification
            UserNotification userNotification = UserNotification.builder()
                    .user(targetUser)
                    .notification(savedNotification)
                    .status(NotificationStatus.UNREAD)
                    .build();
            userNotification.setCreatedBy("SYSTEM");
            userNotification.setUpdateBy("SYSTEM");
            
            UserNotification savedUserNotification = userNotificationRepository.save(userNotification);
            
            // Update Redis cache
            notificationRedisService.incrementUnreadCount(userId);
            NotificationResponse notificationResponse = notificationMapper.toNotificationResponse(savedUserNotification);
            notificationRedisService.addToRecentList(userId, notificationResponse);
            notificationRedisService.publishNotification(userId, notificationResponse);
            
            log.debug("NotificationAggregationService_createAggregatedNotification_Aggregated notification created successfully for user: {}", userId);
        } catch (Exception e) {
            log.error("NotificationAggregationService_createAggregatedNotification_Error creating aggregated notification", e);
        }
    }

    /**
     * Map event type to notification type
     */
    private NotificationType mapEventTypeToNotificationType(String eventType) {
        return switch (eventType.toUpperCase()) {
            case "POST_LIKED" -> NotificationType.POST_LIKED;
            case "POST_COMMENTED" -> NotificationType.POST_COMMENTED;
            case "MENTION" -> NotificationType.MENTION;
            default -> null;
        };
    }

    /**
     * Create aggregated title
     */
    private String createAggregatedTitle(String eventType, int count) {
        return switch (eventType.toUpperCase()) {
            case "POST_LIKED" -> String.format("%d người đã thích bài viết của bạn", count);
            case "POST_COMMENTED" -> String.format("%d người đã bình luận bài viết của bạn", count);
            case "MENTION" -> String.format("%d người đã nhắc đến bạn", count);
            default -> String.format("%d hoạt động mới", count);
        };
    }

    /**
     * Create aggregated content
     */
    private String createAggregatedContent(String eventType, String targetId, int count) {
        return switch (eventType.toUpperCase()) {
            case "POST_LIKED" -> String.format("Có %d người đã thích bài viết của bạn", count);
            case "POST_COMMENTED" -> String.format("Có %d người đã bình luận bài viết của bạn", count);
            case "MENTION" -> String.format("Có %d người đã nhắc đến bạn", count);
            default -> String.format("Có %d hoạt động mới liên quan đến bạn", count);
        };
    }

    /**
     * Extract target type from event type
     */
    private String extractTargetType(String eventType) {
        if (eventType.toUpperCase().contains("POST") || eventType.toUpperCase().contains("BLOG")) {
            return "BLOG";
        }
        return "POST";
    }
}

