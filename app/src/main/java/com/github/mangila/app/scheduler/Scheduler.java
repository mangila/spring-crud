package com.github.mangila.app.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

/**
 * Run some tasks, running with the Spring integrated scheduler.
 * Since the scheduler is running during the JVM lifecycle of the application, it's a good idea to use a separate service with a dedicated Scheduler framework.
 * To prevent resource exhaustion.
 * <br>
 * For minor stuff like cache eviction or stuff that belongs inside the application infra use the Spring scheduler.
 * <br>
 * <a href="https://spring.io/blog/2020/11/10/new-in-spring-5-3-improved-cron-expressions">Spring Scheduler stuffs</a>
 */
@Slf4j
public class Scheduler {

    // Run a task every 5 seconds
    @Scheduled(fixedRateString = "${application.scheduler.fixed-rate}")
    public void fixedRateTask() {
        log.info("Fixed Rate Task started - {}", LocalDateTime.now());
    }

    // Run a task every 5 seconds, but only if the previous run has finished
    @Scheduled(fixedDelayString = "${application.scheduler.fixed-delay}")
    public void fixedDelayTask() {
        log.info("Fixed Delay Task started - {}", LocalDateTime.now());
    }

    // Run a task at a specific time
    @Scheduled(cron = "${application.scheduler.cron}")
    public void cronTask() {
        log.info("Cron Task started - {}", LocalDateTime.now());
    }

}
