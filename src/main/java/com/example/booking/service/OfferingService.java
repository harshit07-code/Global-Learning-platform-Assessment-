package com.example.booking.service;

import com.example.booking.dto.CreateOfferingRequest;
import com.example.booking.dto.OfferingResponse;
import com.example.booking.dto.SessionResponse;
import com.example.booking.dto.SessionTimeRequest;
import com.example.booking.entity.Course;
import com.example.booking.entity.Offering;
import com.example.booking.entity.Session;
import com.example.booking.exception.InvalidRequestException;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.repository.CourseRepository;
import com.example.booking.repository.OfferingRepository;
import com.example.booking.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class OfferingService {

    private final OfferingRepository offeringRepository;
    private final CourseRepository courseRepository;
    private final SessionRepository sessionRepository;

    // DateTimeFormatter is used to convert ZonedDateTime objects into simple readable strings like "2026-06-06 18:00"
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    public OfferingService(OfferingRepository offeringRepository,
                           CourseRepository courseRepository,
                           SessionRepository sessionRepository) {
        this.offeringRepository = offeringRepository;
        this.courseRepository = courseRepository;
        this.sessionRepository = sessionRepository;
    }

    /**
     * Creates a new offering batch for a course.
     */
    @Transactional
    public OfferingResponse createOffering(CreateOfferingRequest request) {
        // 1. Verify that the course template actually exists in the database
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + request.getCourseId()));

        // 2. Validate the timezone provided (e.g. "Asia/Kolkata")
        validateTimezone(request.getTimezone());

        // 3. Create the Offering entity
        Offering offering = new Offering();
        offering.setCourse(course);
        offering.setTeacherId(request.getTeacherId());
        offering.setName(request.getName());
        offering.setTimezone(request.getTimezone());
        offering.setMaxCapacity(request.getMaxCapacity());
        offering.setCurrentBookingsCount(0); // Starts with 0 bookings

        // 4. Save to the database
        Offering savedOffering = offeringRepository.save(offering);

        // 5. Convert to response and return (we use the offering's own timezone for display here)
        return convertToOfferingResponse(savedOffering, savedOffering.getTimezone());
    }

    /**
     * Adds sessions (class timings) to an existing offering batch.
     */
    @Transactional
    public List<SessionResponse> addSessionsToOffering(Long offeringId, List<SessionTimeRequest> sessionRequests) {
        // 1. Retrieving  offering from database

        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found with ID: " + offeringId));

        ZoneId offeringZone = ZoneId.of(offering.getTimezone());
        List<Session> newSessions = new ArrayList<>();

        // 2. Loop through each session request and validate it
        for (SessionTimeRequest sessionReq : sessionRequests) {
            try {
                // Parse strings like "2026-06-06T18:00:00" into Java LocalDateTime
                LocalDateTime startLocal = LocalDateTime.parse(sessionReq.getStartTime());
                LocalDateTime endLocal = LocalDateTime.parse(sessionReq.getEndTime());

                // Validation: A session must start before it ends!
                if (!startLocal.isBefore(endLocal)) {
                    throw new InvalidRequestException("Session start time (" + startLocal + 
                            ") must be before end time (" + endLocal + ")");
                }

                // Convert the local times in teacher's timezone to UTC Instant
                Instant startInstant = startLocal.atZone(offeringZone).toInstant();
                Instant endInstant = endLocal.atZone(offeringZone).toInstant();

                // Build Session Entity
                Session session = new Session();
                session.setOffering(offering);
                session.setTeacherId(offering.getTeacherId());
                session.setStartTime(startInstant);
                session.setEndTime(endInstant);

                newSessions.add(session);
            } catch (DateTimeParseException e) {
                throw new InvalidRequestException("Invalid date-time format. Please use ISO-8601 format like: 'YYYY-MM-DDTHH:MM:SS' (e.g. '2026-06-06T18:00:00')");
            }
        }

        // 3. Save all new sessions in database
        List<Session> savedSessions = sessionRepository.saveAll(newSessions);

        // 4. Return the saved sessions converted to the offering's local timezone
        return savedSessions.stream()
                .map(session -> convertToSessionResponse(session, offering.getTimezone()))
                .collect(Collectors.toList());
    }

    /**
     * Get all offerings created by a specific teacher.
     * We convert the sessions display timezone to whatever timezone the teacher requests.
     */
    @Transactional(readOnly = true)
    public List<OfferingResponse> getOfferingsByTeacher(Long teacherId, String targetTimezone) {
        List<Offering> offerings = offeringRepository.findByTeacherId(teacherId);
        
        // If teacher doesn't specify a timezone query param, default to using their own timezone
        return offerings.stream()
                .map(offering -> {
                    String tz = (targetTimezone != null) ? targetTimezone : offering.getTimezone();
                    validateTimezone(tz);
                    return convertToOfferingResponse(offering, tz);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get all available offerings for parents to browse.
     */
    @Transactional(readOnly = true)
    public List<OfferingResponse> getAllAvailableOfferings(String targetTimezone) {
        List<Offering> offerings = offeringRepository.findAll();
        
        return offerings.stream()
                .map(offering -> {
                    // Convert timings using targetTimezone (e.g. parent's local timezone)
                    // If no timezone is passed, default to the offering's native timezone.
                    String tz = (targetTimezone != null) ? targetTimezone : offering.getTimezone();
                    validateTimezone(tz);
                    return convertToOfferingResponse(offering, tz);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get all courses templates available in the system database.
     */
    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // ==========================================
    // Helper Conversion Methods
    // ==========================================

    /**
     * Helper method to convert an Offering entity to an OfferingResponse DTO.
     */
    public OfferingResponse convertToOfferingResponse(Offering offering, String displayTimezone) {
        OfferingResponse response = new OfferingResponse();
        response.setId(offering.getId());
        response.setCourseId(offering.getCourse().getId());
        response.setCourseTitle(offering.getCourse().getTitle());
        response.setCourseDescription(offering.getCourse().getDescription());
        response.setTeacherId(offering.getTeacherId());
        response.setName(offering.getName());
        response.setTimezone(offering.getTimezone());
        response.setMaxCapacity(offering.getMaxCapacity());
        response.setCurrentBookingsCount(offering.getCurrentBookingsCount());
        response.setFull(offering.getCurrentBookingsCount() >= offering.getMaxCapacity());

        // Map and convert sessions to the display timezone
        if (offering.getSessions() != null) {
            List<SessionResponse> sessionResponses = offering.getSessions().stream()
                    .map(session -> convertToSessionResponse(session, displayTimezone))
                    .collect(Collectors.toList());
            response.setSessions(sessionResponses);
        } else {
            response.setSessions(new ArrayList<>());
        }

        return response;
    }

    /**
     * Helper method to convert a Session entity to a SessionResponse DTO.
     * Maps the UTC start and end time instants into human-readable strings in the target local timezone.
     */
    public SessionResponse convertToSessionResponse(Session session, String displayTimezone) {
        SessionResponse response = new SessionResponse();
        response.setId(session.getId());
        response.setOfferingId(session.getOffering().getId());
        response.setTeacherId(session.getTeacherId());
        response.setStartTimeUtc(session.getStartTime());
        response.setEndTimeUtc(session.getEndTime());

        // Timezone calculation: Convert the UTC Instant into the local timezone
        ZoneId targetZone = ZoneId.of(displayTimezone);
        ZonedDateTime localStart = session.getStartTime().atZone(targetZone);
        ZonedDateTime localEnd = session.getEndTime().atZone(targetZone);

        // Format to a readable string like "2026-06-06 18:00"
        response.setStartTimeLocal(localStart.format(DATE_TIME_FORMATTER));
        response.setEndTimeLocal(localEnd.format(DATE_TIME_FORMATTER));
        response.setTimezoneUsed(displayTimezone);

        return response;
    }

    /**
     * Helper method to validate if a string is a valid timezone identifier.
     */
    private void validateTimezone(String timezoneId) {
        try {
            ZoneId.of(timezoneId);
        } catch (Exception e) {
            throw new InvalidRequestException("Invalid timezone identifier: '" + timezoneId + 
                    "'. Please use valid identifiers like 'Asia/Kolkata', 'America/New_York', or 'UTC'.");
        }
    }
}
