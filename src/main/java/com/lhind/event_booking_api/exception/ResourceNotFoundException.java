package com.lhind.event_booking_api.exception;


public class ResourceNotFoundException extends RuntimeException {

    //Nje Objekt qe nuk gjendet
    public ResourceNotFoundException(String message) {
        super(message);
    }
}