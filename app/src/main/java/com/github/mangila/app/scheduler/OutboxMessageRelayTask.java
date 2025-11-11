package com.github.mangila.app.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.model.outbox.OutboxEntity;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import com.github.mangila.app.repository.OutboxJpaRepository;
import com.github.mangila.app.service.OutboxEventMapper;
import com.github.mangila.app.shared.SpringEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Component
@Slf4j
public class OutboxMessageRelayTask implements Task {

    private final OutboxJpaRepository repository;
    private final OutboxEventMapper eventMapper;
    private final ObjectMapper objectMapper;
    private final SpringEventPublisher publisher;

    public OutboxMessageRelayTask(OutboxJpaRepository repository,
                                  OutboxEventMapper eventMapper,
                                  ObjectMapper objectMapper,
                                  SpringEventPublisher publisher) {
        this.repository = repository;
        this.eventMapper = eventMapper;
        this.objectMapper = objectMapper;
        this.publisher = publisher;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    @Transactional
    public ObjectNode call() {
        List<OutboxEntity> pendingEntities = repository.findAllByStatus(
                OutboxEventStatus.PENDING,
                Sort.by("auditMetadata.created").descending(),
                Limit.of(25));
        List<OutboxEntity> failureEntities = repository.findAllByStatus(
                OutboxEventStatus.FAILURE,
                Sort.by("auditMetadata.created").descending(),
                Limit.of(25));
        var node = objectMapper.createObjectNode();
        if (pendingEntities.isEmpty() && failureEntities.isEmpty()) {
            return node.put("message", "No outbox events to relay");
        }
        Stream.of(pendingEntities, failureEntities)
                .flatMap(Collection::stream)
                .map(eventMapper::map)
                .forEach(publisher::publish);
        node.put("pending-size", pendingEntities.size());
        node.put("failure-size", failureEntities.size());
        var pendingArray = node.putArray("pending");
        pendingEntities.forEach(entity -> pendingArray.add(entity.getId().toString()));
        var failureArray = node.putArray("failure");
        failureEntities.forEach(entity -> failureArray.add(entity.getId().toString()));
        return node;
    }
}
