package com.se.hub.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache Configuration
 * 
 * Configures Redis as cache provider for Spring Cache abstraction
 * Uses JSON serialization for cache values
 * 
 * Best Practices:
 * - Use Redis for distributed caching in microservices
 * - Configure appropriate TTL (Time To Live) for each cache
 * - Use JSON serialization for better compatibility
 * - Cache-specific TTL configurations for different data types
 * - Transaction support enabled for cache consistency
 */
@Configuration
@EnableCaching
public class CacheConfig {

    // Default TTL: 1 hour - suitable for most cache entries
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);
    
    // Cache-specific TTL configurations
    // Individual blog entries: 2 hours (less frequently updated)
    private static final Duration BLOG_TTL = Duration.ofHours(2);
    
    // Blog lists (blogs, popular, latest): 30 minutes (more frequently updated)
    private static final Duration BLOG_LIST_TTL = Duration.ofMinutes(30);

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Configure ObjectMapper with JSR310Module for Java 8 time support
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );
        
        // Create GenericJackson2JsonRedisSerializer with configured ObjectMapper
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(DEFAULT_TTL)
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jsonSerializer))
                .disableCachingNullValues()
                .prefixCacheNameWith("cache:"); // Prefix to avoid key conflicts

        // Cache-specific configurations
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Blog cache: longer TTL for individual blog entries
        cacheConfigurations.put("blog", defaultConfig.entryTtl(BLOG_TTL));
        
        // Blog list caches: shorter TTL for frequently updated lists
        cacheConfigurations.put("blogs", defaultConfig.entryTtl(BLOG_LIST_TTL));
        cacheConfigurations.put("blogsByAuthor", defaultConfig.entryTtl(BLOG_LIST_TTL));
        cacheConfigurations.put("popularBlogs", defaultConfig.entryTtl(BLOG_LIST_TTL));
        cacheConfigurations.put("likedBlogs", defaultConfig.entryTtl(BLOG_LIST_TTL));
        cacheConfigurations.put("latestBlogs", defaultConfig.entryTtl(BLOG_LIST_TTL));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
