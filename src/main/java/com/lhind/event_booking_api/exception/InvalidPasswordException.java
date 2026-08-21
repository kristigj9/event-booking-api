package com.lhind.event_booking_api.exception;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(
            String message
    ) {
        super(message);
    }
}