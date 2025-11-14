package com.se.hub.modules.notification.entity;

import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.notification.constant.NotificationConstants;
import com.se.hub.modules.notification.constant.NotificationErrorCodeConstants;
import com.se.hub.modules.notification.enums.NotificationTemplateType;
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
@Table(name = NotificationConstants.TABLE_NOTIFICATION_TEMPLATE)
@Entity
public class NotificationTemplate extends BaseEntity {

    @NotNull(message = NotificationErrorCodeConstants.NOTIFICATION_TYPE_INVALID)
    @Enumerated(EnumType.STRING)
    @Column(name = NotificationConstants.COL_TEMPLATE_TYPE,
            nullable = false,
            unique = true,
            columnDefinition = NotificationConstants.TARGET_TYPE_DEFINITION)
    NotificationTemplateType templateType;

    @NotBlank
    @Size(min = NotificationConstants.TITLE_MIN_LENGTH,
            max = NotificationConstants.TITLE_MAX_LENGTH)
    @Column(name = NotificationConstants.COL_TEMPLATE_TITLE,
            nullable = false,
            columnDefinition = NotificationConstants.TEMPLATE_TITLE_DEFINITION)
    String templateTitle;

    @NotBlank
    @Size(max = NotificationConstants.CONTENT_MAX_LENGTH)
    @Column(name = NotificationConstants.COL_TEMPLATE_CONTENT,
            nullable = false,
            columnDefinition = NotificationConstants.TEMPLATE_CONTENT_DEFINITION)
    String templateContent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTemplate that = (NotificationTemplate) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}


