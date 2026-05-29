package com.example.booking.service;

import com.example.booking.dto.BookingResponse;
import com.example.booking.dto.BookOfferingRequest;
import com.example.booking.dto.OfferingResponse;
import com.example.booking.entity.Booking;
import com.example.booking.entity.Offering;
import com.example.booking.entity.Parent;
import com.example.booking.entity.Session;
import com.example.booking.exception.DuplicateBookingException;
import com.example.booking.exception.OfferingFullException;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.exception.TimeConflictException;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.OfferingRepository;
import com.example.booking.repository.ParentRepository;
import com.example.booking.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**

 * This is the most critical class of the project! It implements all our core Booking Rules.
 * When a parent tries to book a course batch:
 * 1. We lock the Parent and Offering rows in the database (Pessimistic Locking).
 * 2. We check if they already booked this offering.
 * 3. We check if the offering is full.
 * 4. We check if any session of the new offering overlaps with any session they already booked in the past.
 * 5. If everything passes, we increment the booking count, create a Booking record, and commit!
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ParentRepository parentRepository;
    private final OfferingRepository offeringRepository;
    private final SessionRepository sessionRepository;
    private final OfferingService offeringService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          ParentRepository parentRepository,
                          OfferingRepository offeringRepository,
                          SessionRepository sessionRepository,
                          OfferingService offeringService) {
        this.bookingRepository = bookingRepository;
        this.parentRepository = parentRepository;
        this.offeringRepository = offeringRepository;
        this.sessionRepository = sessionRepository;
        this.offeringService = offeringService;
    }

    /**
     * Books an offering for a parent.
     * We use "@Transactional" to ensure that everything inside this method happens in a single unit of work.
     * If any exception is thrown, the entire operation is cancelled (rolled back) automatically.
     */
    @Transactional
    public BookingResponse bookOffering(BookOfferingRequest request) {
        // 1. Retrieve the Parent from the database and apply a Pessimistic Write Lock on their row.
        // This ensures that if the same parent tries to book two things concurrently, one request waits for the other.
        Parent parent = parentRepository.findByIdForUpdate(request.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with ID: " + request.getParentId()));

        // 2. Retrieve the Offering and apply a Pessimistic Write Lock on it.
        // This locks the offering batch so two parents can't book the final slot at the same microsecond.
        Offering offering = offeringRepository.findByIdForUpdate(request.getOfferingId())
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found with ID: " + request.getOfferingId()));

        // 3. Rule check: Make sure parent hasn't already booked this batch!
        boolean alreadyBooked = bookingRepository.existsByParentIdAndOfferingId(parent.getId(), offering.getId());
        if (alreadyBooked) {
            throw new DuplicateBookingException("You have already booked the offering batch: '" + offering.getName() + "'!");
        }

        // 4. Rule check: Verify if the batch is already full
        if (offering.getCurrentBookingsCount() >= offering.getMaxCapacity()) {
            throw new OfferingFullException("The offering batch: '" + offering.getName() + "' is fully booked!");
        }

        // 5. Rule check: Time Conflict Locking (Rule 2)
        // Find all sessions belonging to the offering the parent wants to book.
        List<Session> newSessionsToBook = sessionRepository.findByOfferingId(offering.getId());
        if (newSessionsToBook.isEmpty()) {
            throw new ResourceNotFoundException("This offering batch has no sessions scheduled yet. Teachers must add sessions first!");
        }

        // Find all sessions this parent has already booked across all past offerings.
        List<Session> existingBookedSessions = sessionRepository.findAllSessionsBookedByParent(parent.getId());

        // Compare every new session with every already booked session to check for time overlaps
        for (Session newSession : newSessionsToBook) {
            for (Session existingSession : existingBookedSessions) {
                if (isOverlapping(newSession, existingSession)) {

                    String newSessionFormatted = formatInstant(newSession.getStartTime(), parent.getTimezone()) + 
                            " to " + formatInstant(newSession.getEndTime(), parent.getTimezone());
                    String existingSessionFormatted = formatInstant(existingSession.getStartTime(), parent.getTimezone()) + 
                            " to " + formatInstant(existingSession.getEndTime(), parent.getTimezone());

                    throw new TimeConflictException("Scheduling Conflict! Your new class session (" + newSessionFormatted + 
                            ") overlaps with an already booked class session (" + existingSessionFormatted + 
                            ") in batch: '" + existingSession.getOffering().getName() + "'.");
                }
            }
        }

        // 6. All rules passed! now we can update the offering's booking count
        offering.setCurrentBookingsCount(offering.getCurrentBookingsCount() + 1);
        offeringRepository.save(offering);

        // 7. Insert  booking record now
        Booking booking = new Booking();
        booking.setParent(parent);
        booking.setOffering(offering);
        booking.setBookedAt(Instant.now());
        
        Booking savedBooking = bookingRepository.save(booking);

        // 8. Convert to response and return (we display sessions using the parent's default timezone!)
        return convertToBookingResponse(savedBooking, parent.getTimezone());
    }

    /**
     * Retrieves all offerings booked by a parent.
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByParent(Long parentId, String targetTimezone) {
        //  parent exists
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with ID: " + parentId));

        // 2. Fetch all bookings for this parent
        List<Booking> bookings = bookingRepository.findByParentId(parentId);


        // If targetTimezone is specified, use it. Otherwise, use parent's default timezone!
        String tz = (targetTimezone != null) ? targetTimezone : parent.getTimezone();
        
        return bookings.stream()
                .map(booking -> convertToBookingResponse(booking, tz))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all registered parents in the system.
     */
    @Transactional(readOnly = true)
    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }



    /**
     * Checks if two class sessions overlap in time.
     */
    private boolean isOverlapping(Session s1, Session s2) {
        return s1.getStartTime().isBefore(s2.getEndTime()) && s1.getEndTime().isAfter(s2.getStartTime());
    }

    /**
     * Converts a Booking entity into a BookingResponse DTO.
     */
    private BookingResponse convertToBookingResponse(Booking booking, String displayTimezone) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getId());
        response.setParentId(booking.getParent().getId());
        response.setParentName(booking.getParent().getName());
        response.setParentTimezone(booking.getParent().getTimezone());
        response.setBookedAt(booking.getBookedAt());

        // Convert the offering details using the target display timezone (e.g. parent's timezone)
        OfferingResponse offeringResp = offeringService.convertToOfferingResponse(booking.getOffering(), displayTimezone);
        response.setOffering(offeringResp);

        return response;
    }

    /**
     * Formats a UTC Instant to a ZonedDateTime string in a target timezone.
     */
    private String formatInstant(Instant instant, String timezoneId) {
        ZoneId targetZone = ZoneId.of(timezoneId);
        ZonedDateTime zonedDateTime = instant.atZone(targetZone);
        return zonedDateTime.format(DATE_TIME_FORMATTER) + " (" + timezoneId + ")";
    }
}
