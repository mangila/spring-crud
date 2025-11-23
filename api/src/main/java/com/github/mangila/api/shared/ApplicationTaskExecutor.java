package com.github.mangila.api.shared;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.model.task.TaskExecutionEntity;
import com.github.mangila.api.model.task.TaskExecutionStatus;
import com.github.mangila.api.repository.TaskExecutionJpaRepository;
import com.github.mangila.api.scheduler.Task;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

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
    public CompletableFuture<ObjectNode> submit(Task task, ObjectNode attributes) {
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
