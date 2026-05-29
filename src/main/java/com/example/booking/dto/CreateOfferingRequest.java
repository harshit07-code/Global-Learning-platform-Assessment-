package com.example.booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * When a teacher wants to create a new batch, they send this request body in JSON.
 */
@Getter
@Setter
public class CreateOfferingRequest {

    @NotNull(message = "Course ID cannot be null")
    private Long courseId;

    @NotNull(message = "Teacher ID cannot be null")
    private Long teacherId;

    @NotBlank(message = "Offering batch name cannot be blank")
    private String name;

    @NotBlank(message = "Timezone cannot be blank")
    private String timezone;

    @Min(value = 1, message = "Max capacity must be at least 1")
    private int maxCapacity = 10; // by default the maximum cattegory will be 10 we can change it later easily.
}
