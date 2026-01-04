package com.se.hub.modules.configuration;

import com.se.hub.common.constant.ApiConstant;
import com.se.hub.common.dto.MessageDTO;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.enums.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Custom Authentication Entry Point
 * Uses whitelist from SecurityConfig to bypass authentication for public endpoints
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    // Whitelist patterns - must match SecurityConfig.WHITELIST_ENDPOINTS
    // This is used to check if endpoint should bypass authentication
    private static final List<String> WHITELIST_PATTERNS = Arrays.asList(
            "/api/v3/api-docs",
            "/api/swagger-ui",
            "/api/swagger-resources",
            "/api/webjars",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-resources",
            "/webjars",
            "/api/drive/callback",
            "/api/drive/auth-url"
    );

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        String requestPath = request.getRequestURI();
        
        // Remove query string for matching
        String pathWithoutQuery = requestPath.contains("?") 
                ? requestPath.substring(0, requestPath.indexOf("?")) 
                : requestPath;
        
        // Check if request is to a whitelist endpoint
        boolean isWhitelisted = WHITELIST_PATTERNS.stream()
                .anyMatch(pattern ->
                        pathWithoutQuery.equals(pattern)
                                || pathWithoutQuery.startsWith(pattern + "/")
                                || pathWithoutQuery.startsWith(pattern + "?")
                );


        // If whitelisted, don't send error response
        // Set anonymous authentication and let the filter chain continue
        if (isWhitelisted) {
            // Set anonymous authentication to allow request to proceed
            org.springframework.security.authentication.AnonymousAuthenticationToken anonymousToken = 
                    new org.springframework.security.authentication.AnonymousAuthenticationToken(
                            "whitelist-bypass-key",
                            "whitelist-user",
                            org.springframework.security.core.authority.AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
                    );
            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(anonymousToken);
            // Don't commit response - let the filter chain continue to the controller
            return;
        }
        
        // For non-whitelisted endpoints, send authentication error
        ErrorCode errorCode = ErrorCode.AUTHZ_UNAUTHORIZED;
        GenericResponse<?> genericResponse = GenericResponse.builder()
                .isSuccess(ApiConstant.FAILURE)
                .message(MessageDTO.builder()
                        .messageCode(errorCode.getCode())
                        .messageDetail(errorCode.getMessage())
                        .build())
                .build();
        response.setStatus(errorCode.getHttpStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(genericResponse));
        response.flushBuffer();
    }
}
