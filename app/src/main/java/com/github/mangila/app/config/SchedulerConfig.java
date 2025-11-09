package com.github.mangila.app.config;

import com.github.mangila.app.scheduler.Scheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulerConfig {

    @Bean
    VirtualThreadTaskExecutor schedulerTaskExecutor() {
        return new VirtualThreadTaskExecutor("task-");
    }

    @Bean
    @ConditionalOnProperty(
            name = "application.scheduler.enabled",
            havingValue = "true"
    )
    Scheduler scheduler(@Qualifier("schedulerTaskExecutor") VirtualThreadTaskExecutor taskExecutor) {
        log.info("Scheduler enabled");
        return new Scheduler(taskExecutor);
    }
}
