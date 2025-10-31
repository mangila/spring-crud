package com.github.mangila.app.model.employee.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.shared.annotation.ValidEmployeeName;
import com.github.mangila.app.shared.annotation.ValidEmployeeSalary;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO (Data Transfer Object) is a data transfer object often used to
 * transfer data over a network, for visual representation,
 * or delegate to another system.
 */
public record EmployeeDto(
        @ValidEmployeeName String firstName,
        @ValidEmployeeName String lastName,
        @ValidEmployeeSalary BigDecimal salary,
        @NotNull ObjectNode attributes,
        @NotNull Instant created,
        @NotNull Instant modified,
        @NotNull Boolean deleted
) {
}
