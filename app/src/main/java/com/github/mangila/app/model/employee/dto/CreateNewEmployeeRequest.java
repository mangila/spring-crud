package com.github.mangila.app.model.employee.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateNewEmployeeRequest(
        @NotBlank @Size(min = 2, max = 255) String firstName,
        @NotBlank @Size(min = 2, max = 255) String lastName,
        @Digits(integer = 5, fraction = 2) BigDecimal salary,
        @NotNull ObjectNode attributes
) {
}
