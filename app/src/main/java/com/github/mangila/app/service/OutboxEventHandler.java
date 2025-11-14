package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.event.CreateNewEmployeeEvent;
import com.github.mangila.app.model.employee.event.SoftDeleteEmployeeEvent;
import com.github.mangila.app.model.employee.event.UpdateEmployeeEvent;
import com.github.mangila.app.model.outbox.OutboxEvent;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import com.github.mangila.app.repository.OutboxJpaRepository;
import com.github.mangila.app.shared.exception.UnprocessableEventException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * The event handling that takes care of all the events.
 * <br>
 * Then proceed with the business logic. Can be anything basically, e.g., send an email, put on a Kafka topic, RabbitMQ queue or whatever.
 * <br>
 * If it goes OK, update the event to "PUBLISHED" status and persist it in the database.
 * If it goes wrong, update the event to "FAILURE" status and persist it in the database.
 */
@Service
@Slf4j
public class OutboxEventHandler {
    private final OutboxJpaRepository repository;

    public OutboxEventHandler(OutboxJpaRepository repository) {
        this.repository = repository;
    }

    public void handle(OutboxEvent event) {
        try {
            Class<?> eventType = Class.forName(event.eventName());
            if (eventType == CreateNewEmployeeEvent.class) {
                handleCreateNewEmployeeEvent(event);
            } else if (eventType == UpdateEmployeeEvent.class) {
                handleUpdateEmployeeEvent(event);
            } else if (eventType == SoftDeleteEmployeeEvent.class) {
                handleSoftDeleteEmployeeEvent(event);
            } else {
                throw new UnprocessableEventException("Event unprocessable: %s".formatted(event.eventName()));
            }
            repository.changeStatus(OutboxEventStatus.PUBLISHED, event.aggregateId());
        } catch (UnprocessableEventException e) {
            log.error("Unprocessable event: {}", event, e);
            repository.changeStatus(OutboxEventStatus.UNPROCESSABLE_EVENT, event.aggregateId());
        } catch (Exception e) {
            log.error("Error handling event: {}", event, e);
            repository.changeStatus(OutboxEventStatus.FAILURE, event.aggregateId());
        }
    }

    /**
     * This is where the event handling logic goes for the event.
     * Here we simulate a 500-ms-second task.
     * If there is more complex event handling logic, this can be split up into multiple Beans
     * e.g., CreateNewEmployeeEventHandler, UpdateEmployeeEventHandler, SoftDeleteEmployeeEventHandler.
     * <br>
     * This is where the event leaves our "Outbox" and hopefully enters someone's "Inbox" :)
     * <br>
     * If it fails here, we just re-deliver from the OutboxMessageRelayTask.
     * <br>
     */
    private void handleCreateNewEmployeeEvent(OutboxEvent event) {
        log.info("Handling CreateNewEmployeeEvent: {}", event);
        try {
            Thread.sleep(Duration.ofMillis(500));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleUpdateEmployeeEvent(OutboxEvent event) {
        log.info("Handling UpdateEmployeeEvent: {}", event);
        try {
            Thread.sleep(Duration.ofMillis(500));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleSoftDeleteEmployeeEvent(OutboxEvent event) {
        log.info("Handling SoftDeleteEmployeeEvent: {}", event);
        try {
            Thread.sleep(Duration.ofMillis(500));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
