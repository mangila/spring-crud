package com.github.mangila.app.service;

import com.github.mangila.app.repository.OutboxJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


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

    private final EmployeeEventHandler eventHandler;

    public OutboxMessageRelay(OutboxJpaRepository repository,
                              EmployeeEventHandler eventHandler) {
        this.repository = repository;
        this.eventHandler = eventHandler;
    }

    @Scheduled(
            initialDelayString = "${application.outbox-relay.initial-delay}",
            fixedDelayString = "${application.outbox-relay.fixed-delay}")
    public void relay() {
        log.info("Relaying outbox messages...");
    }

    @Scheduled(
            initialDelayString = "${application.outbox-relay.initial-delay}",
            fixedDelayString = "${application.outbox-relay.cleanup}")
    public void cleanup() {
        log.info("Cleaning up PUBLISHED outbox messages...");
    }
}
