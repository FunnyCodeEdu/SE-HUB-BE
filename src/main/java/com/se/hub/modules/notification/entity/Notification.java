package com.se.hub.modules.notification.entity;

import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.notification.constant.NotificationConstants;
import com.se.hub.modules.notification.constant.NotificationErrorCodeConstants;
import com.se.hub.modules.notification.enums.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = NotificationConstants.TABLE_NOTIFICATION)
@Entity
public class Notification extends BaseEntity {

    @NotNull(message = NotificationErrorCodeConstants.NOTIFICATION_TYPE_INVALID)
    @Enumerated(EnumType.STRING)
    @Column(name = NotificationConstants.COL_NOTIFICATION_TYPE,
            nullable = false,
            columnDefinition = NotificationConstants.TARGET_TYPE_DEFINITION)
    NotificationType notificationType;

    @NotBlank(message = NotificationErrorCodeConstants.NOTIFICATION_ID_REQUIRED)
    @Size(min = NotificationConstants.TITLE_MIN_LENGTH,
            max = NotificationConstants.TITLE_MAX_LENGTH,
            message = NotificationErrorCodeConstants.NOTIFICATION_ID_REQUIRED)
    @Column(name = NotificationConstants.COL_TITLE,
            nullable = false,
            columnDefinition = NotificationConstants.TITLE_DEFINITION)
    String title;

    @NotBlank
    @Size(max = NotificationConstants.CONTENT_MAX_LENGTH)
    @Column(name = NotificationConstants.COL_CONTENT,
            nullable = false,
            columnDefinition = NotificationConstants.CONTENT_DEFINITION)
    String content;

    @Column(name = NotificationConstants.COL_METADATA,
            columnDefinition = NotificationConstants.METADATA_DEFINITION)
    String metadata; // JSON string for additional data

    @Column(name = NotificationConstants.COL_TARGET_TYPE,
            columnDefinition = NotificationConstants.TARGET_TYPE_DEFINITION)
    String targetType; // e.g., "BLOG", "POST", "COMMENT"

    @Size(max = NotificationConstants.TARGET_ID_MAX_LENGTH)
    @Column(name = NotificationConstants.COL_TARGET_ID,
            columnDefinition = NotificationConstants.TARGET_ID_DEFINITION)
    String targetId; // ID of the target entity

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}



