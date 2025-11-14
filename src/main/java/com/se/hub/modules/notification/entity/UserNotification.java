package com.se.hub.modules.notification.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.notification.constant.NotificationConstants;
import com.se.hub.modules.notification.constant.NotificationErrorCodeConstants;
import com.se.hub.modules.notification.enums.NotificationStatus;
import com.se.hub.modules.profile.entity.Profile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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
@Table(name = NotificationConstants.TABLE_USER_NOTIFICATION)
@Entity
public class UserNotification extends BaseEntity {

    @NotNull(message = NotificationErrorCodeConstants.USER_ID_REQUIRED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = NotificationConstants.COL_USER_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Profile user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = NotificationConstants.COL_NOTIFICATION_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Notification notification;

    @Builder.Default
    @NotNull(message = NotificationErrorCodeConstants.NOTIFICATION_STATUS_INVALID)
    @Enumerated(EnumType.STRING)
    @Column(name = NotificationConstants.COL_STATUS,
            nullable = false,
            columnDefinition = NotificationConstants.TARGET_TYPE_DEFINITION)
    NotificationStatus status = NotificationStatus.UNREAD;

    @Column(name = NotificationConstants.COL_READ_AT)
    Instant readAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserNotification that = (UserNotification) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}


