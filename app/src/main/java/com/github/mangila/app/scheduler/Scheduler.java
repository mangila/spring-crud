package com.github.mangila.app.scheduler;

import org.springframework.stereotype.Service;

/**
 * Run some tasks, running with the Spring integrated scheduler.
 * Since the scheduler is running during the JVM lifecycle of the application, it's a good idea to use a separate service with a dedicated Scheduler framework.
 * To prevent resource exhaustion.
 * For minor stuff like cache eviction or stuff that belongs inside the application infra.
 */
@Service
public class Scheduler {
}
