package com.github.mangila.app.service;

import com.github.mangila.app.model.outbox.OutboxEvent;
import com.github.mangila.app.model.outbox.OutboxProcessedSequenceEntity;
import com.github.mangila.app.repository.OutboxProcessedSequenceJpaRepository;
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
    private final OutboxEventHandler eventHandler;
    private final OutboxProcessedSequenceJpaRepository sequenceRepository;
    private final TransactionTemplate transactionTemplate;

    public EmployeeEventListener(OutboxEventHandler eventHandler,
                                 OutboxProcessedSequenceJpaRepository sequenceRepository,
                                 TransactionTemplate transactionTemplate) {
        this.eventHandler = eventHandler;
        this.sequenceRepository = sequenceRepository;
        this.transactionTemplate = transactionTemplate;
    }

    /**
     * <p>
     * Creates an Exclusive lock for the aggregateId sequence and determine if the event can be processed.
     * </p>
     * <p>
     * Now we are only handling one Employee Domain. But if there should be more domains, an extra condition needs to be added.
     * And an extra field on the OutboxEvent to specify the domain.
     * TransactionalEventListener can use a condition to filter the events.
     * </p>
     * <br>
     * <pre>
     *       {@code
     *            @TransactionalEventListener(
     *                   phase = TransactionPhase.AFTER_COMMIT,
     *                   condition = "#event.domain == employee"
     *           )
     *           public void listen(OutboxEvent event) {}
     *      }
     * </pre>
     */
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    @Async
    public void listen(OutboxEvent event) {
        log.info("Received OutboxEvent: {}", event);
        transactionTemplate.executeWithoutResult(txStatus -> {
            // Exclusive lock for the aggregate and handle the event
            OutboxProcessedSequenceEntity latestProcessedSequence = sequenceRepository.lockById(
                    event.aggregateId(),
                    LockModeType.PESSIMISTIC_WRITE);
            if (latestProcessedSequence == null) {
                latestProcessedSequence = OutboxProcessedSequenceEntity.from(event.aggregateId());
            }
            // Try to find the next event in order for the aggregate
            long expectedSequence = latestProcessedSequence.getSequence() + 1;
            if (canProcess(event.sequence(), expectedSequence)) {
                eventHandler.handle(event);
                latestProcessedSequence.setSequence(event.sequence());
                sequenceRepository.merge(latestProcessedSequence);
            } else if (isDuplicate(event.sequence(), expectedSequence)) {
                log.warn("Duplicate event detected - {}", event);
                // duplicate event, do nothing
            } else if (isEarly(event.sequence(), expectedSequence)) {
                log.warn("Event arrived too early - {}", event);
                // event arrived too early, do nothing
                // TODO: maybe create a staging area for early events
            } else {
                // this is a critical, sequence-ordering mismatch. Might need manual intervention.
                log.error("Outbox event ordering mismatch - Event seq:{} Expected seq:{} Latest seq:{}",
                        event.sequence(),
                        expectedSequence,
                        latestProcessedSequence.getSequence());
            }
        });
    }

    /**
     * Process the event if the sequence is the same as the last processed event + 1
     */
    private boolean canProcess(long eventSequence, long expectedSequence) {
        return eventSequence == expectedSequence;
    }

    /**
     * Check for duplicate events.
     * Duplicate events are not processed.
     */
    private static boolean isDuplicate(long eventSequence, long expectedSequence) {
        return eventSequence == expectedSequence - 1;
    }

    /**
     * Check for Events arrived before the expected event.
     * Early bird catches the worm...
     * But not in this case.
     */
    private static boolean isEarly(long eventSequence, long expectedSequence) {
        return eventSequence > expectedSequence;
    }
}
