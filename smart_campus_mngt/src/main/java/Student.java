package com.project2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Student Entity - Represents a student who registers for events.
 * Mapped to the "students" table in the database.
 */
@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 60, message = "Name must be between 2 and 60 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Student ID is required")
    @Size(min = 3, max = 20, message = "Student ID must be between 3 and 20 characters")
    @Column(nullable = false, unique = true)
    private String studentId;

    @NotBlank(message = "Department is required")
    @Column(nullable = false)
    private String department;

    @Column
    private String phone;

    // ── Relationships ──────────────────────────────────────────────────────────

    /**
     * One Student can have many Registrations (one per event).
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Registration> registrations = new ArrayList<>();

    /**
     * One Student can submit many Feedbacks.
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedbacks = new ArrayList<>();
}
