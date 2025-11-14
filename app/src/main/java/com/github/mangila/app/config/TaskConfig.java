package com.github.mangila.app.config;

import com.github.mangila.app.repository.TaskExecutionJpaRepository;
import com.github.mangila.app.shared.ApplicationContextHolder;
import com.github.mangila.app.shared.ApplicationTaskExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

/**
 * Config for execution of tasks created by ApplicationTaskExecutor
 * that handles {@link com.github.mangila.app.model.task.TaskExecutionEntity}
 */
@Configuration
public class TaskConfig {

    @Bean
    SimpleAsyncTaskExecutor simpleAsyncTaskExecutor() {
        var executor = new SimpleAsyncTaskExecutor();
        executor.setThreadNamePrefix("task-");
        executor.setVirtualThreads(true);
        executor.setTaskDecorator(runnable -> {
            ApplicationContextHolder.setEntry("message", "I'm being Context Propagated");
            try {
                // Run the original task
                runnable.run();
            } finally {
                ApplicationContextHolder.clear();
            }
            return runnable;
        });
        return executor;
    }

    @Bean
    ApplicationTaskExecutor applicationTaskExecutor(
            @Qualifier("simpleAsyncTaskExecutor") SimpleAsyncTaskExecutor taskExecutor,
            TaskExecutionJpaRepository taskExecutionRepository
    ) {
        return new ApplicationTaskExecutor(
                taskExecutor,
                taskExecutionRepository
        );
    }

}
