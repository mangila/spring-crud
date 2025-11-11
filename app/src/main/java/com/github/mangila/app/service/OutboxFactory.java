package com.github.mangila.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.model.AuditMetadata;
import com.github.mangila.app.model.outbox.OutboxEntity;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import com.github.mangila.app.model.outbox.OutboxNextSequenceEntity;
import com.github.mangila.app.repository.OutboxNextSequenceRepository;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxFactory {

    private final OutboxNextSequenceRepository nextSequenceRepository;
    private final ObjectMapper objectMapper;

    public OutboxFactory(OutboxNextSequenceRepository nextSequenceRepository,
                         ObjectMapper objectMapper) {
        this.nextSequenceRepository = nextSequenceRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OutboxEntity from(String aggregateId, Object event) {
        var outbox = new OutboxEntity();
        outbox.setStatus(OutboxEventStatus.PENDING);
        outbox.setEventName(event.getClass().getSimpleName());
        outbox.setPayload(objectMapper.valueToTree(event));
        outbox.setAuditMetadata(AuditMetadata.EMPTY);
        // Exclusive lock for the aggregateId and increment sequence
        OutboxNextSequenceEntity sequence = nextSequenceRepository.lockById(
                aggregateId,
                LockModeType.PESSIMISTIC_WRITE);
        long nextSequence = sequence.getNextSequence() + 1;
        sequence.setNextSequence(nextSequence);
        outbox.setSequence(nextSequence);
        nextSequenceRepository.merge(sequence);
        return outbox;
    }
}
