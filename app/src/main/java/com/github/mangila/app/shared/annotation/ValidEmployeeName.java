package com.github.mangila.app.shared.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Composite annotation for {@link NotBlank} and {@link Size}
 */
@NotBlank
@Size(min = 2, max = 255)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidEmployeeName {

    String message() default "Not valid Employee Name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}