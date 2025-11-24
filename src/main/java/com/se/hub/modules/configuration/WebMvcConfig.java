package com.se.hub.modules.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Add "/api" prefix to all request mappings in com.se.hub packages
     * This replaces the need for server.servlet.context-path
     * Excludes SpringDoc endpoints and other third-party controllers
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api", c -> {
            // Only add prefix to controllers in com.se.hub packages
            String packageName = c.getPackageName();
            boolean isSeHubController = packageName != null && packageName.startsWith("com.se.hub");
            
            // Check if it's a RestController or Controller annotation
            boolean isController = c.isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class)
                    || c.isAnnotationPresent(org.springframework.stereotype.Controller.class);
            
            return isSeHubController && isController;
        });
    }

    /**
     * ForwardedHeaderFilter Bean
     * Processes X-Forwarded-* headers from reverse proxy (Nginx/Cloudflare)
     * Ensures Spring Boot correctly identifies:
     * - Original protocol (https vs http)
     * - Original host
     * - Original IP address
     * 
     * This is critical for:
     * - Correct URL generation in responses
     * - Security context (knowing if request was HTTPS)
     * - CORS validation
     * - SSE connections through reverse proxy
     */
    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        ForwardedHeaderFilter filter = new ForwardedHeaderFilter();
        // Remove only the forwarded headers, keep the original ones
        filter.setRemoveOnly(false);
        return filter;
    }
}

