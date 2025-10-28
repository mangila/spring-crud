package com.github.mangila.app.shared.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID;

public class ValidEmployeeIdValidator implements ConstraintValidator<ValidEmployeeId, String> {

    private static final int ID_LENGTH = 45;

    @Override
    public void initialize(ValidEmployeeId constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Defer null/empty check to @NotBlank or @NotNull
        } else if (value.length() != ID_LENGTH) {
            // fixed length for the id, fail fast
            return false;
        }
        // EMP-<2 chars first_name><2 chars last_name>
        String prefix = value.substring(0, 8);
        // UUID
        String uuid = value.substring(9);
        return prefix.matches("^EMP-[A-Z]{4}$") && canParseUuid(uuid);
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
