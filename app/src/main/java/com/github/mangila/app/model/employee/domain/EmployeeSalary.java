package com.github.mangila.app.model.employee.domain;

import com.github.mangila.app.shared.Ensure;

import java.math.BigDecimal;

public record EmployeeSalary(BigDecimal value) {

    public EmployeeSalary {
        Ensure.notNull(value, "Employee salary must not be null");
    }

}
