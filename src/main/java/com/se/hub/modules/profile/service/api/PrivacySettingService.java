package com.se.hub.modules.profile.service.api;

import com.se.hub.modules.profile.dto.request.UpdatePrivacySettingRequest;
import com.se.hub.modules.profile.dto.response.PrivacySettingResponse;

public interface PrivacySettingService {
    /**
     * Get privacy settings for current user
     * @return privacy settings response
     */
    PrivacySettingResponse getPrivacySettings();

    /**
     * Update privacy settings for current user
     * @param request update privacy settings request
     * @return updated privacy settings response
     */
    PrivacySettingResponse updatePrivacySettings(UpdatePrivacySettingRequest request);
}

