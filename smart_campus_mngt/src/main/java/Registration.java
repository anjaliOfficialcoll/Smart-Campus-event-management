package com.project2.model;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Registration Entity - Represents a student's registration for a specific event.
 * This is a join table entity between Student and Event.
 * Mapped to the "registrations" table in the database.
 */
@Entity
@Table(
    name = "registrations",
    uniqueConstraints = {
        // A student can only register for the same event once
        @UniqueConstraint(columnNames = {"student_id", "event_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many Registrations belong to one Student.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /**
     * Many Registrations belong to one Event.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    @Column
    private String status; // CONFIRMED, CANCELLED, WAITLISTED

    // Automatically set the registration time before persisting
    @PrePersist
    protected void onCreate() {
        this.registeredAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "CONFIRMED";
        }
    }
}