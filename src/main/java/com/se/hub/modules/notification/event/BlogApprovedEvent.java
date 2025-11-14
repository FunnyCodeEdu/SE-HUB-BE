package com.se.hub.modules.notification.event;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;

/**
 * Event emitted when a blog is approved
 */
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BlogApprovedEvent extends ApplicationEvent {
    String blogAuthorUserId;
    String blogId;
    String blogTitle;
    Instant occurredAt;

    public BlogApprovedEvent(Object source, String blogAuthorUserId, String blogId, String blogTitle) {
        super(source);
        this.blogAuthorUserId = blogAuthorUserId;
        this.blogId = blogId;
        this.blogTitle = blogTitle;
        this.occurredAt = Instant.now();
    }
}

