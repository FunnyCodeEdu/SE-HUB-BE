package com.se.hub.modules.profile.service.impl;

import com.se.hub.modules.profile.dto.response.ProfileResponse;
import com.se.hub.modules.profile.entity.PrivacySetting;
import com.se.hub.modules.profile.service.api.PrivacyHelperService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivacyHelperServiceImpl implements PrivacyHelperService {

    @Override
    public ProfileResponse applyPrivacySettings(ProfileResponse profileResponse, PrivacySetting privacySetting, boolean isOwner) {
        if (isOwner) {
            // Owner can see everything
            return profileResponse;
        }

        if (privacySetting == null) {
            // Default: public except email
            profileResponse.setEmail(null);
            return profileResponse;
        }

        // Check if profile is public
        if (Boolean.FALSE.equals(privacySetting.getProfilePublic())) {
            // Private profile - only show basic info
            profileResponse.setPhoneNum(null);
            profileResponse.setEmail(null);
            profileResponse.setAddress(null);
            profileResponse.setDateOfBirth(null);
            profileResponse.setMajor(null);
            profileResponse.setBio(null);
            profileResponse.setUserStats(null);
            profileResponse.setAchievements(null);
            return profileResponse;
        }

        // Public profile - apply individual field visibility
        if (Boolean.FALSE.equals(privacySetting.getEmailVisible())) {
            profileResponse.setEmail(null);
        }
        if (Boolean.FALSE.equals(privacySetting.getPhoneVisible())) {
            profileResponse.setPhoneNum(null);
        }
        if (Boolean.FALSE.equals(privacySetting.getAddressVisible())) {
            profileResponse.setAddress(null);
        }
        if (Boolean.FALSE.equals(privacySetting.getDateOfBirthVisible())) {
            profileResponse.setDateOfBirth(null);
        }
        if (Boolean.FALSE.equals(privacySetting.getMajorVisible())) {
            profileResponse.setMajor(null);
        }
        if (Boolean.FALSE.equals(privacySetting.getBioVisible())) {
            profileResponse.setBio(null);
        }
        if (Boolean.FALSE.equals(privacySetting.getStatsVisible())) {
            profileResponse.setUserStats(null);
        }
        if (Boolean.FALSE.equals(privacySetting.getAchievementsVisible())) {
            profileResponse.setAchievements(null);
        }
        // Note: socialMediaVisible is not directly mapped in ProfileResponse
        // It would need to be handled separately if there's a social media field

        return profileResponse;
    }

    @Override
    public boolean isProfileAccessible(PrivacySetting privacySetting, boolean isOwner) {
        if (isOwner) {
            return true;
        }
        if (privacySetting == null) {
            // Default: public
            return true;
        }
        return Boolean.TRUE.equals(privacySetting.getProfilePublic());
    }
}

