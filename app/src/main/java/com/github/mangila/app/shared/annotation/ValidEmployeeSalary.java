package com.github.mangila.app.shared.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Composite annotation for {@link NotNull}, {@link Min} and {@link Digits}
 */
@NotNull
@Min(value = 1)
@Digits(integer = 5, fraction = 2)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidEmployeeSalary {

    String message() default "Not valid Employee Salary";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
