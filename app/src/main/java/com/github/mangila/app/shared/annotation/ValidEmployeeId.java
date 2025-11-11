package com.github.mangila.app.shared.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidEmployeeIdValidator.class)
public @interface ValidEmployeeId {

    // 1. Error message template
    String message() default "Not valid employee aggregateId";

    // 2. Used to specify constraint groups
    Class<?>[] groups() default {};

    // 3. Used to specify payload (extra metadata)
    Class<? extends Payload>[] payload() default {};
}