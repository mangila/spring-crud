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
    private final Map<String, RunnableTask> runnableTaskMap;
    private final Map<String, CallableTask> callableTaskMap;

    public Scheduler(SchedulerTaskExecutor schedulerTaskExecutor,
                     ObjectMapper objectMapper,
                     Map<String, RunnableTask> runnableTaskMap,
                     Map<String, CallableTask> callableTaskMap) {
        this.schedulerTaskExecutor = schedulerTaskExecutor;
        this.objectMapper = objectMapper;
        this.runnableTaskMap = runnableTaskMap;
        this.callableTaskMap = callableTaskMap;
    }

    /**
     * Soft delete all published outbox events save the sometime for auditing.
     * For the actual deletion, we can use a separate service with a dedicated Scheduler framework.
     * <br>
     * Run a task every n(timeunit) is the purpose of a fixed-rate scheduling.
     */
    @Scheduled(
            initialDelayString = "${application.scheduler.initial-delay}",
            fixedRateString = "${application.scheduler.fixed-rate}"
    )
    void softDeletePublishedOutboxTask() {
        CallableTask task = callableTaskMap.get("softDeletePublishedOutboxTask");
        var node = objectMapper.createObjectNode();
        node.put("executedBy", "Scheduler");
        schedulerTaskExecutor.submitCallable(task, objectMapper.createObjectNode());
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
        CallableTask task = callableTaskMap.get("softDeleteSuccessTaskExecutionTask");
        var node = objectMapper.createObjectNode();
        node.put("executedBy", "Scheduler");
        schedulerTaskExecutor.submitCallable(task, objectMapper.createObjectNode());
    }

    // Run a task at a specific time
    // This is a Spring Cron Expression
    @Scheduled(cron = "${application.scheduler.cron}")
    public void cronTask() {
        RunnableTask task = runnableTaskMap.get("cronTask");
        var node = objectMapper.createObjectNode();
        node.put("executedBy", "Scheduler");
        schedulerTaskExecutor.submitRunnable(task, objectMapper.createObjectNode());
    }
}
