package com.github.mangila.app.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.CompletableFuture;

/**
 * Run some tasks, running with the Spring integrated scheduler.
 * Since the scheduler is running during the JVM lifecycle of the application, it's a good idea to use a separate service with a dedicated Scheduler framework.
 * To prevent resource exhaustion.
 * <br>
 * For minor stuff that belongs inside the application infra use the Spring scheduler. Because of its lightweight nature
 * <br>
 * E.g., force Cache eviction, run a cleanup task, poll from a Queue or something "lightweight"
 * <br>
 * For more complex stuffs, use a dedicated scheduler framework like Quartz, JobRunr, Spring Batch, etc.
 * <br>
 * <a href="https://spring.io/blog/2020/11/10/new-in-spring-5-3-improved-cron-expressions">Spring Scheduler stuffs</a>
 */
@Slf4j
public record Scheduler(VirtualThreadTaskExecutor taskExecutor) {

    // Run a task every 5 seconds
    @Scheduled(fixedRateString = "${application.scheduler.fixed-rate}")
    public void fixedRateTask() {
        log.info("Fixed Rate Task started");
        // Run it as a CompletableFuture to make it cancellable, chain or combine it with other tasks, wait, timeout.
        CompletableFuture<Void> future = taskExecutor.submitCompletable(new FixedRateTask());
        // No need to block here, but can be useful if you want to run some cleanup, insert task execution in a database or something on the Scheduler thread
        future.join();
        log.info("Fixed Rate Task finished");
    }

    // Run a task every 5 seconds, but only if the previous run has finished
    @Scheduled(fixedDelayString = "${application.scheduler.fixed-delay}")
    public void fixedDelayTask() {
        log.info("Fixed Delay Task started");
        CompletableFuture<Void> future = taskExecutor.submitCompletable(new FixedDelayTask());
        // Here we want to block the calling thread until the previous task is finished.
        // If the programmer forgets to block here, the task will be executed every 5 seconds, even if the previous task is still running.
        // Then a fixed-delay task will lose its meaning.
        future.join();
        log.info("Fixed Delay Task finished");
    }

    // Run a task at a specific time
    // This is a Spring Cron Expression
    @Scheduled(cron = "${application.scheduler.cron}")
    public void cronTask() {
        log.info("Cron Task started");
        CompletableFuture<Void> future = taskExecutor.submitCompletable(new CronTask());
        future.join();
        log.info("Cron Task finished");
    }

}
