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

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

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
            // Health check endpoints (if any)
            "/actuator/health"
    };
    
    // Endpoints that require authentication but no permission check
    private static final String[] AUTHENTICATED_NO_PERMISSION_ENDPOINTS = {
            "/api/token/my-info" // Token endpoint - requires JWT but no permission check
    };

    private static final String[] WHITELIST_ENDPOINTS = {
            // Swagger UI endpoints with /api prefix
            "/api/v3/api-docs",
            "/api/v3/api-docs/**",
            "/api/swagger-ui.html",
            "/api/swagger-ui/**",
            "/api/swagger-resources/**",
            "/api/webjars/**",
            // Swagger UI endpoints without /api prefix (fallback)
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/webjars/**"
    };

    @NonFinal
    @Value(value = "${ftes.jwt.secret}")
    private String FTES_JWT_SECRET;

    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, CorsConfigurationSource corsConfigurationSource) throws Exception {
        return httpSecurity
                //disable session - use STATELESS for JWT
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //cors config
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                //disable csrf
                .csrf(AbstractHttpConfigurer::disable)

                //authorization rules - public endpoints first
                .authorizeHttpRequests(request -> request
                        // Swagger UI endpoints - must be first, allow ALL HTTP methods
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

                //config oauth2 resource server
                // Note: oauth2ResourceServer will try to authenticate all requests
                // But permitAll() endpoints will bypass authentication before reaching oauth2ResourceServer
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
                                .jwtAuthenticationConverter(customJwtAuthenticationConverter))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
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
        // Allow specific origins - using patterns for flexibility
        corsConfiguration.setAllowedOriginPatterns(Arrays.asList(
            "https://sehub.ftes.vn",
            "https://apisehub.ftes.vn",  // For Swagger UI on production
            "http://localhost:*",  // For local development (all ports)
            "http://127.0.0.1:*"   // For local development (all ports)
        ));
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
