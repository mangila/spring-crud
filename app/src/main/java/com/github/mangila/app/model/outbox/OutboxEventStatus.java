package com.github.mangila.app.model.outbox;

public enum OutboxEventStatus {
    PENDING,
    PROCESSING,
    PUBLISHED,
    FAILURE,
    UNPROCESSABLE_EVENT
}
