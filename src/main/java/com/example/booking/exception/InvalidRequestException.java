package com.example.booking.exception;

/**
 * Exception thrown when validation fails, like end time being before start time
 * or invalid input timezone format.
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
