package com.github.mangila.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.shared.ApplicationTaskExecutor;
import com.github.mangila.api.scheduler.Task;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
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

    private final ApplicationTaskExecutor taskExecutor;
    private final ObjectMapper objectMapper;
    private final Map<String, Task> taskMap;

    public TaskActuatorController(ApplicationTaskExecutor taskExecutor,
                                  ObjectMapper objectMapper,
                                  Map<String, Task> taskMap) {
        this.taskExecutor = taskExecutor;
        this.objectMapper = objectMapper;
        this.taskMap = taskMap;
    }

    @ReadOperation
    public WebEndpointResponse<Map<String, List<String>>> findAllTasks() {
        var sortedTasks = taskMap.keySet()
                .stream()
                .sorted()
                .toList();
        return new WebEndpointResponse<>(
                Map.of("tasks", sortedTasks),
                HttpStatus.OK.value()
        );
    }

    @WriteOperation
    public WebEndpointResponse<Map<String, Object>> execute(@Selector String taskName) {
        Task task = taskMap.get(taskName);
        if (task == null) {
            return new WebEndpointResponse<>(
                    Map.of("error", "Task not found: %s".formatted(taskName)),
                    HttpStatus.NOT_FOUND.value()
            );
        }
        var node = objectMapper.createObjectNode();
        node.put("executedBy", "Actuator");
        taskExecutor.submit(task, node);
        return new WebEndpointResponse<>(
                Map.of("task", taskName),
                HttpStatus.ACCEPTED.value()
        );
    }

}
