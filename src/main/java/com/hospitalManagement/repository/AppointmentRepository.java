package com.hospitalManagement.repository;

import com.hospitalManagement.entity.Appointment;
import com.hospitalManagement.entity.Appointment.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Appointment entity
 * 
 * Provides data access methods for appointments with support for:
 * - Pessimistic locking to prevent concurrent booking conflicts
 * - Query methods for finding appointments by various criteria
 * - Conflict detection for appointment scheduling
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    /**
     * Find all appointments for a specific patient
     * Used for retrieving patient appointment history
     */
    List<Appointment> findByPatientId(Long patientId);
    
    /**
     * Find all appointments for a specific doctor
     * Used for retrieving doctor's schedule
     */
    List<Appointment> findByDoctorId(Long doctorId);
    
    /**
     * Find appointments for a doctor filtered by status
     * Useful for finding all scheduled, completed, or cancelled appointments
     */
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);
    
    /**
     * Check if there's a conflicting appointment at the same time
     * Excludes cancelled appointments from the check
     * Used for availability checking without locking
     */
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDateTime = :appointmentDateTime " +
           "AND a.status != 'CANCELLED'")
    Optional<Appointment> findConflictingAppointment(
        @Param("doctorId") Long doctorId,
        @Param("appointmentDateTime") LocalDateTime appointmentDateTime
    );
    
    /**
     * Check for conflicting appointments with PESSIMISTIC WRITE lock
     * 
     * PESSIMISTIC_WRITE lock ensures:
     * - No other transaction can read or write the same appointment
     * - Prevents race conditions in high-concurrency scenarios
     * - Used during appointment booking to prevent overbooking
     * 
     * This is critical for preventing double-booking when multiple users
     * try to book the same time slot simultaneously
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDateTime = :appointmentDateTime " +
           "AND a.status != 'CANCELLED'")
    Optional<Appointment> findConflictingAppointmentWithLock(
        @Param("doctorId") Long doctorId,
        @Param("appointmentDateTime") LocalDateTime appointmentDateTime
    );
    
    /**
     * Count appointments for a doctor within a time range
     * Excludes cancelled appointments
     * Useful for scheduling and availability analysis
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDateTime BETWEEN :startTime AND :endTime " +
           "AND a.status != 'CANCELLED'")
    long countAppointmentsInTimeRange(
        @Param("doctorId") Long doctorId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * Find appointment by ID with PESSIMISTIC WRITE lock
     * Used when updating or cancelling appointments to ensure
     * atomic operations and prevent concurrent modifications
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Appointment a WHERE a.id = :id")
    Optional<Appointment> findByIdWithLock(@Param("id") Long id);
}

