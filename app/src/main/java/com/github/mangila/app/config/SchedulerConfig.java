package com.github.mangila.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.scheduler.Scheduler;
import com.github.mangila.app.scheduler.SchedulerTaskExecutor;
import com.github.mangila.app.scheduler.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.task.SimpleAsyncTaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;
import java.util.Map;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulerConfig {

    @Bean
    VirtualThreadTaskExecutor virtualThreadTaskExecutor() {
        return new VirtualThreadTaskExecutor("task-");
    }

    @Bean
    SimpleAsyncTaskSchedulerCustomizer taskSchedulerCustomizer(Clock clock) {
        return scheduler -> {
            // Set our Clock to the scheduler and not use the System default
            scheduler.setClock(clock);
            scheduler.setErrorHandler(throwable -> {
                // Log the error and continue
                log.error("Error in scheduler", throwable);
            });
        };
    }

    @Bean
    @ConditionalOnProperty(
            name = "application.scheduler.enabled",
            havingValue = "true"
    )
    Scheduler scheduler(
            SchedulerTaskExecutor taskExecutor,
            ObjectMapper objectMapper,
            Map<String, Task> taskMap
    ) {
        log.info("Scheduler enabled");
        return new Scheduler(taskExecutor, objectMapper, taskMap);
    }
}
