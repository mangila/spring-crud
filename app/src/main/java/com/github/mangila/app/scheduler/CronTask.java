package com.github.mangila.app.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Good practice to isolate a task in a dedicated class. Easier to test, extend and maintain.
 * <br>
 * Let the Scheduler decide when to run the task, and the Task should run the business logic.
 * Separation of concerns thinking.
 */
@Component
@Slf4j
public class CronTask implements Task {
    private final ObjectMapper objectMapper;

    public CronTask(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public ObjectNode call() throws Exception {
        log.info("Hello from Cron Task");
        return objectMapper.createObjectNode();
    }
}
