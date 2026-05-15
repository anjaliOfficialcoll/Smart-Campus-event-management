package com.project2.controller;



import com.project2.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * HelpPageController — Serves the /help page which contains
 * the interactive campus map and the student help chatbot.
 */
@Controller
public class HelpPageController {

    @Autowired
    private EventService eventService;

    @GetMapping("/help")
    public String helpPage(Model model) {
        // Pass upcoming events so the map can show event pins
        model.addAttribute("upcomingEvents", eventService.getAllUpcomingEvents());
        model.addAttribute("pageTitle", "Help & Campus Map");
        return "student/help";
    }
}
