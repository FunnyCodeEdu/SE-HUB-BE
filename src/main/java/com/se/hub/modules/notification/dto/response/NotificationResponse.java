package com.se.hub.modules.notification.dto.response;

import com.se.hub.modules.notification.enums.NotificationStatus;
import com.se.hub.modules.notification.enums.NotificationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    String id;
    NotificationType notificationType;
    String title;
    String content;
    String metadata;
    String targetType;
    String targetId;
    NotificationStatus status;
    Instant readAt;
    Instant createDate;
    Instant updatedDate;
}


