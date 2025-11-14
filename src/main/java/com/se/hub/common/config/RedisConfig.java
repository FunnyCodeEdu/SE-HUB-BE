package com.se.hub.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Redis Configuration
 * 
 * Centralized Redis configuration for all modules
 * Provides RedisTemplate, StringRedisTemplate, and RedisMessageListenerContainer
 * 
 * Best Practices:
 * - Centralized configuration for better maintainability
 * - Optimized connection pooling via application.properties
 * - Consistent serialization strategy (String for keys, JSON for values)
 * - Dedicated thread pool for Redis message listeners
 * - Transaction support enabled
 */
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.listener.task-executor.core-pool-size:5}")
    private int listenerCorePoolSize;

    @Value("${spring.data.redis.listener.task-executor.max-pool-size:10}")
    private int listenerMaxPoolSize;

    @Value("${spring.data.redis.listener.task-executor.queue-capacity:100}")
    private int listenerQueueCapacity;

    @Value("${spring.data.redis.listener.subscription-executor.core-pool-size:5}")
    private int subscriptionCorePoolSize;

    @Value("${spring.data.redis.listener.subscription-executor.max-pool-size:10}")
    private int subscriptionMaxPoolSize;

    /**
     * StringRedisTemplate for string-based operations
     * Used by Notification and Chat modules for simple key-value operations
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setEnableTransactionSupport(true);
        return template;
    }

    /**
     * RedisTemplate for object-based operations
     * Uses JSON serialization for complex objects
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setEnableTransactionSupport(true);
        
        // Key serialization: String
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Value serialization: JSON
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        // Default serializer for operations without explicit type
        template.setDefaultSerializer(jsonSerializer);
        
        template.afterPropertiesSet();
        return template;
    }

    /**
     * RedisMessageListenerContainer for Pub/Sub operations
     * Configured with dedicated thread pools for optimal performance
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        
        // Task executor for message processing
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(listenerCorePoolSize);
        taskExecutor.setMaxPoolSize(listenerMaxPoolSize);
        taskExecutor.setQueueCapacity(listenerQueueCapacity);
        taskExecutor.setThreadNamePrefix("redis-listener-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(30);
        taskExecutor.initialize();
        container.setTaskExecutor(taskExecutor);
        
        // Subscription executor for channel subscription management
        ThreadPoolTaskExecutor subscriptionExecutor = new ThreadPoolTaskExecutor();
        subscriptionExecutor.setCorePoolSize(subscriptionCorePoolSize);
        subscriptionExecutor.setMaxPoolSize(subscriptionMaxPoolSize);
        subscriptionExecutor.setQueueCapacity(listenerQueueCapacity);
        subscriptionExecutor.setThreadNamePrefix("redis-subscription-");
        subscriptionExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        subscriptionExecutor.setWaitForTasksToCompleteOnShutdown(true);
        subscriptionExecutor.setAwaitTerminationSeconds(30);
        subscriptionExecutor.initialize();
        container.setSubscriptionExecutor(subscriptionExecutor);
        
        // Max waiting time for subscription registration (milliseconds)
        container.setMaxSubscriptionRegistrationWaitingTime(5000);
        
        return container;
    }

    /**
     * ObjectMapper for JSON serialization/deserialization
     * Used by Notification module for converting objects to/from JSON
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

