package com.project2.service;



import com.project2.dto.ChatResponse;
import com.project2.dto.ChatResponse.Action;
import com.project2.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ChatbotService — Rule-based NLP chatbot for student help.
 *
 * Intent categories handled:
 *   greeting       — hi, hello, hey
 *   register       — how to register, sign up, join event
 *   events         — upcoming events, what events, show events
 *   venue / map    — where is, location, find, directions, map
 *   my-events      — my registrations, my events, find my ticket
 *   feedback       — give feedback, review, rate
 *   event-types    — types, workshop, seminar, cultural, sports
 *   email          — confirmation, email, qr code
 *   contact        — admin, contact, help, support
 *   farewell       — bye, thank you, thanks
 *   fallback       — anything not matched
 */
@Service
public class ChatbotService {
 
    @Autowired
    private EventService eventService;
 
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");
 
    // Campus venue coordinates (lat, lng) for map highlighting
    // These match the markers defined in the frontend map
    // Real Vel Tech Avadi campus venues
    private static final String[][] VENUES = {
        {"Main Gate",                "13.1258", "80.1507"},
        {"Admin Block",              "13.1267", "80.1514"},
        {"CS & IT Block",            "13.1272", "80.1518"},
        {"ECE Block",                "13.1274", "80.1511"},
        {"Mechanical Block",         "13.1269", "80.1522"},
        {"Civil Engineering Block",  "13.1263", "80.1520"},
        {"MBA Block",                "13.1260", "80.1515"},
        {"Science & Humanities",     "13.1265", "80.1508"},
        {"R&D Research Block",       "13.1276", "80.1516"},
        {"Main Auditorium",          "13.1268", "80.1505"},
        {"Mini Auditorium",          "13.1271", "80.1510"},
        {"Open Air Theatre",         "13.1262", "80.1518"},
        {"Seminar Hall",             "13.1273", "80.1521"},
        {"Conference Hall",          "13.1266", "80.1516"},
        {"Cricket Ground",           "13.1255", "80.1510"},
        {"Football Ground",          "13.1252", "80.1516"},
        {"Indoor Sports Complex",    "13.1257", "80.1521"},
        {"Central Library",          "13.1270", "80.1507"},
        {"Main Canteen",             "13.1261", "80.1511"},
        {"Medical Centre",           "13.1259", "80.1505"},
        {"Boys Hostel",              "13.1278", "80.1520"},
        {"Girls Hostel",             "13.1279", "80.1510"},
        {"Placement Cell",           "13.1264", "80.1518"},
        {"Bus Stand",                "13.1256", "80.1506"},
        {"ATM & Bank",               "13.1260", "80.1509"},
    };
 
    // ── Main entry point ───────────────────────────────────────────────────────
 
