package com.github.mangila.api.model.employee.domain;

import com.github.mangila.api.shared.annotation.ValidEmployeeIdValidator;
import io.github.mangila.ensure4j.Ensure;

/**
 * Value Object, the identifier or Domain Primitive.
 */
public record EmployeeId(String value) {
    private static final ValidEmployeeIdValidator VALIDATOR = new ValidEmployeeIdValidator();

    public EmployeeId {
        Ensure.notBlank(value, "EmployeeId must not be blank");
        if (!VALIDATOR.isValid(value, null)) {
            throw new IllegalArgumentException("EmployeeId is not valid");
        }
    }
}
