package com.se.hub.modules.configuration;

import org.springframework.context.annotation.Configuration;

/**
 * CORS configuration is now handled in SecurityConfig via CorsConfigurationSource.
 * This class is kept for reference but CorsFilter is removed to avoid conflicts
 * with Spring Security's CORS handling.
 */
@Configuration
public class CorsConfig {
    // CORS configuration moved to SecurityConfig.corsConfigurationSource()
    // to avoid conflicts between CorsFilter and Spring Security's CORS handling
}
