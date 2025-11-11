package com.github.mangila.app.service;

import com.github.mangila.app.model.outbox.OutboxEvent;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import com.github.mangila.app.repository.OutboxJpaRepository;
import org.springframework.stereotype.Service;

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
public class EmployeeEventHandler {

    private final OutboxJpaRepository repository;

    public EmployeeEventHandler(OutboxJpaRepository repository) {
        this.repository = repository;
    }

    public void handle(OutboxEvent event) {
        repository.changeStatus(OutboxEventStatus.PROCESSING, event.id());
        // TODO: match event type and handle it accordingly.
        repository.changeStatus(OutboxEventStatus.PUBLISHED, event.id());
    }
}
