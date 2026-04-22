package com.project2.model;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Event Entity - Represents a campus event.
 * Mapped to the "events" table in the database.
 */
@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Event name is required")
    @Size(min = 3, max = 100, message = "Event name must be between 3 and 100 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotNull(message = "Event date is required")
    @Column(nullable = false)
    private LocalDate date;

    @Column
    private LocalTime time;

    @NotBlank(message = "Venue is required")
    @Column(nullable = false)
    private String venue;

    @NotBlank(message = "Department is required")
    @Column(nullable = false)
    private String department;

    /**
     * Event type: WORKSHOP, SEMINAR, CULTURAL, SPORTS, TECHNICAL, OTHER
     */
    @NotBlank(message = "Event type is required")
    @Column(nullable = false)
    private String eventType;

    @Column
    private Integer capacity;

    @Column
    private String imageUrl;

    // ── Relationships ──────────────────────────────────────────────────────────

    /**
     * One Event can have many Registrations.
     * CascadeType.ALL: deleting an event removes its registrations too.
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Registration> registrations = new ArrayList<>();

    /**
     * One Event can have many Feedbacks.
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedbacks = new ArrayList<>();

    // ── Helper Methods ─────────────────────────────────────────────────────────

    /** Returns total number of students registered for this event. */
    public int getRegistrationCount() {
        return registrations != null ? registrations.size() : 0;
    }

    /** Returns average feedback rating for this event. */
    public double getAverageRating() {
        if (feedbacks == null || feedbacks.isEmpty()) return 0.0;
        return feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
    }

    /** Checks if the event is upcoming (date is today or in the future). */
    public boolean isUpcoming() {
        return date != null && !date.isBefore(LocalDate.now());
    }
}