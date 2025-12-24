package com.hospitalManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Doctor Entity
 * 
 * Represents a doctor in the hospital management system.
 * Stores doctor profile information, specialization, availability, and appointment schedule.
 * 
 * Key Features:
 * - One-to-many relationship with appointments
 * - Availability flag for appointment booking
 * - Automatic timestamp management
 * - Unique email constraint to prevent duplicates
 */
@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** Doctor's first name - required field */
    @Column(nullable = false)
    private String firstName;
    
    /** Doctor's last name - required field */
    @Column(nullable = false)
    private String lastName;
    
    /** 
     * Doctor's email address
     * Unique constraint ensures no duplicate doctor records with same email
     */
    @Column(nullable = false, unique = true)
    private String email;
    
    /** Doctor's contact phone number - required field */
    @Column(nullable = false)
    private String phoneNumber;
    
    /** 
     * Doctor's medical specialization
     * Examples: Cardiology, Pediatrics, Orthopedics, etc.
     */
    @Column(nullable = false)
    private String specialization;
    
    /** 
     * Department where the doctor works
     * Examples: Heart Care, Children's Health, Bone & Joint, etc.
     */
    @Column(nullable = false)
    private String department;
    
    /** 
     * Consultation fee charged by the doctor
     * Used for billing and appointment cost calculation
     */
    @Column(nullable = false)
    private Integer consultationFee;
    
    /** 
     * Availability flag for appointment booking
     * When false, doctor cannot accept new appointments
     * Defaults to true (available) when created
     */
    @Column(nullable = false)
    private Boolean isAvailable = true;
    
    /** 
     * Timestamp when doctor record was created
     * Automatically set and cannot be updated
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /** 
     * Timestamp when doctor record was last updated
     * Automatically updated on every modification
     */
    private LocalDateTime updatedAt;
    
    /**
     * List of appointments for this doctor
     * 
     * Cascade settings:
     * - ALL: Operations on doctor cascade to appointments
     * - orphanRemoval: Deleting doctor removes all associated appointments
     * 
     * mappedBy: Indicates this is the inverse side of the relationship
     * (the Appointment entity owns the foreign key)
     */
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();
    
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
}

