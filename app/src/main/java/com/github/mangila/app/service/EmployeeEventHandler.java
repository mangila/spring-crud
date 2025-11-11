package com.github.mangila.app.service;

import com.github.mangila.app.model.outbox.OutboxEvent;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import com.github.mangila.app.repository.OutboxJpaRepository;
import com.github.mangila.app.shared.exception.UnprocessableEventException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * The event handling that takes care of all the events.
 * <br>
 * Updates the event to "PROCESSING" status and persists it in the database.
 * <br>
 * Then proceed with the business logic. Can be anything basically, e.g., send an email, put on a Kafka topic, RabbitMQ queue or whatever.
 * <br>
 * If it goes OK, update the event to "PUBLISHED" status and persist it in the database.
 * If it goes wrong, update the event to "FAILURE" status and persist it in the database.
 */
@Service
@Slf4j
public class EmployeeEventHandler {

    private final TransactionTemplate transactionTemplate;
    private final OutboxJpaRepository repository;

    public EmployeeEventHandler(TransactionTemplate transactionTemplate,
                                OutboxJpaRepository repository) {
        this.transactionTemplate = transactionTemplate;
        this.repository = repository;
    }

    public void handle(OutboxEvent event) {
        Integer claim = acquireOptimisticClaim(event);
        if (hasClaim(claim)) {
            try {
                // Do the business logic
                transactionTemplate.executeWithoutResult(txStatus -> {
                    switch (event.eventName()) {
                        case "CreateNewEmployeeEvent" -> handleCreateNewEmployeeEvent(event);
                        case "UpdateEmployeeEvent" -> handleUpdateEmployeeEvent(event);
                        case "SoftDeleteEmployeeEvent" -> handleSoftDeleteEmployeeEvent(event);
                        default ->
                                throw new UnprocessableEventException("Event unprocessable: %s".formatted(event.eventName()));
                    }
                    repository.changeStatus(OutboxEventStatus.PUBLISHED, event.id());
                });
            } catch (UnprocessableEventException e) {
                log.error("Unprocessable event: {}", event, e);
                repository.changeStatus(OutboxEventStatus.UNPROCESSABLE_EVENT, event.id());
            } catch (Exception e) {
                log.error("Error handling event: {}", event, e);
                repository.changeStatus(OutboxEventStatus.FAILURE, event.id());
            }
        }
    }

    /**
     * Acquire an optimistic claim on the event.
     * If the row was changed, we can proceed with the business logic.
     * Change the row to PROCESSING if that fails, then roll back the transaction.
     */
    private @Nullable Integer acquireOptimisticClaim(OutboxEvent event) {
        return transactionTemplate.execute(txStatus -> {
            int claim = repository.optimisticClaim(event.id());
            if (claim == 0) {
                log.warn("Optimistic Claim on event {} failed", event.id());
                txStatus.setRollbackOnly();
            }
            return claim;
        });
    }

    /**
     * Check if the optimistic claim was successful.
     * The row was changed, and we can safely proceed with the business logic.
     */
    private static boolean hasClaim(Integer claim) {
        return claim != null && claim == 1;
    }

    private void handleCreateNewEmployeeEvent(OutboxEvent event) {
        log.info("Handling CreateNewEmployeeEvent: {}", event);
    }

    private void handleUpdateEmployeeEvent(OutboxEvent event) {
        log.info("Handling UpdateEmployeeEvent: {}", event);
    }

    private void handleSoftDeleteEmployeeEvent(OutboxEvent event) {
        log.info("Handling SoftDeleteEmployeeEvent: {}", event);
    }
}
