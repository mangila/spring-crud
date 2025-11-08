package com.github.mangila.app.shared.event;

import com.github.mangila.app.model.employee.domain.EmployeeId;

/**
 * Naming events can sometimes be a bit confusing.
 * Here we name it "<ACTION><DOMAIN>Event"
 */
public record UpdateEmployeeEvent(EmployeeId employeeId) {
}
