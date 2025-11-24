package com.github.mangila.api.shared.exception;

public class UnprocessableEventException extends RuntimeException {

    public UnprocessableEventException(String message) {
        super(message);
    }
    public UnprocessableEventException(String message, Throwable cause) {
        super(message, cause);
    }
}
