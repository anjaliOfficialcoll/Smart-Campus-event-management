/**
 * campus-map.js
 * Vel Tech Rangarajan Dr. Sagunthala R&D Institute of Science and Technology
 * #42, Avadi-Vel Tech Road, Avadi, Chennai - 600062, Tamil Nadu
 *
 * Real GPS coordinates for all campus buildings.
 * Uses Leaflet.js + OpenStreetMap (free, no API key needed).
 */

// ── Real Vel Tech Campus GPS Centre ──────────────────────────────────────────
const CAMPUS_CENTER = [13.1267, 80.1514];
const CAMPUS_ZOOM   = 17;

// ── All Campus Venues with Real Coordinates ───────────────────────────────────
const CAMPUS_VENUES = [

  // ── Entry ────────────────────────────────────────────────────────────────────
  { name: "Main Gate",             lat: 13.1258, lng: 80.1507, cat: "facility", icon: "🚪",
    desc: "Main entrance on Avadi–Vel Tech Road. Security post open 24/7. Show your ID card for entry." },

  // ── Academic ─────────────────────────────────────────────────────────────────
  { name: "Admin Block",           lat: 13.1267, lng: 80.1514, cat: "facility", icon: "🏢",
    desc: "Principal's office, accounts, exam cell, and all administrative offices. Mon–Fri 9AM–5PM." },
  { name: "CS & IT Block",         lat: 13.1272, lng: 80.1518, cat: "academic", icon: "💻",
    desc: "Computer Science & IT labs, seminar halls, project labs. Home to hackathons and coding workshops." },
  { name: "ECE Block",             lat: 13.1274, lng: 80.1511, cat: "academic", icon: "📡",
    desc: "Electronics & Communication Engineering — VLSI, signal processing and embedded systems labs." },
  { name: "Mechanical Block",      lat: 13.1269, lng: 80.1522, cat: "academic", icon: "⚙️",
    desc: "Mechanical Engineering workshops, CAD/CAM and thermal engineering labs." },
  { name: "Civil Engineering Block",lat:13.1263, lng: 80.1520, cat: "academic", icon: "🏗️",
    desc: "Civil Engineering labs — survey equipment, concrete testing and structural design studio." },
  { name: "MBA Block",             lat: 13.1260, lng: 80.1515, cat: "academic", icon: "💼",
    desc: "School of Management Studies with seminar hall, Bloomberg terminals and case study rooms." },
  { name: "Science & Humanities",  lat: 13.1265, lng: 80.1508, cat: "academic", icon: "🔬",
    desc: "Physics, Chemistry, Maths and English departments with fully equipped science labs." },
  { name: "R&D Research Block",    lat: 13.1276, lng: 80.1516, cat: "academic", icon: "🧪",
    desc: "Advanced research labs for AI, IoT, Robotics, Nano-technology. Funded research projects." },

  // ── Events & Auditoriums ─────────────────────────────────────────────────────
  { name: "Main Auditorium",       lat: 13.1268, lng: 80.1505, cat: "events",   icon: "🎭",
    desc: "1200-seat main auditorium. Annual day, graduation, major seminars and college fests." },
  { name: "Mini Auditorium",       lat: 13.1271, lng: 80.1510, cat: "events",   icon: "🎪",
    desc: "300-seat mini auditorium for department workshops, guest lectures and presentations." },
  { name: "Open Air Theatre",      lat: 13.1262, lng: 80.1518, cat: "events",   icon: "🌟",
    desc: "Open-air stage for cultural nights, fresher's day and outdoor fests. Capacity: 2000+." },
  { name: "Seminar Hall",          lat: 13.1273, lng: 80.1521, cat: "events",   icon: "🏛️",
    desc: "200-seat seminar hall for technical symposiums, paper presentations and industry talks." },
  { name: "Conference Hall",       lat: 13.1266, lng: 80.1516, cat: "events",   icon: "📊",
    desc: "80-seat AC conference hall for board meetings, FDPs and small seminars." },

  // ── Sports ───────────────────────────────────────────────────────────────────
  { name: "Cricket Ground",        lat: 13.1255, lng: 80.1510, cat: "sports",   icon: "🏏",
    desc: "Full-size cricket ground with practice nets. Hosts inter-college tournaments." },
  { name: "Football Ground",       lat: 13.1252, lng: 80.1516, cat: "sports",   icon: "⚽",
    desc: "Full-size football ground. Also used for athletics and the annual sports meet." },
  { name: "Indoor Sports Complex", lat: 13.1257, lng: 80.1521, cat: "sports",   icon: "🏸",
    desc: "Multi-sport indoor arena — basketball, badminton, table tennis, chess. Open 6AM–9PM." },
  { name: "Basketball Court",      lat: 13.1254, lng: 80.1513, cat: "sports",   icon: "🏀",
    desc: "Outdoor floodlit basketball court. Practice sessions 5PM–8PM daily." },
  { name: "Volleyball Court",      lat: 13.1256, lng: 80.1519, cat: "sports",   icon: "🏐",
    desc: "Outdoor volleyball court for inter-department tournaments and practice." },

  // ── Facilities ───────────────────────────────────────────────────────────────
  { name: "Central Library",       lat: 13.1270, lng: 80.1507, cat: "facility", icon: "📚",
    desc: "60,000+ books, digital journals, reading halls, e-resources. Open 8AM–8PM on working days." },
  { name: "Main Canteen",          lat: 13.1261, lng: 80.1511, cat: "facility", icon: "🍽️",
    desc: "South Indian breakfast, lunch and snacks. Juice bar and bakery inside. Open 7AM–9PM." },
  { name: "Mini Canteen",          lat: 13.1274, lng: 80.1519, cat: "facility", icon: "☕",
    desc: "Quick refreshment near academic blocks — tea, coffee, snacks. Open during class hours." },
  { name: "Medical Centre",        lat: 13.1259, lng: 80.1505, cat: "facility", icon: "🏥",
    desc: "Full-time doctor and nurse on campus. First aid and basic treatment. Emergency: 044-2680-1999." },
  { name: "Boys Hostel",           lat: 13.1278, lng: 80.1520, cat: "facility", icon: "🏠",
    desc: "On-campus hostel for male students. 4-seater rooms, 24/7 Wi-Fi, mess and recreation room." },
  { name: "Girls Hostel",          lat: 13.1279, lng: 80.1510, cat: "facility", icon: "🏠",
    desc: "On-campus hostel for female students. 2/4-seater rooms, 24/7 security, mess and study room." },
  { name: "Placement Cell",        lat: 13.1264, lng: 80.1518, cat: "facility", icon: "👔",
    desc: "Training & Placement Centre. Campus drives, aptitude training, mock interviews and career guidance." },
  { name: "ATM & Bank",            lat: 13.1260, lng: 80.1509, cat: "facility", icon: "🏦",
    desc: "On-campus ATM and bank branch. Open Mon–Fri 10AM–4PM." },
  { name: "Stationery & Xerox",    lat: 13.1263, lng: 80.1513, cat: "facility", icon: "🖨️",
    desc: "Stationery shop near library. Printing, binding, lamination and spiral binding available." },
  { name: "Bus Stand",             lat: 13.1256, lng: 80.1506, cat: "facility", icon: "🚌",
    desc: "College bus routes covering Chennai, Avadi, Ambattur and surrounding areas. Contact transport office for routes." },
];

