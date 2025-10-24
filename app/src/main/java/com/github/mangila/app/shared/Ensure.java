package com.github.mangila.app.shared;

public final class Ensure {

    public static void notNull(Object value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("Value must not be null");
        }
    }
}
