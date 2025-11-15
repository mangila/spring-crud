package com.github.mangila.app.shared.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Composite annotation for {@link NotBlank} and {@link Size}
 * Unicode all chars regex for {@link Pattern}
 * This validation for a name can be a bit tricky, but it's a good practice to validate something.
 * This is just a simple example.
 */
@NotBlank
@Size(min = 2, max = 255)
// Do allow hyphens but not in the beginning or end and allow Unicode alphabetic chars
@Pattern(regexp = "^(?!-)(?!.*-$)[\\p{IsAlphabetic}-]+$")
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidEmployeeName {

    String message() default "Not valid Employee Name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}