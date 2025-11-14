package com.se.hub.modules.notification.event;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;

/**
 * Event emitted when a post/blog is liked
 */
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PostLikedEvent extends ApplicationEvent {
    String postOwnerUserId; // User who owns the post
    String likerUserId; // User who liked
    String targetType; // e.g., "BLOG", "POST"
    String targetId;
    String targetTitle; // Title of the post/blog
    Instant occurredAt;

    public PostLikedEvent(Object source, String postOwnerUserId, String likerUserId, 
                         String targetType, String targetId, String targetTitle) {
        super(source);
        this.postOwnerUserId = postOwnerUserId;
        this.likerUserId = likerUserId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.targetTitle = targetTitle;
        this.occurredAt = Instant.now();
    }
}

