package com.github.mangila.app.model.employee.domain;

import com.github.mangila.app.shared.Ensure;

public record EmployeeName(String value) {
    public EmployeeName {
        Ensure.notBlank(value, "Employee name must not be blank");
    }
}
