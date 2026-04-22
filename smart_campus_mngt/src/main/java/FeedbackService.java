package com.project2.service;



import com.project2.model.*;
//import com.campus.events.entity.Feedback;
//import com.campus.events.entity.Student;
import com.project2.exception.ResourceNotFoundException;
import com.project2.dao.*;
//import com.campus.events.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * FeedbackService - Business logic for event feedback.
 */
@Service
@Transactional
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    /**
     * Submit feedback for an event.
     * Student must be registered for the event and must not have already submitted feedback.
     */
    public Feedback submitFeedback(String studentEmail, Long eventId, Feedback feedbackData) {
        // 1. Verify the student is registered for this event
        if (!registrationService.isStudentRegistered(studentEmail, eventId)) {
            throw new IllegalStateException("You must be registered for an event to submit feedback.");
        }

        // 2. Check for duplicate feedback
        if (feedbackRepository.existsByStudentEmailAndEventId(studentEmail, eventId)) {
            throw new IllegalStateException("You have already submitted feedback for this event.");
        }

        // 3. Look up the Student and Event
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentEmail));
        Event event = eventService.getEventById(eventId);

        // 4. Set references and save
        feedbackData.setStudent(student);
        feedbackData.setEvent(event);
        return feedbackRepository.save(feedbackData);
    }

    /**
     * Get all feedback for an event.
     */
    @Transactional(readOnly = true)
    public List<Feedback> getFeedbackByEventId(Long eventId) {
        return feedbackRepository.findByEventId(eventId);
    }

    /**
     * Get average rating for an event (returns 0.0 if no feedback).
     */
    @Transactional(readOnly = true)
    public double getAverageRating(Long eventId) {
        Double avg = feedbackRepository.getAverageRatingByEventId(eventId);
        return avg != null ? avg : 0.0;
    }

    /**
     * Check if a student has already submitted feedback.
     */
    @Transactional(readOnly = true)
    public boolean hasSubmittedFeedback(String email, Long eventId) {
        return feedbackRepository.existsByStudentEmailAndEventId(email, eventId);
    }
}
