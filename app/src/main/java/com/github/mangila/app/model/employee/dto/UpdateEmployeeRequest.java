package com.github.mangila.app.model.employee.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.shared.annotation.ValidEmployeeId;
import com.github.mangila.app.shared.annotation.ValidEmployeeName;
import com.github.mangila.app.shared.annotation.ValidEmployeeSalary;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Request DTO for updating an existing employee.
 * This is what would be called a PUT update in HTTP verbs.
 * It requires the full data model to be sent.
 */
public record UpdateEmployeeRequest(
        @NotBlank @ValidEmployeeId String employeeId,
        @ValidEmployeeName String firstName,
        @ValidEmployeeName String lastName,
        @ValidEmployeeSalary BigDecimal salary,
        @NotNull ObjectNode attributes
) {
}
