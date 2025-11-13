package com.se.hub.modules.user.service.api;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.user.dto.request.ChangePasswordRequest;
import com.se.hub.modules.user.dto.request.UserCreationRequest;
import com.se.hub.modules.user.dto.request.UserRolesUpdateRequest;
import com.se.hub.modules.user.dto.response.UserResponse;


public interface UserService {
    /**
     * Create user
     * @author catsocute
     *
     */
    UserResponse create(UserCreationRequest request);

    /**
     * Change password
     * @author catsocute
     *
     */
    UserResponse changePassword(ChangePasswordRequest request);

    /**
     * Update roles for user
     * @author catsocute
     *
     */
    UserResponse updateRoles(String userId, UserRolesUpdateRequest request);

    /**
     * get all users
     * @author catsocute
     *
     */
    PagingResponse<UserResponse> getUsers(PagingRequest request);

    /**
     * get my info
     * @author catsocute
     *
     */
    UserResponse getMyInfo();

    /**
     * Delete user by id
     * @author catsocute
     *
     */
    void deleteById(String userId);
}
