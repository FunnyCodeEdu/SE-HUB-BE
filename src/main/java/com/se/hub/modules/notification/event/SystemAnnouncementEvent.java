package com.se.hub.modules.notification.event;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.List;

/**
 * Event emitted for system announcements
 */
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SystemAnnouncementEvent extends ApplicationEvent {
    List<String> targetUserIds; // null or empty means all users
    String title;
    String content;
    String metadata; // JSON string for additional data
    Instant occurredAt;

    public SystemAnnouncementEvent(Object source, List<String> targetUserIds, 
                                   String title, String content, String metadata) {
        super(source);
        this.targetUserIds = targetUserIds;
        this.title = title;
        this.content = content;
        this.metadata = metadata;
        this.occurredAt = Instant.now();
    }
}

