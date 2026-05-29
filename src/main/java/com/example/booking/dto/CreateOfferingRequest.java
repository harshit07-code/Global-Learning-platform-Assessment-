package com.example.booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for creating a new offering batch.
 * 
 * Student explanation:
 * When a teacher wants to create a new batch, they send this request body in JSON.
 * We use validation annotations like @NotNull and @NotBlank to automatically check
 * if they sent the proper data. If they didn't, Spring Boot will reject the request automatically.
 */
@Getter
@Setter
public class CreateOfferingRequest {

    @NotNull(message = "Course ID cannot be null")
    private Long courseId; // The ID of the course they want to schedule, e.g., Python Coding (ID: 1)

    @NotNull(message = "Teacher ID cannot be null")
    private Long teacherId; // The ID of the teacher who will teach this batch

    @NotBlank(message = "Offering batch name cannot be blank")
    private String name; // E.g., "Saturday Batch" or "Summer Camp"

    @NotBlank(message = "Timezone cannot be blank")
    private String timezone; // The timezone of the teacher, e.g., "Asia/Kolkata" or "America/New_York"

    @Min(value = 1, message = "Max capacity must be at least 1")
    private int maxCapacity = 10; // Maximum number of slots for students (defaults to 10)
}
