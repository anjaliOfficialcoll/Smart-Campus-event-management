package com.project2.controller;



import com.project2.model.*;
//import com.campus.events.entity.Feedback;
//import com.campus.events.entity.Registration;
//import com.campus.events.entity.Student;
import com.project2.service.EventService;
import com.project2.service.FeedbackService;
import com.project2.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * HomeController - Handles all student-facing pages.
 *
 * URL Mappings:
 *   GET  /              → Home page (list of upcoming events)
 *   GET  /events/{id}   → Event details page
 *   GET  /register/{id} → Registration form
 *   POST /register/{id} → Process registration
 *   GET  /my-events     → Student's registered events
 *   GET  /feedback/{id} → Feedback form
 *   POST /feedback/{id} → Submit feedback
 */
@Controller
public class HomeController {

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private FeedbackService feedbackService;

    // ── Home Page ──────────────────────────────────────────────────────────────

    /**
     * Show the homepage with paginated upcoming events.
     * ?page=0 → first page, ?page=1 → second page, etc.
     */
    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "6") int size) {

        Page<Event> eventPage = eventService.getUpcomingEvents(PageRequest.of(page, size));

        model.addAttribute("events", eventPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", eventPage.getTotalPages());
        model.addAttribute("totalItems", eventPage.getTotalElements());
        model.addAttribute("pageTitle", "Upcoming Campus Events");

        return "student/home"; // → templates/student/home.html
    }

    // ── Event Details ──────────────────────────────────────────────────────────

    /**
     * Show event details page.
     */
    @GetMapping("/events/{id}")
    public String eventDetails(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        List<Feedback> feedbacks = feedbackService.getFeedbackByEventId(id);
        double avgRating = feedbackService.getAverageRating(id);

        model.addAttribute("event", event);
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("averageRating", avgRating);
        model.addAttribute("pageTitle", event.getName());

        return "student/event-details"; // → templates/student/event-details.html
    }

    // ── Registration ───────────────────────────────────────────────────────────

    /**
     * Show the event registration form.
     */
    @GetMapping("/register/{eventId}")
    public String showRegistrationForm(@PathVariable Long eventId, Model model) {
        Event event = eventService.getEventById(eventId);

        model.addAttribute("event", event);
        model.addAttribute("student", new Student()); // empty Student for the form
        model.addAttribute("pageTitle", "Register for " + event.getName());

        return "student/register"; // → templates/student/register.html
    }

    /**
     * Process the registration form.
     * @Valid triggers bean validation (@NotBlank, @Email, etc.)
     * BindingResult holds validation errors.
     */
    @PostMapping("/register/{eventId}")
    public String processRegistration(@PathVariable Long eventId,
                                      @Valid @ModelAttribute("student") Student student,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {

        // If validation fails, show form again with error messages
        if (bindingResult.hasErrors()) {
            model.addAttribute("event", eventService.getEventById(eventId));
            return "student/register";
        }

        try {
            registrationService.registerStudentForEvent(student, eventId);
            // Flash attribute persists across redirect
            redirectAttributes.addFlashAttribute("successMessage",
                "🎉 Registration successful! You are now registered for the event.");
            return "redirect:/events/" + eventId;

        } catch (Exception e) {
            // e.g. DuplicateRegistrationException
            model.addAttribute("event", eventService.getEventById(eventId));
            model.addAttribute("errorMessage", e.getMessage());
            return "student/register";
        }
    }

    // ── My Events ─────────────────────────────────────────────────────────────

    /**
     * Show all events a student has registered for.
     * Student looks up their registrations by email.
     */
    @GetMapping("/my-events")
    public String myEvents(Model model) {
        model.addAttribute("pageTitle", "My Registered Events");
        model.addAttribute("emailForm", true); // show the email input form
        return "student/my-events";
    }

    @PostMapping("/my-events")
    public String lookupMyEvents(@RequestParam String email, Model model) {
        List<Registration> registrations = registrationService.getRegistrationsByStudentEmail(email);

        model.addAttribute("registrations", registrations);
        model.addAttribute("studentEmail", email);
        model.addAttribute("pageTitle", "My Registered Events");

        if (registrations.isEmpty()) {
            model.addAttribute("infoMessage", "No registrations found for email: " + email);
        }

        return "student/my-events";
    }

    // ── Feedback ───────────────────────────────────────────────────────────────

    /**
     * Show the feedback form for an event.
     */
    @GetMapping("/feedback/{eventId}")
    public String showFeedbackForm(@PathVariable Long eventId,
                                   @RequestParam(required = false) String email,
                                   Model model) {
        Event event = eventService.getEventById(eventId);

        model.addAttribute("event", event);
        model.addAttribute("feedback", new Feedback());
        model.addAttribute("studentEmail", email);
        model.addAttribute("pageTitle", "Submit Feedback");

        return "student/feedback"; // → templates/student/feedback.html
    }

    /**
     * Process the feedback form submission.
     */
    @PostMapping("/feedback/{eventId}")
    public String submitFeedback(@PathVariable Long eventId,
                                 @RequestParam String studentEmail,
                                 @Valid @ModelAttribute("feedback") Feedback feedback,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("event", eventService.getEventById(eventId));
            model.addAttribute("studentEmail", studentEmail);
            return "student/feedback";
        }

        try {
            feedbackService.submitFeedback(studentEmail, eventId, feedback);
            redirectAttributes.addFlashAttribute("successMessage",
                "✅ Thank you! Your feedback has been submitted successfully.");
            return "redirect:/events/" + eventId;

        } catch (Exception e) {
            model.addAttribute("event", eventService.getEventById(eventId));
            model.addAttribute("studentEmail", studentEmail);
            model.addAttribute("errorMessage", e.getMessage());
            return "student/feedback";
        }
    }
}
