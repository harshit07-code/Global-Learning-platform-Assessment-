package com.example.booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;

/**
 * Booking Entity representing a booking transaction.
 * 
 * This connects a Parent to an Offering batch.
 * The constraint in database (unique_parent_offering) makes sure that a parent
 * can never book the exact same offering multiple times.
 */
@Entity
@Table(name = "bookings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"parent_id", "offering_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent; // The parent who made the booking

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "offering_id", nullable = false)
    private Offering offering; // The course batch/offering that was booked

    @Column(name = "booked_at", nullable = false)
    private Instant bookedAt = Instant.now(); // The exact moment the booking was made
}
