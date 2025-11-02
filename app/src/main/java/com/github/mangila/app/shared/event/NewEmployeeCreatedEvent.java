package com.github.mangila.app.shared.event;

import com.github.mangila.app.model.employee.entity.EmployeeEntity;

public record NewEmployeeCreatedEvent(EmployeeEntity entity) {
}
