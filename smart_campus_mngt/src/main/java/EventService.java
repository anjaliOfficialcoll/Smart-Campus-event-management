package com.project2.service;



import com.project2.model.Event;
import com.project2.exception.ResourceNotFoundException;
import com.project2.dao.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * EventService - Business logic layer for Event operations.
 * Controllers call Service → Service calls Repository.
 */
@Service
@Transactional
public class EventService {

    // Spring auto-injects the EventRepository implementation
    @Autowired
    private EventRepository eventRepository;

    // ── Read Operations ────────────────────────────────────────────────────────

    /**
     * Get all upcoming events with pagination.
     * Used on the student homepage.
     */
    @Transactional(readOnly = true)
    public Page<Event> getUpcomingEvents(Pageable pageable) {
        return eventRepository.findByDateGreaterThanEqualOrderByDateAsc(LocalDate.now(), pageable);
    }

    /**
     * Get all upcoming events (no pagination).
     */
    @Transactional(readOnly = true)
    public List<Event> getAllUpcomingEvents() {
        return eventRepository.findByDateGreaterThanEqualOrderByDateAsc(LocalDate.now());
    }

    /**
     * Get all events (for admin — includes past events).
     */
    @Transactional(readOnly = true)
    public Page<Event> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    /**
     * Get a specific event by ID.
     * Throws ResourceNotFoundException if not found.
     */
    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));
    }

    /**
     * Search/filter events (for Admin Dashboard).
     */
    @Transactional(readOnly = true)
    public Page<Event> searchEvents(String department, String eventType, LocalDate date, Pageable pageable) {
        return eventRepository.searchEvents(department, eventType, date, pageable);
    }

    // ── Write Operations ───────────────────────────────────────────────────────

    /**
     * Save a new event to the database.
     */
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    /**
     * Update an existing event.
     * Throws ResourceNotFoundException if the event doesn't exist.
     */
    public Event updateEvent(Long id, Event updatedEvent) {
        Event existing = getEventById(id);    // validates it exists
        updatedEvent.setId(existing.getId()); // preserve the same ID
        return eventRepository.save(updatedEvent);
    }

    /**
     * Delete an event by ID.
     * Registrations and feedbacks are removed automatically (CascadeType.ALL).
     */
    public void deleteEvent(Long id) {
        Event event = getEventById(id); // validates it exists
        eventRepository.delete(event);
    }

    // ── Statistics ─────────────────────────────────────────────────────────────

    /**
     * Get registration statistics for all events.
     * Returns a map: { eventName -> registrationCount }
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getEventRegistrationStats() {
        List<Object[]> results = eventRepository.findEventRegistrationStats();
        Map<String, Long> stats = new LinkedHashMap<>();
        for (Object[] row : results) {
            String eventName = (String) row[1];
            Long count = (Long) row[2];
            stats.put(eventName, count);
        }
        return stats;
    }

    /**
     * Get total number of events.
     */
    @Transactional(readOnly = true)
    public long getTotalEventCount() {
        return eventRepository.count();
    }

    // ── Filter Dropdown Data ───────────────────────────────────────────────────

    /** Get all unique departments for filter dropdowns */
    @Transactional(readOnly = true)
    public List<String> getAllDepartments() {
        return eventRepository.findDistinctDepartments();
    }

    /** Get all unique event types for filter dropdowns */
    @Transactional(readOnly = true)
    public List<String> getAllEventTypes() {
        return eventRepository.findDistinctEventTypes();
    }
}
