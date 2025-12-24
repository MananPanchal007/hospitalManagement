package com.hospitalManagement.repository;

import com.hospitalManagement.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Doctor entity
 * 
 * Provides data access methods for doctors with support for:
 * - Standard CRUD operations (inherited from JpaRepository)
 * - Specialization and availability filtering
 * - Email-based queries
 * - Pessimistic locking for concurrent update operations
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    /**
     * Find doctor by email address
     * Used for login, duplicate checking, etc.
     * 
     * @param email Email address to search for
     * @return Optional containing doctor if found
     */
    Optional<Doctor> findByEmail(String email);
    
    /**
     * Find all doctors with a specific specialization
     * Used for filtering doctors by medical specialty
     * 
     * Examples: "Cardiology", "Pediatrics", "Orthopedics"
     * 
     * @param specialization Medical specialization to filter by
     * @return List of doctors with the given specialization
     */
    List<Doctor> findBySpecialization(String specialization);
    
    /**
     * Find all available doctors
     * Used for appointment booking to show only doctors accepting appointments
     * 
     * @return List of doctors where isAvailable = true
     */
    List<Doctor> findByIsAvailableTrue();
    
    /**
     * Find doctor by ID with PESSIMISTIC WRITE lock
     * 
     * Used during appointment booking to prevent concurrent modifications
     * to doctor availability or other critical fields.
     * 
     * PESSIMISTIC_WRITE lock:
     * - Blocks other transactions from reading or writing the same record
     * - Critical for appointment booking to prevent overbooking
     * - Ensures atomic check-and-update operations
     * 
     * @param id Doctor ID
     * @return Optional containing doctor if found (with lock acquired)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Doctor d WHERE d.id = :id")
    Optional<Doctor> findByIdWithLock(@Param("id") Long id);
    
    /**
     * Check if a doctor with the given email exists
     * Used for validation before creating new doctors
     * 
     * @param email Email address to check
     * @return true if doctor exists, false otherwise
     */
    boolean existsByEmail(String email);
}

