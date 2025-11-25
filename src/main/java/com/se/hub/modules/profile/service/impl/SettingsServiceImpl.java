package com.se.hub.modules.profile.service.impl;

import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.notification.dto.request.UpdateNotificationSettingRequest;
import com.se.hub.modules.notification.dto.response.NotificationSettingResponse;
import com.se.hub.modules.notification.service.api.NotificationService;
import com.se.hub.modules.profile.dto.request.UpdatePrivacySettingRequest;
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
    public NotificationSettingResponse updateNotificationSettings(UpdateNotificationSettingRequest request) {
        log.debug("SettingsService_updateNotificationSettings_Updating notification settings for user: {}", AuthUtils.getCurrentUserId());
        return notificationService.updateSettings(request);
    }

    @Override
    public PrivacySettingResponse updatePrivacySettings(UpdatePrivacySettingRequest request) {
        log.debug("SettingsService_updatePrivacySettings_Updating privacy settings for user: {}", AuthUtils.getCurrentUserId());
        return privacySettingService.updatePrivacySettings(request);
    }
}