// ── Category colours ──────────────────────────────────────────────────────────
const CAT_COLORS = {
  academic: "#1a56e8",
  events:   "#f4a228",
  sports:   "#1aab78",
  facility: "#8b6fc9",
};

let map;
let markers = {};

// ── Initialise ────────────────────────────────────────────────────────────────
document.addEventListener("DOMContentLoaded", () => {
  initMap();
  const params = new URLSearchParams(window.location.search);
  const venue  = params.get("venue");
  if (venue) setTimeout(() => flyToVenue(decodeURIComponent(venue)), 800);
});

function initMap() {
  map = L.map("campusMap", {
    center: CAMPUS_CENTER, zoom: CAMPUS_ZOOM,
    zoomControl: true, scrollWheelZoom: true,
  });

  // OpenStreetMap — free, no API key
  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> | Vel Tech University, Avadi',
    maxZoom: 20,
  }).addTo(map);

  // Campus boundary ring
  L.circle(CAMPUS_CENTER, {
    color: '#f4a228', fillColor: '#f4a228',
    fillOpacity: 0.04, weight: 2, dashArray: '8,6', radius: 380
  }).addTo(map)
    .bindTooltip("Vel Tech Rangarajan Dr. Sagunthala R&D Institute · Avadi, Chennai", {
      permanent: false, direction: "top"
    });

  // University name label at centre
  L.marker(CAMPUS_CENTER, {
    icon: L.divIcon({
      html: `<div style="
        background:rgba(13,27,42,0.85);color:#f4a228;
        padding:5px 12px;border-radius:20px;
        font-size:11px;font-weight:700;white-space:nowrap;
        box-shadow:0 2px 8px rgba(0,0,0,0.3);
        font-family:sans-serif;letter-spacing:0.05em;">
        🎓 Vel Tech University
      </div>`,
      className: "", iconAnchor: [80, -8]
    })
  }).addTo(map);

  CAMPUS_VENUES.forEach(v => addVenueMarker(v));
  addEventPins();

  const bounds = L.latLngBounds(CAMPUS_VENUES.map(v => [v.lat, v.lng]));
  map.fitBounds(bounds.pad(0.12));
}

