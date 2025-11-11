package com.github.mangila.app.model.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * We need a sequence table to keep track of the latest processed sequence number for each aggregate.
 * So we can both replay the events and keep them in order.
 */
@Entity
@Table(name = "outbox_current_sequence")
@lombok.NoArgsConstructor
@lombok.Data
public class OutboxCurrentSequenceEntity {

    @Id
    @Column(name = "aggregate_id",
            updatable = false,
            nullable = false)
    private String aggregateId;

    @Column(name = "current_sequence",
            nullable = false)
    private long currentSequence;
}
