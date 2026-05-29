package com.example.booking.repository;

import com.example.booking.entity.Parent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {

    /**
     * Using "@Lock(LockModeType.PESSIMISTIC_WRITE)" compiles into SQL: "SELECT ... FROM parents WHERE id = ? FOR UPDATE".
     * This locks the row in MySQL. The second request has to wait until the first one is completely done.
     * This prevents double booking and race conditions!
     */

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Parent p WHERE p.id = :id")
    Optional<Parent> findByIdForUpdate(@Param("id") Long id);
}
