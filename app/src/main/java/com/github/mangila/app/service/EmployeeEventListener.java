package com.github.mangila.app.service;

import com.github.mangila.app.model.outbox.OutboxEvent;
import com.github.mangila.app.model.outbox.OutboxCurrentSequenceEntity;
import com.github.mangila.app.repository.OutboxCurrentSequenceRepository;
import jakarta.persistence.LockModeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionTemplate;

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
    private final OutboxCurrentSequenceRepository sequenceRepository;
    private final TransactionTemplate transactionTemplate;

    public EmployeeEventListener(EmployeeEventHandler eventHandler,
                                 OutboxCurrentSequenceRepository sequenceRepository,
                                 TransactionTemplate transactionTemplate) {
        this.eventHandler = eventHandler;
        this.sequenceRepository = sequenceRepository;
        this.transactionTemplate = transactionTemplate;
    }

    /**
     * Listen for the OutboxEvent after the transaction has been committed
     * This is a happy path scenario. For immediate handling.
     */
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    @Async
    public void listen(OutboxEvent event) {
        log.info("Received OutboxEvent: {}", event);
        transactionTemplate.executeWithoutResult(txStatus -> {
            // Exclusive lock for the aggregate and handle the event
            OutboxCurrentSequenceEntity sequence = sequenceRepository.lockById(
                    event.aggregateId(),
                    LockModeType.PESSIMISTIC_WRITE);
            // Try to find the next event in order for the aggregate
            long expectedSequence = sequence.getCurrentSequence() + 1;
            if (event.sequence() == expectedSequence) {
                eventHandler.handle(event);
                sequence.setCurrentSequence(event.sequence());
                sequenceRepository.merge(sequence);
            } else if (event.sequence() < expectedSequence) {
                log.warn("OutboxEvent sequence mismatch: {} - {}", event.sequence(), expectedSequence);
                // do nothing and wait for the next event message relay cycle
            } else {
                log.warn("OutboxEvent sequence duplicate: {} - {}", event.sequence(), expectedSequence);
                // do nothing and wait for the next event message relay cycle
            }
        });
    }
}
