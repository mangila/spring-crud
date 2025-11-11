package com.github.mangila.app.scheduler;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.model.task.TaskExecutionEntity;
import com.github.mangila.app.model.task.TaskExecutionStatus;
import com.github.mangila.app.repository.TaskExecutionJpaRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class SchedulerTaskExecutor {

    private final VirtualThreadTaskExecutor taskExecutor;
    private final TaskExecutionJpaRepository taskExecutionRepository;

    public SchedulerTaskExecutor(@Qualifier("virtualThreadTaskExecutor") VirtualThreadTaskExecutor taskExecutor,
                                 TaskExecutionJpaRepository taskExecutionRepository) {
        this.taskExecutor = taskExecutor;
        this.taskExecutionRepository = taskExecutionRepository;
    }

    /**
     * Submit a task to the scheduler executor and return a completable future.
     * <br>
     * DRY method with the database inserts since all tasks share the same expected behavior.
     * <br>
     * If a task needs more stuffs to create better insight, a new method can be created
     */
    public CompletableFuture<Void> submitRunnable(RunnableTask task, @NonNull ObjectNode attributes) {
        var taskExecution = taskExecutionRepository.persist(
                TaskExecutionEntity.newExecution(task.name(), attributes)
        );
        return taskExecutor.submitCompletable(task)
                .orTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .whenComplete((unused, throwable) -> {
                    if (throwable != null) {
                        taskExecution.setStatus(TaskExecutionStatus.FAILURE);
                        attributes.put("error", throwable.getMessage());
                    } else {
                        taskExecution.setStatus(TaskExecutionStatus.SUCCESS);
                    }
                    taskExecution.setAttributes(attributes);
                    taskExecutionRepository.merge(taskExecution);
                });
    }

    public CompletableFuture<ObjectNode> submitCallable(CallableTask task, @NonNull ObjectNode attributes) {
        var taskExecution = taskExecutionRepository.persist(
                TaskExecutionEntity.newExecution(task.name(), attributes)
        );
        return taskExecutor.submitCompletable(task)
                .orTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .whenComplete((objectNode, throwable) -> {
                    if (throwable != null) {
                        taskExecution.setStatus(TaskExecutionStatus.FAILURE);
                        attributes.put("error", throwable.getMessage());
                    } else {
                        taskExecution.setStatus(TaskExecutionStatus.SUCCESS);
                        attributes.setAll(objectNode);
                    }
                    taskExecution.setAttributes(attributes);
                    taskExecutionRepository.merge(taskExecution);
                });
    }
}
