package com.github.mangila.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

/**
 * Configuration for when using {@link org.springframework.scheduling.annotation.Async} annotation.
 */
@EnableAsync
@Configuration
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        var executor = new SimpleAsyncTaskExecutor();
        executor.setThreadNamePrefix("async-");
        executor.setVirtualThreads(true);
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("Exception occurred in async method {} with params {}", method, params, ex);
        };
    }
}
