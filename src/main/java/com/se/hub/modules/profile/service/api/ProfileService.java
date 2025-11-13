package com.se.hub.modules.profile.service.api;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.profile.dto.request.CreateDefaultProfileRequest;
import com.se.hub.modules.profile.dto.request.UpdateProfileRequest;
import com.se.hub.modules.profile.dto.response.ProfileResponse;

public interface ProfileService {
    /**
     * Create default profile when register user
     * @author catsocute
     *
     */
    void createDefaultProfile(CreateDefaultProfileRequest request);

    /**
     * Update profile API
     * @author catsocute
     *
     */
    ProfileResponse updateProfile(UpdateProfileRequest request);

    /**
     * Get profile detail by userId
     * @author catsocute
     *
     */
    ProfileResponse getDetailProfileByUserId(String userId);

    /**
     * Get profile detail by profileId
     * @author catsocute
     *
     */
    ProfileResponse getProfileById(String profileId);
    
    /**
     * Get profile detail by profileId (alias for getProfileById)
     * @author catsocute
     *
     */
    default ProfileResponse getDetailProfileByProfileId(String profileId) {
        return getProfileById(profileId);
    }

    /**
     * Get all profiles with pagination
     * @author catsocute
     * @param pagingRequest PagingRequest
     * @since 9/16/2025
     */
    PagingResponse<ProfileResponse> getAllProfiles(PagingRequest pagingRequest);

    /**
     * Get current user's profile
     * @author catsocute
     * @since 9/16/2025
     */
    ProfileResponse getMyProfile();
}
