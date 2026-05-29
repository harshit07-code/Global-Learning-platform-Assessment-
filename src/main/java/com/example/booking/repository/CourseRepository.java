package com.example.booking.repository;

import com.example.booking.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * CRUD operations (save, findById, findAll, delete) for us!
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}
