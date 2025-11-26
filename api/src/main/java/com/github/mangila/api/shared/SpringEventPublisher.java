package com.github.mangila.api.shared;

import com.github.mangila.api.model.outbox.OutboxEntity;
import com.github.mangila.api.model.outbox.OutboxEvent;
import com.github.mangila.api.repository.OutboxJpaRepository;
import com.github.mangila.api.service.OutboxEventMapper;
import com.github.mangila.api.service.OutboxFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NullMarked;
import org.postgresql.PGNotification;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Wrapper for Spring's ApplicationEventPublisher.
 * <br>
 * Spring's ApplicationEventPublisher is used to publish events, very convenient when want to run a side effect.
 * This is just an example of how to use Spring's ApplicationEventPublisher.
 * <br>
 * Simple use case when using event stuffs in the same Spring Boot application.
 * <br>
 * Since we use Postgres as our database, we can also use LISTEN/NOTIFY from Postgres.
 * But this is not the focus of this example.
 */
@Service
@Validated
@NullMarked
public class SpringEventPublisher {

    private final ApplicationEventPublisher publisher;

    private final OutboxFactory outboxFactory;

    private final OutboxJpaRepository outboxRepository;

    private final OutboxEventMapper eventMapper;

    public SpringEventPublisher(ApplicationEventPublisher publisher,
                                OutboxFactory outboxFactory,
                                OutboxJpaRepository outboxRepository,
                                OutboxEventMapper eventMapper) {
        this.publisher = publisher;
        this.outboxFactory = outboxFactory;
        this.outboxRepository = outboxRepository;
        this.eventMapper = eventMapper;
    }

    /**
     * Creates an outbox event from the given event and persists it in the database
     * before publishing it.
     * <br>
     * MANDATORY transaction propagation is used to ensure that the event is persisted inside a tx
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(@NotBlank String aggregateId, @Valid @NotNull Object event) {
        OutboxEntity entity = outboxFactory.from(aggregateId, event);
        outboxRepository.persist(entity);
        OutboxEvent outboxEvent = eventMapper.map(entity);
        publisher.publishEvent(outboxEvent);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(@Valid @NotNull OutboxEvent event) {
        publisher.publishEvent(event);
    }

    public void publish(@NotNull PGNotification[] notification) {
        publisher.publishEvent(notification);
    }
}
