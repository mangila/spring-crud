package com.github.mangila.app.model.employee.domain;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Domain object, the business entity.
 * This is the data object that will execute logic.
 */
public record Employee(
        EmployeeId id,
        EmployeeName firstName,
        EmployeeName lastName,
        BigDecimal salary,
        ObjectNode attributes,
        Instant created,
        Instant modified,
        boolean deleted
) {
}