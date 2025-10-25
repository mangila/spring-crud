package com.github.mangila.app.shared;

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
