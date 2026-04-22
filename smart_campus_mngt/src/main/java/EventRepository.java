package com.project2.dao;



import com.project2.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * EventRepository - Handles all database operations for Event entity.
 * Spring Data JPA auto-generates implementations for these methods.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // ── Basic Queries ──────────────────────────────────────────────────────────

    /** Find all upcoming events (date >= today), ordered by date ascending */
    Page<Event> findByDateGreaterThanEqualOrderByDateAsc(LocalDate date, Pageable pageable);

    /** Find all upcoming events as a list (no pagination) */
    List<Event> findByDateGreaterThanEqualOrderByDateAsc(LocalDate date);

    // ── Filter Queries (for Admin Dashboard) ──────────────────────────────────

    /** Filter events by department */
    Page<Event> findByDepartmentContainingIgnoreCase(String department, Pageable pageable);

    /** Filter events by event type */
    Page<Event> findByEventTypeIgnoreCase(String eventType, Pageable pageable);

    /** Filter events by exact date */
    Page<Event> findByDate(LocalDate date, Pageable pageable);

    /** Filter events by department AND event type */
    Page<Event> findByDepartmentContainingIgnoreCaseAndEventTypeIgnoreCase(
            String department, String eventType, Pageable pageable);

    // ── Combined Filter Query ─────────────────────────────────────────────────

    /**
     * Search/filter events with optional parameters.
     * Uses JPQL (Java Persistence Query Language).
     * Parameters are optional — if null or empty, that filter is skipped.
     */
    @Query("SELECT e FROM Event e WHERE " +
           "(:department IS NULL OR :department = '' OR LOWER(e.department) LIKE LOWER(CONCAT('%', :department, '%'))) AND " +
           "(:eventType IS NULL OR :eventType = '' OR LOWER(e.eventType) = LOWER(:eventType)) AND " +
           "(:date IS NULL OR e.date = :date) " +
           "ORDER BY e.date ASC")
    Page<Event> searchEvents(
            @Param("department") String department,
            @Param("eventType") String eventType,
            @Param("date") LocalDate date,
            Pageable pageable);

    // ── Statistics Queries ─────────────────────────────────────────────────────

    /**
     * Count total registrations per event — used for Admin statistics.
     * Returns a list of Object arrays: [eventId, eventName, count]
     */
    @Query("SELECT e.id, e.name, COUNT(r.id) FROM Event e LEFT JOIN e.registrations r GROUP BY e.id, e.name ORDER BY COUNT(r.id) DESC")
    List<Object[]> findEventRegistrationStats();

    /** Get all distinct departments for the filter dropdown */
    @Query("SELECT DISTINCT e.department FROM Event e ORDER BY e.department ASC")
    List<String> findDistinctDepartments();

    /** Get all distinct event types for the filter dropdown */
    @Query("SELECT DISTINCT e.eventType FROM Event e ORDER BY e.eventType ASC")
    List<String> findDistinctEventTypes();
}
