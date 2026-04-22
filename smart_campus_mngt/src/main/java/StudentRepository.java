package com.project2.dao;



import com.project2.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * StudentRepository - Handles database operations for the Student entity.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /** Find a student by email (used to check if already registered) */
    Optional<Student> findByEmail(String email);

    /** Find a student by their student ID */
    Optional<Student> findByStudentId(String studentId);

    /** Check if an email already exists in the database */
    boolean existsByEmail(String email);

    /** Check if a student ID already exists */
    boolean existsByStudentId(String studentId);
}
