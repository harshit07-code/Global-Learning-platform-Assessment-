package com.example.booking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * When adding sessions, the teacher inputs times in their local timezone.
 * For example: "2026-06-06T18:00:00" (which means 6:00 PM on June 6).
 * We accept these as strings and parse them into LocalDateTime.
 * Then, we convert them to UTC using the offering's timezone (e.g. Asia/Kolkata)
 * before saving to the database.
 */
@Getter
@Setter
public class SessionTimeRequest {

    @NotBlank(message = "Start time cannot be blank")
    private String startTime; // Local start time, e.g. "2026-06-06T18:00:00"

    @NotBlank(message = "End time cannot be blank")
    private String endTime; // Local end time, e.g. "2026-06-06T19:00:00"
}
