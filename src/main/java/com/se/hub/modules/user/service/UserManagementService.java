package com.se.hub.modules.user.service;

import com.se.hub.modules.user.dto.request.UpdateUserRoleRequest;
import com.se.hub.modules.user.dto.request.UpdateUserStatusRequest;
import com.se.hub.modules.user.dto.response.UserInfoResponse;

public interface UserManagementService {
    
    /**
     * Update user role (Admin only)
     *
     * @param userId User ID
     * @param request Update role request
     * @return Updated user info
     */
    UserInfoResponse updateUserRole(String userId, UpdateUserRoleRequest request);
    
    /**
     * Update user status (Admin only)
     *
     * @param userId User ID
     * @param request Update status request
     * @return Updated user info
     */
    UserInfoResponse updateUserStatus(String userId, UpdateUserStatusRequest request);
}

