package com.github.mangila.api.model.employee.domain;

import org.jspecify.annotations.Nullable;

import java.time.Instant;

public record EmployeeAudit(
        @Nullable Instant created,
        @Nullable Instant modified,
        boolean deleted
) {
    public static EmployeeAudit EMPTY = new EmployeeAudit(null, null, false);

    public EmployeeAudit {
        //TODO: validate created and modified timestamps
    }
}
