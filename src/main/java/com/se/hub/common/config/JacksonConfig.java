package com.se.hub.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson Configuration
 * Configures Jackson ObjectMapper to properly handle Java 8 time types (Instant, LocalDateTime, etc.)
 * This ensures that Instant fields are serialized correctly in JSON responses
 */
@Configuration
public class JacksonConfig {

    /**
     * Primary ObjectMapper Bean
     * Used by Spring MVC for HTTP message conversion
     * Registers JavaTimeModule to support java.time.* types (Instant, LocalDateTime, etc.)
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }
}

