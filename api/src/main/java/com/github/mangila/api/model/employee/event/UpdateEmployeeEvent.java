package com.github.mangila.api.model.employee.event;

import com.github.mangila.api.model.employee.dto.EmployeeEventDto;
import jakarta.validation.constraints.NotNull;

public record UpdateEmployeeEvent(@NotNull EmployeeEventDto dto) {
}
