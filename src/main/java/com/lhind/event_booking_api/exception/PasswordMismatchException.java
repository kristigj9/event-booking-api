package com.lhind.event_booking_api.exception;

public class PasswordMismatchException extends RuntimeException {
// kur newPassword dhe confirmPassword nuk perkojn me njeritjetrin
    public PasswordMismatchException(String message) {
        super(message);
    }
}