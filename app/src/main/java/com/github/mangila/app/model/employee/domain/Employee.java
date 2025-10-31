package com.github.mangila.app.model.employee.domain;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Domain object, the business entity.
 * This is the data object that will execute logic.
 */
@lombok.Data
public class Employee {
    private EmployeeId id;
    private EmployeeName firstName;
    private EmployeeName lastName;
    private BigDecimal salary;
    private ObjectNode attributes;
    private Instant created;
    private Instant modified;
    private Boolean deleted = false;
}
