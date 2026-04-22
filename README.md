# 🎓 Smart Campus Event Management System

A full-stack web application built with **Java Spring Boot**, **Spring MVC**, **Spring Data JPA**, **Thymeleaf**, **HTML5**, and **CSS3**.

---

## 📋 Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Default Credentials](#default-credentials)
- [Application URLs](#application-urls)
- [REST API Endpoints](#rest-api-endpoints)
- [Database Schema](#database-schema)
- [Switching to MySQL](#switching-to-mysql)
- [Annotation Reference](#annotation-reference)

---

## ✨ Features

### 👩‍🎓 Student Module
| Feature | Description |
|---|---|
| Browse Events | View all upcoming events with pagination |
| Event Details | Full event info, feedback, and ratings |
| Register | Register for any event with form validation |
| My Events | Look up registered events by email |
| Feedback | Submit star-rating + comment for attended events |

### 🛠️ Admin Module
| Feature | Description |
|---|---|
| Secure Login | Spring Security with BCrypt password hashing |
| Dashboard | Total events, registrations, upcoming count |
| Add Event | Create events with all fields + validation |
| Edit Event | Update any event detail |
| Delete Event | Remove events (cascades to registrations) |
| Search/Filter | Filter by department, event type, or date |
| Statistics | Visual bar chart of registrations per event |

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2 |
| MVC | Spring MVC (`@Controller`, `@RestController`) |
| ORM | Spring Data JPA + Hibernate |
| Security | Spring Security 6 |
| Templating | Thymeleaf 3 |
| Database | H2 (embedded, default) / MySQL (optional) |
| Validation | Jakarta Bean Validation (`@NotBlank`, `@Email`, etc.) |
| Build | Maven |
| Java | Java 17 |
| Frontend | HTML5, CSS3, Vanilla JS |

---

## 📁 Project Structure

```
smart-campus/
├── pom.xml
└── src/main/
    ├── java/com/campus/events/
    │   ├── SmartCampusApplication.java       ← Main entry point
    │   │
    │   ├── entity/                           ← JPA Entities (@Entity)
    │   │   ├── Event.java
    │   │   ├── Student.java
    │   │   ├── Registration.java
    │   │   └── Feedback.java
    │   │
    │   ├── repository/                       ← Spring Data JPA Repos
    │   │   ├── EventRepository.java
    │   │   ├── StudentRepository.java
    │   │   ├── RegistrationRepository.java
    │   │   └── FeedbackRepository.java
    │   │
    │   ├── service/                          ← Business Logic Layer
    │   │   ├── EventService.java
    │   │   ├── RegistrationService.java
    │   │   └── FeedbackService.java
    │   │
    │   ├── controller/                       ← MVC + REST Controllers
    │   │   ├── HomeController.java           ← Student pages
    │   │   ├── AdminController.java          ← Admin pages
    │   │   └── EventApiController.java       ← REST API (@RestController)
    │   │
    │   ├── config/
    │   │   ├── SecurityConfig.java           ← Spring Security rules
    │   │   └── DataInitializer.java          ← Sample data seeder
    │   │
    │   └── exception/
    │       ├── ResourceNotFoundException.java
    │       ├── DuplicateRegistrationException.java
    │       └── GlobalExceptionHandler.java   ← @ControllerAdvice
    │
    └── resources/
        ├── application.properties
        ├── templates/
        │   ├── fragments/
        │   │   └── layout.html               ← Navbar, footer, messages
        │   ├── student/
        │   │   ├── home.html                 ← Event listing + pagination
        │   │   ├── event-details.html        ← Event + feedback view
        │   │   ├── register.html             ← Registration form
        │   │   ├── my-events.html            ← Student's events
        │   │   └── feedback.html             ← Feedback form
        │   ├── admin/
        │   │   ├── login.html                ← Admin login page
        │   │   ├── dashboard.html            ← Stats + quick actions
        │   │   ├── events.html               ← Event table + filters
        │   │   ├── event-form.html           ← Add/Edit event form
        │   │   └── statistics.html           ← Registration charts
        │   └── error.html                    ← Global error page
        └── static/
            ├── css/
            │   ├── main.css                  ← Full design system
            │   └── admin.css                 ← Admin panel styles
            └── js/
                └── main.js                   ← UI enhancements
```

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+

### Steps

```bash
# 1. Clone / download the project
cd smart-campus

# 2. Build the project
mvn clean install -DskipTests

# 3. Run the application
mvn spring-boot:run
```

The app starts on **http://localhost:8080** with 8 sample events pre-loaded.

---

## 🔑 Default Credentials

| Role | Username | Password | URL |
|---|---|---|---|
| Admin | `admin` | `admin123` | http://localhost:8080/admin/login |
| H2 Console | `sa` | *(empty)* | http://localhost:8080/h2-console |

H2 Console JDBC URL: `jdbc:h2:mem:campusdb`

---

## 🌐 Application URLs

### Student (Public)
| URL | Description |
|---|---|
| `GET /` | Home — upcoming events list |
| `GET /events/{id}` | Event details + feedback |
| `GET /register/{id}` | Registration form |
| `POST /register/{id}` | Submit registration |
| `GET /my-events` | My events lookup form |
| `POST /my-events` | Lookup by email |
| `GET /feedback/{id}` | Feedback form |
| `POST /feedback/{id}` | Submit feedback |

### Admin (Requires Login)
| URL | Description |
|---|---|
| `GET /admin/login` | Login page |
| `GET /admin/dashboard` | Dashboard |
| `GET /admin/events` | Events list with filter |
| `GET /admin/events/new` | Add event form |
| `POST /admin/events/new` | Save new event |
| `GET /admin/events/{id}/edit` | Edit event form |
| `POST /admin/events/{id}/edit` | Update event |
| `POST /admin/events/{id}/delete` | Delete event |
| `GET /admin/statistics` | Registration stats |

---

## 🔌 REST API Endpoints

Base URL: `http://localhost:8080/api`

| Method | Endpoint | Description | Response |
|---|---|---|---|
| GET | `/api/events` | All upcoming events | `[Event]` JSON array |
| GET | `/api/events/{id}` | Single event by ID | `Event` JSON |
| GET | `/api/events/{id}/registrations/count` | Registration count | `{eventId, count}` |
| GET | `/api/stats` | System statistics | `{totalEvents, totalRegistrations, upcomingEvents}` |

### Example Response — GET /api/events/1
```json
{
  "id": 1,
  "name": "AI & Machine Learning Workshop",
  "description": "Hands-on workshop...",
  "date": "2025-04-25",
  "time": "10:00:00",
  "venue": "CS Lab Block A, Room 204",
  "department": "Computer Science",
  "eventType": "WORKSHOP",
  "capacity": 60,
  "registrationCount": 3
}
```

---

## 🗄 Database Schema

```
┌──────────────┐       ┌───────────────────┐       ┌──────────────┐
│   students   │       │   registrations   │       │    events    │
├──────────────┤       ├───────────────────┤       ├──────────────┤
│ id (PK)      │──────<│ id (PK)           │>──────│ id (PK)      │
│ name         │       │ student_id (FK)   │       │ name         │
│ email        │       │ event_id (FK)     │       │ description  │
│ student_id   │       │ registered_at     │       │ date         │
│ department   │       │ status            │       │ time         │
│ phone        │       └───────────────────┘       │ venue        │
└──────────────┘                                   │ department   │
       │                ┌───────────────────┐      │ event_type   │
       │                │    feedbacks      │      │ capacity     │
       │                ├───────────────────┤      └──────────────┘
       └───────────────<│ id (PK)           │>─────────────┘
                        │ student_id (FK)   │
                        │ event_id (FK)     │
                        │ comments          │
                        │ rating (1–5)      │
                        │ submitted_at      │
                        └───────────────────┘

Relationships:
  Event       → Registrations : OneToMany
  Event       → Feedbacks     : OneToMany
  Student     → Registrations : OneToMany
  Student     → Feedbacks     : OneToMany
  Registration → Event        : ManyToOne
  Registration → Student      : ManyToOne
```

---

## 🐬 Switching to MySQL

1. Create database: `CREATE DATABASE campusdb;`

2. In `application.properties`, comment out H2 and uncomment MySQL:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campusdb?useSSL=false&serverTimezone=UTC
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
```

3. In `pom.xml`, uncomment the MySQL dependency (and comment out H2).

4. Restart the app — tables are auto-created by Hibernate.

---

## 📌 Annotation Reference

| Annotation | Location | Purpose |
|---|---|---|
| `@SpringBootApplication` | Main class | Enables auto-config, component scan |
| `@Entity` | All entities | Marks class as JPA/DB table |
| `@Id` + `@GeneratedValue` | Entity PK fields | Auto-increment primary key |
| `@OneToMany` / `@ManyToOne` | Entity relationships | JPA relationship mapping |
| `@Repository` | Repository interfaces | Spring Data DAO layer |
| `@Service` | Service classes | Business logic layer |
| `@Controller` | HomeController, AdminController | Returns HTML views |
| `@RestController` | EventApiController | Returns JSON responses |
| `@RequestMapping` | AdminController | Base URL prefix `/admin` |
| `@GetMapping` / `@PostMapping` | All controllers | HTTP method routing |
| `@Autowired` | Services, controllers | Dependency injection |
| `@Valid` | Controller params | Triggers bean validation |
| `@NotBlank` / `@Size` / `@Email` | Entity fields | Field-level validation |
| `@ControllerAdvice` | GlobalExceptionHandler | Global exception handling |
| `@ExceptionHandler` | Handler methods | Catches specific exceptions |
| `@Transactional` | Service methods | DB transaction management |
| `@PrePersist` | Entity lifecycle | Auto-sets timestamps |
| `@Configuration` | SecurityConfig | Spring config class |
| `@EnableWebSecurity` | SecurityConfig | Activates Spring Security |
| `@Component` | DataInitializer | Auto-detected Spring bean |

---

## 🧪 Testing the Application

### As a Student:
1. Go to http://localhost:8080
2. Browse the 8 pre-loaded events
3. Click **Register** on any event, fill in your details
4. Visit **My Events**, enter your email to see registrations
5. Submit **Feedback** for an event you registered for

### As an Admin:
1. Go to http://localhost:8080/admin/login
2. Login with `admin` / `admin123`
3. View the **Dashboard** summary
4. Go to **Manage Events** — try filtering by department/type
5. Click **Add New Event** and create one
6. Edit or delete existing events
7. Check **Statistics** for registration counts

---

*Built with ❤️ using Spring Boot 3.2 + Thymeleaf*
