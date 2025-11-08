package com.github.mangila.app.model.employee.domain;

import com.github.mangila.app.shared.Ensure;

import java.time.Instant;

public record EmployeeAudit(
        Instant created,
        Instant modified,
        boolean deleted
) {
    public static EmployeeAudit EMPTY = new EmployeeAudit(null, null, false);

    public EmployeeAudit {
        Ensure.notNull(created, "Employee created must not be null");
        Ensure.notNull(modified, "Employee modified must not be null");
        //TODO: validate created and modified timestamps
    }
}
