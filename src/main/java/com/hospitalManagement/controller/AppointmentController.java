package com.hospitalManagement.controller;

import com.hospitalManagement.dto.ApiResponse;
import com.hospitalManagement.dto.AppointmentDTO;
import com.hospitalManagement.entity.Appointment.AppointmentStatus;
import com.hospitalManagement.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Appointment Management
 * 
 * Provides endpoints for:
 * - Booking appointments with pessimistic locking
 * - Cancelling appointments
 * - Retrieving appointment information
 * - Checking doctor availability
 * 
 * All endpoints use @Valid for input validation and return standardized ApiResponse objects.
 * Swagger annotations provide API documentation.
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment Management", description = "APIs for booking, canceling, and managing appointments")
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    
    /**
     * Book a new appointment
     * 
     * Features:
     * - @Valid annotation validates the request body using Bean Validation
     * - Transaction management ensures atomic booking operation
     * - Pessimistic locking prevents overbooking in concurrent scenarios
     * 
     * Validation:
     * - Patient ID and Doctor ID are required
     * - Appointment date must be in the future
     * 
     * @param appointmentDTO Appointment details to book
     * @return ResponseEntity with created appointment or error message
     */
    @PostMapping
    @Operation(summary = "Book an appointment", 
               description = "Books a new appointment with transaction management and pessimistic locking to prevent overbooking")
    public ResponseEntity<ApiResponse<AppointmentDTO>> bookAppointment(
            @Valid @RequestBody AppointmentDTO appointmentDTO) {
        try {
            AppointmentDTO createdAppointment = appointmentService.bookAppointment(appointmentDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdAppointment, "Appointment booked successfully"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Handle business logic exceptions (validation failures, conflicts, etc.)
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Cancel an existing appointment
     * 
     * Features:
     * - Uses pessimistic locking to ensure atomic cancellation
     * - Validates appointment can be cancelled (not already cancelled/completed)
     * 
     * @param id Appointment ID to cancel
     * @return ResponseEntity with cancelled appointment or error message
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an appointment", 
               description = "Cancels an existing appointment with transaction management")
    public ResponseEntity<ApiResponse<AppointmentDTO>> cancelAppointment(
            @Parameter(description = "Appointment ID") @PathVariable Long id) {
        try {
            AppointmentDTO cancelledAppointment = appointmentService.cancelAppointment(id);
            return ResponseEntity.ok(ApiResponse.success(cancelledAppointment, "Appointment cancelled successfully"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Handle business logic exceptions
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID", description = "Retrieves an appointment record by its ID")
    public ResponseEntity<ApiResponse<AppointmentDTO>> getAppointment(
            @Parameter(description = "Appointment ID") @PathVariable Long id) {
        try {
            AppointmentDTO appointment = appointmentService.getAppointmentById(id);
            return ResponseEntity.ok(ApiResponse.success(appointment, "Appointment retrieved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get appointments by patient", description = "Retrieves all appointments for a specific patient")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getAppointmentsByPatient(
            @Parameter(description = "Patient ID") @PathVariable Long patientId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.success(appointments, "Appointments retrieved successfully"));
    }
    
    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get appointments by doctor", description = "Retrieves all appointments for a specific doctor")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getAppointmentsByDoctor(
            @Parameter(description = "Doctor ID") @PathVariable Long doctorId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDoctor(doctorId);
        return ResponseEntity.ok(ApiResponse.success(appointments, "Appointments retrieved successfully"));
    }
    
    @GetMapping("/doctor/{doctorId}/status/{status}")
    @Operation(summary = "Get appointments by doctor and status", 
               description = "Retrieves appointments for a doctor filtered by status")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getAppointmentsByDoctorAndStatus(
            @Parameter(description = "Doctor ID") @PathVariable Long doctorId,
            @Parameter(description = "Appointment status") @PathVariable AppointmentStatus status) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDoctorAndStatus(doctorId, status);
        return ResponseEntity.ok(ApiResponse.success(appointments, "Appointments retrieved successfully"));
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update appointment status", description = "Updates the status of an appointment")
    public ResponseEntity<ApiResponse<AppointmentDTO>> updateAppointmentStatus(
            @Parameter(description = "Appointment ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam AppointmentStatus status) {
        try {
            AppointmentDTO updatedAppointment = appointmentService.updateAppointmentStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success(updatedAppointment, "Appointment status updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/doctor/{doctorId}/availability")
    @Operation(summary = "Check doctor availability at specific time", 
               description = "Checks if a doctor is available at a specific date and time")
    public ResponseEntity<ApiResponse<Boolean>> checkDoctorAvailabilityAtTime(
            @Parameter(description = "Doctor ID") @PathVariable Long doctorId,
            @Parameter(description = "Appointment date and time") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime) {
        try {
            Boolean isAvailable = appointmentService.checkDoctorAvailabilityAtTime(doctorId, appointmentDateTime);
            return ResponseEntity.ok(ApiResponse.success(isAvailable, "Doctor availability checked"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}

