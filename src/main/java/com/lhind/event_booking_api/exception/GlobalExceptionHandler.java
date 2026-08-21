package com.lhind.event_booking_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LogManager.getLogger(
                    GlobalExceptionHandler.class
            );

    // 404 - RESOURCE NOT FOUND
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
    }

    // 409 - DUPLICATE RESOURCE
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex,
            HttpServletRequest request
    ) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );
    }

    // 400 - BUSINESS RULE
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOperation(
            InvalidOperationException ex,
            HttpServletRequest request
    ) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    // 400 - PASSWORD ERRORS
    @ExceptionHandler({
            InvalidPasswordException.class,
            PasswordMismatchException.class
    })
    public ResponseEntity<ErrorResponse> handlePasswordException(
            RuntimeException ex,
            HttpServletRequest request
    ) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    // 400 - INVALID ARGUMENT / OLD BUSINESS EXCEPTION
    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ErrorResponse> handleIllegalOperation(
            RuntimeException ex,
            HttpServletRequest request
    ) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    // 400 - VALIDATION
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        Map<String, String> errors =
                new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .timestamp(
                                LocalDateTime.now()
                        )
                        .status(
                                HttpStatus.BAD_REQUEST.value()
                        )
                        .error(
                                HttpStatus.BAD_REQUEST
                                        .getReasonPhrase()
                        )
                        .message(
                                "Validation failed"
                        )
                        .path(
                                request.getRequestURI()
                        )
                        .validationErrors(
                                errors
                        )
                        .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 400 - INVALID JSON / INVALID ENUM
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request body or invalid value",
                request
        );
    }

    // 500 - UNEXPECTED ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error(
                "Unexpected error while processing request {}",
                request.getRequestURI(),
                ex
        );

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request
        );
    }

    // PRIVATE HELPER METHOD

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {

        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .timestamp(
                                LocalDateTime.now()
                        )
                        .status(
                                status.value()
                        )
                        .error(
                                status.getReasonPhrase()
                        )
                        .message(
                                message
                        )
                        .path(
                                request.getRequestURI()
                        )
                        .build();

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }
}