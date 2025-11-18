package com.github.mangila.api.model.employee.domain;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.mangila.ensure4j.Ensure;

public record EmployeeAttributes(ObjectNode value) {

    public EmployeeAttributes {
        Ensure.notNull(value, "Employee attributes must not be null");
    }
}
