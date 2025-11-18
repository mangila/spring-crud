package com.github.mangila.api.service;

import com.github.mangila.api.model.outbox.OutboxEntity;
import com.github.mangila.api.model.outbox.OutboxEvent;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventMapper {

    public OutboxEvent map(OutboxEntity entity) {
        return new OutboxEvent(
                entity.getId(),
                entity.getAggregateId(),
                entity.getSequence(),
                entity.getEventName(),
                entity.getStatus(),
                entity.getPayload()
        );
    }
}
