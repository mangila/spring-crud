package com.github.mangila.app.model.employee.domain;

import java.time.Instant;

public record EmployeeAudit(
        Instant created,
        Instant modified,
        boolean deleted
) {
    public static EmployeeAudit EMPTY = new EmployeeAudit(null, null, false);
}
