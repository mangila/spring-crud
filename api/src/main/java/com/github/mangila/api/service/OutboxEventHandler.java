package com.github.mangila.api.service;

import com.github.mangila.api.model.employee.event.CreateNewEmployeeEvent;
import com.github.mangila.api.model.employee.event.SoftDeleteEmployeeEvent;
import com.github.mangila.api.model.employee.event.UpdateEmployeeEvent;
import com.github.mangila.api.model.outbox.OutboxEvent;
import com.github.mangila.api.model.outbox.OutboxEventStatus;
import com.github.mangila.api.repository.OutboxJpaRepository;
import com.github.mangila.api.shared.exception.UnprocessableEventException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * If else our way to find what type of the event we are dealing with.
     * Updates the event status in the database and do whatever we want to do with the event
     * NOTE: We are in a database X-lock here!!!
     */
    @Transactional
    public void handle(@NonNull OutboxEvent event) {
        try {
            Class<?> eventType = getEventType(event.eventName());
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
    void handleCreateNewEmployeeEvent(OutboxEvent event) {
        log.info("Handling CreateNewEmployeeEvent: {}", event);
        try {
            Thread.sleep(Duration.ofMillis(500));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void handleUpdateEmployeeEvent(OutboxEvent event) {
        log.info("Handling UpdateEmployeeEvent: {}", event);
        try {
            Thread.sleep(Duration.ofMillis(500));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void handleSoftDeleteEmployeeEvent(OutboxEvent event) {
        log.info("Handling SoftDeleteEmployeeEvent: {}", event);
        try {
            Thread.sleep(Duration.ofMillis(500));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * The class needs to be in our classpath to be loaded.
     * <br>
     * Explicitly avoids running static blocks (initialization phase)
     * since we are just doing a reference check.
     * Just using Class.forName() can cause some issues if there is some static initialization code.
     * </p>
     */
    private static Class<?> getEventType(String eventName) throws UnprocessableEventException {
        try {
            boolean init = false;
            return Class.forName(
                    eventName,
                    init,
                    Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            throw new UnprocessableEventException("Event unprocessable: %s".formatted(eventName), e);
        }
    }
}
