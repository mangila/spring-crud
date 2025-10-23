package com.github.mangila.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Embeddable
@lombok.NoArgsConstructor
public class AuditMetadata {
    @CreatedDate
    @Column(name = "created",
            nullable = false,
            updatable = false)
    private Instant created;

    @LastModifiedDate
    @Column(name = "modified",
            nullable = false)
    private Instant modified;

    @Column(name = "deleted",
            nullable = false)
    private Boolean deleted = false;
}
