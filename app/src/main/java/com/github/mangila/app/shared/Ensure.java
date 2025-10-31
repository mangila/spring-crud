package com.github.mangila.app.shared;

/**
 * Ensure is (in)sanity checks to be run before any logic or after any logic.
 * <br>
 * Pre- or Post-Condition to make sure input or output is expected.
 * <br>
 * Optional to use since Spring Boot has the {@link org.springframework.util.Assert}
 * But with an own version it can be used in the project to wrap custom exceptions
 */
public final class Ensure {

    public static void notNull(Object value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("Value must not be null");
        }
    }

    public static void notNull(Object value, String message) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notBlank(String value, String message) throws IllegalArgumentException {
        notNull(value, message);
        if (value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
