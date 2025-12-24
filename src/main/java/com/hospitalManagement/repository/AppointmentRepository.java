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

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByPatientId(Long patientId);
    
    List<Appointment> findByDoctorId(Long doctorId);
    
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDateTime = :appointmentDateTime " +
           "AND a.status != 'CANCELLED'")
    Optional<Appointment> findConflictingAppointment(
        @Param("doctorId") Long doctorId,
        @Param("appointmentDateTime") LocalDateTime appointmentDateTime
    );
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDateTime = :appointmentDateTime " +
           "AND a.status != 'CANCELLED'")
    Optional<Appointment> findConflictingAppointmentWithLock(
        @Param("doctorId") Long doctorId,
        @Param("appointmentDateTime") LocalDateTime appointmentDateTime
    );
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDateTime BETWEEN :startTime AND :endTime " +
           "AND a.status != 'CANCELLED'")
    long countAppointmentsInTimeRange(
        @Param("doctorId") Long doctorId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Appointment a WHERE a.id = :id")
    Optional<Appointment> findByIdWithLock(@Param("id") Long id);
}

