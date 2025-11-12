package com.github.mangila.app.model.outbox;

import com.github.mangila.app.model.AuditMetadata;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * We need a sequence table to keep track of the latest processed sequence number for each aggregate.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "outbox_latest_processed_sequence")
@lombok.NoArgsConstructor
@lombok.Data
public class OutboxLatestProcessedSequenceEntity {

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

    public static OutboxLatestProcessedSequenceEntity from(String aggregateId) {
        var sequence = new OutboxLatestProcessedSequenceEntity();
        sequence.setAggregateId(aggregateId);
        sequence.setAuditMetadata(AuditMetadata.EMPTY);
        return sequence;
    }
}
