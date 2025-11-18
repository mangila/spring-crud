package com.github.mangila.api.model.outbox;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OutboxEvent(
        @NotNull UUID id,
        @NotNull String aggregateId,
        @Min(1) long sequence,
        @NotNull String eventName,
        @NotNull OutboxEventStatus status,
        @NotNull ObjectNode payload
) {
}
