package com.lhind.event_booking_api.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(
            String message
    ) {
        super(message);
    }
}