package com.example.booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;


@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many sessions belong to one offering batch.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id", nullable = false)
    private Offering offering; // The parent offering batch this session belongs to

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId; // The ID of the teacher conducting the class

    @Column(name = "start_time", nullable = false)
    private Instant startTime; // Date and time when the class starts (stored in UTC)

    @Column(name = "end_time", nullable = false)
    private Instant endTime; // Date and time when the class ends (stored in UTC)
}
