package com.se.hub.modules.notification.service.impl;

import com.se.hub.modules.notification.event.AchievementUnlockedEvent;
import com.se.hub.modules.notification.event.BlogApprovedEvent;
import com.se.hub.modules.notification.event.MentionEvent;
import com.se.hub.modules.notification.event.PostCommentedEvent;
import com.se.hub.modules.notification.event.PostLikedEvent;
import com.se.hub.modules.notification.event.SystemAnnouncementEvent;
import com.se.hub.modules.notification.service.api.NotificationEventHandler;
import com.se.hub.modules.notification.service.api.NotificationInternalService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Notification Event Handler Implementation
 * 
 * Handles all notification-related domain events from other modules
 * Uses @Async to process events asynchronously with virtual threads
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationEventHandlerImpl implements NotificationEventHandler {
    
    NotificationInternalService notificationInternalService;

    @Override
    @Async
    @EventListener
    public void handleMentionEvent(MentionEvent event) {
        log.debug("NotificationEventHandler_handleMentionEvent_Processing mention event for user: {}", 
                event.getMentionedUserId());
        try {
            notificationInternalService.createMentionNotification(event);
            log.debug("NotificationEventHandler_handleMentionEvent_Mention notification created successfully");
        } catch (Exception e) {
            log.error("NotificationEventHandler_handleMentionEvent_Error processing mention event", e);
        }
    }

    @Override
    @Async
    @EventListener
    public void handlePostLikedEvent(PostLikedEvent event) {
        log.debug("NotificationEventHandler_handlePostLikedEvent_Processing post liked event for user: {}", 
                event.getPostOwnerUserId());
        try {
            notificationInternalService.createPostLikedNotification(event);
            log.debug("NotificationEventHandler_handlePostLikedEvent_Post liked notification created successfully");
        } catch (Exception e) {
            log.error("NotificationEventHandler_handlePostLikedEvent_Error processing post liked event", e);
        }
    }

    @Override
    @Async
    @EventListener
    public void handlePostCommentedEvent(PostCommentedEvent event) {
        log.debug("NotificationEventHandler_handlePostCommentedEvent_Processing post commented event for user: {}", 
                event.getPostOwnerUserId());
        try {
            notificationInternalService.createPostCommentedNotification(event);
            log.debug("NotificationEventHandler_handlePostCommentedEvent_Post commented notification created successfully");
        } catch (Exception e) {
            log.error("NotificationEventHandler_handlePostCommentedEvent_Error processing post commented event", e);
        }
    }

    @Override
    @Async
    @EventListener
    public void handleBlogApprovedEvent(BlogApprovedEvent event) {
        log.debug("NotificationEventHandler_handleBlogApprovedEvent_Processing blog approved event for user: {}", 
                event.getBlogAuthorUserId());
        try {
            notificationInternalService.createBlogApprovedNotification(event);
            log.debug("NotificationEventHandler_handleBlogApprovedEvent_Blog approved notification created successfully");
        } catch (Exception e) {
            log.error("NotificationEventHandler_handleBlogApprovedEvent_Error processing blog approved event", e);
        }
    }

    @Override
    @Async
    @EventListener
    public void handleAchievementUnlockedEvent(AchievementUnlockedEvent event) {
        log.debug("NotificationEventHandler_handleAchievementUnlockedEvent_Processing achievement unlocked event for user: {}", 
                event.getUserId());
        try {
            notificationInternalService.createAchievementUnlockedNotification(event);
            log.debug("NotificationEventHandler_handleAchievementUnlockedEvent_Achievement unlocked notification created successfully");
        } catch (Exception e) {
            log.error("NotificationEventHandler_handleAchievementUnlockedEvent_Error processing achievement unlocked event", e);
        }
    }

    @Override
    @Async
    @EventListener
    public void handleSystemAnnouncementEvent(SystemAnnouncementEvent event) {
        log.debug("NotificationEventHandler_handleSystemAnnouncementEvent_Processing system announcement event");
        try {
            notificationInternalService.createSystemAnnouncementNotification(event);
            log.debug("NotificationEventHandler_handleSystemAnnouncementEvent_System announcement notification created successfully");
        } catch (Exception e) {
            log.error("NotificationEventHandler_handleSystemAnnouncementEvent_Error processing system announcement event", e);
        }
    }
}

