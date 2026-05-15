package com.project2.dto;



/**
 * ChatRequest — incoming message from the student chatbot UI.
 */
public class ChatRequest {
    private String message;

    public ChatRequest() {}
    public ChatRequest(String message) { this.message = message; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
