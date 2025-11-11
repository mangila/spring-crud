package com.github.mangila.app.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.model.outbox.OutboxEntity;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import com.github.mangila.app.repository.OutboxJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
public class SoftDeletePublishedOutboxTask implements CallableTask {

    private final OutboxJpaRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public SoftDeletePublishedOutboxTask(OutboxJpaRepository outboxRepository,
                                         ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    @Transactional
    public ObjectNode call() {
        List<OutboxEntity> entities = outboxRepository.findAllByStatusAndAuditMetadataDeleted(
                OutboxEventStatus.PUBLISHED,
                false,
                Sort.by("auditMetadata.created").descending(),
                Limit.of(50));
        var node = objectMapper.createObjectNode();
        if (entities.isEmpty()) {
            return node.put("message", "No published outbox events to soft delete");
        }
        node.put("message", "Soft deleting %d published outbox events".formatted(entities.size()));
        var arrayNode = node.putArray("ids");
        for (OutboxEntity entity : entities) {
            var audit = entity.getAuditMetadata();
            // Could happen if change in jpql query to include NULL
            if (audit == null) {
                log.warn("Outbox event {} has no audit metadata", entity.getId());
                node.put(entity.getId().toString(), "Outbox event has no audit metadata");
                continue;
            }
            arrayNode.add(entity.getId().toString());
            audit.setDeleted(true);
        }
        outboxRepository.mergeAll(entities);
        outboxRepository.flush();
        return node;
    }
}
