package com.project2.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * AsyncConfig — Enables @Async so emails are sent in a background thread.
 *
 * This means the student's browser gets the "Registration Successful"
 * page immediately, and the email is sent in the background without
 * blocking the HTTP response.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Custom thread pool for async email tasks.
     * Core: 2 threads always alive.
     * Max: 5 threads under heavy load.
     * Queue: 100 pending tasks before rejecting.
     */
    @Bean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("EmailThread-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
