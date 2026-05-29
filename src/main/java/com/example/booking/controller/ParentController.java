package com.example.booking.controller;

import com.example.booking.dto.BookOfferingRequest;
import com.example.booking.dto.BookingResponse;
import com.example.booking.dto.OfferingResponse;
import com.example.booking.service.BookingService;
import com.example.booking.service.OfferingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/parents")
public class ParentController {

    private final OfferingService offeringService;
    private final BookingService bookingService;

    @Autowired
    public ParentController(OfferingService offeringService, BookingService bookingService) {
        this.offeringService = offeringService;
        this.bookingService = bookingService;
    }

    /**
     * URL: GET http://localhost:8080/api/parents/offerings
     */
    @GetMapping("/offerings")
    public ResponseEntity<List<OfferingResponse>> getAvailableOfferings(
            @RequestParam(required = false) String timezone) {
        
        List<OfferingResponse> responses = offeringService.getAllAvailableOfferings(timezone);
        return ResponseEntity.ok(responses);
    }

    /**
     * URL: POST http://localhost:8080/api/parents/bookings
     * Body format:
     * {
     *     "parentId": 1,
     *     "offeringId": 2
     * }
     */
    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> bookOffering(@Valid @RequestBody BookOfferingRequest request) {
        BookingResponse response = bookingService.bookOffering(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * URL: GET http://localhost:8080/api/parents/{parentId}/bookings
     */
    @GetMapping("/{parentId}/bookings")
    public ResponseEntity<List<BookingResponse>> getParentBookings(
            @PathVariable Long parentId,
            @RequestParam(required = false) String timezone) {
        
        List<BookingResponse> responses = bookingService.getBookingsByParent(parentId, timezone);
        return ResponseEntity.ok(responses);
    }

    /**
     * URL: GET http://localhost:8080/api/parents
     */
    @GetMapping
    public ResponseEntity<List<com.example.booking.entity.Parent>> getAllParents() {
        List<com.example.booking.entity.Parent> parents = bookingService.getAllParents();
        return ResponseEntity.ok(parents);
    }
}
