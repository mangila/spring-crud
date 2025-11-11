package com.github.mangila.app.scheduler;

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
public class SoftDeletePublishedOutboxTask implements Task {

    private final OutboxJpaRepository outboxRepository;

    public SoftDeletePublishedOutboxTask(OutboxJpaRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    @Transactional
    public void run() {
        List<OutboxEntity> entities = outboxRepository.findAllByStatusAndAuditMetadataDeleted(
                OutboxEventStatus.PUBLISHED,
                false,
                Sort.by("auditMetadata.created").descending(),
                Limit.of(50));
        if (entities.isEmpty()) {
            log.info("No published outbox events to soft delete");
            return;
        }
        log.info("Soft deleting {} published outbox events", entities.size());
        for (OutboxEntity entity : entities) {
            var audit = entity.getAuditMetadata();
            // Could happen if change in jpql query to include NULL
            if (audit == null) {
                log.warn("Outbox event {} has no audit metadata", entity.getId());
                continue;
            }
            audit.setDeleted(true);
        }
        outboxRepository.mergeAll(entities);
        outboxRepository.flush();
    }
}
