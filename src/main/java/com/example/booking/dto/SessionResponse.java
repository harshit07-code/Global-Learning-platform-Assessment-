package com.example.booking.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

/**
 * When we return session details in the API, we show the absolute UTC time
 */
@Getter
@Setter
public class SessionResponse {

    private Long id; // Unique ID of the session
    private Long offeringId; // ID of the offering batch this session belongs to
    private Long teacherId; // ID of the teacher who runs the session
    
    private Instant startTimeUtc;
    private Instant endTimeUtc;

    private String startTimeLocal; // Start time formatted for your local timezone (e.g., "2026-06-06 18:00")
    private String endTimeLocal; // End time formatted for your local timezone (e.g., "2026-06-06 19:00")
    private String timezoneUsed; // The timezone name we used to calculate the local times (e.g., "America/New_York")
}
