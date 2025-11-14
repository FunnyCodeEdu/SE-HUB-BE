package com.se.hub.modules.notification.service.api;

import com.se.hub.modules.notification.event.AchievementUnlockedEvent;
import com.se.hub.modules.notification.event.BlogApprovedEvent;
import com.se.hub.modules.notification.event.MentionEvent;
import com.se.hub.modules.notification.event.PostCommentedEvent;
import com.se.hub.modules.notification.event.PostLikedEvent;
import com.se.hub.modules.notification.event.SystemAnnouncementEvent;

/**
 * Interface for handling notification events
 * All event handlers should implement this interface
 */
public interface NotificationEventHandler {
    void handleMentionEvent(MentionEvent event);
    void handlePostLikedEvent(PostLikedEvent event);
    void handlePostCommentedEvent(PostCommentedEvent event);
    void handleBlogApprovedEvent(BlogApprovedEvent event);
    void handleAchievementUnlockedEvent(AchievementUnlockedEvent event);
    void handleSystemAnnouncementEvent(SystemAnnouncementEvent event);
}

