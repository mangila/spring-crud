package com.github.mangila.app.service;

import com.github.mangila.app.model.outbox.OutboxEvent;
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

    public EmployeeEventListener(EmployeeEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * Listen for the OutboxEvent after the transaction has been committed
     * This is a happy path scenario. For immediate handling.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void listen(OutboxEvent event) {
        log.info("Received OutboxEvent: {}", event);
        eventHandler.handle(event);
    }
}
