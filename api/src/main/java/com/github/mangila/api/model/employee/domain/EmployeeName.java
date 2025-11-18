package com.github.mangila.api.model.employee.domain;


import io.github.mangila.ensure4j.Ensure;

public record EmployeeName(String value) {
    public EmployeeName {
        Ensure.notBlank(value, "Employee name must not be blank");
    }
}
