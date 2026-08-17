package com.lhind.event_booking_api.exception;

public class InvalidPasswordException extends RuntimeException {

    //Kur pasword ne databaze nuk perkon me pass qe kemi vendosur
    public InvalidPasswordException(String message) {
        super(message);
    }
}