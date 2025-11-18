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

/**
 * DTO for updating an existing employee.
 * All fields are required, this is convenient for the frontend if displayed in rows or forms or any other full page display.
 * This would be considered as an HTTP PUT update.
 */
public record UpdateEmployeeRequest(
        @NotBlank @ValidEmployeeId String employeeId,
        @ValidEmployeeName String firstName,
        @ValidEmployeeName String lastName,
        @ValidEmployeeSalary BigDecimal salary,
        @NotNull EmploymentActivity employmentActivity,
        @NotNull EmploymentStatus employmentStatus,
        @NotNull ObjectNode attributes
) {
}
