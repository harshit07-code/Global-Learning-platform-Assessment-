package com.example.booking.repository;

import com.example.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for database queries on the Booking entity.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByParentId(Long parentId);

    /**
     * Checks if a parent has already booked a specific offering.
     * Maps to SQL: "SELECT COUNT(*) FROM bookings WHERE parent_id = ? AND offering_id = ?"
     */
    boolean existsByParentIdAndOfferingId(Long parentId, Long offeringId);
}
