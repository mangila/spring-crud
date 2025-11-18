package com.github.mangila.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Embeddable
@lombok.NoArgsConstructor
@lombok.Data
public class AuditMetadata {

    @CreatedDate
    @Column(name = "created", nullable = false, updatable = false)
    private Instant created;

    @LastModifiedDate
    @Column(name = "modified", nullable = false)
    private Instant modified;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    public static final AuditMetadata EMPTY = new AuditMetadata(null, null, false);

    public AuditMetadata(Instant created, Instant modified, boolean deleted) {
        this.created = created;
        this.modified = modified;
        this.deleted = deleted;
    }
}
