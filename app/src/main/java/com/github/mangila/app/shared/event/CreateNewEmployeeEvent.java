package com.github.mangila.app.shared.event;

import com.github.mangila.app.model.employee.domain.EmployeeId;

/**
 * Events often published it's a good practice to keep events as small as possible.
 * With not too much data. Buffers in event-based infra can have a limit.
 */
public record CreateNewEmployeeEvent(EmployeeId employeeId) {
}