    public ChatResponse processMessage(String userMessage) {
        String msg = userMessage.toLowerCase().trim();
 
        // ── Greeting ──────────────────────────────────────────────────────────
        if (matches(msg, "hi", "hello", "hey", "good morning", "good afternoon",
                         "good evening", "howdy", "greetings", "sup", "what's up")) {
            return greeting();
        }
 
        // ── Registration help ─────────────────────────────────────────────────
        if (matches(msg, "register", "registration", "sign up", "signup", "join",
                         "enroll", "book", "how do i register", "how to register")) {
            return registrationHelp();
        }
 
        // ── Show upcoming events ───────────────────────────────────────────────
        if (matches(msg, "event", "upcoming", "schedule", "what's on", "show event",
                         "list event", "all event", "available event", "today", "this week")) {
            return upcomingEvents();
        }
 
        // ── Venue / Map / Location ────────────────────────────────────────────
        if (matches(msg, "where", "location", "venue", "map", "find", "directions",
                         "how to reach", "navigate", "campus map", "show map", "auditorium",
                         "lab", "ground", "block", "sports", "canteen", "library", "medical")) {
            return venueHelp(msg);
        }
 
        // ── My Events / My Registrations ──────────────────────────────────────
        if (matches(msg, "my event", "my registration", "my ticket", "registered event",
                         "find my", "check my", "view my", "enrolled", "booked")) {
            return myEventsHelp();
        }
 
        // ── Feedback ──────────────────────────────────────────────────────────
        if (matches(msg, "feedback", "review", "rating", "rate", "comment",
                         "submit feedback", "give feedback")) {
            return feedbackHelp();
        }
 
        // ── Event Types ───────────────────────────────────────────────────────
        if (matches(msg, "type", "workshop", "seminar", "cultural", "sports",
                         "technical", "category", "kind of event", "what kind")) {
            return eventTypesHelp();
        }
 
        // ── Email / QR Code ───────────────────────────────────────────────────
        if (matches(msg, "email", "confirmation", "qr", "qr code", "pass",
                         "entry pass", "ticket", "not received", "no email")) {
            return emailHelp();
        }
 
        // ── Admin / Contact ───────────────────────────────────────────────────
        if (matches(msg, "admin", "contact", "support", "help", "issue", "problem",
                         "complain", "report", "staff")) {
            return contactHelp();
        }
 
        // ── Farewell ──────────────────────────────────────────────────────────
        if (matches(msg, "bye", "goodbye", "see you", "thanks", "thank you",
                         "thx", "ok thanks", "that's all", "done", "exit")) {
            return farewell();
        }
 
        // ── Fallback ──────────────────────────────────────────────────────────
        return fallback(userMessage);
    }
 
    // ── Intent Handlers ───────────────────────────────────────────────────────
 
    private ChatResponse greeting() {
        List<Action> actions = List.of(
            new Action("📅 Upcoming Events",      "/",         null),
            new Action("✏️ Register for Event",   "/",         null),
            new Action("🗺️ Campus Map",           "/help#map", null),
            new Action("🎫 My Registrations",     "/my-events",null)
        );
        return new ChatResponse(
            "👋 Hi there! Welcome to **CampusEvents Help Bot**!\n\n" +
            "I can help you with:\n" +
            "• Finding and registering for events\n" +
            "• Locating venues on the campus map\n" +
            "• Checking your registrations\n" +
            "• Submitting event feedback\n\n" +
            "What would you like to do today?",
            "text", actions
        );
    }
 
    private ChatResponse registrationHelp() {
        List<Action> actions = List.of(
            new Action("Browse Events →", "/", null),
            new Action("View My Events", "/my-events", null)
        );
        return new ChatResponse(
            "✏️ **How to Register for an Event:**\n\n" +
            "1️⃣  Go to the **Home page** and browse upcoming events.\n" +
            "2️⃣  Click on any event card to view its full details.\n" +
            "3️⃣  Click the **\"Register\"** button on the event page.\n" +
            "4️⃣  Fill in your **Name, Student ID, Email, Department**.\n" +
            "5️⃣  Submit the form — you'll see a success message.\n" +
            "6️⃣  Check your **email inbox** — a confirmation with your QR entry pass will arrive shortly! 📧\n\n" +
            "💡 *Tip: Use \"My Events\" to see all events you've registered for.*",
            "text", actions
        );
    }
 
