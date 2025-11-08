package com.github.mangila.app.controller;

import com.github.mangila.app.shared.exception.EnsureException;
import com.github.mangila.app.shared.exception.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class RestErrorHandler {

    /**
     * Log this or not, that's the question. Might create a lot of noise in the logs.
     * Since we often know what it's all about.
     */
    @ExceptionHandler(EnsureException.class)
    public ProblemDetail handleEnsureException(EnsureException e) {
        log.error("ERR", e);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        );
        return problemDetail;
    }

    /**
     * Log this or not, that's the question. Might create a lot of noise in the logs.
     * Since we often know what it's all about.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("ERR", e);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
        return problemDetail;
    }

    /**
     * MethodArgumentNotValidException - Can be called the "@Valid validation"
     * <br>
     * Log this or not, that's the question. Might create a lot of noise in the logs.
     * Since we often know what it's all about.
     * Might log for some time and remove it later when the app is getting more mature.
     * The client gets its feedback from the exception message.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("ERR", e);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        );
        return problemDetail;
    }

    /**
     * ConstraintViolationException - Can be called the "@Validated validation"
     * <br>
     * Log this or not, that's the question. Might create a lot of noise in the logs.
     * Since we often know what it's all about.
     * Might log for some time and remove it later when the app is getting more mature.
     * The client gets its feedback from the exception message.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException e) {
        log.error("ERR", e);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        );
        return problemDetail;
    }

    /**
     * This one might be a security concern to since it might expose internal details to the client.
     * Uncontrollable runtime exceptions not thrown by the programmer
     */
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(RuntimeException e) {
        log.error("ERR", e);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Something went wrong. Please try again later."
        );
        return problemDetail;
    }

    /**
     * General sec practice to doesn't expose internal server errors to the client.
     * If in an Enterprise setting within a closed network, this might be desired.
     * But generally it's a good idea to hide internal server errors.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception e) {
        log.error("ERR", e);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong. Please try again later."
        );
        return problemDetail;
    }
}
