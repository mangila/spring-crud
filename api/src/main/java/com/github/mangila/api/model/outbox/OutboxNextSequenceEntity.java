package com.github.mangila.api.model.outbox;

import com.github.mangila.api.model.AuditMetadata;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Keep track of the next sequence number for each aggregate.
 * This is used to determine the next sequence number for a new event.
 * <br>
 * "The producer of sequences, because without it, we can't get the events in order"
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "outbox_next_sequence")
@lombok.NoArgsConstructor
@lombok.Data
public class OutboxNextSequenceEntity {

    @Id
    @Column(name = "aggregate_id",
            updatable = false,
            nullable = false)
    private String aggregateId;

    @Column(name = "sequence",
            nullable = false)
    private long sequence;

    @Embedded
    private AuditMetadata auditMetadata;

    public static OutboxNextSequenceEntity from(String aggregateId) {
        var entity = new OutboxNextSequenceEntity();
        entity.setAggregateId(aggregateId);
        entity.setAuditMetadata(AuditMetadata.EMPTY);
        return entity;
    }
}
