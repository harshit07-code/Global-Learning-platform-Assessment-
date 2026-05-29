package com.example.booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "offerings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Offering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many offerings belongs to the same course.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String timezone;

    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity = 10;

    @Column(name = "current_bookings_count", nullable = false)
    private int currentBookingsCount = 0;


    // "cascade = CascadeType.ALL" means if we delete an Offering, all its sessions are deleted too.
    @OneToMany(mappedBy = "offering", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Session> sessions = new ArrayList<>();
}
