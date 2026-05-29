package com.example.booking.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * DTO returned when parents or teachers fetch available batches.
 * Includes a boolean flag indicating whether the offering is fully booked.
 */
@Getter
@Setter
public class OfferingResponse {

    private Long id;                     // ID of the offering batch
    private Long courseId;
    private String courseTitle;
    private String courseDescription;

    private Long teacherId;
    private String name;                  // e.g., "Weekday Summer Camp"
    private String timezone;              // Teacher's timezone (e.g., "Asia/Kolkata")

    private int maxCapacity;
    private int currentBookingsCount;

    /** Indicates if the offering has reached its capacity. */
    private boolean full;                 // <-- added

    private List<SessionResponse> sessions; // Sessions in this batch
}