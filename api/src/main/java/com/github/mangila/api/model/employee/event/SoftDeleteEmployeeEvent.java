package com.github.mangila.api.model.employee.event;

import com.github.mangila.api.model.employee.dto.EmployeeEventDto;
import jakarta.validation.constraints.NotNull;

public record SoftDeleteEmployeeEvent(@NotNull EmployeeEventDto dto) {
}
