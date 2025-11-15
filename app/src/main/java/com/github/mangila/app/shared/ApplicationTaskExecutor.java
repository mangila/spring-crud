package com.github.mangila.app.shared;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.model.task.TaskExecutionEntity;
import com.github.mangila.app.model.task.TaskExecutionStatus;
import com.github.mangila.app.repository.TaskExecutionJpaRepository;
import com.github.mangila.app.scheduler.Task;
import org.jspecify.annotations.NonNull;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.Assert;

import java.util.concurrent.CompletableFuture;

public class ApplicationTaskExecutor {

    private final SimpleAsyncTaskExecutor taskExecutor;
    private final TaskExecutionJpaRepository taskExecutionRepository;

    public ApplicationTaskExecutor(SimpleAsyncTaskExecutor taskExecutor,
                                   TaskExecutionJpaRepository taskExecutionRepository) {
        this.taskExecutor = taskExecutor;
        this.taskExecutionRepository = taskExecutionRepository;
    }

    /**
     * Submit a task to the scheduler executor and return a completable future.
     * <br>
     * Using Jackson ObjectNode to create insight about the task execution.
     */
    public CompletableFuture<ObjectNode> submit(Task task, @NonNull ObjectNode attributes) {
        var taskExecution = taskExecutionRepository.persist(
                TaskExecutionEntity.from(task.name(), attributes)
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
