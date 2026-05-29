package com.example.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler to catch any exceptions thrown in controllers or services
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // Helper method to create a standard error JSON response structure
    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String errorMessage) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", Instant.now().toString());
        errorBody.put("status", status.value());
        errorBody.put("error", status.getReasonPhrase());
        errorBody.put("message", errorMessage);
        return new ResponseEntity<>(errorBody, status);
    }

    // Handles resource not found (e.g., parent not found, offering not found) - HTTP 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Handles schedule overlapping session conflicts - HTTP 409 Conflict
    @ExceptionHandler(TimeConflictException.class)
    public ResponseEntity<Object> handleTimeConflict(TimeConflictException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Handles double booking of the same offering by same parent - HTTP 400 Bad Request
    @ExceptionHandler(DuplicateBookingException.class)
    public ResponseEntity<Object> handleDuplicateBooking(DuplicateBookingException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Handles offering capacity full exception - HTTP 400 Bad Request
    @ExceptionHandler(OfferingFullException.class)
    public ResponseEntity<Object> handleOfferingFull(OfferingFullException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Handles invalid request arguments (e.g. invalid date/timezone format) - HTTP 400 Bad Request
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Object> handleInvalidRequest(InvalidRequestException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Handles Spring validation errors (@Valid failures) - HTTP 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", Instant.now().toString());
        errorBody.put("status", HttpStatus.BAD_REQUEST.value());
        errorBody.put("error", "Validation Failed");
        errorBody.put("message", "One or more input values are incorrect.");
        errorBody.put("details", validationErrors);

        return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }

    // Handles URL path not found (Spring Boot 3 default routing exception) - HTTP 404
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFound(org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "The requested URL path was not found on this server: /" + ex.getResourcePath());
    }

    // Handles any other general runtime exceptions - HTTP 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralExceptions(Exception ex) {
        ex.printStackTrace(); // Logs the error in console for debugging
        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred: " + ex.getMessage()
        );
    }
}
