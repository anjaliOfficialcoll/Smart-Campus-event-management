package com.project2.controller;
import com.project2.model.*;

import com.project2.service.EventService;
import com.project2.service.FeedbackService;
import com.project2.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Map;

/**
 * AdminController - Handles all admin-facing pages.
 * All routes under /admin/** are protected by Spring Security.
 *
 * URL Mappings:
 *   GET  /admin/login          → Login page
 *   GET  /admin/dashboard      → Admin dashboard with stats
 *   GET  /admin/events         → List all events (with search/filter)
 *   GET  /admin/events/new     → Add new event form
 *   POST /admin/events/new     → Save new event
 *   GET  /admin/events/{id}/edit  → Edit event form
 *   POST /admin/events/{id}/edit  → Update event
 *   POST /admin/events/{id}/delete → Delete event
 *   GET  /admin/statistics     → Registration statistics
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    // ── Login ──────────────────────────────────────────────────────────────────

    /** Spring Security handles the actual authentication. This just shows the page. */
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login"; // → templates/admin/login.html
    }

    // ── Dashboard ──────────────────────────────────────────────────────────────

    /**
     * Admin dashboard showing summary statistics.
     */
    @GetMapping({"/dashboard", "/"})
    public String dashboard(Model model) {
        long totalEvents = eventService.getTotalEventCount();
        long totalRegistrations = registrationService.getTotalRegistrationCount();
        long upcomingCount = eventService.getAllUpcomingEvents().size();

        model.addAttribute("totalEvents", totalEvents);
        model.addAttribute("totalRegistrations", totalRegistrations);
        model.addAttribute("upcomingCount", upcomingCount);
        model.addAttribute("recentEvents", eventService.getAllUpcomingEvents()
                .stream().limit(5).toList());
        model.addAttribute("pageTitle", "Admin Dashboard");

        return "admin/dashboard"; // → templates/admin/dashboard.html
    }

    // ── Event List (with Search/Filter) ───────────────────────────────────────

    /**
     * List all events with optional search/filter parameters.
     *
     * @param department  filter by department (optional)
     * @param eventType   filter by type: WORKSHOP, SEMINAR, etc. (optional)
     * @param date        filter by specific date (optional)
     * @param page        pagination page number
     */
    @GetMapping("/events")
    public String listEvents(Model model,
                             @RequestParam(required = false) String department,
                             @RequestParam(required = false) String eventType,
                             @RequestParam(required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "8") int size) {

        Page<Event> eventPage;

        // If any filter is active, use search; otherwise get all events
        boolean hasFilter = (department != null && !department.isBlank())
                || (eventType != null && !eventType.isBlank())
                || date != null;

        if (hasFilter) {
            eventPage = eventService.searchEvents(department, eventType, date, PageRequest.of(page, size));
        } else {
            eventPage = eventService.getAllEvents(PageRequest.of(page, size));
        }

        model.addAttribute("events", eventPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", eventPage.getTotalPages());
        model.addAttribute("totalItems", eventPage.getTotalElements());

        // Pass filter values back to the view so they persist in the form
        model.addAttribute("filterDepartment", department);
        model.addAttribute("filterEventType", eventType);
        model.addAttribute("filterDate", date);

        // Dropdown options
        model.addAttribute("departments", eventService.getAllDepartments());
        model.addAttribute("eventTypes", eventService.getAllEventTypes());
        model.addAttribute("pageTitle", "Manage Events");

        return "admin/events"; // → templates/admin/events.html
    }

    // ── Add New Event ──────────────────────────────────────────────────────────

    /** Show the "Add New Event" form */
    @GetMapping("/events/new")
    public String showAddEventForm(Model model) {
        model.addAttribute("event", new Event()); // empty Event for form binding
        model.addAttribute("pageTitle", "Add New Event");
        model.addAttribute("formAction", "/admin/events/new");
        model.addAttribute("isEditMode", false);
        return "admin/event-form"; // → templates/admin/event-form.html
    }

    /** Process the "Add New Event" form */
    @PostMapping("/events/new")
    public String saveNewEvent(@Valid @ModelAttribute("event") Event event,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Add New Event");
            model.addAttribute("formAction", "/admin/events/new");
            model.addAttribute("isEditMode", false);
            return "admin/event-form";
        }

        eventService.saveEvent(event);
        redirectAttributes.addFlashAttribute("successMessage",
            "✅ Event '" + event.getName() + "' created successfully!");
        return "redirect:/admin/events";
    }

    // ── Edit Event ─────────────────────────────────────────────────────────────

    /** Show the "Edit Event" form pre-filled with existing data */
    @GetMapping("/events/{id}/edit")
    public String showEditEventForm(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);

        model.addAttribute("event", event);
        model.addAttribute("pageTitle", "Edit Event");
        model.addAttribute("formAction", "/admin/events/" + id + "/edit");
        model.addAttribute("isEditMode", true);

        return "admin/event-form";
    }

    /** Process the "Edit Event" form */
    @PostMapping("/events/{id}/edit")
    public String updateEvent(@PathVariable Long id,
                              @Valid @ModelAttribute("event") Event event,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Event");
            model.addAttribute("formAction", "/admin/events/" + id + "/edit");
            model.addAttribute("isEditMode", true);
            return "admin/event-form";
        }

        eventService.updateEvent(id, event);
        redirectAttributes.addFlashAttribute("successMessage",
            "✅ Event updated successfully!");
        return "redirect:/admin/events";
    }

    // ── Delete Event ───────────────────────────────────────────────────────────

    /** Delete an event and all its registrations/feedbacks */
    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Event event = eventService.getEventById(id);
        eventService.deleteEvent(id);
        redirectAttributes.addFlashAttribute("successMessage",
            "🗑️ Event '" + event.getName() + "' has been deleted.");
        return "redirect:/admin/events";
    }

    // ── Statistics ─────────────────────────────────────────────────────────────

    /** Show registration statistics per event */
    @GetMapping("/statistics")
    public String statistics(Model model) {
        Map<String, Long> stats = eventService.getEventRegistrationStats();

        model.addAttribute("stats", stats);
        model.addAttribute("totalRegistrations", registrationService.getTotalRegistrationCount());
        model.addAttribute("totalEvents", eventService.getTotalEventCount());
        model.addAttribute("pageTitle", "Registration Statistics");

        return "admin/statistics"; // → templates/admin/statistics.html
    }
}
