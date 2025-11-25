package com.se.hub.modules.profile.service.api;

import com.se.hub.modules.profile.dto.request.UpdateSettingsRequest;
import com.se.hub.modules.profile.dto.response.CombinedSettingsResponse;

public interface SettingsService {
    /**
     * Get combined settings (notification + privacy) for current user
     * @return combined settings response
     */
    CombinedSettingsResponse getCombinedSettings();

    /**
     * Update both notification and privacy settings for current user
     * @param request update settings request (contains both notification and privacy settings)
     * @return updated combined settings response
     */
    CombinedSettingsResponse updateSettings(UpdateSettingsRequest request);
}

