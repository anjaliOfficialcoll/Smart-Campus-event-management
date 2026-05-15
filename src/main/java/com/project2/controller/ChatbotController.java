package com.project2.controller;



import com.project2.dto.ChatRequest;
import com.project2.dto.ChatResponse;
import com.project2.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ChatbotController — REST endpoints for the student help chatbot.
 *
 * POST /api/chat  →  accepts a student message, returns a bot reply
 * GET  /help      →  serves the help page (map + chatbot UI)
 */
@RestController
@RequestMapping("/api")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    /**
     * Main chat endpoint.
     * Receives { "message": "..." } and returns { "reply": "...", "type": "..." }
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ChatResponse("Please type a message.", "error", null));
        }
        ChatResponse response = chatbotService.processMessage(request.getMessage().trim());
        return ResponseEntity.ok(response);
    }

    /**
     * Quick-reply suggestions shown as buttons in the chatbot.
     */
    @GetMapping("/chat/suggestions")
    public ResponseEntity<String[]> getSuggestions() {
        return ResponseEntity.ok(new String[]{
            "How do I register for an event?",
            "Show upcoming events",
            "Where is the Main Auditorium?",
            "How do I find my registered events?",
            "What event types are available?",
            "How do I submit feedback?",
            "Contact admin"
        });
    }
}
