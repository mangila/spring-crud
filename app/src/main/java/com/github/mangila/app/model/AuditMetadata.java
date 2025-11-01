package com.github.mangila.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Embeddable
public record AuditMetadata(
        @CreatedDate
        @Column(name = "created", updatable = false)
        Instant created,
        @LastModifiedDate
        @Column(name = "modified")
        Instant modified,
        @Column(name = "deleted")
        boolean deleted
) {
}
