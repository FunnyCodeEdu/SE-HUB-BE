package com.se.hub.modules.auth.utils;

import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.auth.constant.JwtClaimSetConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Slf4j
public class AuthUtils {
    private  AuthUtils(){}
    /**
     * Get current user ID or null if not authenticated (for public endpoints)
     */
    public static String getCurrentUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() 
                || JwtClaimSetConstant.ANONIMOUS_USER.equals(authentication.getPrincipal())) {
            return null;
        }
        
        // First try to get from authentication name
        String userId = authentication.getName();
        if (userId != null && !userId.isEmpty() && !JwtClaimSetConstant.ANONIMOUS_USER.equals(userId)) {
            return userId;
        }
        
        // Fallback: try to get from JWT claims
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            
            Object claim = getClaim(jwt);
            
            if (claim != null) {
                return claim.toString();
            }
        }
        
        return null;
    }

    public static String getCurrentUserId()  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.info("No authenticated user found in SecurityContext");
            throw new AppException(ErrorCode.JWT_CLAIM_MISSING);
        }
        
        // First try to get from authentication name (username is set to userId in CustomJwtAuthenticationConverter)
        String userId = authentication.getName();
        if (userId != null && !userId.isEmpty()) {
            log.info("getCurrentUserId()_userId = {}", userId);
            return userId;
        }
        
        // Fallback: try to get from JWT claims
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            
            // Try different claim names
            Object claim = getClaim(jwt);
            
            if (claim != null) {
                String userIdFromClaim = claim.toString();
                log.debug("Got userId from JWT claim: {}", userIdFromClaim);
                return userIdFromClaim;
            }
        }
        
        log.warn("Cannot get current user id from authentication or JWT claims");
        throw new AppException(ErrorCode.JWT_CLAIM_MISSING);
    }

    private static Object getClaim(Jwt jwt) {
        Object claim = jwt.getClaims().get(JwtClaimSetConstant.CLAIM_USER_ID);
        if (claim == null) {
            claim = jwt.getClaims().get(JwtClaimSetConstant.CLAIM_SUB); // Subject claim
        }
        if (claim == null) {
            claim = jwt.getClaims().get(JwtClaimSetConstant.CLAIM_ID);
        }
        return claim;
    }

    public static String getCurrentUserName()  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        } else {
            log.info("Cannot get current user name from context");
            throw new AppException(ErrorCode.JWT_CLAIM_MISSING);
        }
    }
}
