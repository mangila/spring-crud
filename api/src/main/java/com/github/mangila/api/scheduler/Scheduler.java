package com.github.mangila.api.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.shared.ApplicationTaskExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.CompletableFuture;

/**
 * Run some tasks, running with the Spring integrated scheduler.
 * Since the scheduler is running during the JVM lifecycle of the application, it's a good idea to use a separate service with a dedicated Scheduler framework.
 * To prevent resource exhaustion and not to disrupt important scheduled tasks during an application upgrade.
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

    private final ApplicationTaskExecutor applicationTaskExecutor;
    private final ObjectMapper objectMapper;
    private final TaskMap taskMap;

    public Scheduler(ApplicationTaskExecutor applicationTaskExecutor,
                     ObjectMapper objectMapper,
                     TaskMap taskMap) {
        this.applicationTaskExecutor = applicationTaskExecutor;
        this.objectMapper = objectMapper;
        this.taskMap = taskMap;
    }

    /**
     * The Outbox message relay task. Since we use Spring in-memory event bus.
     * We use this task to relay from our Outbox for sad path scenarios.
     * Failed messages etc.
     * <br>
     * Run a cron task every minute
     */
    @Scheduled(cron = "0 * * * * *")
    public void outboxMessageRelayTask() {
        Task task = taskMap.getTaskOrThrow("outboxMessageRelayTask");
        CompletableFuture<ObjectNode> result = applicationTaskExecutor.submitCompletable(task, createInitNode(objectMapper));
        try {
            ObjectNode node = result.join();
            log.debug("Outbox message relay task result: {}", node);
        } catch (Exception e) {
            log.error("Error relaying messages", e);
        }
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
        log.info("Running softDeleteSuccessTaskExecutionTask");
        Task task = taskMap.getTaskOrThrow("softDeleteSuccessTaskExecutionTask");
        CompletableFuture<ObjectNode> result = applicationTaskExecutor.submitCompletable(task, createInitNode(objectMapper));
        result.whenComplete((objectNode, throwable) -> {
            if (throwable != null) {
                log.error("Soft delete success task failed: {}", throwable.getMessage());
            }
            log.debug("Soft delete success task result: {}", objectNode);
        });
    }

    /**
     * Fetch secure headers from OWASP.
     */
    @Scheduled(
            cron = "${application.scheduler.cron}"
    )
    public void fetchOwaspSecureHeadersAddTask() {
        Task task = taskMap.getTaskOrThrow("fetchOwaspSecureHeadersAddTask");
        CompletableFuture<ObjectNode> result = applicationTaskExecutor.submitCompletable(task, createInitNode(objectMapper));
        result.whenComplete((objectNode, throwable) -> {
            if (throwable != null) {
                log.error("Fetch OWASP secure headers to add task failed: {}", throwable.getMessage());
            }
            log.debug("Fetch OWASP secure headers to add task result: {}", objectNode);
        });
    }

    /**
     * Fetch secure headers from OWASP.
     */
    @Scheduled(
            cron = "${application.scheduler.cron}"
    )
    public void fetchOwaspSecureHeadersRemoveTask() {
        Task task = taskMap.getTaskOrThrow("fetchOwaspSecureHeadersRemoveTask");
        CompletableFuture<ObjectNode> result = applicationTaskExecutor.submitCompletable(task, createInitNode(objectMapper));
        result.whenComplete((objectNode, throwable) -> {
            if (throwable != null) {
                log.error("Fetch OWASP secure headers to remove task failed: {}", throwable.getMessage());
            }
            log.debug("Fetch OWASP secure headers to add remove result: {}", objectNode);
        });
    }

    private ObjectNode createInitNode(ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("executedBy", "Scheduler");
        return node;
    }
}
