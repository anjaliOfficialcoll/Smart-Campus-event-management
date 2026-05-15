package com.project2.service;



import com.project2.model.Event;
import com.project2.model.Registration;
import com.project2.model.Student;
import com.project2.exception.DuplicateRegistrationException;
import com.project2.exception.ResourceNotFoundException;
import com.project2.dao.RegistrationRepository;
import com.project2.dao.StudentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;

/**
 * RegistrationService - Business logic for event registrations.
 */
@Service
@Transactional
public class RegistrationService {
 
    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);
 
    @Autowired private RegistrationRepository registrationRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private EventService eventService;
    @Autowired private EmailService emailService;
 
    public Registration registerStudentForEvent(Student studentInfo, Long eventId) {
        Student student = studentRepository.findByEmail(studentInfo.getEmail())
                .orElseGet(() -> studentRepository.save(studentInfo));
        Event event = eventService.getEventById(eventId);
        if (registrationRepository.existsByStudentEmailAndEventId(student.getEmail(), eventId)) {
            throw new DuplicateRegistrationException("You are already registered for: " + event.getName());
        }
        Registration registration = new Registration();
        registration.setStudent(student);
        registration.setEvent(event);
        Registration saved = registrationRepository.save(registration);
        try {
            emailService.sendRegistrationConfirmationEmail(saved);
        } catch (Exception e) {
            log.warn("Email trigger failed for registration ID={}: {}", saved.getId(), e.getMessage());
        }
        return saved;
    }
 
    @Transactional(readOnly = true)
    public List<Registration> getRegistrationsByStudentEmail(String email) {
        if (email == null || email.isBlank()) return Collections.emptyList();
        try { return registrationRepository.findByStudentEmailWithEvent(email.trim()); }
        catch (Exception e) { return Collections.emptyList(); }
    }
 
    @Transactional(readOnly = true)
    public List<Registration> getRegistrationsByEventId(Long eventId) {
        try { return registrationRepository.findByEventIdWithStudent(eventId); }
        catch (Exception e) { return Collections.emptyList(); }
    }
 
    @Transactional(readOnly = true)
    public List<Registration> getAllRegistrations() {
        try { return registrationRepository.findAllWithStudentAndEvent(); }
        catch (Exception e) { return Collections.emptyList(); }
    }
 
    @Transactional(readOnly = true)
    public boolean isStudentRegistered(String email, Long eventId) {
        if (email == null || email.isBlank()) return false;
        return registrationRepository.existsByStudentEmailAndEventId(email, eventId);
    }
 
    public void cancelRegistration(Long registrationId) {
        Registration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + registrationId));
        registrationRepository.delete(reg);
    }
 
    @Transactional(readOnly = true)
    public long getTotalRegistrationCount() { return registrationRepository.count(); }
}