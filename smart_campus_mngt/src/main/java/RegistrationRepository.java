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

    /** Find all registrations for a specific student (by email) */
    @Query("SELECT r FROM Registration r JOIN FETCH r.event WHERE r.student.email = :email ORDER BY r.registeredAt DESC")
    List<Registration> findByStudentEmailWithEvent(@Param("email") String email);

    /** Find all registrations for a specific event */
    List<Registration> findByEventId(Long eventId);

    /** Check if a student (by email) is already registered for an event */
    @Query("SELECT COUNT(r) > 0 FROM Registration r WHERE r.student.email = :email AND r.event.id = :eventId")
    boolean existsByStudentEmailAndEventId(@Param("email") String email, @Param("eventId") Long eventId);

    /** Find a specific registration by student email and event ID */
    @Query("SELECT r FROM Registration r WHERE r.student.email = :email AND r.event.id = :eventId")
    Optional<Registration> findByStudentEmailAndEventId(@Param("email") String email, @Param("eventId") Long eventId);

    /** Count total registrations for an event */
    long countByEventId(Long eventId);
}
