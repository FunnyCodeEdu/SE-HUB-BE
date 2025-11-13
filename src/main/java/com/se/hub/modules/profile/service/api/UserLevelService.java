package com.se.hub.modules.profile.service.api;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.profile.dto.request.CreateUserLevelRequest;
import com.se.hub.modules.profile.dto.response.UserLevelResponse;
import com.se.hub.modules.profile.entity.UserLevel;

public interface UserLevelService {
    /**
     * Create user level
     *
     * @author catsocute
     * @since 9/16/2025
     */
    UserLevel createUserLevel(CreateUserLevelRequest request);

    /**
     * Get user level by points
     *
     * @author catsocute
     * @since 9/16/2025
     */
    UserLevel getUserLevelByPoints(int points);

    /**
     * Delete user level by id
     *
     * @author catsocute
     * @param id String
     * @since 9/16/2025
     */
    void deleteById(String id);

    /**
     * Get all user levels with pagination
     * @author catsocute
     * @param pagingRequest PagingRequest
     * @since 9/16/2025
     */
    PagingResponse<UserLevelResponse> getAllUserLevels(PagingRequest pagingRequest);

    /**
     * Get user level by ID
     * @author catsocute
     * @param id String
     * @since 9/16/2025
     */
    UserLevelResponse getUserLevelById(String id);

}
