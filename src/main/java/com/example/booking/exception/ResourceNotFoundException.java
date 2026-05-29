package com.example.booking.exception;

/**
 * Exception thrown when a requested resource is not found in the database.
 * we will throw this exception.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
