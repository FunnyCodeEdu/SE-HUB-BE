package com.se.hub.modules.user.controller;

import com.se.hub.common.constant.ApiConstant;
import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.dto.MessageDTO;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.modules.user.dto.request.UpdateUserRoleRequest;
import com.se.hub.modules.user.dto.request.UpdateUserStatusRequest;
import com.se.hub.modules.user.dto.response.UserInfoResponse;
import com.se.hub.modules.user.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserManagementController {
    
    private final UserManagementService userManagementService;
    
    /**
     * Update user role (Admin only)
     * 
     * @param userId User ID
     * @param request Update role request
     * @return Updated user info
     */
    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse<UserInfoResponse>> updateUserRole(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        
        UserInfoResponse data = userManagementService.updateUserRole(userId, request);
        
        GenericResponse<UserInfoResponse> genericResponse = GenericResponse.<UserInfoResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail("User role updated successfully")
                        .build())
                .data(data)
                .build();
        
        return ResponseEntity.ok(genericResponse);
    }
    
    /**
     * Update user status (Admin only)
     * 
     * @param userId User ID
     * @param request Update status request
     * @return Updated user info
     */
    @PutMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse<UserInfoResponse>> updateUserStatus(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        
        UserInfoResponse data = userManagementService.updateUserStatus(userId, request);
        
        GenericResponse<UserInfoResponse> genericResponse = GenericResponse.<UserInfoResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail("User status updated successfully")
                        .build())
                .data(data)
                .build();
        
        return ResponseEntity.ok(genericResponse);
    }
}

