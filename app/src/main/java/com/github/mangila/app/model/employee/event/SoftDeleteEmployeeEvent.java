package com.github.mangila.app.model.employee.event;

import com.github.mangila.app.model.employee.dto.EmployeeEventDto;
import jakarta.validation.constraints.NotNull;

public record SoftDeleteEmployeeEvent(@NotNull EmployeeEventDto dto) {
}
