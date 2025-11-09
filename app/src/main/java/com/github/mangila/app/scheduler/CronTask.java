package com.github.mangila.app.scheduler;

import lombok.extern.slf4j.Slf4j;

/**
 * Good practice to isolate a task in a dedicated class. Easier to test, extend and maintain.
 * <br>
 * Let the Scheduler decide when to run the task, and the Task should run the business logic.
 * Separation of concerns thinking.
 */
@Slf4j
public record CronTask() implements Runnable {
    @Override
    public void run() {
        log.info("Hello from Cron Task");
    }
}
