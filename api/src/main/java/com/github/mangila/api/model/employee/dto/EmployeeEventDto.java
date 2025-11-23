package com.github.mangila.api.model.employee.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.model.employee.type.EmploymentActivity;
import com.github.mangila.api.model.employee.type.EmploymentStatus;
import com.github.mangila.api.shared.annotation.ValidEmployeeId;
import com.github.mangila.api.shared.annotation.ValidEmployeeName;
import com.github.mangila.api.shared.annotation.ValidEmployeeSalary;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * <p>
 * DTO for employee domain events.
 * This is a representation of the event payload as it is sent to the event bus.
 * </p>
 */
public record EmployeeEventDto(
        @NotBlank @ValidEmployeeId String employeeId,
        @ValidEmployeeName String firstName,
        @ValidEmployeeName String lastName,
        @ValidEmployeeSalary BigDecimal salary,
        @NotNull EmploymentActivity employmentActivity,
        @NotNull EmploymentStatus employmentStatus,
        @NotNull ObjectNode attributes,
        @NotNull Instant created,
        @NotNull Instant modified,
        @NotNull boolean deleted
) {
}
