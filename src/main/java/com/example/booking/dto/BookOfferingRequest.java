package com.example.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for booking a class offering batch.
 * 
 * Student explanation:
 * When a parent wants to book a class, they send this request body in JSON.
 * It contains the ID of the parent who is booking and the ID of the offering they want to book.
 */
@Getter
@Setter
public class BookOfferingRequest {

    @NotNull(message = "Parent ID cannot be null")
    private Long parentId; // The ID of the parent booking the batch

    @NotNull(message = "Offering ID cannot be null")
    private Long offeringId; // The ID of the offering batch being booked
}
