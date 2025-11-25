package com.se.hub.modules.profile.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.profile.constant.privacy.PrivacyConstants;
import com.se.hub.modules.profile.constant.privacy.PrivacyErrorCodeConstants;
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
@Table(name = PrivacyConstants.TABLE_PRIVACY_SETTING)
@Entity
public class PrivacySetting extends BaseEntity {

    @NotNull(message = PrivacyErrorCodeConstants.USER_ID_REQUIRED)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = PrivacyConstants.COL_USER_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false,
            unique = true)
    Profile user;

    @Builder.Default
    @Column(name = PrivacyConstants.COL_PROFILE_PUBLIC,
            nullable = false)
    Boolean profilePublic = true; // Trang cá nhân công khai/riêng tư

    @Builder.Default
    @Column(name = PrivacyConstants.COL_EMAIL_VISIBLE,
            nullable = false)
    Boolean emailVisible = false; // Mặc định ẩn email

    @Builder.Default
    @Column(name = PrivacyConstants.COL_PHONE_VISIBLE,
            nullable = false)
    Boolean phoneVisible = true;

    @Builder.Default
    @Column(name = PrivacyConstants.COL_ADDRESS_VISIBLE,
            nullable = false)
    Boolean addressVisible = true;

    @Builder.Default
    @Column(name = PrivacyConstants.COL_DATE_OF_BIRTH_VISIBLE,
            nullable = false)
    Boolean dateOfBirthVisible = true;

    @Builder.Default
    @Column(name = PrivacyConstants.COL_MAJOR_VISIBLE,
            nullable = false)
    Boolean majorVisible = true;

    @Builder.Default
    @Column(name = PrivacyConstants.COL_BIO_VISIBLE,
            nullable = false)
    Boolean bioVisible = true;

    @Builder.Default
    @Column(name = PrivacyConstants.COL_SOCIAL_MEDIA_VISIBLE,
            nullable = false)
    Boolean socialMediaVisible = true; // Mạng xã hội

    @Builder.Default
    @Column(name = PrivacyConstants.COL_ACHIEVEMENTS_VISIBLE,
            nullable = false)
    Boolean achievementsVisible = true;

    @Builder.Default
    @Column(name = PrivacyConstants.COL_STATS_VISIBLE,
            nullable = false)
    Boolean statsVisible = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivacySetting that = (PrivacySetting) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}

