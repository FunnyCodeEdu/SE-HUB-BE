package com.se.hub.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis Configuration
 * Configures RedisTemplate and StringRedisTemplate for direct Redis operations
 * Uses Lettuce as Redis client (default in Spring Boot 2.0+)
 * Connection pooling is configured via application.properties (spring.data.redis.lettuce.pool.*)
 * 
 * Virtual Thread Best Practice:
 * - Redis operations are blocking I/O
 * - Virtual threads automatically handle blocking operations efficiently
 * - Lettuce supports both blocking and reactive modes
 * - Connection pooling (commons-pool2) improves performance under high load
 */
@Configuration
public class RedisConfig {

    /**
     * StringRedisTemplate Bean
     * Used for string-based Redis operations (session management, simple key-value)
     * Uses LettuceConnectionFactory with connection pooling (configured in application.properties)
     * Virtual Thread Best Practice:
     * - Redis operations are blocking I/O
     * - Virtual threads automatically handle blocking operations efficiently
     * - Connection pool (max-active=20, max-idle=10, min-idle=2) handles concurrent requests
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.setEnableTransactionSupport(false); // Disable transaction for better performance
        return template;
    }

    /**
     * RedisTemplate Bean (generic)
     * Used for complex object serialization if needed
     * Uses LettuceConnectionFactory with connection pooling
     * Virtual Thread Best Practice:
     * - Redis operations are blocking I/O
     * - Virtual threads automatically handle blocking operations efficiently
     * - Connection pool manages connections efficiently across virtual threads
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.setEnableTransactionSupport(false); // Disable transaction for better performance
        template.afterPropertiesSet();
        return template;
    }
}

