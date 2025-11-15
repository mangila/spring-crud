package com.github.mangila.app.shared;

import com.github.mangila.app.shared.exception.EnsureException;

import java.util.function.Supplier;

/**
 * Ensure is (in)sanity checks to be run before any logic or after any logic.
 * <br>
 * Pre- or Post-Condition to make sure input or output is expected.
 * <br>
 * Optional to use since Spring Boot has the {@link org.springframework.util.Assert}
 * But with an own version it can be used in the project to wrap custom exceptions
 */
public final class Ensure {

    private Ensure() {
        throw new IllegalStateException("Utility class");
    }

    public static void notNull(Object value, Supplier<RuntimeException> supplier) throws RuntimeException {
        if (value == null) {
            throw supplier.get();
        }
    }

    public static void notNull(Object value, String message) throws EnsureException {
        notNull(value, () -> new EnsureException(message));
    }

    public static void notNull(Object value) throws EnsureException {
        notNull(value, "Value must not be null");
    }

    public static void notBlank(String value, Supplier<RuntimeException> supplier) throws EnsureException {
        notNull(value, supplier);
        if (value.isBlank()) {
            throw supplier.get();
        }
    }

    public static void notBlank(String value, String message) throws EnsureException {
        notBlank(value, () -> new EnsureException(message));
    }

    public static void notBlank(String value) throws EnsureException {
        notBlank(value, "Value must not be blank");
    }

    public static void isTrue(boolean expression,
                              Supplier<RuntimeException> exceptionSupplier) throws RuntimeException {
        if (!expression) {
            throw exceptionSupplier.get();
        }
    }

    public static void isTrue(boolean expression, String message) throws EnsureException {
        isTrue(expression, () -> new EnsureException(message));
    }

    public static void isTrue(boolean expression) throws EnsureException {
        isTrue(expression, "Expression must be true");
    }

    public static void isFalse(boolean expression,
                               Supplier<RuntimeException> exceptionSupplier) throws RuntimeException {
        if (expression) {
            throw exceptionSupplier.get();
        }
    }

    public static void isFalse(boolean expression, String message) throws EnsureException {
        isTrue(expression, () -> new EnsureException(message));
    }

    public static void isFalse(boolean expression) throws EnsureException {
        isTrue(expression, "Expression must be false");
    }
}
