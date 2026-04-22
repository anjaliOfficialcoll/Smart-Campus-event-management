package com.project2.service;



import com.project2.model.Event;
import com.project2.model.Registration;
import com.project2.model.Student;
import com.project2.exception.DuplicateRegistrationException;
import com.project2.exception.ResourceNotFoundException;
import com.project2.dao.RegistrationRepository;
import com.project2.dao.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * RegistrationService - Business logic for event registrations.
 */
@Service
@Transactional
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EventService eventService;

    /**
     * Register a student for an event.
     * - If the student's email already exists, reuses that Student record.
     * - Throws DuplicateRegistrationException if already registered.
     */
    public Registration registerStudentForEvent(Student studentInfo, Long eventId) {
        // 1. Get or create the Student record
        Student student = studentRepository.findByEmail(studentInfo.getEmail())
                .orElseGet(() -> studentRepository.save(studentInfo));

        // 2. Get the Event
        Event event = eventService.getEventById(eventId);

        // 3. Check for duplicate registration
        if (registrationRepository.existsByStudentEmailAndEventId(student.getEmail(), eventId)) {
            throw new DuplicateRegistrationException(
                "You are already registered for the event: " + event.getName());
        }

        // 4. Create and save the Registration
        Registration registration = new Registration();
        registration.setStudent(student);
        registration.setEvent(event);
        return registrationRepository.save(registration);
    }

    /**
     * Get all events a student has registered for (by email).
     */
    @Transactional(readOnly = true)
    public List<Registration> getRegistrationsByStudentEmail(String email) {
        return registrationRepository.findByStudentEmailWithEvent(email);
    }

    /**
     * Check if a student is registered for an event.
     */
    @Transactional(readOnly = true)
    public boolean isStudentRegistered(String email, Long eventId) {
        return registrationRepository.existsByStudentEmailAndEventId(email, eventId);
    }

    /**
     * Cancel a registration.
     */
    public void cancelRegistration(Long registrationId) {
        Registration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + registrationId));
        registrationRepository.delete(reg);
    }

    /**
     * Get total registrations count for admin stats.
     */
    @Transactional(readOnly = true)
    public long getTotalRegistrationCount() {
        return registrationRepository.count();
    }
}
