package com.example.booking.repository;

import com.example.booking.entity.Offering;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface OfferingRepository extends JpaRepository<Offering, Long> {

    List<Offering> findByTeacherId(Long teacherId);

    /**
     * Finds an offering by ID and locks it using a Pessimistic Write Lock.
     * This is useful when multiple parents try to book the LAST available slot in the SAME offering.
     * By locking the offering row ("FOR UPDATE"), we ensure that parent booking requests are processed
     * one-by-one. This prevents the "over-booking" bug where both requests see 9/10 seats taken,
     * both succeed, and we end up with 11 students booked!
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Offering o WHERE o.id = :id")
    Optional<Offering> findByIdForUpdate(@Param("id") Long id);
}
