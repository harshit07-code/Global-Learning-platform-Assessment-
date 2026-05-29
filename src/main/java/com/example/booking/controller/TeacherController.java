package com.example.booking.controller;

import com.example.booking.dto.CreateOfferingRequest;
import com.example.booking.dto.OfferingResponse;
import com.example.booking.dto.SessionResponse;
import com.example.booking.dto.SessionTimeRequest;
import com.example.booking.service.OfferingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final OfferingService offeringService;

    @Autowired
    public TeacherController(OfferingService offeringService) {

        this.offeringService = offeringService;
    }

    /**
     * Written here just to test the API help to test api again and again with the given data
     * URL: POST http://localhost:8080/api/teachers/offerings
     * 
     * Body format:
     * {
     *     "courseId": 1,
     *     "teacherId": 101,
     *     "name": "Saturday Batch",
     *     "timezone": "Asia/Kolkata",
     *     "maxCapacity": 15
     * }
     */

    @PostMapping("/offerings")
    public ResponseEntity<OfferingResponse> createOffering(@Valid @RequestBody CreateOfferingRequest request) {
        OfferingResponse response = offeringService.createOffering(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * URL: POST http://localhost:8080/api/teachers/offerings/{offeringId}/sessions
     * 
     * Body format:
     * [
     *     {
     *         "startTime": "2026-06-06T18:00:00",
     *         "endTime": "2026-06-06T19:00:00"
     *     },
     *     {
     *         "startTime": "2026-06-13T18:00:00",
     *         "endTime": "2026-06-13T19:00:00"
     *     }
     * ]
     */

    @PostMapping("/offerings/{offeringId}/sessions")
    public ResponseEntity<List<SessionResponse>> addSessions(
            @PathVariable Long offeringId,
            @Valid @RequestBody List<SessionTimeRequest> sessionsRequest) {
        
        List<SessionResponse> responses = offeringService.addSessionsToOffering(offeringId, sessionsRequest);
        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }

    /**
     * URL: GET http://localhost:8080/api/teachers/{teacherId}/offerings
     */
    @GetMapping("/{teacherId}/offerings")
    public ResponseEntity<List<OfferingResponse>> getTeacherOfferings(
            @PathVariable Long teacherId,
            @RequestParam(required = false) String timezone) {
        
        List<OfferingResponse> responses = offeringService.getOfferingsByTeacher(teacherId, timezone);
        return ResponseEntity.ok(responses);
    }
}
