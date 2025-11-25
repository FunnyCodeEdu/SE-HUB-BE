package com.se.hub.modules.profile.service.api;

import com.se.hub.modules.profile.dto.response.ProfileResponse;
import com.se.hub.modules.profile.entity.PrivacySetting;

public interface PrivacyHelperService {
    /**
     * Apply privacy settings to profile response
     * @param profileResponse profile response to filter
     * @param privacySetting privacy settings
     * @param isOwner true if the requester is the owner of the profile
     * @return filtered profile response
     */
    ProfileResponse applyPrivacySettings(ProfileResponse profileResponse, PrivacySetting privacySetting, boolean isOwner);
    
    /**
     * Check if profile is accessible based on privacy settings
     * @param privacySetting privacy settings
     * @param isOwner true if the requester is the owner of the profile
     * @return true if profile is accessible
     */
    boolean isProfileAccessible(PrivacySetting privacySetting, boolean isOwner);
}

