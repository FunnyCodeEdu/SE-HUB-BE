package com.se.hub.modules.notification.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.profile.repository.ProfileRepository;
import com.se.hub.modules.notification.dto.request.UpdateNotificationSettingRequest;
import com.se.hub.modules.notification.dto.response.NotificationResponse;
import com.se.hub.modules.notification.dto.response.NotificationSettingResponse;
import com.se.hub.modules.notification.dto.response.UnreadCountResponse;
import com.se.hub.modules.notification.entity.NotificationSetting;
import com.se.hub.modules.notification.mapper.NotificationSettingMapper;
import com.se.hub.modules.notification.repository.NotificationSettingRepository;
import com.se.hub.modules.notification.entity.UserNotification;
import com.se.hub.modules.notification.enums.NotificationStatus;
import com.se.hub.modules.notification.exception.NotificationErrorCode;
import com.se.hub.modules.notification.mapper.NotificationMapper;
import com.se.hub.modules.notification.repository.UserNotificationRepository;
import com.se.hub.modules.notification.service.api.NotificationRedisService;
import com.se.hub.modules.notification.service.api.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Notification Service Implementation
 * 
 * Virtual Thread Best Practice:
 * - This service uses synchronous blocking I/O operations (JPA repository calls)
 * - Virtual threads automatically handle blocking operations efficiently
 * - No need to use CompletableFuture or reactive APIs
 * - Each method call will run on a virtual thread, allowing high concurrency
 * - Database operations are blocking but virtual threads handle them efficiently
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {
    UserNotificationRepository userNotificationRepository;
    NotificationMapper notificationMapper;
    NotificationRedisService notificationRedisService;
    NotificationSettingRepository notificationSettingRepository;
    NotificationSettingMapper notificationSettingMapper;
    ProfileRepository profileRepository;

    @Override
    public PagingResponse<NotificationResponse> getNotifications(PagingRequest request) {
        log.debug("NotificationService_getNotifications_Fetching notifications for user: {}", AuthUtils.getCurrentUserId());
        String userId = AuthUtils.getCurrentUserId();
        
        // Try to get from Redis cache first (recent list)
        if (request.getPage() == 1) {
            List<NotificationResponse> recentNotifications = notificationRedisService.getRecentList(
                    userId, 
                    request.getPageSize()
            );
            if (!recentNotifications.isEmpty()) {
                return PagingResponse.<NotificationResponse>builder()
                        .currentPage(0)
                        .totalPages(1)
                        .pageSize(recentNotifications.size())
                        .totalElement(recentNotifications.size())
                        .data(recentNotifications)
                        .build();
            }
        }
        
        // Fallback to DB
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<UserNotification> userNotifications = userNotificationRepository.findAllByUser_IdOrderByCreateDateDesc(userId, pageable);
        
        List<NotificationResponse> responses = userNotifications.getContent().stream()
                .map(notificationMapper::toNotificationResponse)
                .toList();
        
        // Cache first page to Redis
        if (request.getPage() == 1 && !responses.isEmpty()) {
            notificationRedisService.clearRecentList(userId);
            responses.forEach(notif -> notificationRedisService.addToRecentList(userId, notif));
        }
        
        return PagingResponse.<NotificationResponse>builder()
                .currentPage(userNotifications.getNumber())
                .totalPages(userNotifications.getTotalPages())
                .pageSize(userNotifications.getSize())
                .totalElement(userNotifications.getTotalElements())
                .data(responses)
                .build();
    }

    @Override
    public NotificationResponse getNotificationById(String notificationId) {
        log.debug("NotificationService_getNotificationById_Fetching notification with id: {} for user: {}", 
                notificationId, AuthUtils.getCurrentUserId());
        String userId = AuthUtils.getCurrentUserId();
        
        UserNotification userNotification = userNotificationRepository.findByIdAndUser_Id(notificationId, userId)
                .orElseThrow(() -> {
                    log.error("NotificationService_getNotificationById_Notification not found with id: {} for user: {}", 
                            notificationId, userId);
                    return NotificationErrorCode.USER_NOTIFICATION_NOT_FOUND.toException();
                });
        
        return notificationMapper.toNotificationResponse(userNotification);
    }

    @Override
    @Transactional
    public void markAsRead(String notificationId) {
        log.debug("NotificationService_markAsRead_Marking notification as read: {} for user: {}", 
                notificationId, AuthUtils.getCurrentUserId());
        String userId = AuthUtils.getCurrentUserId();
        
        int updated = userNotificationRepository.markAsReadByIdAndUserId(
                notificationId,
                userId,
                NotificationStatus.READ,
                Instant.now()
        );
        
        if (updated == 0) {
            log.error("NotificationService_markAsRead_Notification not found with id: {} for user: {}", 
                    notificationId, userId);
            throw NotificationErrorCode.USER_NOTIFICATION_NOT_FOUND.toException();
        }
        
        // Update Redis cache
        notificationRedisService.decrementUnreadCount(userId);
        // Clear recent list cache to force refresh from DB with updated status
        notificationRedisService.clearRecentList(userId);
        
        log.debug("NotificationService_markAsRead_Notification marked as read successfully");
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        log.debug("NotificationService_markAllAsRead_Marking all notifications as read for user: {}", 
                AuthUtils.getCurrentUserId());
        String userId = AuthUtils.getCurrentUserId();
        
        userNotificationRepository.markAllAsReadByUserId(
                userId,
                NotificationStatus.READ,
                NotificationStatus.UNREAD,
                Instant.now()
        );
        
        // Update Redis cache
        notificationRedisService.setUnreadCount(userId, 0);
        // Clear recent list cache to force refresh from DB with updated status
        notificationRedisService.clearRecentList(userId);
        
        log.debug("NotificationService_markAllAsRead_All notifications marked as read successfully");
    }

    @Override
    public UnreadCountResponse getUnreadCount() {
        log.debug("NotificationService_getUnreadCount_Getting unread count for user: {}", AuthUtils.getCurrentUserId());
        String userId = AuthUtils.getCurrentUserId();
        
        // Try to get from Redis cache first
        Long cachedCount = notificationRedisService.getUnreadCount(userId);
        if (cachedCount != null) {
            return UnreadCountResponse.builder()
                    .unreadCount(cachedCount)
                    .build();
        }
        
        // Fallback to DB
        long unreadCount = userNotificationRepository.countUnreadByUserIdAndStatus(userId, NotificationStatus.UNREAD);
        
        // Cache to Redis
        notificationRedisService.setUnreadCount(userId, unreadCount);
        
        return UnreadCountResponse.builder()
                .unreadCount(unreadCount)
                .build();
    }

    @Override
    @Transactional
    public void deleteNotification(String notificationId) {
        log.debug("NotificationService_deleteNotification_Deleting notification: {} for user: {}", 
                notificationId, AuthUtils.getCurrentUserId());
        String userId = AuthUtils.getCurrentUserId();
        
        UserNotification userNotification = userNotificationRepository.findByIdAndUser_Id(notificationId, userId)
                .orElseThrow(() -> {
                    log.error("NotificationService_deleteNotification_Notification not found with id: {} for user: {}", 
                            notificationId, userId);
                    return NotificationErrorCode.USER_NOTIFICATION_NOT_FOUND.toException();
                });
        
        boolean wasUnread = userNotification.getStatus() == NotificationStatus.UNREAD;
        
        userNotificationRepository.delete(userNotification);
        
        // Update Redis cache
        if (wasUnread) {
            notificationRedisService.decrementUnreadCount(userId);
        }
        // Clear recent list cache to remove deleted notification
        notificationRedisService.clearRecentList(userId);
        
        log.debug("NotificationService_deleteNotification_Notification deleted successfully");
    }

    @Override
    public NotificationSettingResponse getSettings() {
        log.debug("NotificationService_getSettings_Getting notification settings for user: {}", AuthUtils.getCurrentUserId());
        String userId = AuthUtils.getCurrentUserId();
        
        NotificationSetting setting = notificationSettingRepository.findByUser_User_Id(userId)
                .orElseGet(() -> {
                    // Create default settings if not exists
                    NotificationSetting defaultSetting = NotificationSetting.builder()
                            .user(profileRepository.findByUserId(userId)
                                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND)))
                            .build();
                    defaultSetting.setCreatedBy(userId);
                    defaultSetting.setUpdateBy(userId);
                    return notificationSettingRepository.save(defaultSetting);
                });
        
        return notificationSettingMapper.toNotificationSettingResponse(setting);
    }

    @Override
    @Transactional
    public NotificationSettingResponse updateSettings(UpdateNotificationSettingRequest request) {
        log.debug("NotificationService_updateSettings_Updating notification settings for user: {}", AuthUtils.getCurrentUserId());
        String userId = AuthUtils.getCurrentUserId();
        
        NotificationSetting setting = notificationSettingRepository.findByUser_User_Id(userId)
                .orElseGet(() -> {
                    NotificationSetting newSetting = NotificationSetting.builder()
                            .user(profileRepository.findByUserId(userId)
                                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND)))
                            .build();
                    newSetting.setCreatedBy(userId);
                    newSetting.setUpdateBy(userId);
                    return notificationSettingRepository.save(newSetting);
                });
        
        setting = notificationSettingMapper.updateSettingFromRequest(setting, request);
        setting.setUpdateBy(userId);
        
        NotificationSetting savedSetting = notificationSettingRepository.save(setting);
        log.debug("NotificationService_updateSettings_Notification settings updated successfully");
        
        return notificationSettingMapper.toNotificationSettingResponse(savedSetting);
    }
}

