package com.se.hub.modules.notification.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.notification.constant.NotificationConstants;
import com.se.hub.modules.notification.constant.NotificationErrorCodeConstants;
import com.se.hub.modules.profile.entity.Profile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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
@Table(name = NotificationConstants.TABLE_NOTIFICATION_SETTING)
@Entity
public class NotificationSetting extends BaseEntity {

    @NotNull(message = NotificationErrorCodeConstants.USER_ID_REQUIRED)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = NotificationConstants.COL_SETTING_USER_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false,
            unique = true)
    Profile user;

    @Builder.Default
    @Column(name = NotificationConstants.COL_EMAIL_ENABLED,
            nullable = false)
    Boolean emailEnabled = true;

    @Builder.Default
    @Column(name = NotificationConstants.COL_PUSH_ENABLED,
            nullable = false)
    Boolean pushEnabled = true;

    @Builder.Default
    @Column(name = NotificationConstants.COL_MENTION_ENABLED,
            nullable = false)
    Boolean mentionEnabled = true;

    @Builder.Default
    @Column(name = NotificationConstants.COL_LIKE_ENABLED,
            nullable = false)
    Boolean likeEnabled = true;

    @Builder.Default
    @Column(name = NotificationConstants.COL_COMMENT_ENABLED,
            nullable = false)
    Boolean commentEnabled = true;

    @Builder.Default
    @Column(name = NotificationConstants.COL_BLOG_ENABLED,
            nullable = false)
    Boolean blogEnabled = true;

    @Builder.Default
    @Column(name = NotificationConstants.COL_ACHIEVEMENT_ENABLED,
            nullable = false)
    Boolean achievementEnabled = true;

    @Builder.Default
    @Column(name = NotificationConstants.COL_FOLLOW_ENABLED,
            nullable = false)
    Boolean followEnabled = true;

    @Builder.Default
    @Column(name = NotificationConstants.COL_SYSTEM_ENABLED,
            nullable = false)
    Boolean systemEnabled = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationSetting that = (NotificationSetting) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}



