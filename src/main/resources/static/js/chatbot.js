/**
 * 
 */
/**
 * chatbot.js
 * Handles the student help chatbot UI:
 *  - Sends messages to POST /api/chat
 *  - Renders bot replies with basic markdown formatting
 *  - Shows a typing indicator while waiting for response
 *  - Renders action buttons (quick replies, links, map triggers)
 *  - Integrates with the campus map (flyToVenue)
 *  - Persists conversation history in sessionStorage
 */

const chatMessages  = document.getElementById("chatMessages");
const chatInput     = document.getElementById("chatInput");
const sendBtn       = document.getElementById("sendBtn");
const quickReplies  = document.getElementById("quickReplies");

let isWaiting = false;

// ── Send on Enter key ──────────────────────────────────────────────────────────
function handleKey(event) {
  if (event.key === "Enter" && !event.shiftKey) {
    event.preventDefault();
    sendMessage();
  }
}

// ── Send a message typed by user ───────────────────────────────────────────────
function sendMessage() {
  const text = chatInput.value.trim();
  if (!text || isWaiting) return;

  chatInput.value = "";
  appendUserMessage(text);
  hideQuickReplies();
  callChatAPI(text);
}

// ── Send a quick reply button click ───────────────────────────────────────────
function sendQuick(text) {
  if (isWaiting) return;
  appendUserMessage(text);
  hideQuickReplies();
  callChatAPI(text);
}

// ── Call the Spring Boot /api/chat endpoint ────────────────────────────────────
async function callChatAPI(message) {
  isWaiting = true;
  setSendBtnLoading(true);

  const typingId = showTypingIndicator();

  try {
    const response = await fetch("/api/chat", {
      method:  "POST",
      headers: { "Content-Type": "application/json" },
      body:    JSON.stringify({ message }),
    });

    if (!response.ok) throw new Error("Server error " + response.status);

    const data = await response.json();

    // Small delay so typing indicator feels natural (min 600ms)
    await sleep(600);
    removeTypingIndicator(typingId);

    appendBotMessage(data.reply, data.actions, data.type);

    // If chatbot returns a map action, fly to that venue
    if (data.type === "map" && data.actions) {
      const mapAction = data.actions.find(a => a.mapTarget);
      if (mapAction && typeof window.flyToVenue === "function") {
        setTimeout(() => {
          window.flyToVenue(mapAction.mapTarget);
          scrollToMap();
        }, 800);
      }
    }

  } catch (err) {
    removeTypingIndicator(typingId);
    appendBotMessage(
      "⚠️ Sorry, I'm having trouble responding right now. Please try again in a moment.",
      null, "error"
    );
    console.error("Chat API error:", err);
  } finally {
    isWaiting = false;
    setSendBtnLoading(false);
    chatInput.focus();
  }
}

// ── Append a USER message bubble ──────────────────────────────────────────────
function appendUserMessage(text) {
  const div = document.createElement("div");
  div.className = "msg user-msg";
  div.innerHTML = `
    <div class="msg-bubble user-bubble">
      <p>${escapeHtml(text)}</p>
    </div>
    <div class="msg-avatar user-avatar">👤</div>`;
  chatMessages.appendChild(div);
  scrollChat();
}

// ── Append a BOT message bubble ───────────────────────────────────────────────
function appendBotMessage(reply, actions, type) {
  const div = document.createElement("div");
  div.className = "msg bot-msg";

  const formattedReply = renderMarkdown(reply);

  let actionsHtml = "";
  if (actions && actions.length > 0) {
    actionsHtml = `<div class="bot-actions">`;
    actions.forEach(action => {
      if (action.mapTarget) {
        // Map button — triggers flyToVenue instead of navigating
        actionsHtml += `
          <button class="action-btn action-map"
                  onclick="handleMapAction('${escapeAttr(action.mapTarget)}', event)">
            📍 ${escapeHtml(action.label)}
          </button>`;
      } else if (action.url) {
        actionsHtml += `
          <a href="${escapeAttr(action.url)}" class="action-btn action-link">
            ${escapeHtml(action.label)}
          </a>`;
      }
    });
    actionsHtml += `</div>`;
  }

  div.innerHTML = `
    <div class="msg-avatar">🤖</div>
    <div class="msg-bubble">
      ${formattedReply}
      ${actionsHtml}
    </div>`;

  chatMessages.appendChild(div);
  scrollChat();
}

