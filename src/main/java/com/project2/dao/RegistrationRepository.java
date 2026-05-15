package com.project2.dao;



import com.project2.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * RegistrationRepository - Handles database operations for the Registration entity.
 */
@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
 
    /**
     * Find all registrations for a specific student (by email).
     * Uses LEFT JOIN so it safely returns empty list if student not found.
     */
    @Query("SELECT r FROM Registration r " +
           "JOIN FETCH r.event e " +
           "JOIN FETCH r.student s " +
           "WHERE LOWER(r.student.email) = LOWER(:email) " +
           "ORDER BY r.registeredAt DESC")
    List<Registration> findByStudentEmailWithEvent(@Param("email") String email);
 
    /**
     * Find ALL registrations with student and event eagerly loaded (for admin view).
     */
    @Query("SELECT r FROM Registration r " +
           "LEFT JOIN FETCH r.event e " +
           "LEFT JOIN FETCH r.student s " +
           "ORDER BY r.registeredAt DESC")
    List<Registration> findAllWithStudentAndEvent();
 
    /**
     * Find all registrations for a specific event, with student details loaded.
     */
    @Query("SELECT r FROM Registration r " +
           "LEFT JOIN FETCH r.student s " +
           "WHERE r.event.id = :eventId " +
           "ORDER BY r.registeredAt DESC")
    List<Registration> findByEventIdWithStudent(@Param("eventId") Long eventId);
 
    /** Find all registrations for a specific event (basic) */
    List<Registration> findByEventId(Long eventId);
 
    /** Check if a student (by email) is already registered for an event */
    @Query("SELECT COUNT(r) > 0 FROM Registration r " +
           "WHERE LOWER(r.student.email) = LOWER(:email) AND r.event.id = :eventId")
    boolean existsByStudentEmailAndEventId(
            @Param("email") String email,
            @Param("eventId") Long eventId);
 
    /** Find a specific registration by student email and event ID */
    @Query("SELECT r FROM Registration r " +
           "WHERE LOWER(r.student.email) = LOWER(:email) AND r.event.id = :eventId")
    Optional<Registration> findByStudentEmailAndEventId(
            @Param("email") String email,
            @Param("eventId") Long eventId);
 
    /** Count total registrations for an event */
    long countByEventId(Long eventId);
 
    /** Count total registrations across all events */
    long count();
}
 