    private ChatResponse upcomingEvents() {
        List<Event> events = eventService.getAllUpcomingEvents();
 
        if (events.isEmpty()) {
            return new ChatResponse(
                "📭 There are no upcoming events at the moment. Check back soon!",
                "text",
                List.of(new Action("Go to Home", "/", null))
            );
        }
 
        StringBuilder sb = new StringBuilder();
        sb.append("📅 **Upcoming Events** (next ").append(Math.min(events.size(), 5)).append("):\n\n");
 
        events.stream().limit(5).forEach(e -> {
            sb.append("🔹 **").append(e.getName()).append("**\n");
            sb.append("   📅 ").append(e.getDate().format(DATE_FMT));
            if (e.getTime() != null) sb.append(" at ").append(
                    e.getTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            sb.append("\n");
            sb.append("   📍 ").append(e.getVenue()).append("\n");
            sb.append("   🏷️ ").append(e.getEventType()).append("\n\n");
        });
 
        if (events.size() > 5) {
            sb.append("...and ").append(events.size() - 5).append(" more events!");
        }
 
        List<Action> actions = new ArrayList<>();
        actions.add(new Action("See All Events →", "/", null));
        // Add direct links to first 3 events
        events.stream().limit(3).forEach(e ->
            actions.add(new Action("📌 " + e.getName(), "/events/" + e.getId(), null))
        );
 
        return new ChatResponse(sb.toString(), "list", actions);
    }
 
    private ChatResponse venueHelp(String msg) {
        // Try to match a specific venue from the message
        String matchedVenue = null;
        String matchedLat   = null;
        String matchedLng   = null;
 
        for (String[] venue : VENUES) {
            if (msg.contains(venue[0].toLowerCase())) {
                matchedVenue = venue[0];
                matchedLat   = venue[1];
                matchedLng   = venue[2];
                break;
            }
        }
 
        List<Action> actions = new ArrayList<>();
 
        if (matchedVenue != null) {
            // Specific venue found
            actions.add(new Action("📍 Show on Map", "/help#map?venue=" +
                    matchedVenue.replace(" ", "+"), matchedVenue));
            actions.add(new Action("🗺️ Open Full Map", "/help#map", null));
 
            return new ChatResponse(
                "📍 **" + matchedVenue + "** is located on the campus map.\n\n" +
                "Click the button below to highlight it on the interactive map, " +
                "or scroll down to the **Campus Map** section to explore all venues.",
                "map", actions
            );
        }
 
        // General venue / map help
        actions.add(new Action("🗺️ Open Campus Map", "/help#map", null));
        actions.add(new Action("📅 Browse Events",   "/",         null));
 
        StringBuilder sb = new StringBuilder();
        sb.append("🗺️ **Campus Venues Guide:**\n\n");
        sb.append("Here are the key locations on campus:\n\n");
        sb.append("🏛️ **Academic:** CS Lab Block A & B, IT Department, Engineering Block, ECE Seminar Room\n");
        sb.append("🎭 **Events:** Main Auditorium, Open Air Theatre, MBA Block\n");
        sb.append("⚽ **Sports:** Sports Ground, Indoor Sports Complex\n");
        sb.append("🍽️ **Facilities:** Canteen, Library, Medical Centre, Admin Block\n\n");
        sb.append("👇 Use the **interactive map** below to click on any building!");
 
        return new ChatResponse(sb.toString(), "map", actions);
    }
 
    private ChatResponse myEventsHelp() {
        List<Action> actions = List.of(
            new Action("🎫 Go to My Events", "/my-events", null),
            new Action("📅 Browse Events",   "/",          null)
        );
        return new ChatResponse(
            "🎫 **Viewing Your Registered Events:**\n\n" +
            "1️⃣  Click **\"My Events\"** in the navigation bar (or the button below).\n" +
            "2️⃣  Enter the **email address** you used when registering.\n" +
            "3️⃣  Click **\"Find My Events\"** — all your registrations will appear.\n\n" +
            "📧 *You can also check your email inbox for the registration confirmation* " +
            "*which includes your event details and QR entry pass.*\n\n" +
            "💡 *Make sure you enter the exact same email used during registration.*",
            "text", actions
        );
    }
 
    private ChatResponse feedbackHelp() {
        List<Action> actions = List.of(
            new Action("📅 View Events",    "/",          null),
            new Action("🎫 My Events",      "/my-events", null)
        );
        return new ChatResponse(
            "💬 **How to Submit Feedback:**\n\n" +
            "1️⃣  Go to the **event details page** of an event you attended.\n" +
            "2️⃣  Scroll down and click **\"Submit Feedback\"**.\n" +
            "3️⃣  Enter your registered **email address**.\n" +
            "4️⃣  Give a **star rating** (1–5) and write your **comments**.\n" +
            "5️⃣  Click **Submit** — your feedback helps us improve!\n\n" +
            "⚠️ *Note: You must be registered for the event to submit feedback.*",
            "text", actions
        );
    }
 
    private ChatResponse eventTypesHelp() {
        List<Action> actions = List.of(
            new Action("Browse All Events →", "/", null)
        );
        return new ChatResponse(
            "🏷️ **Event Types Available:**\n\n" +
            "🔧 **WORKSHOP** — Hands-on learning sessions (coding, design, etc.)\n" +
            "🎓 **SEMINAR** — Expert talks, guest lectures, industry sessions\n" +
            "🎭 **CULTURAL** — Fests, performances, talent shows, exhibitions\n" +
            "⚽ **SPORTS** — Tournaments, fitness sessions, athletic meets\n" +
            "💻 **TECHNICAL** — Hackathons, expos, tech demos, competitions\n\n" +
            "You can **filter events by type** on the home page using the category buttons.\n\n" +
            "💡 *New events are added regularly — check back often!*",
            "text", actions
        );
    }
 
    private ChatResponse emailHelp() {
        List<Action> actions = List.of(
            new Action("Register for Event", "/",         null),
            new Action("My Events",          "/my-events", null)
        );
        return new ChatResponse(
            "📧 **Registration Email & QR Code:**\n\n" +
            "After successful registration, you automatically receive:\n" +
            "• ✅ A **confirmation email** with event details\n" +
            "• 📲 A **QR code** — your digital entry pass for the event\n\n" +
            "**Didn't receive the email?**\n" +
            "1️⃣ Check your **Spam / Junk** folder\n" +
            "2️⃣ Make sure you entered the **correct email** during registration\n" +
            "3️⃣ Wait up to **2–3 minutes** — emails are sent in the background\n" +
            "4️⃣ If still missing, contact the **Admin** for help\n\n" +
            "📌 *Show the QR code at the event entrance for instant check-in.*",
            "text", actions
        );
    }
 
    private ChatResponse contactHelp() {
        List<Action> actions = List.of(
            new Action("Admin Login", "/admin/login", null),
            new Action("Home",        "/",             null)
        );
        return new ChatResponse(
            "📞 **Contact & Support:**\n\n" +
            "👤 **Admin Portal:** " +
            "If you're an admin or need to escalate an issue, log in at /admin/login\n\n" +
            "📧 **Email Support:** contact@campusevents.edu.in\n\n" +
            "🏛️ **In Person:** Visit the **Admin Block** (visible on the campus map)\n\n" +
            "⏰ **Support Hours:** Monday–Friday, 9:00 AM – 5:00 PM\n\n" +
            "**Common Issues:**\n" +
            "• Can't register? → Check if capacity is full\n" +
            "• No confirmation email? → Check spam folder\n" +
            "• Wrong details submitted? → Contact admin to update\n" +
            "• Event cancelled? → Admin will notify via email",
            "text", actions
        );
    }
 
    private ChatResponse farewell() {
        return new ChatResponse(
            "👋 **Goodbye!** Have a great time at your events!\n\n" +
            "Feel free to come back anytime you need help. " +
            "Don't forget to show your QR code at the event entrance! 🎉",
            "text",
            List.of(new Action("🏠 Go Home", "/", null))
        );
    }
 
    private ChatResponse fallback(String originalMessage) {
        List<Action> actions = List.of(
            new Action("Register for Event",  "/",          null),
            new Action("View My Events",      "/my-events", null),
            new Action("Campus Map",          "/help#map",  null),
            new Action("Contact Admin",       "/admin/login",null)
        );
        return new ChatResponse(
            "🤔 I'm not sure I understood **\"" + originalMessage + "\"**.\n\n" +
            "Here are some things I can help with — just click or type:\n" +
            "• *How do I register for an event?*\n" +
            "• *Show upcoming events*\n" +
            "• *Where is the Main Auditorium?*\n" +
            "• *How do I find my registered events?*\n" +
            "• *How do I submit feedback?*\n" +
            "• *Contact admin*",
            "text", actions
        );
    }
 
    // ── Helper: keyword matching ───────────────────────────────────────────────
 
    private boolean matches(String message, String... keywords) {
        for (String kw : keywords) {
            if (message.contains(kw.toLowerCase())) return true;
        }
        return false;
    }
}