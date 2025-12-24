package com.hospitalManagement.dto;

import com.hospitalManagement.entity.Appointment.AppointmentStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    
    private Long id;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    @NotNull(message = "Appointment date and time is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDateTime;
    
    private AppointmentStatus status;
    
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}

