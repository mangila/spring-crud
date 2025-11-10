package com.github.mangila.app.scheduler;

import com.github.mangila.app.model.task.ExecutionStatus;
import com.github.mangila.app.model.task.TaskExecutionEntity;
import com.github.mangila.app.repository.TaskExecutionJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
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
public class Scheduler {

    private final VirtualThreadTaskExecutor taskExecutor;
    private final TaskExecutionJpaRepository taskExecutionRepository;

    // Spring magic, wires a map of tasks with their bean names.
    private final Map<String, Task> tasks;

    public Scheduler(VirtualThreadTaskExecutor taskExecutor,
                     TaskExecutionJpaRepository taskExecutionRepository,
                     Map<String, Task> tasks) {
        this.taskExecutor = taskExecutor;
        this.taskExecutionRepository = taskExecutionRepository;
        this.tasks = tasks;
    }

    // Run a task every 5 seconds
    @Scheduled(fixedRateString = "${application.scheduler.fixed-rate}")
    public void fixedRateTask() {
        Task task = tasks.get("fixedRateTask");
        TaskExecutionEntity taskExecution = taskExecutionRepository.save(new TaskExecutionEntity(task.name(), ExecutionStatus.RUNNING, null));
        taskExecutor.submitCompletable(task)
                .orTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        taskExecution.setStatus(ExecutionStatus.FAILURE);
                        taskExecutionRepository.save(taskExecution);
                    } else {
                        taskExecution.setStatus(ExecutionStatus.SUCCESS);
                        taskExecutionRepository.save(taskExecution);
                    }
                });
    }

    // Run a task every 5 seconds, but only if the previous run has finished
    @Scheduled(fixedDelayString = "${application.scheduler.fixed-delay}")
    public void fixedDelayTask() {
        Task task = tasks.get("fixedDelayTask");
        TaskExecutionEntity taskExecution = taskExecutionRepository.save(new TaskExecutionEntity(task.name(), ExecutionStatus.RUNNING, null));
        CompletableFuture<Void> future = taskExecutor.submitCompletable(task)
                .orTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        taskExecution.setStatus(ExecutionStatus.FAILURE);
                        taskExecutionRepository.save(taskExecution);
                    } else {
                        taskExecution.setStatus(ExecutionStatus.SUCCESS);
                        taskExecutionRepository.save(taskExecution);
                    }
                });
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
        Task task = tasks.get("cronTask");
        TaskExecutionEntity taskExecution = taskExecutionRepository.save(new TaskExecutionEntity(task.name(), ExecutionStatus.RUNNING, null));
        taskExecutor.submitCompletable(task)
                .orTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        taskExecution.setStatus(ExecutionStatus.FAILURE);
                        taskExecutionRepository.save(taskExecution);
                    } else {
                        taskExecution.setStatus(ExecutionStatus.SUCCESS);
                        taskExecutionRepository.save(taskExecution);
                    }
                });
    }

}
