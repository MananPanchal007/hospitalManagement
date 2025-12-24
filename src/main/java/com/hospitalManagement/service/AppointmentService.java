package com.hospitalManagement.service;

import com.hospitalManagement.dto.AppointmentDTO;
import com.hospitalManagement.entity.Appointment;
import com.hospitalManagement.entity.Appointment.AppointmentStatus;
import com.hospitalManagement.entity.Doctor;
import com.hospitalManagement.entity.Patient;
import com.hospitalManagement.repository.AppointmentRepository;
import com.hospitalManagement.repository.DoctorRepository;
import com.hospitalManagement.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing appointments
 * 
 * Handles appointment booking, cancellation, and status updates with:
 * - Transactional operations ensuring data consistency
 * - Pessimistic locking to prevent overbooking in concurrent scenarios
 * - Business rule validation (doctor availability, time conflicts, etc.)
 */
@Service
@RequiredArgsConstructor
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    
    /**
     * Book a new appointment
     * 
     * Transaction Management:
     * - REQUIRED propagation: Uses existing transaction or creates new one
     * - rollbackFor = Exception.class: Rolls back transaction on any exception
     * 
     * Concurrency Control:
     * - Uses pessimistic locking on doctor and appointment checks
     * - Prevents multiple users from booking the same time slot simultaneously
     * 
     * Business Rules:
     * 1. Doctor must exist and be available
     * 2. Patient must exist
     * 3. No conflicting appointments at the same time
     * 4. Appointment time must be in the future
     * 
     * @param appointmentDTO Appointment details to book
     * @return Created appointment DTO
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException if doctor is unavailable or time slot is taken
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AppointmentDTO bookAppointment(AppointmentDTO appointmentDTO) {
        // Step 1: Lock doctor record to prevent concurrent modifications
        // PESSIMISTIC_WRITE ensures no other transaction can modify doctor availability
        Doctor doctor = doctorRepository.findByIdWithLock(appointmentDTO.getDoctorId())
            .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + appointmentDTO.getDoctorId()));
        
        // Step 2: Validate doctor availability
        if (!doctor.getIsAvailable()) {
            throw new IllegalStateException("Doctor is not available for appointments");
        }
        
        // Step 3: Verify patient exists
        Patient patient = patientRepository.findById(appointmentDTO.getPatientId())
            .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + appointmentDTO.getPatientId()));
        
        // Step 4: Check for time conflicts with pessimistic lock
        // This prevents race conditions where two users try to book the same slot
        // The lock ensures only one transaction can check and book at a time
        appointmentRepository.findConflictingAppointmentWithLock(
            appointmentDTO.getDoctorId(),
            appointmentDTO.getAppointmentDateTime()
        ).ifPresent(existing -> {
            throw new IllegalStateException("Doctor already has an appointment at this time");
        });
        
        // Step 5: Validate appointment time is in the future
        // Business rule: Cannot book appointments in the past
        if (appointmentDTO.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment date must be in the future");
        }
        
        // Step 6: Create and save appointment
        // All validations passed, safe to create the appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(appointmentDTO.getAppointmentDateTime());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setReason(appointmentDTO.getReason());
        appointment.setNotes(appointmentDTO.getNotes());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return convertToDTO(savedAppointment);
    }
    
    /**
     * Cancel an existing appointment
     * 
     * Uses pessimistic locking to ensure atomic cancellation and prevent
     * concurrent modifications (e.g., someone trying to complete it at the same time)
     * 
     * Business Rules:
     * - Cannot cancel an already cancelled appointment
     * - Cannot cancel a completed appointment
     * 
     * @param appointmentId ID of appointment to cancel
     * @return Cancelled appointment DTO
     * @throws IllegalArgumentException if appointment not found
     * @throws IllegalStateException if appointment cannot be cancelled
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AppointmentDTO cancelAppointment(Long appointmentId) {
        // Lock appointment to prevent concurrent status changes
        Appointment appointment = appointmentRepository.findByIdWithLock(appointmentId)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));
        
        // Validate appointment can be cancelled
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Appointment is already cancelled");
        }
        
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed appointment");
        }
        
        // Update status and save
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return convertToDTO(updatedAppointment);
    }
    
    /**
     * Retrieve appointment by ID
     * Read-only transaction for better performance
     */
    @Transactional(readOnly = true)
    public AppointmentDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + id));
        return convertToDTO(appointment);
    }
    
    /**
     * Get all appointments for a specific patient
     * Used for patient history tracking
     * Read-only transaction for better performance
     */
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all appointments for a specific doctor
     * Used for doctor schedule management
     * Read-only transaction for better performance
     */
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get appointments for a doctor filtered by status
     * Useful for finding all scheduled, completed, or cancelled appointments
     * Read-only transaction for better performance
     */
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByDoctorAndStatus(Long doctorId, AppointmentStatus status) {
        return appointmentRepository.findByDoctorIdAndStatus(doctorId, status).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Update appointment status
     * Uses pessimistic locking to ensure atomic status updates
     * 
     * @param appointmentId ID of appointment to update
     * @param status New status to set
     * @return Updated appointment DTO
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AppointmentDTO updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        // Lock appointment to prevent concurrent status changes
        Appointment appointment = appointmentRepository.findByIdWithLock(appointmentId)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));
        
        appointment.setStatus(status);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return convertToDTO(updatedAppointment);
    }
    
    /**
     * Check if a doctor is available at a specific date and time
     * 
     * This method does NOT use pessimistic locking as it's only for checking,
     * not for booking. For actual booking, use bookAppointment() which includes locking.
     * 
     * @param doctorId ID of doctor to check
     * @param appointmentDateTime Date and time to check availability
     * @return true if doctor is available, false otherwise
     */
    @Transactional(readOnly = true)
    public Boolean checkDoctorAvailabilityAtTime(Long doctorId, LocalDateTime appointmentDateTime) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + doctorId));
        
        // Check if doctor is marked as available
        if (!doctor.getIsAvailable()) {
            return false;
        }
        
        // Check if there's a conflicting appointment
        // Note: This check is not locked, so it's only for informational purposes
        // Actual booking uses pessimistic locking to prevent race conditions
        return appointmentRepository.findConflictingAppointment(doctorId, appointmentDateTime).isEmpty();
    }
    
    /**
     * Convert Appointment entity to AppointmentDTO
     * Helper method for data transformation
     */
    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setAppointmentDateTime(appointment.getAppointmentDateTime());
        dto.setStatus(appointment.getStatus());
        dto.setReason(appointment.getReason());
        dto.setNotes(appointment.getNotes());
        return dto;
    }
}

