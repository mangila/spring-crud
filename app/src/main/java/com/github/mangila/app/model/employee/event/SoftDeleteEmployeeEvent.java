package com.github.mangila.app.model.employee.event;

import com.github.mangila.app.model.employee.domain.EmployeeId;

public record SoftDeleteEmployeeEvent(EmployeeId employeeId) {
}
