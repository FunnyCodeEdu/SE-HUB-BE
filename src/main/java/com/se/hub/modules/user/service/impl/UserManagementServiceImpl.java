package com.se.hub.modules.user.service.impl;

import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.user.dto.request.UpdateUserRoleRequest;
import com.se.hub.modules.user.dto.request.UpdateUserStatusRequest;
import com.se.hub.modules.user.dto.response.UserInfoResponse;
import com.se.hub.modules.user.entity.Role;
import com.se.hub.modules.user.entity.User;
import com.se.hub.modules.user.enums.UserStatus;
import com.se.hub.modules.user.repository.RoleRepository;
import com.se.hub.modules.user.repository.UserRepository;
import com.se.hub.modules.user.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    @Override
    @Transactional
    public UserInfoResponse updateUserRole(String userId, UpdateUserRoleRequest request) {
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        // Find role
        Role role = roleRepository.findById(request.getRoleName())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        
        // Update role
        user.setRole(role);
        User updatedUser = userRepository.save(user);
        
        // Build response (only id and role)
        return UserInfoResponse.builder()
                .id(updatedUser.getId())
                .role(updatedUser.getRole() != null ? updatedUser.getRole().getName() : null)
                .build();
    }
    
    @Override
    @Transactional
    public UserInfoResponse updateUserStatus(String userId, UpdateUserStatusRequest request) {
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        // Validate and parse status
        UserStatus newStatus;
        try {
            newStatus = UserStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.DATA_INVALID);
        }
        
        // Update status
        user.setStatus(newStatus);
        User updatedUser = userRepository.save(user);
        
        // Build response (only id and role)
        return UserInfoResponse.builder()
                .id(updatedUser.getId())
                .role(updatedUser.getRole() != null ? updatedUser.getRole().getName() : null)
                .build();
    }
}

