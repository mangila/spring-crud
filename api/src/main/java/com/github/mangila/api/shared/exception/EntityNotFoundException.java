package com.github.mangila.api.shared.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super("Entity with id: %s".formatted(message));
    }
}