// ── Marker ────────────────────────────────────────────────────────────────────
function addVenueMarker(venue) {
  const color = CAT_COLORS[venue.cat] || "#666";
  const icon  = L.divIcon({
    html: `<div class="custom-marker" style="background:${color}" title="${venue.name}">
             <span class="marker-icon">${venue.icon}</span></div>`,
    className: "", iconSize: [36,36], iconAnchor: [18,36], popupAnchor: [0,-38],
  });

  const eventsHtml = buildEventsHtml(getEventsAtVenue(venue.name));
  const popup = L.popup({ maxWidth: 300, className: "campus-popup" }).setContent(`
    <div class="popup-inner">
      <div class="popup-header" style="background:${color}">
        <span class="popup-icon">${venue.icon}</span>
        <div>
          <div class="popup-name">${venue.name}</div>
          <div class="popup-cat">${venue.cat.charAt(0).toUpperCase()+venue.cat.slice(1)} · Vel Tech</div>
        </div>
      </div>
      <div class="popup-body">
        <p class="popup-desc">${venue.desc}</p>
        ${eventsHtml}
      </div>
    </div>`);

  markers[venue.name] = L.marker([venue.lat, venue.lng], { icon })
    .addTo(map).bindPopup(popup);
}

// ── Event pins ────────────────────────────────────────────────────────────────
function addEventPins() {
  if (!Array.isArray(upcomingEvents) || !upcomingEvents.length) return;
  upcomingEvents.forEach(event => {
    const venue = CAMPUS_VENUES.find(v =>
      event.venue && event.venue.toLowerCase().includes(v.name.toLowerCase())
    );
    if (!venue) return;
    L.marker([venue.lat + 0.00006, venue.lng + 0.00006], {
      icon: L.divIcon({
        html: `<div class="event-pulse-marker" title="${event.name}">⭐</div>`,
        className: "", iconSize:[22,22], iconAnchor:[11,11],
      })
    }).addTo(map).bindTooltip(`📅 ${event.name}`, { direction:"top" });
  });
}

// ── Fly to venue ──────────────────────────────────────────────────────────────
function flyToVenue(venueName) {
  const venue = CAMPUS_VENUES.find(v =>
    v.name.toLowerCase().includes(venueName.toLowerCase()) ||
    venueName.toLowerCase().includes(v.name.toLowerCase())
  );
  if (!venue) return;
  map.flyTo([venue.lat, venue.lng], 19, { duration: 1.2 });
  setTimeout(() => {
    const m = markers[venue.name];
    if (m) {
      m.openPopup();
      const el = m.getElement();
      if (el) { el.classList.add("marker-pulse"); setTimeout(()=>el.classList.remove("marker-pulse"),2000); }
    }
  }, 1300);
}

// ── Venue search ──────────────────────────────────────────────────────────────
function searchVenue(query) {
  if (!query || query.length < 2) return;
  const match = CAMPUS_VENUES.find(v => v.name.toLowerCase().includes(query.toLowerCase()));
  if (match) flyToVenue(match.name);
}

function getEventsAtVenue(venueName) {
  if (!Array.isArray(upcomingEvents)) return [];
  return upcomingEvents.filter(e =>
    e.venue && e.venue.toLowerCase().includes(venueName.toLowerCase())
  ).slice(0, 3);
}

function buildEventsHtml(events) {
  if (!events.length) return "";
  return `<div class="popup-events"><div class="popup-events-title">📅 Events Here:</div>` +
    events.map(e => `<a href="/events/${e.id}" class="popup-event-item">
      <span class="popup-event-dot"></span>
      <span class="popup-event-name">${e.name}</span>
      <span class="popup-event-date">${e.date}</span></a>`).join("") +
    `</div>`;
}

window.flyToVenue = flyToVenue;