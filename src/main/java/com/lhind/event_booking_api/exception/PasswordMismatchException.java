package com.lhind.event_booking_api.exception;

public class PasswordMismatchException extends RuntimeException {

    public PasswordMismatchException(
            String message
    ) {
        super(message);
    }
}