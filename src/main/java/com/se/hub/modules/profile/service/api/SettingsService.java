package com.se.hub.modules.profile.service.api;

import com.se.hub.modules.notification.dto.request.UpdateNotificationSettingRequest;
import com.se.hub.modules.notification.dto.response.NotificationSettingResponse;
import com.se.hub.modules.profile.dto.request.UpdatePrivacySettingRequest;
import com.se.hub.modules.profile.dto.response.CombinedSettingsResponse;
import com.se.hub.modules.profile.dto.response.PrivacySettingResponse;

public interface SettingsService {
    /**
     * Get combined settings (notification + privacy) for current user
     * @return combined settings response
     */
    CombinedSettingsResponse getCombinedSettings();

    /**
     * Update notification settings for current user
     * @param request update notification settings request
     * @return updated notification settings response
     */
    NotificationSettingResponse updateNotificationSettings(UpdateNotificationSettingRequest request);

    /**
     * Update privacy settings for current user
     * @param request update privacy settings request
     * @return updated privacy settings response
     */
    PrivacySettingResponse updatePrivacySettings(UpdatePrivacySettingRequest request);
}

