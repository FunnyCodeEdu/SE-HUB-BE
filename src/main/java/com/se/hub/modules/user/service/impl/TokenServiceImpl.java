package com.se.hub.modules.user.service.impl;

import com.se.hub.modules.user.dto.response.UserInfoResponse;
import com.se.hub.modules.user.entity.User;
import com.se.hub.modules.user.service.TokenService;
import com.se.hub.modules.user.service.UserUtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    
    private final UserUtilService userUtilService;
    
    @Override
    public UserInfoResponse getCurrentUserInfo() {
        User user = userUtilService.getCurrentUser();
        
        // Only return id and role as requested
        return UserInfoResponse.builder()
                .id(user.getId())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .build();
    }
}

