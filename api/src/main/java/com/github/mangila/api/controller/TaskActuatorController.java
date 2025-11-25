package com.github.mangila.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.scheduler.Task;
import com.github.mangila.api.scheduler.TaskMap;
import com.github.mangila.api.shared.ApplicationTaskExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Actuator endpoint for executing tasks.
 * Since this is part of a management thingy (not part of the usual work flow), it's a good idea to use an actuator endpoint for it.
 * Or just create a new controller. Opinionated approach.
 */
@WebEndpoint(id = "task")
@Component
@Slf4j
public class TaskActuatorController {

    private final ApplicationTaskExecutor taskExecutor;
    private final ObjectMapper objectMapper;
    private final TaskMap taskMap;

    public TaskActuatorController(ApplicationTaskExecutor taskExecutor,
                                  ObjectMapper objectMapper,
                                  Map<String, Task> taskMap) {
        this.taskExecutor = taskExecutor;
        this.objectMapper = objectMapper;
        this.taskMap = new TaskMap(taskMap);
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
        Task task = taskMap.getTaskOrThrow(taskName);
        var node = objectMapper.createObjectNode();
        node.put("executedBy", "Actuator");
        CompletableFuture<ObjectNode> future = taskExecutor.submitCompletable(task, node)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        log.error("error executing actuator task: {}", error.getMessage());
                    }
                    log.debug("result from actuator task: {}", result.toPrettyString());
                });
        return new WebEndpointResponse<>(
                Map.of("task", taskName),
                HttpStatus.ACCEPTED.value()
        );
    }

}
