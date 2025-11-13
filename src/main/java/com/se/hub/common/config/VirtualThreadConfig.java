package com.se.hub.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executors;

/**
 * Virtual Thread Configuration
 * 
 * This configuration enables virtual threads for:
 * - HTTP requests (via WebMvcConfigurer)
 * - @Async methods (via AsyncConfigurer)
 * - @Scheduled tasks (via SchedulingConfigurer)
 * 
 * Virtual threads are lightweight threads that are managed by the Java Virtual Machine.
 * They are ideal for I/O-bound operations and can handle millions of concurrent tasks
 * with minimal overhead compared to platform threads.
 * 
 * Best Practices:
 * - Use synchronous blocking I/O in service methods (don't use CompletableFuture or reactive APIs)
 * - Virtual threads automatically handle blocking operations efficiently
 * - Avoid pinning virtual threads (e.g., synchronized blocks, native methods)
 * - Use ReentrantLock instead of synchronized for better virtual thread compatibility
 */
@Configuration
@EnableAsync
@EnableScheduling
public class VirtualThreadConfig implements WebMvcConfigurer, AsyncConfigurer, SchedulingConfigurer {

    /**
     * Configure async support for Spring MVC to use virtual threads
     * This enables virtual threads for async request processing
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor()));
    }

    /**
     * Configure async task executor for @Async methods to use virtual threads
     * This enables virtual threads for methods annotated with @Async
     */
    @Override
    public AsyncTaskExecutor getAsyncExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * Configure scheduled task executor to use virtual threads
     * This enables virtual threads for @Scheduled methods
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * Bean for application task executor using virtual threads
     * Can be used for custom async operations
     */
    @Bean(name = "applicationTaskExecutor")
    public AsyncTaskExecutor applicationTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
}

