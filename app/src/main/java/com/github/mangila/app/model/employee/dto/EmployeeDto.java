package com.github.mangila.app.model.employee.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.model.employee.type.EmploymentActivity;
import com.github.mangila.app.model.employee.type.EmploymentStatus;
import com.github.mangila.app.shared.annotation.ValidEmployeeId;
import com.github.mangila.app.shared.annotation.ValidEmployeeName;
import com.github.mangila.app.shared.annotation.ValidEmployeeSalary;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * DTO (Data Transfer Object) is a data transfer object often used to
 * transfer data over a network, for visual representation,
 * or delegate to another system.
 * <br>
 * Mostly converted to a well-known transfer protocol like JSON or XML.
 */
public record EmployeeDto(
        @NotBlank @ValidEmployeeId String employeeId,
        @ValidEmployeeName String firstName,
        @ValidEmployeeName String lastName,
        @ValidEmployeeSalary BigDecimal salary,
        @NotNull EmploymentActivity employmentActivity,
        @NotNull EmploymentStatus employmentStatus,
        @NotNull ObjectNode attributes,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
        @NotNull ZonedDateTime created,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
        @NotNull ZonedDateTime modified,
        @NotNull boolean deleted
) {
}
