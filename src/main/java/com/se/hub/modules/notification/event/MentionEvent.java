package com.se.hub.modules.notification.event;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;

/**
 * Event emitted when a user is mentioned in a comment
 */
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MentionEvent extends ApplicationEvent {
    String mentionedUserId; // User who was mentioned
    String mentionerUserId; // User who mentioned
    String commentId;
    String commentContent;
    String targetType; // e.g., "BLOG", "POST"
    String targetId;
    Instant occurredAt;

    public MentionEvent(Object source, String mentionedUserId, String mentionerUserId, 
                       String commentId, String commentContent, String targetType, String targetId) {
        super(source);
        this.mentionedUserId = mentionedUserId;
        this.mentionerUserId = mentionerUserId;
        this.commentId = commentId;
        this.commentContent = commentContent;
        this.targetType = targetType;
        this.targetId = targetId;
        this.occurredAt = Instant.now();
    }
}

