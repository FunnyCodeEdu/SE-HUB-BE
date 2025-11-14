package com.se.hub.modules.notification.service.api;

import com.se.hub.modules.notification.event.AchievementUnlockedEvent;
import com.se.hub.modules.notification.event.BlogApprovedEvent;
import com.se.hub.modules.notification.event.MentionEvent;
import com.se.hub.modules.notification.event.PostCommentedEvent;
import com.se.hub.modules.notification.event.PostLikedEvent;
import com.se.hub.modules.notification.event.SystemAnnouncementEvent;

/**
 * Internal service for creating notifications from events
 * This service is used internally by event handlers
 */
public interface NotificationInternalService {
    void createMentionNotification(MentionEvent event);
    void createPostLikedNotification(PostLikedEvent event);
    void createPostCommentedNotification(PostCommentedEvent event);
    void createBlogApprovedNotification(BlogApprovedEvent event);
    void createAchievementUnlockedNotification(AchievementUnlockedEvent event);
    void createSystemAnnouncementNotification(SystemAnnouncementEvent event);
}

