package com.hospitalManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Appointment Entity
 * 
 * Represents a medical appointment between a patient and a doctor.
 * Uses pessimistic locking to prevent overbooking in high-concurrency scenarios.
 * 
 * Key Features:
 * - Unique constraint on doctor_id and appointmentDateTime to prevent double-booking
 * - Version field for optimistic locking (additional concurrency control)
 * - Automatic timestamp management via JPA lifecycle callbacks
 * - Status tracking through enum (SCHEDULED, COMPLETED, CANCELLED, NO_SHOW)
 */
@Entity
@Table(name = "appointments", 
       // Unique constraint ensures a doctor cannot have multiple appointments at the same time
       uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "appointmentDateTime"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Patient associated with this appointment
     * LAZY loading: Patient data is loaded only when accessed
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    /**
     * Doctor associated with this appointment
     * LAZY loading: Doctor data is loaded only when accessed
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    /**
     * Date and time of the appointment
     * Must be in the future (validated at service layer)
     */
    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;
    
    /**
     * Current status of the appointment
     * Defaults to SCHEDULED when created
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;
    
    /**
     * Reason for the appointment (e.g., "Regular checkup", "Follow-up")
     */
    private String reason;
    
    /**
     * Additional notes about the appointment
     */
    private String notes;
    
    /**
     * Timestamp when the appointment record was created
     * Set automatically and cannot be updated
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the appointment record was last updated
     * Updated automatically on every modification
     */
    private LocalDateTime updatedAt;
    
    /**
     * Version field for optimistic locking
     * Hibernate automatically increments this on each update
     * Used to detect concurrent modifications
     */
    @Version
    private Long version;
    
    /**
     * JPA lifecycle callback: executed before persisting a new entity
     * Automatically sets creation and update timestamps
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * JPA lifecycle callback: executed before updating an existing entity
     * Automatically updates the modification timestamp
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Enumeration of possible appointment statuses
     * Used to track the lifecycle of an appointment
     */
    public enum AppointmentStatus {
        SCHEDULED,   // Appointment is booked and pending
        COMPLETED,   // Appointment has been completed
        CANCELLED,   // Appointment was cancelled
        NO_SHOW      // Patient did not show up for the appointment
    }
}

