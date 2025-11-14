package com.se.hub.modules.notification.event;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;

/**
 * Event emitted when a post/blog is commented
 */
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PostCommentedEvent extends ApplicationEvent {
    String postOwnerUserId; // User who owns the post
    String commenterUserId; // User who commented
    String commentId;
    String commentContent;
    String targetType; // e.g., "BLOG", "POST"
    String targetId;
    String targetTitle; // Title of the post/blog
    Instant occurredAt;

    public PostCommentedEvent(Object source, String postOwnerUserId, String commenterUserId,
                              String commentId, String commentContent, String targetType, 
                              String targetId, String targetTitle) {
        super(source);
        this.postOwnerUserId = postOwnerUserId;
        this.commenterUserId = commenterUserId;
        this.commentId = commentId;
        this.commentContent = commentContent;
        this.targetType = targetType;
        this.targetId = targetId;
        this.targetTitle = targetTitle;
        this.occurredAt = Instant.now();
    }
}


