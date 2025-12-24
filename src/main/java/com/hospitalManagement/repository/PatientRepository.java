package com.hospitalManagement.repository;

import com.hospitalManagement.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    Optional<Patient> findByEmail(String email);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Patient p WHERE p.id = :id")
    Optional<Patient> findByIdWithLock(@Param("id") Long id);
    
    boolean existsByEmail(String email);
}

