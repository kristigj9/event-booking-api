package com.lhind.event_booking_api.exception;

public class DuplicateResourceException extends RuntimeException {
    //Objekti eshte gjetur dhe po provohet te shtohet 2-here
    public DuplicateResourceException( String message){
        super(message);
    }
}
