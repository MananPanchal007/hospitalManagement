package com.hospitalManagement.repository;

import com.hospitalManagement.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

/**
 * Repository interface for Patient entity
 * 
 * Provides data access methods for patients with support for:
 * - Standard CRUD operations (inherited from JpaRepository)
 * - Email-based queries
 * - Pessimistic locking for concurrent update operations
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    /**
     * Find patient by email address
     * Used for login, duplicate checking, etc.
     * 
     * @param email Email address to search for
     * @return Optional containing patient if found
     */
    Optional<Patient> findByEmail(String email);
    
    /**
     * Find patient by ID with PESSIMISTIC WRITE lock
     * 
     * Used when updating patient records to prevent concurrent modifications.
     * The lock ensures only one transaction can modify the patient at a time.
     * 
     * PESSIMISTIC_WRITE lock:
     * - Blocks other transactions from reading or writing the same record
     * - Ensures data consistency during updates
     * - Prevents lost updates in concurrent scenarios
     * 
     * @param id Patient ID
     * @return Optional containing patient if found (with lock acquired)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Patient p WHERE p.id = :id")
    Optional<Patient> findByIdWithLock(@Param("id") Long id);
    
    /**
     * Check if a patient with the given email exists
     * Used for validation before creating new patients
     * 
     * @param email Email address to check
     * @return true if patient exists, false otherwise
     */
    boolean existsByEmail(String email);
}

