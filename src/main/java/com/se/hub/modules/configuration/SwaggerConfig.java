package com.se.hub.modules.configuration;

import org.springframework.context.annotation.Configuration;

/**
 * Swagger configuration is handled by SpringDoc OpenAPI automatically.
 * No custom configuration needed - SpringDoc will serve Swagger UI at /api/swagger-ui/index.html
 * (with context path /api)
 */
@Configuration
public class SwaggerConfig {
    // SpringDoc handles Swagger UI configuration automatically
    // Swagger UI will be available at: http://localhost:8080/api/swagger-ui/index.html
}

