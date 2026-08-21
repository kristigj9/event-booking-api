package com.lhind.event_booking_api.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(
            String message
    ) {
        super(message);
    }
}