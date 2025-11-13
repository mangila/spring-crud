package com.github.mangila.app.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;

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
public class Scheduler {

    private final SchedulerTaskExecutor schedulerTaskExecutor;
    private final ObjectMapper objectMapper;

    // Spring magic, wires a map of tasks with their bean names.
    private final Map<String, Task> taskMap;

    public Scheduler(SchedulerTaskExecutor schedulerTaskExecutor,
                     ObjectMapper objectMapper,
                     Map<String, Task> taskMap) {
        this.schedulerTaskExecutor = schedulerTaskExecutor;
        this.objectMapper = objectMapper;
        this.taskMap = taskMap;
    }

    /**
     * The Outbox message relay task. Since we use Spring in-memory event bus.
     * We use this task to relay from our Outbox for sad path scenarios.
     * Failed messages etc.
     * <br>
     * Run a task every n(timeunit), but only if the previous run has finished
     * Is the purpose of a fixed-delay scheduling.
     */
    @Scheduled(
            initialDelayString = "${application.outbox-relay.initial-delay}",
            fixedDelayString = "${application.outbox-relay.fixed-delay}"
    )
    public void outboxMessageRelayTask() {
        Task task = taskMap.get("outboxMessageRelayTask");
        var node = objectMapper.createObjectNode();
        node.put("executedBy", "Scheduler");
        schedulerTaskExecutor.submit(task, node);
    }

    /**
     * Soft delete all success task executions.
     * For the actual deletion, we can use a separate service with a dedicated Scheduler framework.
     * <br>
     * Run a task every n(timeunit) is the purpose of a fixed-rate scheduling.
     */
    @Scheduled(
            initialDelayString = "${application.scheduler.initial-delay}",
            fixedRateString = "${application.scheduler.fixed-rate}"
    )
    void softDeleteSuccessTaskExecutionTask() {
        Task task = taskMap.get("softDeleteSuccessTaskExecutionTask");
        var node = objectMapper.createObjectNode();
        node.put("executedBy", "Scheduler");
        schedulerTaskExecutor.submit(task, node);
    }

    /**
     * Fetch secure headers from OWASP.
     */
    @Scheduled(
            cron = "${application.scheduler.cron}"
    )
    public void fetchOwaspSecureHeadersAddTask() {
        Task task = taskMap.get("fetchOwaspSecureHeadersAddTask");
        var node = objectMapper.createObjectNode();
        node.put("executedBy", "Scheduler");
        schedulerTaskExecutor.submit(task, node);
    }

    /**
     * Fetch secure headers from OWASP.
     */
    @Scheduled(
            cron = "${application.scheduler.cron}"
    )
    public void fetchOwaspSecureHeadersRemoveTask() {
        Task task = taskMap.get("fetchOwaspSecureHeadersRemoveTask");
        var node = objectMapper.createObjectNode();
        node.put("executedBy", "Scheduler");
        schedulerTaskExecutor.submit(task, node);
    }
}
