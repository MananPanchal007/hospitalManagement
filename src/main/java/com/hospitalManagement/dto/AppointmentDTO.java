package com.hospitalManagement.dto;

import com.hospitalManagement.entity.Appointment.AppointmentStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for Appointment
 * 
 * Used for transferring appointment data between layers (Controller <-> Service).
 * Contains validation annotations to ensure data integrity before processing.
 * 
 * Validation Rules:
 * - Patient and Doctor IDs are required
 * - Appointment date must be in the future
 * - Reason and notes have maximum length constraints
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    
    /** Appointment ID - set automatically when retrieved from database */
    private Long id;
    
    /** 
     * ID of the patient for this appointment
     * Required field - validated at API level
     */
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    /** 
     * ID of the doctor for this appointment
     * Required field - validated at API level
     */
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    /** 
     * Date and time of the appointment
     * 
     * Validation:
     * - @NotNull: Must be provided
     * - @Future: Must be in the future (cannot book past appointments)
     * 
     * Format: ISO 8601 (e.g., "2024-12-25T10:00:00")
     */
    @NotNull(message = "Appointment date and time is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDateTime;
    
    /** 
     * Current status of the appointment
     * Values: SCHEDULED, COMPLETED, CANCELLED, NO_SHOW
     * Optional in DTO - defaults to SCHEDULED when creating new appointment
     */
    private AppointmentStatus status;
    
    /** 
     * Reason for the appointment
     * Optional field with maximum length of 500 characters
     * Examples: "Regular checkup", "Follow-up", "Emergency consultation"
     */
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
    
    /** 
     * Additional notes about the appointment
     * Optional field with maximum length of 1000 characters
     * Used for storing additional information or instructions
     */
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}

