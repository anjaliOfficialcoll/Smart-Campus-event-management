package com.project2.dto;



import java.util.List;

/**
 * ChatResponse — bot reply sent back to the chatbot UI.
 *
 * Fields:
 *   reply    — the text response to show
 *   type     — "text" | "list" | "link" | "map" | "error"
 *   actions  — optional quick-reply buttons or links to show after the reply
 */
public class ChatResponse {
    private String       reply;
    private String       type;
    private List<Action> actions;

    public ChatResponse() {}

    public ChatResponse(String reply, String type, List<Action> actions) {
        this.reply   = reply;
        this.type    = type;
        this.actions = actions;
    }

    // ── Getters & Setters ──────────────────────────────────────────────────────
    public String getReply()              { return reply; }
    public void   setReply(String reply)  { this.reply = reply; }

    public String getType()               { return type; }
    public void   setType(String type)    { this.type = type; }

    public List<Action> getActions()                   { return actions; }
    public void         setActions(List<Action> actions) { this.actions = actions; }

    // ── Inner class for quick-reply buttons / links ────────────────────────────
    public static class Action {
        private String label;
        private String url;
        private String mapTarget; // venue name to highlight on the map

        public Action() {}
        public Action(String label, String url, String mapTarget) {
            this.label     = label;
            this.url       = url;
            this.mapTarget = mapTarget;
        }

        public String getLabel()                { return label; }
        public void   setLabel(String label)    { this.label = label; }

        public String getUrl()                  { return url; }
        public void   setUrl(String url)        { this.url = url; }

        public String getMapTarget()                      { return mapTarget; }
        public void   setMapTarget(String mapTarget)      { this.mapTarget = mapTarget; }
    }
}
