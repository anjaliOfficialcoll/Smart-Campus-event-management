package com.project2.controller;


import com.project2.model.*;

import com.project2.service.EventService;
//import com.project2.service.FeedbackService;
import com.project2.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EventApiController - REST API endpoints.
 * These return JSON data (not HTML pages) and can be used
 * for AJAX calls or by external clients.
 *
 * All responses are wrapped in ResponseEntity for proper HTTP status codes.
 */
@RestController
@RequestMapping("/api")
public class EventApiController {

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    /**
     * GET /api/events
     * Returns all upcoming events as JSON.
     */
    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAllUpcomingEvents();
        return ResponseEntity.ok(events);
    }

    /**
     * GET /api/events/{id}
     * Returns a specific event as JSON.
     */
    @GetMapping("/events/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    /**
     * GET /api/events/{id}/registrations/count
     * Returns the number of registrations for an event.
     */
    @GetMapping("/events/{id}/registrations/count")
    public ResponseEntity<Map<String, Object>> getRegistrationCount(@PathVariable Long id) {
        long count = registrationService.getTotalRegistrationCount();
        Map<String, Object> response = new HashMap<>();
        response.put("eventId", id);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/stats
     * Returns overall system statistics as JSON.
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEvents", eventService.getTotalEventCount());
        stats.put("totalRegistrations", registrationService.getTotalRegistrationCount());
        stats.put("upcomingEvents", eventService.getAllUpcomingEvents().size());
        return ResponseEntity.ok(stats);
    }
}
