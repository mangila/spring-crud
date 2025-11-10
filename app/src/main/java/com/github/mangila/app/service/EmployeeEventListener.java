package com.github.mangila.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.model.outbox.OutboxEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Synchronous is the default for Spring @EventListener. To run it asynchronously, use @Async.
 * <br>
 * Use @Async to run side effects and fire and forget stuffs.
 * If not using the @Async annotation, it will block the current thread.
 * Can be good if you want a decoupling between components and not a tight dependency.
 */
@Service
@Slf4j
public class EmployeeEventListener {

    private final EmployeeEventHandler eventHandler;
    private final ObjectMapper objectMapper;

    public EmployeeEventListener(EmployeeEventHandler eventHandler, ObjectMapper objectMapper) {
        this.eventHandler = eventHandler;
        this.objectMapper = objectMapper;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT,
            condition = "#event.status.toString() != 'PUBLISHED'"
    )
    @Async
    public void listen(OutboxEntity event) {
        log.info("Received OutboxEvent: {}", event);
    }
}
