package com.github.mangila.app.model.employee.domain;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.shared.Ensure;

public record EmployeeAttributes(ObjectNode value) {

    public EmployeeAttributes {
        Ensure.notNull(value, "Employee attributes must not be null");
    }
}
