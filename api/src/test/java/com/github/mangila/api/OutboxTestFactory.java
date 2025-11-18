package com.github.mangila.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.model.AuditMetadata;
import com.github.mangila.api.model.outbox.OutboxEntity;
import com.github.mangila.api.model.outbox.OutboxEventStatus;
import com.github.mangila.api.model.outbox.OutboxNextSequenceEntity;
import com.github.mangila.api.model.outbox.OutboxProcessedSequenceEntity;

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

    public static OutboxProcessedSequenceEntity createOutboxProcessedSequenceEntity() {
        return OutboxProcessedSequenceEntity.from("test");
    }

    public static OutboxNextSequenceEntity createOutboxNextSequenceEntity() {
        return OutboxNextSequenceEntity.from("test");
    }

}
