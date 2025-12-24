package com.hospitalManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Patient Entity
 * 
 * Represents a patient in the hospital management system.
 * Stores patient personal information, medical history, and appointment records.
 * 
 * Key Features:
 * - One-to-many relationship with appointments
 * - Automatic timestamp management
 * - Unique email constraint to prevent duplicates
 */
@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** Patient's first name - required field */
    @Column(nullable = false)
    private String firstName;
    
    /** Patient's last name - required field */
    @Column(nullable = false)
    private String lastName;
    
    /** 
     * Patient's email address
     * Unique constraint ensures no duplicate patient records with same email
     */
    @Column(nullable = false, unique = true)
    private String email;
    
    /** Patient's contact phone number - required field */
    @Column(nullable = false)
    private String phoneNumber;
    
    /** Patient's date of birth - used for age calculation and medical records */
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    
    /** Patient's residential address - required field */
    @Column(nullable = false)
    private String address;
    
    /** 
     * Patient's medical history
     * Optional field for storing past medical conditions, allergies, etc.
     */
    private String medicalHistory;
    
    /** 
     * Timestamp when patient record was created
     * Automatically set and cannot be updated
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /** 
     * Timestamp when patient record was last updated
     * Automatically updated on every modification
     */
    private LocalDateTime updatedAt;
    
    /**
     * List of appointments for this patient
     * 
     * Cascade settings:
     * - ALL: Operations on patient cascade to appointments
     * - orphanRemoval: Deleting patient removes all associated appointments
     * 
     * mappedBy: Indicates this is the inverse side of the relationship
     * (the Appointment entity owns the foreign key)
     */
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
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

