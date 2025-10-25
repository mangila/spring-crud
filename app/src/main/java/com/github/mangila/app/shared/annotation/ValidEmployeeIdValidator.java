package com.github.mangila.app.shared.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID;

public class ValidEmployeeIdValidator implements ConstraintValidator<ValidEmployeeId, String> {

    @Override
    public void initialize(ValidEmployeeId constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Defer null/empty check to @NotBlank or @NotNull
        }
        // EMP-<2 chars name>
        String substring = value.substring(0, 8);
        // UUID
        String uuid = value.substring(9);
        return substring.matches("^EMP-[A-Z]{4}$") && canParseUuid(uuid);
    }

    static boolean canParseUuid(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
