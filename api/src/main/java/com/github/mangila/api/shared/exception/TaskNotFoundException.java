package com.github.mangila.api.shared.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String message) {
        super("Task with name: %s".formatted(message));
    }
}
