package com.github.mangila.app.shared.event;

import com.github.mangila.app.model.employee.domain.EmployeeId;

public record NewEmployeeCreatedEvent(EmployeeId employeeId) {
}
