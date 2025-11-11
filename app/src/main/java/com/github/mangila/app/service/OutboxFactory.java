package com.github.mangila.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.model.AuditMetadata;
import com.github.mangila.app.model.outbox.OutboxEntity;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import com.github.mangila.app.model.outbox.OutboxSequenceEntity;
import com.github.mangila.app.repository.OutboxSequenceRepository;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxFactory {

    private final OutboxSequenceRepository sequenceRepository;
    private final ObjectMapper objectMapper;

    public OutboxFactory(OutboxSequenceRepository sequenceRepository,
                         ObjectMapper objectMapper) {
        this.sequenceRepository = sequenceRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OutboxEntity from(String aggregateId, Object event) {
        var outbox = new OutboxEntity();
        outbox.setStatus(OutboxEventStatus.PENDING);
        outbox.setEventName(event.getClass().getSimpleName());
        outbox.setPayload(objectMapper.valueToTree(event));
        outbox.setAuditMetadata(AuditMetadata.EMPTY);
        OutboxSequenceEntity sequence = sequenceRepository.lockById(aggregateId, LockModeType.PESSIMISTIC_WRITE);
        outbox.setSequence(sequence.getLatestSequence() + 1);
        return outbox;
    }
}
