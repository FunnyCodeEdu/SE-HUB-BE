package com.se.hub.modules.user.service;

import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.user.entity.User;
import com.se.hub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserUtilService {
    private final UserRepository userRepository;

    /**
     * Get current user ID from security context (JWT)
     * @return User ID or null
     */
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            // Try to get userId from various possible claim names
            Object userId = jwt.getClaims().get("userId");
            if (userId == null) {
                userId = jwt.getClaims().get("sub"); // Subject claim
            }
            if (userId == null) {
                userId = jwt.getClaims().get("id");
            }
            return userId != null ? userId.toString() : null;
        }
        return null;
    }

    /**
     * Get current user from security context
     * User will be automatically created if not exists (handled by JWT converter)
     * @return Current user
     */
    public User getCurrentUser() {
        String userId = getCurrentUserId();
        if (userId == null) {
            throw new AppException(ErrorCode.AUTH_MISSING_TOKEN);
        }
        
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        
        return user.get();
    }

    /**
     * Get current user ID
     * @return Current user ID
     */
    public String getIdCurrentUser() {
        return getCurrentUserId();
    }
}

