package com.example.booking.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

/**
 * DTO representing a successful booking details.
 * When a parent books a batch, or lists their bookings, the API returns this.
 */
@Getter
@Setter
public class BookingResponse {

    private Long bookingId;
    private Long parentId;
    private String parentName;
    private String parentTimezone;
    
    private OfferingResponse offering;
    private Instant bookedAt;
}
