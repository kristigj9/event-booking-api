package com.lhind.event_booking_api.exception;

public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(
            String message
    ) {
        super(message);
    }
}