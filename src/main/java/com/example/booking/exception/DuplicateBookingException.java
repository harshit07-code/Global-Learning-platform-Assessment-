package com.example.booking.exception;

/**
 * Exception thrown when a parent attempts to book the same offering twice.
 */
public class DuplicateBookingException extends RuntimeException {
    public DuplicateBookingException(String message) {
        super(message);
    }
}
