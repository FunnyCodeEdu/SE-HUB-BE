package com.se.hub.modules.configuration;

import com.se.hub.modules.user.entity.User;
import com.se.hub.modules.user.service.UserSyncService;
import lombok.NonNull;
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

    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public JwtAuthenticationToken convert(@NonNull Jwt jwt) {
        User user = userSyncService.getOrCreateUser(jwt);
        Collection<GrantedAuthority> authorities = getAuthoritiesFromDatabase(user);

        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<GrantedAuthority> getAuthoritiesFromDatabase(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (user.getRole() != null && user.getRole().getName() != null) {
            String roleName = user.getRole().getName();
            if (!roleName.startsWith(ROLE_PREFIX)) {
                roleName = ROLE_PREFIX + roleName;
            }
            authorities.add(new SimpleGrantedAuthority(roleName));
        } else {
            log.warn("User {} has no role assigned", user.getId());
        }
        return authorities;
    }
}

