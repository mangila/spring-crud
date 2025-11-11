package com.github.mangila.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.model.AuditMetadata;
import com.github.mangila.app.model.outbox.OutboxEntity;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import org.springframework.stereotype.Service;

@Service
public class OutboxFactory {

    private final ObjectMapper objectMapper;

    public OutboxFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OutboxEntity from(Object event) {
        var outbox = new OutboxEntity();
        outbox.setStatus(OutboxEventStatus.PENDING);
        outbox.setEventName(event.getClass().getSimpleName());
        outbox.setPayload(objectMapper.valueToTree(event));
        outbox.setAuditMetadata(AuditMetadata.EMPTY);
        return outbox;
    }
}
