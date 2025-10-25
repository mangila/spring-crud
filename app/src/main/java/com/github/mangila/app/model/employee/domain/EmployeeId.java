package com.github.mangila.app.model.employee.domain;

import com.github.mangila.app.shared.Ensure;
import com.github.mangila.app.shared.annotation.ValidEmployeeIdValidator;

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
