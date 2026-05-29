package com.example.booking.repository;

import com.example.booking.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for database queries on the Session entity.
 */
@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    /**
     * Find all sessions belonging to a specific offering batch.
     */
    List<Session> findByOfferingId(Long offeringId);

    /**
     * Finds all sessions that have already been booked by a parent.
     * This query is super important for our Time Conflict checks.
     * We join the Session, its Offering, and the Parent's Booking together.
     * It basically says: "Give me all class times (sessions) for all the courses (offerings)
     * that this parent has successfully booked."
     * We will use this list to make sure no new classes overlap with these times.
     */
    @Query("SELECT s FROM Session s " +
           "JOIN s.offering o " +
           "JOIN Booking b ON b.offering.id = o.id " +
           "WHERE b.parent.id = :parentId")
    List<Session> findAllSessionsBookedByParent(@Param("parentId") Long parentId);
}
