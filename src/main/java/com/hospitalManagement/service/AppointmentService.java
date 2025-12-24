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

@Service
@RequiredArgsConstructor
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AppointmentDTO bookAppointment(AppointmentDTO appointmentDTO) {
        // Use pessimistic locking to prevent overbooking
        Doctor doctor = doctorRepository.findByIdWithLock(appointmentDTO.getDoctorId())
            .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + appointmentDTO.getDoctorId()));
        
        if (!doctor.getIsAvailable()) {
            throw new IllegalStateException("Doctor is not available for appointments");
        }
        
        Patient patient = patientRepository.findById(appointmentDTO.getPatientId())
            .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + appointmentDTO.getPatientId()));
        
        // Check for conflicting appointments using pessimistic lock
        appointmentRepository.findConflictingAppointmentWithLock(
            appointmentDTO.getDoctorId(),
            appointmentDTO.getAppointmentDateTime()
        ).ifPresent(existing -> {
            throw new IllegalStateException("Doctor already has an appointment at this time");
        });
        
        // Validate appointment time is in the future
        if (appointmentDTO.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment date must be in the future");
        }
        
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
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AppointmentDTO cancelAppointment(Long appointmentId) {
        // Use pessimistic locking to ensure atomic cancellation
        Appointment appointment = appointmentRepository.findByIdWithLock(appointmentId)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));
        
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Appointment is already cancelled");
        }
        
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed appointment");
        }
        
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return convertToDTO(updatedAppointment);
    }
    
    @Transactional(readOnly = true)
    public AppointmentDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + id));
        return convertToDTO(appointment);
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByDoctorAndStatus(Long doctorId, AppointmentStatus status) {
        return appointmentRepository.findByDoctorIdAndStatus(doctorId, status).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AppointmentDTO updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findByIdWithLock(appointmentId)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));
        
        appointment.setStatus(status);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return convertToDTO(updatedAppointment);
    }
    
    @Transactional(readOnly = true)
    public Boolean checkDoctorAvailabilityAtTime(Long doctorId, LocalDateTime appointmentDateTime) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + doctorId));
        
        if (!doctor.getIsAvailable()) {
            return false;
        }
        
        return appointmentRepository.findConflictingAppointment(doctorId, appointmentDateTime).isEmpty();
    }
    
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

