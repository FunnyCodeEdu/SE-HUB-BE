package com.se.hub.modules.profile.service.impl;

import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.notification.dto.request.UpdateNotificationSettingRequest;
import com.se.hub.modules.notification.dto.response.NotificationSettingResponse;
import com.se.hub.modules.notification.service.api.NotificationService;
import com.se.hub.modules.profile.dto.request.UpdatePrivacySettingRequest;
import com.se.hub.modules.profile.dto.request.UpdateSettingsRequest;
import com.se.hub.modules.profile.dto.response.CombinedSettingsResponse;
import com.se.hub.modules.profile.dto.response.PrivacySettingResponse;
import com.se.hub.modules.profile.service.api.PrivacySettingService;
import com.se.hub.modules.profile.service.api.SettingsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SettingsServiceImpl implements SettingsService {
    NotificationService notificationService;
    PrivacySettingService privacySettingService;

    @Override
    public CombinedSettingsResponse getCombinedSettings() {
        log.debug("SettingsService_getCombinedSettings_Getting combined settings for user: {}", AuthUtils.getCurrentUserId());
        
        NotificationSettingResponse notificationSettings = notificationService.getSettings();
        PrivacySettingResponse privacySettings = privacySettingService.getPrivacySettings();
        
        return CombinedSettingsResponse.builder()
                .notificationSettings(notificationSettings)
                .privacySettings(privacySettings)
                .build();
    }

    @Override
    public CombinedSettingsResponse updateSettings(UpdateSettingsRequest request) {
        log.debug("SettingsService_updateSettings_Updating settings for user: {}", AuthUtils.getCurrentUserId());
        
        NotificationSettingResponse notificationSettings = null;
        PrivacySettingResponse privacySettings = null;
        
        // Update notification settings if provided
        if (hasNotificationSettings(request)) {
            UpdateNotificationSettingRequest notificationRequest = UpdateNotificationSettingRequest.builder()
                    .emailEnabled(request.getEmailEnabled())
                    .pushEnabled(request.getPushEnabled())
                    .mentionEnabled(request.getMentionEnabled())
                    .likeEnabled(request.getLikeEnabled())
                    .commentEnabled(request.getCommentEnabled())
                    .blogEnabled(request.getBlogEnabled())
                    .achievementEnabled(request.getAchievementEnabled())
                    .followEnabled(request.getFollowEnabled())
                    .systemEnabled(request.getSystemEnabled())
                    .build();
            notificationSettings = notificationService.updateSettings(notificationRequest);
        } else {
            notificationSettings = notificationService.getSettings();
        }
        
        // Update privacy settings if provided
        if (hasPrivacySettings(request)) {
            UpdatePrivacySettingRequest privacyRequest = UpdatePrivacySettingRequest.builder()
                    .profilePublic(request.getProfilePublic())
                    .emailVisible(request.getEmailVisible())
                    .phoneVisible(request.getPhoneVisible())
                    .addressVisible(request.getAddressVisible())
                    .dateOfBirthVisible(request.getDateOfBirthVisible())
                    .majorVisible(request.getMajorVisible())
                    .bioVisible(request.getBioVisible())
                    .socialMediaVisible(request.getSocialMediaVisible())
                    .achievementsVisible(request.getAchievementsVisible())
                    .statsVisible(request.getStatsVisible())
                    .build();
            privacySettings = privacySettingService.updatePrivacySettings(privacyRequest);
        } else {
            privacySettings = privacySettingService.getPrivacySettings();
        }
        
        return CombinedSettingsResponse.builder()
                .notificationSettings(notificationSettings)
                .privacySettings(privacySettings)
                .build();
    }
    
    private boolean hasNotificationSettings(UpdateSettingsRequest request) {
        return request.getEmailEnabled() != null ||
               request.getPushEnabled() != null ||
               request.getMentionEnabled() != null ||
               request.getLikeEnabled() != null ||
               request.getCommentEnabled() != null ||
               request.getBlogEnabled() != null ||
               request.getAchievementEnabled() != null ||
               request.getFollowEnabled() != null ||
               request.getSystemEnabled() != null;
    }
    
    private boolean hasPrivacySettings(UpdateSettingsRequest request) {
        return request.getProfilePublic() != null ||
               request.getEmailVisible() != null ||
               request.getPhoneVisible() != null ||
               request.getAddressVisible() != null ||
               request.getDateOfBirthVisible() != null ||
               request.getMajorVisible() != null ||
               request.getBioVisible() != null ||
               request.getSocialMediaVisible() != null ||
               request.getAchievementsVisible() != null ||
               request.getStatsVisible() != null;
    }
}

