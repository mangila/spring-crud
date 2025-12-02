package com.github.mangila.api.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.model.outbox.OutboxEntity;
import com.github.mangila.api.model.outbox.OutboxEventStatus;
import com.github.mangila.api.repository.OutboxJpaRepository;
import com.github.mangila.api.service.OutboxEventMapper;
import com.github.mangila.api.shared.ApplicationContextHolder;
import com.github.mangila.api.shared.SpringEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

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
        log.info("Message from ApplicationContextHolder: - {}", ApplicationContextHolder.getEntry("message"));
        List<OutboxEntity> pendingEntities = repository.findAllByStatus(
                OutboxEventStatus.PENDING,
                Sort.by("auditMetadata.created").descending(),
                Limit.of(25));
        List<OutboxEntity> failureEntities = repository.findAllByStatus(
                OutboxEventStatus.FAILURE,
                Sort.by("auditMetadata.created").descending(),
                Limit.of(25));
        List<OutboxEntity> unprocessableEntities = repository.findAllByStatus(
                OutboxEventStatus.UNPROCESSABLE_EVENT,
                Sort.by("auditMetadata.created").descending(),
                Limit.of(25));
        ObjectNode node = objectMapper.createObjectNode();
        var lists = List.of(pendingEntities, failureEntities, unprocessableEntities);
        lists.stream()
                .flatMap(Collection::stream)
                .map(eventMapper::map)
                .forEach(publisher::publish);
        node.put("pending-size", pendingEntities.size());
        node.put("failure-size", failureEntities.size());
        node.put("unprocessable-size", unprocessableEntities.size());
        ArrayNode pendingArray = node.putArray("pending");
        pendingEntities.forEach(entity -> pendingArray.add(entity.getId().toString()));
        ArrayNode failureArray = node.putArray("failure");
        failureEntities.forEach(entity -> failureArray.add(entity.getId().toString()));
        ArrayNode unprocessableArray = node.putArray("unprocessable");
        unprocessableEntities.forEach(entity -> unprocessableArray.add(entity.getId().toString()));
        return node;
    }
}
