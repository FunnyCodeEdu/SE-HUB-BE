package com.se.hub.modules.notification.event;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;

/**
 * Event emitted when a user unlocks an achievement
 */
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AchievementUnlockedEvent extends ApplicationEvent {
    String userId;
    String achievementId;
    String achievementName;
    String achievementDescription;
    Instant occurredAt;

    public AchievementUnlockedEvent(Object source, String userId, String achievementId, 
                                   String achievementName, String achievementDescription) {
        super(source);
        this.userId = userId;
        this.achievementId = achievementId;
        this.achievementName = achievementName;
        this.achievementDescription = achievementDescription;
        this.occurredAt = Instant.now();
    }
}

