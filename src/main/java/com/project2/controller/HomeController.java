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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

/**
 * HomeController - All student-facing pages.
 */
@Controller
public class HomeController {
 
    @Autowired private EventService eventService;
    @Autowired private RegistrationService registrationService;
    @Autowired private FeedbackService feedbackService;
 
    @Value("${app.mail.mock-mode:false}")
    private boolean mockMode;
 
    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "6") int size) {
        try {
            Page<Event> eventPage = eventService.getUpcomingEvents(PageRequest.of(page, size));
            model.addAttribute("events",      eventPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages",  eventPage.getTotalPages());
            model.addAttribute("totalItems",  eventPage.getTotalElements());
        } catch (Exception e) {
            model.addAttribute("events",      Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages",  0);
            model.addAttribute("totalItems",  0);
        }
        model.addAttribute("pageTitle", "Upcoming Campus Events");
        return "student/home";
    }
 
    @GetMapping("/events/{id}")
    public String eventDetails(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        List<Feedback> feedbacks = feedbackService.getFeedbackByEventId(id);
        double avgRating = feedbackService.getAverageRating(id);
        model.addAttribute("event",         event);
        model.addAttribute("feedbacks",     feedbacks);
        model.addAttribute("averageRating", avgRating);
        model.addAttribute("pageTitle",     event.getName());
        return "student/event-details";
    }
 
    @GetMapping("/register/{eventId}")
    public String showRegistrationForm(@PathVariable Long eventId, Model model) {
        Event event = eventService.getEventById(eventId);
        model.addAttribute("event",     event);
        model.addAttribute("student",   new Student());
        model.addAttribute("pageTitle", "Register for " + event.getName());
        return "student/register";
    }
 
    @PostMapping("/register/{eventId}")
    public String processRegistration(
            @PathVariable Long eventId,
            @Valid @ModelAttribute("student") Student student,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
 
        if (bindingResult.hasErrors()) {
            model.addAttribute("event", eventService.getEventById(eventId));
            return "student/register";
        }
        try {
            registrationService.registerStudentForEvent(student, eventId);
 
            String emailNote = mockMode
                ? " (Mock mode ON — email logged to console)"
                : " A confirmation email with your QR code pass has been sent to "
                  + student.getEmail() + " 📧";
 
            redirectAttributes.addFlashAttribute("successMessage",
                "🎉 Registration successful!" + emailNote);
            return "redirect:/events/" + eventId;
 
        } catch (Exception e) {
            model.addAttribute("event",        eventService.getEventById(eventId));
            model.addAttribute("errorMessage", e.getMessage());
            return "student/register";
        }
    }
 
    @GetMapping("/my-events")
    public String myEvents(Model model) {
        model.addAttribute("pageTitle", "My Registered Events");
        return "student/my-events";
    }
 
    @PostMapping("/my-events")
    public String lookupMyEvents(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("pageTitle", "My Registered Events");
        if (email == null || email.isBlank()) {
            model.addAttribute("errorMessage", "Please enter your email address.");
            return "student/my-events";
        }
        String trimmed = email.trim().toLowerCase();
        model.addAttribute("studentEmail", trimmed);
        try {
            List<Registration> registrations =
                    registrationService.getRegistrationsByStudentEmail(trimmed);
            model.addAttribute("registrations", registrations);
            if (registrations.isEmpty()) {
                model.addAttribute("infoMessage",
                    "No registrations found for: " + trimmed + ". Check your registered email.");
            }
        } catch (Exception e) {
            model.addAttribute("registrations", Collections.emptyList());
            model.addAttribute("errorMessage",  "Something went wrong. Please try again.");
        }
        return "student/my-events";
    }
 
    @GetMapping("/feedback/{eventId}")
    public String showFeedbackForm(@PathVariable Long eventId,
                                   @RequestParam(required = false) String email, Model model) {
        Event event = eventService.getEventById(eventId);
        model.addAttribute("event",        event);
        model.addAttribute("feedback",     new Feedback());
        model.addAttribute("studentEmail", email != null ? email : "");
        model.addAttribute("pageTitle",    "Submit Feedback");
        return "student/feedback";
    }
 
    @PostMapping("/feedback/{eventId}")
    public String submitFeedback(
            @PathVariable Long eventId,
            @RequestParam String studentEmail,
            @Valid @ModelAttribute("feedback") Feedback feedback,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
 
        if (bindingResult.hasErrors()) {
            model.addAttribute("event",        eventService.getEventById(eventId));
            model.addAttribute("studentEmail", studentEmail);
            return "student/feedback";
        }
        try {
            feedbackService.submitFeedback(studentEmail, eventId, feedback);
            redirectAttributes.addFlashAttribute("successMessage",
                "✅ Thank you! Your feedback has been submitted.");
            return "redirect:/events/" + eventId;
        } catch (Exception e) {
            model.addAttribute("event",        eventService.getEventById(eventId));
            model.addAttribute("studentEmail", studentEmail);
            model.addAttribute("errorMessage", e.getMessage());
            return "student/feedback";
        }
    }
}