// ── Map action handler ────────────────────────────────────────────────────────
function handleMapAction(venueName, event) {
  event.preventDefault();
  if (typeof window.flyToVenue === "function") {
    window.flyToVenue(venueName);
    scrollToMap();

    // Visual feedback
    const btn = event.currentTarget;
    btn.textContent = "✅ Showing on map!";
    btn.disabled = true;
    setTimeout(() => {
      btn.textContent = "📍 " + venueName;
      btn.disabled = false;
    }, 3000);
  }
}

// ── Typing indicator ──────────────────────────────────────────────────────────
function showTypingIndicator() {
  const id  = "typing-" + Date.now();
  const div = document.createElement("div");
  div.id        = id;
  div.className = "msg bot-msg typing-msg";
  div.innerHTML = `
    <div class="msg-avatar">🤖</div>
    <div class="msg-bubble typing-bubble">
      <span class="dot"></span>
      <span class="dot"></span>
      <span class="dot"></span>
    </div>`;
  chatMessages.appendChild(div);
  scrollChat();
  return id;
}

function removeTypingIndicator(id) {
  const el = document.getElementById(id);
  if (el) el.remove();
}

// ── Quick replies ─────────────────────────────────────────────────────────────
function hideQuickReplies() {
  if (quickReplies) {
    quickReplies.style.opacity = "0.4";
    quickReplies.style.pointerEvents = "none";
    setTimeout(() => {
      quickReplies.style.opacity = "1";
      quickReplies.style.pointerEvents = "auto";
    }, 2000);
  }
}

// ── Markdown → HTML renderer (lightweight, email-safe subset) ─────────────────
function renderMarkdown(text) {
  if (!text) return "";
  let html = escapeHtml(text);

  // **bold**
  html = html.replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>");
  // *italic*
  html = html.replace(/\*(.*?)\*/g, "<em>$1</em>");
  // `code`
  html = html.replace(/`(.*?)`/g, "<code>$1</code>");
  // Bullet items starting with •
  html = html.replace(/^[•]\s(.+)$/gm, "<li>$1</li>");
  // Numbered list 1️⃣ 2️⃣ etc — wrap as paragraphs
  html = html.replace(/^\d[️⃣]\s(.+)$/gm, "<p class='numbered-item'>$1</p>");
  // Wrap consecutive <li> in <ul>
  html = html.replace(/(<li>.*<\/li>\n?)+/g, m => "<ul class='bot-list'>" + m + "</ul>");
  // Newlines → <br> (but not inside lists)
  html = html.replace(/\n/g, "<br/>");

  return `<div class="bot-text">${html}</div>`;
}

// ── Utilities ─────────────────────────────────────────────────────────────────
function scrollChat() {
  chatMessages.scrollTo({ top: chatMessages.scrollHeight, behavior: "smooth" });
}

function scrollToMap() {
  const mapEl = document.getElementById("campusMap");
  if (mapEl) mapEl.scrollIntoView({ behavior: "smooth", block: "center" });
}

function setSendBtnLoading(loading) {
  const icon = document.getElementById("sendIcon");
  if (loading) {
    sendBtn.disabled  = true;
    if (icon) icon.textContent = "⏳";
  } else {
    sendBtn.disabled  = false;
    if (icon) icon.textContent = "➤";
  }
}

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

function escapeHtml(str) {
  if (!str) return "";
  return str
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

function escapeAttr(str) {
  if (!str) return "";
  return str.replace(/'/g, "\\'").replace(/"/g, "&quot;");
}