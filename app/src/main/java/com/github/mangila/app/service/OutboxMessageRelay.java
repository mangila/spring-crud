package com.github.mangila.app.service;

import com.github.mangila.app.model.outbox.OutboxEventStatus;
import com.github.mangila.app.repository.OutboxJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Stream;


/**
 * Outbox message relay service.
 * Polls the database for messages and publish them to the application.
 * And also cleans up the database.
 * <br>
 * With this construct we have persisted our messages that should go outbound from the application
 * And stays in our "Outbox" until published
 */
@Service
@Slf4j
public class OutboxMessageRelay {
    private final OutboxJpaRepository repository;
    private final OutboxEventMapper eventMapper;
    private final EmployeeEventHandler eventHandler;

    public OutboxMessageRelay(OutboxJpaRepository repository,
                              OutboxEventMapper eventMapper,
                              EmployeeEventHandler eventHandler) {
        this.repository = repository;
        this.eventMapper = eventMapper;
        this.eventHandler = eventHandler;
    }

    /**
     * The Outbox message relay service. Since we use Spring in-memory event bus.
     * We use this service to relay from our Outbox for sad path scenarios.
     * Failed messages etc.
     * <br>
     * Run a task every n(timeunit), but only if the previous run has finished
     * Is the purpose of a fixed-delay scheduling.
     * <br>
     * If there is a failed message stuck in "PROCESSING" status, manual intervention is required.
     * Change status to "FAILURE" and retry.
     */
    @Scheduled(
            initialDelayString = "${application.outbox-relay.initial-delay}",
            fixedDelayString = "${application.outbox-relay.fixed-delay}")
    public void relay() {
        Stream.of(
                        repository.findAllByStatus(
                                OutboxEventStatus.FAILURE,
                                Sort.by("auditMetadata.created").descending(),
                                Limit.of(25)),
                        repository.findAllByStatus(
                                OutboxEventStatus.PENDING,
                                Sort.by("auditMetadata.created").descending(),
                                Limit.of(25))
                )
                .flatMap(Collection::stream)
                .peek(entity -> log.info("Relaying outbox message: {}", entity))
                .map(eventMapper::map)
                .forEach(eventHandler::handle);
    }
}
