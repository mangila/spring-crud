package com.github.mangila.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.scheduler.CallableTask;
import com.github.mangila.app.scheduler.RunnableTask;
import com.github.mangila.app.scheduler.SchedulerTaskExecutor;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Actuator endpoint for executing tasks.
 * Since this is part of a management thingy (not part of the usual work flow), it's a good idea to use an actuator endpoint for it.
 * Or just create a new controller. Opinionated approach.
 */
@WebEndpoint(id = "task")
@Component
public class TaskActuatorController {

    private final SchedulerTaskExecutor taskExecutor;
    private final ObjectMapper objectMapper;
    private final Map<String, RunnableTask> runnableTaskMap;
    private final Map<String, CallableTask> callableTaskMap;

    public TaskActuatorController(SchedulerTaskExecutor taskExecutor,
                                  ObjectMapper objectMapper,
                                  Map<String, RunnableTask> runnableTaskMap,
                                  Map<String, CallableTask> callableTaskMap) {
        this.taskExecutor = taskExecutor;
        this.objectMapper = objectMapper;
        this.runnableTaskMap = runnableTaskMap;
        this.callableTaskMap = callableTaskMap;
    }

    @ReadOperation
    public WebEndpointResponse<Map<String, List<String>>> findAllTasks() {
        var tasks = Stream.of(runnableTaskMap, callableTaskMap)
                .flatMap(map -> map.keySet().stream())
                .toList();
        return new WebEndpointResponse<>(
                Map.of("tasks", tasks),
                HttpStatus.OK.value()
        );
    }

    @WriteOperation
    public WebEndpointResponse<Map<String, Object>> execute(@Selector String taskName) {
        RunnableTask runnableTask = runnableTaskMap.get(taskName);
        if (runnableTask != null) {
            var node = objectMapper.createObjectNode();
            node.put("executedBy", "Actuator");
            taskExecutor.submitRunnable(runnableTask, node);
            return new WebEndpointResponse<>(
                    Map.of("task", taskName),
                    HttpStatus.ACCEPTED.value()
            );
        }
        CallableTask callableTask = callableTaskMap.get(taskName);
        if (callableTask != null) {
            var node = objectMapper.createObjectNode();
            node.put("executedBy", "Actuator");
            taskExecutor.submitCallable(callableTask, node);
            return new WebEndpointResponse<>(
                    Map.of("task", taskName),
                    HttpStatus.ACCEPTED.value()
            );
        }
        return new WebEndpointResponse<>(
                Map.of("error", "Task not found: %s".formatted(taskName)),
                HttpStatus.NOT_FOUND.value()
        );
    }

}
