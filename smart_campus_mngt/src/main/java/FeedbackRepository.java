package com.project2.dao;



import com.project2.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * FeedbackRepository - Handles database operations for the Feedback entity.
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    /** Find all feedbacks for a specific event */
    List<Feedback> findByEventId(Long eventId);

    /** Find all feedbacks submitted by a student */
    List<Feedback> findByStudentEmail(String email);

    /** Check if a student has already submitted feedback for an event */
    @Query("SELECT COUNT(f) > 0 FROM Feedback f WHERE f.student.email = :email AND f.event.id = :eventId")
    boolean existsByStudentEmailAndEventId(@Param("email") String email, @Param("eventId") Long eventId);

    /** Get average rating for an event */
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.event.id = :eventId")
    Double getAverageRatingByEventId(@Param("eventId") Long eventId);
}
