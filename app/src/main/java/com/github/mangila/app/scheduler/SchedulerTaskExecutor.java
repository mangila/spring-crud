package com.github.mangila.app.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.model.task.TaskExecutionEntity;
import com.github.mangila.app.model.task.TaskExecutionStatus;
import com.github.mangila.app.repository.TaskExecutionJpaRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class SchedulerTaskExecutor {

    private final VirtualThreadTaskExecutor taskExecutor;
    private final TaskExecutionJpaRepository taskExecutionRepository;
    private final ObjectMapper objectMapper;

    public SchedulerTaskExecutor(@Qualifier("virtualThreadTaskExecutor") VirtualThreadTaskExecutor taskExecutor,
                                 TaskExecutionJpaRepository taskExecutionRepository,
                                 ObjectMapper objectMapper) {
        this.taskExecutor = taskExecutor;
        this.taskExecutionRepository = taskExecutionRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Submit a task to the scheduler executor and return a completable future.
     * <br>
     * DRY method with the database inserts since all tasks share the same expected behavior.
     * <br>
     * If a task needs more stuffs to create better insight, a new method can be created
     */
    public CompletableFuture<Void> submitCompletable(Task task, @Nullable ObjectNode attributes) {
        var taskExecution = taskExecutionRepository.persist(
                TaskExecutionEntity.newExecution(task.name(), attributes)
        );
        return taskExecutor.submitCompletable(task)
                .orTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .whenComplete((unused, throwable) -> {
                    if (throwable != null) {
                        taskExecution.setStatus(TaskExecutionStatus.FAILURE);
                        ObjectNode error = attributes == null ? objectMapper.createObjectNode() : attributes;
                        error.put("error", throwable.getMessage());
                        taskExecution.setAttributes(attributes);
                        taskExecutionRepository.merge(taskExecution);
                    } else {
                        taskExecution.setStatus(TaskExecutionStatus.SUCCESS);
                        taskExecutionRepository.merge(taskExecution);
                    }
                });
    }
}
