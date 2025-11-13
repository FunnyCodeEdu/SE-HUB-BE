package com.se.hub.modules.profile.service;

import com.se.hub.modules.profile.dto.response.FtesProfileResponse;
import com.se.hub.modules.profile.dto.response.FtesUserInfoResponse;

/**
 * Service to call FTES (FunnyCodeEdu) API to get profile information
 */
public interface FtesProfileService {
    /**
     * Get profile from FTES system by userId
     * @param userId User ID
     * @param authToken Authorization token (Bearer token)
     * @return Profile data from FTES
     */
    FtesProfileResponse getProfileFromFtes(String userId, String authToken);
    
    /**
     * Get user info from FTES system
     * @param authToken Authorization token (Bearer token)
     * @return User info data from FTES
     */
    FtesUserInfoResponse getUserInfoFromFtes(String authToken);
}

