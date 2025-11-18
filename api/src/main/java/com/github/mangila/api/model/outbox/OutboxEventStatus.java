package com.github.mangila.api.model.outbox;

public enum OutboxEventStatus {
    PENDING,
    PUBLISHED,
    FAILURE,
    UNPROCESSABLE_EVENT
}
