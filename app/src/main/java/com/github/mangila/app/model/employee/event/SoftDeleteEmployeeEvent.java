package com.github.mangila.app.model.employee.event;

import com.github.mangila.app.model.employee.dto.EmployeeDto;

public record SoftDeleteEmployeeEvent(EmployeeDto dto) {
}
