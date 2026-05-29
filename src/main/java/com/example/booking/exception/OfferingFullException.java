package com.example.booking.exception;

/**
 * Exception thrown when an offering is fully booked (no remaining slots).
 */
public class OfferingFullException extends RuntimeException {
    public OfferingFullException(String message) {
        super(message);
    }
}
