package com.github.mangila.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.model.AuditMetadata;
import com.github.mangila.api.model.outbox.OutboxEntity;
import com.github.mangila.api.model.outbox.OutboxEventStatus;
import com.github.mangila.api.model.outbox.OutboxNextSequenceEntity;
import com.github.mangila.api.model.outbox.OutboxProcessedSequenceEntity;

public class OutboxTestFactory {

    public static OutboxEntity createOutboxEntity(String aggregateId,
                                                  String eventName,
                                                  OutboxEventStatus status,
                                                  ObjectNode payload) {
        var entity = new OutboxEntity();
        entity.setAggregateId(aggregateId);
        entity.setEventName(eventName);
        entity.setStatus(status);
        entity.setSequence(1);
        entity.setPayload(payload);
        entity.setAuditMetadata(AuditMetadata.EMPTY);
        return entity;
    }

    public static OutboxProcessedSequenceEntity createOutboxProcessedSequenceEntity(String aggregateId) {
        return OutboxProcessedSequenceEntity.from(aggregateId);
    }

    public static OutboxNextSequenceEntity createOutboxNextSequenceEntity(String aggregateId) {
        return OutboxNextSequenceEntity.from(aggregateId);
    }

}
