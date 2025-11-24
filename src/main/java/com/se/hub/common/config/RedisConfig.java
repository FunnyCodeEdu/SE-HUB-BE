package com.se.hub.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

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
@Slf4j
@Configuration
public class RedisConfig {

    private RedisMessageListenerContainer messageListenerContainer;

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

    /**
     * RedisMessageListenerContainer Bean
     * Used for Redis Pub/Sub message listening
     * Required by notification module for real-time notification delivery via SSE
     * 
     * IMPORTANT: This bean is singleton and shared across the application
     * - Only ONE instance should exist to avoid duplicate subscriptions
     * - Container is started automatically by Spring
     * - Connection factory is shared with other Redis beans
     * 
     * Virtual Thread Best Practice:
     * - Redis Pub/Sub operations are blocking I/O
     * - Virtual threads automatically handle blocking operations efficiently
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        log.info("RedisConfig_redisMessageListenerContainer_Creating RedisMessageListenerContainer");
        
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        
        // Configure container to handle reconnection automatically
        // Lettuce will automatically reconnect on connection loss
        container.setRecoveryInterval(2000L); // Retry every 2 seconds on failure
        
        // Start the container immediately
        container.afterPropertiesSet();
        container.start();
        
        log.info("RedisConfig_redisMessageListenerContainer_RedisMessageListenerContainer started successfully");
        
        this.messageListenerContainer = container;
        return container;
    }
    
    /**
     * Cleanup on shutdown
     */
    @PreDestroy
    public void cleanup() {
        if (messageListenerContainer != null && messageListenerContainer.isRunning()) {
            log.info("RedisConfig_cleanup_Stopping RedisMessageListenerContainer");
            messageListenerContainer.stop();
            log.info("RedisConfig_cleanup_RedisMessageListenerContainer stopped");
        }
    }
}

