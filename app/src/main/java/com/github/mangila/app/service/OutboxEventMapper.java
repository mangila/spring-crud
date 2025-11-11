package com.github.mangila.app.service;

import com.github.mangila.app.model.outbox.OutboxEntity;
import com.github.mangila.app.model.outbox.OutboxEvent;
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
