package com.github.mangila.app.config;

import com.github.mangila.app.scheduler.Scheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulerConfig {

    @Bean
    @ConditionalOnProperty(
            name = "application.scheduler.enabled",
            havingValue = "true"
    )
    Scheduler scheduler() {
        log.info("Scheduler enabled");
        return new Scheduler();
    }
}
