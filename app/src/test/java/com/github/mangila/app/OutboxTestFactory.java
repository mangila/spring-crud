package com.github.mangila.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.model.AuditMetadata;
import com.github.mangila.app.model.outbox.OutboxEntity;
import com.github.mangila.app.model.outbox.OutboxEventStatus;

public class OutboxTestFactory {

    public static OutboxEntity createOutboxEntity(OutboxEventStatus status, ObjectMapper objectMapper) {
        var entity = new OutboxEntity();
        entity.setAggregateId("test");
        entity.setEventName("test");
        entity.setStatus(status);
        entity.setPayload(objectMapper.createObjectNode());
        entity.setAuditMetadata(AuditMetadata.EMPTY);
        return entity;
    }

}
