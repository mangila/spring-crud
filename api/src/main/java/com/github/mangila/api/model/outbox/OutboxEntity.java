package com.github.mangila.api.model.outbox;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.model.AuditMetadata;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

/**
 * Playing around with events it's important to have an Event storage.
 * Since we are using in-memory publishing, we will need some kind of backup for resilience.
 * So let's implement an Outbox event pattern
 * <br>
 * <a href="https://microservices.io/patterns/data/transactional-outbox.html">Outbox pattern stuffs</a>
 * <br>
 * We are not in a microservice env, but we still need backup from in-memory publishing.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "outbox_event")
@lombok.NoArgsConstructor
@lombok.Data
public class OutboxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid",
            updatable = false,
            nullable = false)
    private UUID id;

    @Column(name = "aggregate_id",
            updatable = false,
            nullable = false)
    private String aggregateId;

    @Column(name = "sequence",
            nullable = false)
    private long sequence;

    @Column(name = "event_name",
            nullable = false)
    private String eventName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",
            nullable = false)
    private OutboxEventStatus status;

    @Type(JsonBinaryType.class)
    @Column(name = "payload",
            columnDefinition = "jsonb",
            nullable = false)
    private ObjectNode payload;

    @Embedded
    private AuditMetadata auditMetadata;
}
