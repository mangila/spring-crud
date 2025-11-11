package com.github.mangila.app.model.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Keep track of the next sequence number for each aggregate.
 * This is used to determine the next sequence number for a new event.
 */
@Entity
@Table(name = "outbox_next_sequence")
@lombok.NoArgsConstructor
@lombok.Data
public class OutboxNextSequenceEntity {

    @Id
    @Column(name = "aggregate_id",
            updatable = false,
            nullable = false)
    private String aggregateId;

    @Column(name = "next_sequence",
            nullable = false)
    private long nextSequence;

}
