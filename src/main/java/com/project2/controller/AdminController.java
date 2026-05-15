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
import java.util.List;
import java.util.Map;

/**
 * AdminController - All admin-facing pages.
 * All /admin/** routes are protected by Spring Security.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
 
    @Autowired
    private EventService eventService;
 
    @Autowired
    private RegistrationService registrationService;
 
    // ── Login ──────────────────────────────────────────────────────────────────
 
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }
 
    // ── Dashboard ──────────────────────────────────────────────────────────────
 
    @GetMapping({"/dashboard", "/"})
    public String dashboard(Model model) {
        long totalEvents        = eventService.getTotalEventCount();
        long totalRegistrations = registrationService.getTotalRegistrationCount();
        long upcomingCount      = eventService.getAllUpcomingEvents().size();
 
        model.addAttribute("totalEvents",        totalEvents);
        model.addAttribute("totalRegistrations", totalRegistrations);
        model.addAttribute("upcomingCount",      upcomingCount);
        model.addAttribute("recentEvents",
            eventService.getAllUpcomingEvents().stream().limit(5).toList());
        model.addAttribute("pageTitle", "Admin Dashboard");
        return "admin/dashboard";
    }
 
    // ── Event List (with Search / Filter) ─────────────────────────────────────
 
    @GetMapping("/events")
    public String listEvents(
            Model model,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "8")  int size) {
 
        boolean hasFilter = (department != null && !department.isBlank())
                || (eventType != null && !eventType.isBlank())
                || date != null;
 
        Page<Event> eventPage = hasFilter
                ? eventService.searchEvents(department, eventType, date,
                                            PageRequest.of(page, size))
                : eventService.getAllEvents(PageRequest.of(page, size));
 
        model.addAttribute("events",          eventPage.getContent());
        model.addAttribute("currentPage",     page);
        model.addAttribute("totalPages",      eventPage.getTotalPages());
        model.addAttribute("totalItems",      eventPage.getTotalElements());
        model.addAttribute("filterDepartment",department);
        model.addAttribute("filterEventType", eventType);
        model.addAttribute("filterDate",      date);
        model.addAttribute("departments",     eventService.getAllDepartments());
        model.addAttribute("eventTypes",      eventService.getAllEventTypes());
        model.addAttribute("pageTitle",       "Manage Events");
        return "admin/events";
    }
 
    // ── NEW: View Registered Students for a specific event ────────────────────
 
    /**
     * GET /admin/events/{id}/registrations
     * Shows a table of all students registered for the given event.
     */
    @GetMapping("/events/{id}/registrations")
    public String viewEventRegistrations(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        List<Registration> registrations =
                registrationService.getRegistrationsByEventId(id);
 
        model.addAttribute("event",         event);
        model.addAttribute("registrations", registrations);
        model.addAttribute("pageTitle",     "Registrations — " + event.getName());
        return "admin/event-registrations";   // → new template
    }
 
    // ── NEW: View ALL registered students across all events ───────────────────
 
    /**
     * GET /admin/registrations
     * Shows every registration in the system with student + event info.
     */
    @GetMapping("/registrations")
    public String viewAllRegistrations(Model model) {
        List<Registration> all = registrationService.getAllRegistrations();
        model.addAttribute("registrations", all);
        model.addAttribute("totalCount",    all.size());
        model.addAttribute("pageTitle",     "All Student Registrations");
        return "admin/all-registrations";    // → new template
    }
 
    // ── NEW: Cancel / delete a registration (admin action) ───────────────────
 
    /**
     * POST /admin/registrations/{id}/cancel
     * Admin can cancel any registration.
     */
    @PostMapping("/registrations/{id}/cancel")
    public String cancelRegistration(
            @PathVariable Long id,
            @RequestParam(required = false) Long eventId,
            RedirectAttributes redirectAttributes) {
        try {
            registrationService.cancelRegistration(id);
            redirectAttributes.addFlashAttribute("successMessage",
                "✅ Registration cancelled successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        // Redirect back to the event's registration list if we know which event
        if (eventId != null) {
            return "redirect:/admin/events/" + eventId + "/registrations";
        }
        return "redirect:/admin/registrations";
    }
 
    // ── Add New Event ──────────────────────────────────────────────────────────
 
    @GetMapping("/events/new")
    public String showAddEventForm(Model model) {
        model.addAttribute("event",      new Event());
        model.addAttribute("pageTitle",  "Add New Event");
        model.addAttribute("formAction", "/admin/events/new");
        model.addAttribute("isEditMode", false);
        return "admin/event-form";
    }
 
    @PostMapping("/events/new")
    public String saveNewEvent(
            @Valid @ModelAttribute("event") Event event,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
 
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle",  "Add New Event");
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
 
    @GetMapping("/events/{id}/edit")
    public String showEditEventForm(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        model.addAttribute("event",      event);
        model.addAttribute("pageTitle",  "Edit Event");
        model.addAttribute("formAction", "/admin/events/" + id + "/edit");
        model.addAttribute("isEditMode", true);
        return "admin/event-form";
    }
 
    @PostMapping("/events/{id}/edit")
    public String updateEvent(
            @PathVariable Long id,
            @Valid @ModelAttribute("event") Event event,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
 
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle",  "Edit Event");
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
 
    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        Event event = eventService.getEventById(id);
        eventService.deleteEvent(id);
        redirectAttributes.addFlashAttribute("successMessage",
            "🗑️ Event '" + event.getName() + "' has been deleted.");
        return "redirect:/admin/events";
    }
 
    // ── Statistics ─────────────────────────────────────────────────────────────
 
    @GetMapping("/statistics")
    public String statistics(Model model) {
        Map<String, Long> stats = eventService.getEventRegistrationStats();
        model.addAttribute("stats",              stats);
        model.addAttribute("totalRegistrations", registrationService.getTotalRegistrationCount());
        model.addAttribute("totalEvents",        eventService.getTotalEventCount());
        model.addAttribute("pageTitle",          "Registration Statistics");
        return "admin/statistics";
    }
}
 