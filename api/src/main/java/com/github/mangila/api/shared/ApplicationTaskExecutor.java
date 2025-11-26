package com.github.mangila.api.shared;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.model.task.TaskExecutionEntity;
import com.github.mangila.api.model.task.TaskExecutionStatus;
import com.github.mangila.api.repository.TaskExecutionJpaRepository;
import com.github.mangila.api.scheduler.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

/**
 * Threads that are started by the programmer can be submitted from here.
 */
@Slf4j
public class ApplicationTaskExecutor {

    private final SimpleAsyncTaskExecutor taskExecutor;
    private final TaskExecutionJpaRepository taskExecutionRepository;

    public ApplicationTaskExecutor(SimpleAsyncTaskExecutor taskExecutor,
                                   TaskExecutionJpaRepository taskExecutionRepository) {
        this.taskExecutor = taskExecutor;
        this.taskExecutionRepository = taskExecutionRepository;
    }

    public CompletableFuture<ObjectNode> submitCompletable(Callable<ObjectNode> task, String name, ObjectNode attributes) {
        var taskExecution = taskExecutionRepository.persist(
                TaskExecutionEntity.from(name, attributes)
        );
        var future = taskExecutor.submitCompletable(task);
        setStatusWhenComplete(future, taskExecution);
        return future;
    }

    public CompletableFuture<ObjectNode> submitCompletable(Task task, ObjectNode attributes) {
        var taskExecution = taskExecutionRepository.persist(
                TaskExecutionEntity.from(task.name(), attributes)
        );
        var future = taskExecutor.submitCompletable(task);
        setStatusWhenComplete(future, taskExecution);
        return future;
    }

    private void setStatusWhenComplete(CompletableFuture<ObjectNode> future, TaskExecutionEntity taskExecution) {
        future.whenCompleteAsync((objectNode, throwable) -> {
            var attributes = taskExecution.getAttributes();
            if (throwable != null) {
                if (throwable instanceof CancellationException) {
                    taskExecution.setStatus(TaskExecutionStatus.CANCELLED);
                    attributes.put("error", "Cancelled");
                } else {
                    taskExecution.setStatus(TaskExecutionStatus.FAILURE);
                    attributes.put("error", throwable.getMessage());
                }
                taskExecution.setAttributes(attributes);
                taskExecutionRepository.merge(taskExecution);
            } else {
                taskExecution.setStatus(TaskExecutionStatus.SUCCESS);
                attributes.setAll(objectNode);
            }
            taskExecutionRepository.merge(taskExecution);
        }, taskExecutor);
    }
}
