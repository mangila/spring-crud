package com.github.mangila.app.model.outbox;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.UUID;

public record OutboxEvent(
        UUID id,
        String eventName,
        OutboxEventStatus status,
        ObjectNode payload) {
}
