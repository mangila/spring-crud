package com.github.mangila.api.model.employee.domain;


import io.github.mangila.ensure4j.Ensure;

import java.math.BigDecimal;

public record EmployeeSalary(BigDecimal value) {

    public EmployeeSalary {
        Ensure.notNull(value, "Employee salary must not be null");
    }

}
