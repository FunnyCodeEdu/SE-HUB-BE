package com.se.hub.modules.configuration;

import com.se.hub.modules.user.entity.User;
import com.se.hub.modules.user.service.UserSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom JWT Authentication Converter that:
 * 1. Automatically creates user if not exists in database
 * 2. Uses roles from database instead of JWT
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationConverter implements Converter<Jwt, JwtAuthenticationToken> {

    private final UserSyncService userSyncService;

    @Override
    public JwtAuthenticationToken convert(Jwt jwt) {
        // Get or create user from JWT (this loads user with role)
        User user = userSyncService.getOrCreateUser(jwt);
        
        // Get authorities from database (role only)
        Collection<GrantedAuthority> authorities = getAuthoritiesFromDatabase(user);
        
        // Ensure at least one authority exists (default to ROLE_USER if none)
        if (authorities.isEmpty()) {
            log.warn("User {} has no authorities, assigning default ROLE_USER", user.getId());
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        log.info("User {} authenticated with authorities: {}", user.getId(), 
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(", ")));
        
        // Create JwtAuthenticationToken with userId as name (for getCurrentUserId to work)
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities);
        // Note: The name will be set from JWT's "sub" claim by default
        // We need to ensure userId is accessible via getName() for AuthUtils compatibility
        return token;
    }

    /**
     * Get authorities from database user (role only)
     */
    private Collection<GrantedAuthority> getAuthoritiesFromDatabase(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // Add role as authority (format: ROLE_XXX)
        if (user.getRole() != null && user.getRole().getName() != null) {
            String roleName = user.getRole().getName();
            // Ensure role name starts with ROLE_ prefix for Spring Security
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
            }
            authorities.add(new SimpleGrantedAuthority(roleName));
            log.debug("Added role authority: {} for user {}", roleName, user.getId());
        } else {
            log.warn("User {} has no role assigned", user.getId());
        }
        
        return authorities;
    }
}

