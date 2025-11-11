package com.github.mangila.app.model;

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
    @Column(name = "created", updatable = false)
    private Instant created;

    @LastModifiedDate
    @Column(name = "modified")
    private Instant modified;

    @Column(name = "deleted")
    private boolean deleted;

    public static final AuditMetadata EMPTY = new AuditMetadata(null, null, false);

    public AuditMetadata(Instant created, Instant modified, boolean deleted) {
        this.created = created;
        this.modified = modified;
        this.deleted = deleted;
    }
}
