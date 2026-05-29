package com.example.booking.exception;

/**
 * Exception thrown when a session timing overlaps with an already booked session.
 */
public class TimeConflictException extends RuntimeException {
    public TimeConflictException(String message) {
        super(message);
    }
}
