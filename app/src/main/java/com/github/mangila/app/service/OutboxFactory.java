package com.github.mangila.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.model.AuditMetadata;
import com.github.mangila.app.model.outbox.OutboxEntity;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import com.github.mangila.app.model.outbox.OutboxNextSequenceEntity;
import com.github.mangila.app.repository.OutboxNextSequenceJpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxFactory {

    private final OutboxNextSequenceJpaRepository nextSequenceRepository;
    private final ObjectMapper objectMapper;

    public OutboxFactory(OutboxNextSequenceJpaRepository nextSequenceRepository,
                         ObjectMapper objectMapper) {
        this.nextSequenceRepository = nextSequenceRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OutboxEntity from(String aggregateId, Object event) {
        var outbox = new OutboxEntity();
        outbox.setAggregateId(aggregateId);
        outbox.setStatus(OutboxEventStatus.PENDING);
        outbox.setEventName(event.getClass().getSimpleName());
        outbox.setPayload(objectMapper.valueToTree(event));
        outbox.setAuditMetadata(AuditMetadata.EMPTY);
        // Exclusive lock for the aggregateId and increment nextSequenceEntity
        // TODO: lock timeout
        OutboxNextSequenceEntity nextSequenceEntity = nextSequenceRepository.lockById(
                aggregateId,
                LockModeType.PESSIMISTIC_WRITE);
        long sequence = nextSequenceEntity.getSequence() + 1;
        nextSequenceEntity.setSequence(sequence);
        outbox.setSequence(sequence);
        nextSequenceRepository.merge(nextSequenceEntity);
        return outbox;
    }
}
