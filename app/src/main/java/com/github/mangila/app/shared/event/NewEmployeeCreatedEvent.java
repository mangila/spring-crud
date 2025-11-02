package com.github.mangila.app.shared.event;

import com.github.mangila.app.model.employee.domain.Employee;

public record NewEmployeeCreatedEvent(Employee employee) {
}
