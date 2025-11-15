package com.github.mangila.app.model.outbox;

import com.github.mangila.app.model.AuditMetadata;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * We need a sequence table to keep track of the latest processed sequence number for each aggregate.
 * <br>
 * "The consumer of sequences, because without it, we can't get the events in order"
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "outbox_processed_sequence")
@lombok.NoArgsConstructor
@lombok.Data
public class OutboxProcessedSequenceEntity {

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

    public static OutboxProcessedSequenceEntity from(String aggregateId) {
        var entity = new OutboxProcessedSequenceEntity();
        entity.setAggregateId(aggregateId);
        entity.setAuditMetadata(AuditMetadata.EMPTY);
        return entity;
    }
}
