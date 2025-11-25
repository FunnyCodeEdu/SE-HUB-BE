package com.se.hub.modules.notification.service.impl;

import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.notification.dto.response.NotificationResponse;
import com.se.hub.modules.notification.service.api.NotificationTemplateService;
import com.se.hub.modules.notification.entity.Notification;
import com.se.hub.modules.notification.entity.UserNotification;
import com.se.hub.modules.notification.enums.NotificationStatus;
import com.se.hub.modules.notification.enums.NotificationType;
import com.se.hub.modules.notification.event.AchievementUnlockedEvent;
import com.se.hub.modules.notification.event.BlogApprovedEvent;
import com.se.hub.modules.notification.event.MentionEvent;
import com.se.hub.modules.notification.event.PostCommentedEvent;
import com.se.hub.modules.notification.event.PostLikedEvent;
import com.se.hub.modules.notification.event.SystemAnnouncementEvent;
import com.se.hub.modules.notification.mapper.NotificationMapper;
import com.se.hub.modules.notification.repository.NotificationRepository;
import com.se.hub.modules.notification.repository.NotificationSettingRepository;
import com.se.hub.modules.notification.repository.UserNotificationRepository;
import com.se.hub.modules.notification.service.api.NotificationInternalService;
import com.se.hub.modules.notification.service.api.NotificationRedisService;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Internal service implementation for creating notifications from events
 * This service handles the creation of Notification and UserNotification entities
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationInternalServiceImpl implements NotificationInternalService {
    NotificationRepository notificationRepository;
    UserNotificationRepository userNotificationRepository;
    ProfileRepository profileRepository;
    NotificationRedisService notificationRedisService;
    NotificationMapper notificationMapper;
    NotificationTemplateService notificationTemplateService;
    NotificationSettingRepository notificationSettingRepository;

    @Override
    @Transactional
    public void createMentionNotification(MentionEvent event) {
        log.debug("NotificationInternalService_createMentionNotification_Creating mention notification");
        
        // Check user's notification settings
        if (!shouldSendNotification(event.getMentionedUserId(), "mention")) {
            log.debug("NotificationInternalService_createMentionNotification_User {} has disabled mention notifications", event.getMentionedUserId());
            return;
        }
        
        Profile mentionedUser = profileRepository.findByUserId(event.getMentionedUserId())
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        
        Profile mentionerUser = profileRepository.findByUserId(event.getMentionerUserId())
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        
        String mentionerName = mentionerUser.getFullName() != null ? mentionerUser.getFullName() : mentionerUser.getUsername();
        String title = notificationTemplateService.getTemplateTitle(
                com.se.hub.modules.notification.enums.NotificationTemplateType.MENTION,
                mentionerName
        );
        
        String content = notificationTemplateService.getTemplateContent(
                com.se.hub.modules.notification.enums.NotificationTemplateType.MENTION,
                mentionerName,
                event.getCommentContent()
        );
        
        Notification notification = Notification.builder()
                .notificationType(NotificationType.MENTION)
                .title(title)
                .content(content)
                .targetType(event.getTargetType())
                .targetId(event.getTargetId())
                .build();
        notification.setCreatedBy(event.getMentionerUserId());
        notification.setUpdateBy(event.getMentionerUserId());
        
        notification = notificationRepository.save(notification);
        
        UserNotification userNotification = UserNotification.builder()
                .user(mentionedUser)
                .notification(notification)
                .status(NotificationStatus.UNREAD)
                .build();
        userNotification.setCreatedBy(event.getMentionerUserId());
        userNotification.setUpdateBy(event.getMentionerUserId());
        
        UserNotification savedUserNotification = userNotificationRepository.save(userNotification);
        
        // Update Redis cache
        notificationRedisService.incrementUnreadCount(event.getMentionedUserId());
        NotificationResponse notificationResponse = notificationMapper.toNotificationResponse(savedUserNotification);
        notificationRedisService.addToRecentList(event.getMentionedUserId(), notificationResponse);
        
        // Check push notification setting before publishing
        if (shouldPushNotification(event.getMentionedUserId())) {
            notificationRedisService.publishNotification(event.getMentionedUserId(), notificationResponse);
        }
        
        log.debug("NotificationInternalService_createMentionNotification_Mention notification created successfully");
    }

    @Override
    @Transactional
    public void createPostLikedNotification(PostLikedEvent event) {
        log.debug("NotificationInternalService_createPostLikedNotification_Creating post liked notification");
        
        // Check user's notification settings
        if (!shouldSendNotification(event.getPostOwnerUserId(), "like")) {
            log.debug("NotificationInternalService_createPostLikedNotification_User {} has disabled like notifications", event.getPostOwnerUserId());
            return;
        }
        
        Profile postOwner = profileRepository.findByUserId(event.getPostOwnerUserId())
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        
        Profile liker = profileRepository.findByUserId(event.getLikerUserId())
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        
        String likerName = liker.getFullName() != null ? liker.getFullName() : liker.getUsername();
        String title = notificationTemplateService.getTemplateTitle(
                com.se.hub.modules.notification.enums.NotificationTemplateType.POST_LIKED,
                likerName
        );
        
        String content = notificationTemplateService.getTemplateContent(
                com.se.hub.modules.notification.enums.NotificationTemplateType.POST_LIKED,
                likerName,
                event.getTargetTitle()
        );
        
        Notification notification = Notification.builder()
                .notificationType(NotificationType.POST_LIKED)
                .title(title)
                .content(content)
                .targetType(event.getTargetType())
                .targetId(event.getTargetId())
                .build();
        notification.setCreatedBy(event.getLikerUserId());
        notification.setUpdateBy(event.getLikerUserId());
        
        notification = notificationRepository.save(notification);
        
        UserNotification userNotification = UserNotification.builder()
                .user(postOwner)
                .notification(notification)
                .status(NotificationStatus.UNREAD)
                .build();
        userNotification.setCreatedBy(event.getLikerUserId());
        userNotification.setUpdateBy(event.getLikerUserId());
        
        UserNotification savedUserNotification = userNotificationRepository.save(userNotification);
        
        // Update Redis cache
        notificationRedisService.incrementUnreadCount(event.getPostOwnerUserId());
        NotificationResponse notificationResponse = notificationMapper.toNotificationResponse(savedUserNotification);
        notificationRedisService.addToRecentList(event.getPostOwnerUserId(), notificationResponse);
        
        // Check push notification setting before publishing
        if (shouldPushNotification(event.getPostOwnerUserId())) {
            notificationRedisService.publishNotification(event.getPostOwnerUserId(), notificationResponse);
        }
        
        log.debug("NotificationInternalService_createPostLikedNotification_Post liked notification created successfully");
    }

    @Override
    @Transactional
    public void createPostCommentedNotification(PostCommentedEvent event) {
        log.debug("NotificationInternalService_createPostCommentedNotification_Creating post commented notification");
        
        // Check user's notification settings
        if (!shouldSendNotification(event.getPostOwnerUserId(), "comment")) {
            log.debug("NotificationInternalService_createPostCommentedNotification_User {} has disabled comment notifications", event.getPostOwnerUserId());
            return;
        }
        
        Profile postOwner = profileRepository.findByUserId(event.getPostOwnerUserId())
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        
        Profile commenter = profileRepository.findByUserId(event.getCommenterUserId())
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        
        String commenterName = commenter.getFullName() != null ? commenter.getFullName() : commenter.getUsername();
        String title = notificationTemplateService.getTemplateTitle(
                com.se.hub.modules.notification.enums.NotificationTemplateType.POST_COMMENTED,
                commenterName
        );
        
        String content = notificationTemplateService.getTemplateContent(
                com.se.hub.modules.notification.enums.NotificationTemplateType.POST_COMMENTED,
                commenterName,
                event.getCommentContent()
        );
        
        Notification notification = Notification.builder()
                .notificationType(NotificationType.POST_COMMENTED)
                .title(title)
                .content(content)
                .targetType(event.getTargetType())
                .targetId(event.getTargetId())
                .build();
        notification.setCreatedBy(event.getCommenterUserId());
        notification.setUpdateBy(event.getCommenterUserId());
        
        notification = notificationRepository.save(notification);
        
        UserNotification userNotification = UserNotification.builder()
                .user(postOwner)
                .notification(notification)
                .status(NotificationStatus.UNREAD)
                .build();
        userNotification.setCreatedBy(event.getCommenterUserId());
        userNotification.setUpdateBy(event.getCommenterUserId());
        
        UserNotification savedUserNotification = userNotificationRepository.save(userNotification);
        
        // Update Redis cache
        notificationRedisService.incrementUnreadCount(event.getPostOwnerUserId());
        NotificationResponse notificationResponse = notificationMapper.toNotificationResponse(savedUserNotification);
        notificationRedisService.addToRecentList(event.getPostOwnerUserId(), notificationResponse);
        
        // Check push notification setting before publishing
        if (shouldPushNotification(event.getPostOwnerUserId())) {
            notificationRedisService.publishNotification(event.getPostOwnerUserId(), notificationResponse);
        }
        
        log.debug("NotificationInternalService_createPostCommentedNotification_Post commented notification created successfully");
    }

    @Override
    @Transactional
    public void createBlogApprovedNotification(BlogApprovedEvent event) {
        log.debug("NotificationInternalService_createBlogApprovedNotification_Creating blog approved notification");
        
        // Check user's notification settings
        if (!shouldSendNotification(event.getBlogAuthorUserId(), "blog")) {
            log.debug("NotificationInternalService_createBlogApprovedNotification_User {} has disabled blog notifications", event.getBlogAuthorUserId());
            return;
        }
        
        Profile blogAuthor = profileRepository.findByUserId(event.getBlogAuthorUserId())
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        
        String title = notificationTemplateService.getTemplateTitle(
                com.se.hub.modules.notification.enums.NotificationTemplateType.BLOG_APPROVED
        );
        
        String content = notificationTemplateService.getTemplateContent(
                com.se.hub.modules.notification.enums.NotificationTemplateType.BLOG_APPROVED,
                event.getBlogTitle()
        );
        
        Notification notification = Notification.builder()
                .notificationType(NotificationType.BLOG_APPROVED)
                .title(title)
                .content(content)
                .targetType("BLOG")
                .targetId(event.getBlogId())
                .build();
        notification.setCreatedBy("SYSTEM");
        notification.setUpdateBy("SYSTEM");
        
        notification = notificationRepository.save(notification);
        
        UserNotification userNotification = UserNotification.builder()
                .user(blogAuthor)
                .notification(notification)
                .status(NotificationStatus.UNREAD)
                .build();
        userNotification.setCreatedBy("SYSTEM");
        userNotification.setUpdateBy("SYSTEM");
        
        UserNotification savedUserNotification = userNotificationRepository.save(userNotification);
        
        // Update Redis cache
        notificationRedisService.incrementUnreadCount(event.getBlogAuthorUserId());
        NotificationResponse notificationResponse = notificationMapper.toNotificationResponse(savedUserNotification);
        notificationRedisService.addToRecentList(event.getBlogAuthorUserId(), notificationResponse);
        
        // Check push notification setting before publishing
        if (shouldPushNotification(event.getBlogAuthorUserId())) {
            notificationRedisService.publishNotification(event.getBlogAuthorUserId(), notificationResponse);
        }
        
        log.debug("NotificationInternalService_createBlogApprovedNotification_Blog approved notification created successfully");
    }

    @Override
    @Transactional
    public void createAchievementUnlockedNotification(AchievementUnlockedEvent event) {
        log.debug("NotificationInternalService_createAchievementUnlockedNotification_Creating achievement unlocked notification");
        
        // Check user's notification settings
        if (!shouldSendNotification(event.getUserId(), "achievement")) {
            log.debug("NotificationInternalService_createAchievementUnlockedNotification_User {} has disabled achievement notifications", event.getUserId());
            return;
        }
        
        Profile user = profileRepository.findByUserId(event.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        
        String title = notificationTemplateService.getTemplateTitle(
                com.se.hub.modules.notification.enums.NotificationTemplateType.ACHIEVEMENT_UNLOCKED,
                event.getAchievementName()
        );
        
        String content = notificationTemplateService.getTemplateContent(
                com.se.hub.modules.notification.enums.NotificationTemplateType.ACHIEVEMENT_UNLOCKED,
                event.getAchievementName()
        );
        
        Notification notification = Notification.builder()
                .notificationType(NotificationType.ACHIEVEMENT_UNLOCKED)
                .title(title)
                .content(content)
                .targetType("ACHIEVEMENT")
                .targetId(event.getAchievementId())
                .build();
        notification.setCreatedBy("SYSTEM");
        notification.setUpdateBy("SYSTEM");
        
        notification = notificationRepository.save(notification);
        
        UserNotification userNotification = UserNotification.builder()
                .user(user)
                .notification(notification)
                .status(NotificationStatus.UNREAD)
                .build();
        userNotification.setCreatedBy("SYSTEM");
        userNotification.setUpdateBy("SYSTEM");
        
        UserNotification savedUserNotification = userNotificationRepository.save(userNotification);
        
        // Update Redis cache
        notificationRedisService.incrementUnreadCount(event.getUserId());
        NotificationResponse notificationResponse = notificationMapper.toNotificationResponse(savedUserNotification);
        notificationRedisService.addToRecentList(event.getUserId(), notificationResponse);
        
        // Check push notification setting before publishing
        if (shouldPushNotification(event.getUserId())) {
            notificationRedisService.publishNotification(event.getUserId(), notificationResponse);
        }
        
        log.debug("NotificationInternalService_createAchievementUnlockedNotification_Achievement unlocked notification created successfully");
    }

    @Override
    @Transactional
    public void createSystemAnnouncementNotification(SystemAnnouncementEvent event) {
        log.debug("NotificationInternalService_createSystemAnnouncementNotification_Creating system announcement notification");
        
        Notification notification = Notification.builder()
                .notificationType(NotificationType.SYSTEM_ANNOUNCEMENT)
                .title(event.getTitle())
                .content(event.getContent())
                .metadata(event.getMetadata())
                .targetType("SYSTEM")
                .build();
        notification.setCreatedBy("SYSTEM");
        notification.setUpdateBy("SYSTEM");
        
        Notification savedNotification = notificationRepository.save(notification);
        
        if (event.getTargetUserIds() == null || event.getTargetUserIds().isEmpty()) {
            // Send to all users - use pagination to avoid loading all into memory
            processSystemAnnouncementForAllUsers(savedNotification);
        } else {
            // Send to specific users - use batch query to avoid N+1
            processSystemAnnouncementForSpecificUsers(savedNotification, event.getTargetUserIds());
        }
        
        log.debug("NotificationInternalService_createSystemAnnouncementNotification_System announcement notification created");
    }

    /**
     * Process system announcement for all users with pagination
     */
    private void processSystemAnnouncementForAllUsers(Notification notification) {
        int pageSize = com.se.hub.modules.notification.constant.NotificationConstants.SYSTEM_ANNOUNCEMENT_PAGE_SIZE;
        int page = 0;
        Page<Profile> profilePage;
        
        do {
            Pageable pageable = PageRequest.of(page, pageSize);
            profilePage = profileRepository.findAll(pageable);
            List<Profile> targetUsers = profilePage.getContent();
            
            if (!targetUsers.isEmpty()) {
                processBatchUserNotifications(notification, targetUsers);
            }
            
            page++;
        } while (profilePage.hasNext());
    }

    /**
     * Process system announcement for specific users with batch query
     */
    private void processSystemAnnouncementForSpecificUsers(Notification notification, List<String> targetUserIds) {
        // Use batch query to avoid N+1 queries
        List<Profile> targetUsers = profileRepository.findAllByUserIds(targetUserIds);
        
        if (!targetUsers.isEmpty()) {
            processBatchUserNotifications(notification, targetUsers);
        }
    }

    /**
     * Process batch of user notifications with chunk processing
     */
    private void processBatchUserNotifications(Notification notification, List<Profile> targetUsers) {
        int batchSize = com.se.hub.modules.notification.constant.NotificationConstants.BATCH_SIZE;
        
        // Process in chunks to avoid memory issues
        for (int i = 0; i < targetUsers.size(); i += batchSize) {
            int end = Math.min(i + batchSize, targetUsers.size());
            List<Profile> chunk = targetUsers.subList(i, end);
            
            List<UserNotification> userNotifications = chunk.stream()
                    .map(user -> {
                        UserNotification userNotification = UserNotification.builder()
                                .user(user)
                                .notification(notification)
                                .status(NotificationStatus.UNREAD)
                                .build();
                        userNotification.setCreatedBy("SYSTEM");
                        userNotification.setUpdateBy("SYSTEM");
                        return userNotification;
                    })
                    .toList();
            
            List<UserNotification> savedUserNotifications = userNotificationRepository.saveAll(userNotifications);
            
            // Batch Redis operations using pipeline
            batchUpdateRedisCache(savedUserNotifications);
        }
    }

    /**
     * Batch update Redis cache for multiple user notifications
     */
    private void batchUpdateRedisCache(List<UserNotification> savedUserNotifications) {
        // Group by userId to prepare batch operations
        Map<String, List<UserNotification>> notificationsByUser = savedUserNotifications.stream()
                .collect(Collectors.groupingBy(un -> un.getUser().getId()));
        
        // Use batch operations if available, otherwise process individually
        for (Map.Entry<String, List<UserNotification>> entry : notificationsByUser.entrySet()) {
            String userId = entry.getKey();
            List<UserNotification> userNotifications = entry.getValue();
            
            // Increment unread count for each notification
            for (int i = 0; i < userNotifications.size(); i++) {
                notificationRedisService.incrementUnreadCount(userId);
            }
            
            // Add to recent list and publish (only the latest one)
            if (!userNotifications.isEmpty()) {
                UserNotification latest = userNotifications.get(userNotifications.size() - 1);
                NotificationResponse notificationResponse = notificationMapper.toNotificationResponse(latest);
                notificationRedisService.addToRecentList(userId, notificationResponse);
                
                // Check push notification setting before publishing
                if (shouldPushNotification(userId)) {
                    notificationRedisService.publishNotification(userId, notificationResponse);
                }
            }
        }
    }

    /**
     * Check if notification should be sent based on user's settings
     * @param userId user ID
     * @param notificationType notification type (mention, like, comment, blog, achievement, follow, system)
     * @return true if notification should be sent, false otherwise
     */
    private boolean shouldSendNotification(String userId, String notificationType) {
        return notificationSettingRepository.findByUser_User_Id(userId)
                .map(setting -> {
                    return switch (notificationType) {
                        case "mention" -> setting.getMentionEnabled();
                        case "like" -> setting.getLikeEnabled();
                        case "comment" -> setting.getCommentEnabled();
                        case "blog" -> setting.getBlogEnabled();
                        case "achievement" -> setting.getAchievementEnabled();
                        case "follow" -> setting.getFollowEnabled();
                        case "system" -> setting.getSystemEnabled();
                        default -> true; // Default to enabled if type not recognized
                    };
                })
                .orElse(true); // Default to enabled if settings not found
    }

    /**
     * Check if push notification should be sent based on user's pushEnabled setting
     * @param userId user ID
     * @return true if push notification should be sent, false otherwise
     */
    private boolean shouldPushNotification(String userId) {
        return notificationSettingRepository.findByUser_User_Id(userId)
                .map(setting -> Boolean.TRUE.equals(setting.getPushEnabled()))
                .orElse(true); // Default to enabled if settings not found
    }
}

