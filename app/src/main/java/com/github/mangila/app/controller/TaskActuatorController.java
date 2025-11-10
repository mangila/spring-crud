package com.github.mangila.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.model.task.ExecutionStatus;
import com.github.mangila.app.model.task.TaskExecutionEntity;
import com.github.mangila.app.repository.TaskExecutionJpaRepository;
import com.github.mangila.app.scheduler.Task;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Actuator endpoint for executing tasks.
 * Since this is part of a management thingy (not part of the usual work flow), it's a good idea to use an actuator endpoint for it.
 * Or just create a new controller. Opinionated approach.
 */
@WebEndpoint(id = "task")
@Component
public class TaskActuatorController {
    private final VirtualThreadTaskExecutor taskExecutor;
    private final TaskExecutionJpaRepository taskExecutionRepository;
    private final Map<String, Task> taskMap;

    private final ObjectMapper objectMapper;

    public TaskActuatorController(@Qualifier("schedulerTaskExecutor") VirtualThreadTaskExecutor taskExecutor,
                                  TaskExecutionJpaRepository taskExecutionRepository,
                                  Map<String, Task> taskMap,
                                  ObjectMapper objectMapper) {
        this.taskExecutor = taskExecutor;
        this.taskExecutionRepository = taskExecutionRepository;
        this.taskMap = taskMap;
        this.objectMapper = objectMapper;
    }

    @ReadOperation
    public WebEndpointResponse<Map<String, List<String>>> findAllTasks() {
        return new WebEndpointResponse<>(
                Map.of("tasks", taskMap.keySet().stream().toList()),
                HttpStatus.OK.value()
        );
    }

    @WriteOperation
    public WebEndpointResponse<Map<String, Object>> execute(@Selector String taskName) {
        Task task = taskMap.get(taskName);
        if (task == null) {
            return new WebEndpointResponse<>(
                    Map.of("error", "Task not found"),
                    HttpStatus.NOT_FOUND.value()
            );
        }
        TaskExecutionEntity taskExecution = taskExecutionRepository.save(new TaskExecutionEntity(task.name(), ExecutionStatus.RUNNING, null));
        taskExecutor.submitCompletable(task)
                .orTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        taskExecution.setStatus(ExecutionStatus.FAILURE);
                        ObjectNode attributes = objectMapper.createObjectNode()
                                .put("error", throwable.getMessage());
                        taskExecution.setAttributes(attributes);
                        taskExecutionRepository.save(taskExecution);
                    } else {
                        taskExecution.setStatus(ExecutionStatus.SUCCESS);
                        taskExecutionRepository.save(taskExecution);
                    }
                });
        return new WebEndpointResponse<>(
                Map.of("task", taskName),
                HttpStatus.ACCEPTED.value()
        );
    }
}
