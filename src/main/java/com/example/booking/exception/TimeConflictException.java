package com.example.booking.exception;

/**
 * Exception thrown when a session timing overlaps with an already booked session.
 * 
 * Student explanation:
 * This handles "Rule 2 (Time Conflict Locking)". If a parent tries to book a new class
 * that overlaps with an already booked class, we throw this to cancel the transaction.
 */
public class TimeConflictException extends RuntimeException {
    public TimeConflictException(String message) {
        super(message);
    }
}
