package com.lhind.event_booking_api.exception;

public class InvalidOperationException extends RuntimeException {
    //Perdoret per business Rules. Psh: "This seat is not Avaible"
    public InvalidOperationException(String message) {
        super(message);
    }
}