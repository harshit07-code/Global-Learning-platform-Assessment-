package com.example.booking.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;


//  When parents or teachers fetch available batches, they get this object.

@Getter
@Setter
public class OfferingResponse {

    private Long id; //  ID of the offering batch
    
    private Long courseId;
    private String courseTitle;
    private String courseDescription;

    private Long teacherId;
    private String name; // Name of the batch, e.g., "Weekday Summer Camp"
    private String timezone; // The teacher's timezone in which it was created (e.g. "Asia/Kolkata")
    
    private int maxCapacity;
    private int currentBookingsCount;

    private List<SessionResponse> sessions; // List of all classes (sessions) in this batch

    public void setFull(boolean b) {

    }
}
