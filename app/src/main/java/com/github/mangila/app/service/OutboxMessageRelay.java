package com.github.mangila.app.service;

import com.github.mangila.app.repository.OutboxJpaRepository;
import com.github.mangila.app.shared.SpringEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OutboxMessageRelay {
    private final OutboxJpaRepository repository;
    private final SpringEventPublisher publisher;

    public OutboxMessageRelay(OutboxJpaRepository repository,
                              SpringEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
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
