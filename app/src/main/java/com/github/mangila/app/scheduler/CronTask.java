package com.github.mangila.app.scheduler;

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
public record CronTask() implements RunnableTask {
    @Override
    public void run() {
        log.info("Hello from Cron Task");
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }
}
