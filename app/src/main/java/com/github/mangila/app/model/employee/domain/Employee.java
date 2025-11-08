package com.github.mangila.app.model.employee.domain;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.model.employee.type.EmploymentActivity;
import com.github.mangila.app.model.employee.type.EmploymentStatus;

import java.math.BigDecimal;

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
        EmploymentActivity employmentActivity,
        EmploymentStatus employmentStatus,
        EmployeeAudit audit
) {
}