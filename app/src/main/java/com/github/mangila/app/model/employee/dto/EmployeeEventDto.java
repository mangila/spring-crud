package com.github.mangila.app.model.employee.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.model.employee.type.EmploymentActivity;
import com.github.mangila.app.model.employee.type.EmploymentStatus;
import com.github.mangila.app.shared.annotation.ValidEmployeeId;
import com.github.mangila.app.shared.annotation.ValidEmployeeName;
import com.github.mangila.app.shared.annotation.ValidEmployeeSalary;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

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
