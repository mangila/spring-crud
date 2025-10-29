package com.github.mangila.app.model.employee.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.shared.annotation.ValidEmployeeName;
import com.github.mangila.app.shared.annotation.ValidEmployeeSalary;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateNewEmployeeRequest(
        @ValidEmployeeName String firstName,
        @ValidEmployeeName String lastName,
        @ValidEmployeeSalary BigDecimal salary,
        @NotNull ObjectNode attributes
) {
}
