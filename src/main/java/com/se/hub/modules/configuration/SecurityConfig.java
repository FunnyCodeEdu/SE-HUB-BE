package com.se.hub.modules.configuration;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false) // Disable @PreAuthorize permission checks for entire project
@RequiredArgsConstructor
public class SecurityConfig {

    private final String[] PUBLIC_POST_ENDPOINT = {
            // No public POST endpoints - all require authentication via FTES JWT
    };

    // Public GET endpoints (read-only, public information)
    private final String[] PUBLIC_GET_ENDPOINTS = {
            // Health check endpoints
            "/actuator/health",
            // Blog endpoints
            "/api/blogs",
            "/api/blogs/**",
            // Course endpoints
            "/api/courses",
            "/api/courses/**",
            // Exam endpoints
            "/api/exams",
            "/api/exams/**",
            // Document endpoints (only approved documents are public)
            "/api/documents",
            "/api/documents/{documentId}",
            "/api/documents/course/{courseId}",
            "/api/documents/latest",
            "/api/documents/{documentId}/suggestions",
            // Comment endpoints
            "/api/comments",
            "/api/comments/**",
            // Reaction endpoints
            "/api/reactions/**",
            // Ranking endpoints
            "/api/ranking",
            "/api/ranking/**",
            // User Level endpoints
            "/api/levels",
            "/api/levels/**",
            // Achievement endpoints (public read)
            "/api/achievements",
            "/api/achievements/*",
            // Question endpoints
            "/api/questions",
            "/api/questions/**"
    };
    
    // Endpoints that require authentication but no permission check
    private static final String[] AUTHENTICATED_NO_PERMISSION_ENDPOINTS = {
            "/api/token/my-info"
    };

    private static final String[] WHITELIST_ENDPOINTS = {
            // WebSocket/Socket.IO endpoints - allow all methods for WebSocket upgrade
            "/socket.io/**",
            "/api/v3/api-docs",
            "/api/v3/api-docs/**",
            "/api/swagger-ui.html",
            "/api/swagger-ui/**",
            "/api/swagger-resources/**",
            "/api/webjars/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/webjars/**",
            // Google Drive OAuth callback endpoints
            "/api/drive/callback",
            "/api/drive/auth-url"
    };

    @NonFinal
    @Value(value = "${ftes.jwt.secret}")
    private String FTES_JWT_SECRET;

    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @NonFinal
    @Value("${security.cors.allowed-origin-patterns}")
    private String corsAllowedOriginPatterns;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, CorsConfigurationSource corsConfigurationSource) throws Exception {
        return httpSecurity
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                //disable csrf
                .csrf(AbstractHttpConfigurer::disable)

                //authorization rules - public endpoints first
                .authorizeHttpRequests(request -> request
                        // Whitelist endpoints - MUST BE FIRST
                        .requestMatchers(WHITELIST_ENDPOINTS).permitAll()
                        // Public POST endpoints
                        .requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINT).permitAll()
                        // Public GET endpoints (other public endpoints)
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                        // Endpoints that require authentication but no permission check
                        .requestMatchers(AUTHENTICATED_NO_PERMISSION_ENDPOINTS).authenticated()
                        // Public profile endpoints - GET profile by ID or user ID (read-only, public info)
                        .requestMatchers(HttpMethod.GET, "/api/profile/user/*").permitAll() // GET /api/profile/user/{userId}
                        // Note: We need to exclude /api/profile/my-profile from public access
                        // So we check for exact match first, then allow pattern matching
                        .requestMatchers(HttpMethod.GET, "/api/profile/*").permitAll() // GET /api/profile/{profileId} - but /api/profile/my-profile will be checked by method security
                        // Allow CORS preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )

                //config oauth2 resource server with custom bearer token resolver
                // The resolver will return null for whitelisted endpoints, effectively skipping OAuth2 processing
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenResolver(request -> {
                            String requestPath = request.getRequestURI();
                            
                            // Check if this is a whitelisted endpoint
                            boolean isWhitelisted = Arrays.stream(WHITELIST_ENDPOINTS)
                                    .anyMatch(pattern -> {
                                        if (pattern.endsWith("/**")) {
                                            String basePattern = pattern.substring(0, pattern.length() - 3);
                                            return requestPath.startsWith(basePattern);
                                        }
                                        return requestPath.equals(pattern) || requestPath.startsWith(pattern + "/");
                                    });
                            
                            // If whitelisted, return null to skip OAuth2 processing
                            if (isWhitelisted) {
                                return null;
                            }
                            
                            // For non-whitelisted endpoints, extract bearer token from Authorization header
                            String authorization = request.getHeader("Authorization");
                            if (authorization != null && authorization.startsWith("Bearer ")) {
                                return authorization.substring(7);
                            }
                            return null;
                        })
                        .jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
                                .jwtAuthenticationConverter(customJwtAuthenticationConverter))
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                .build();
    }


    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(FTES_JWT_SECRET.getBytes(), "HS512");
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // Allow specific origins - using patterns configured via application properties
        List<String> allowedOriginPatterns = Arrays.stream(corsAllowedOriginPatterns.split(","))
                .map(String::trim)
                .filter(pattern -> !pattern.isEmpty())
                .collect(Collectors.toList());
        corsConfiguration.setAllowedOriginPatterns(allowedOriginPatterns);
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Total-Count"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L); // Cache preflight request for 1 hour
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
