package com.example.booking.exception;

/**
 * Exception thrown when validation fails, like end time being before start time
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
