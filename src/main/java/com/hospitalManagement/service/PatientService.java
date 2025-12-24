package com.hospitalManagement.service;

import com.hospitalManagement.dto.PatientDTO;
import com.hospitalManagement.entity.Patient;
import com.hospitalManagement.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing patient operations
 * 
 * Handles CRUD operations for patients with:
 * - Transactional management ensuring data consistency
 * - Pessimistic locking for update operations to prevent concurrent modifications
 * - Email uniqueness validation
 */
@Service
@RequiredArgsConstructor
public class PatientService {
    
    private final PatientRepository patientRepository;
    
    /**
     * Create a new patient record
     * 
     * Transaction Management:
     * - REQUIRED propagation: Uses existing transaction or creates new one
     * - rollbackFor = Exception.class: Rolls back on any exception
     * 
     * Business Rules:
     * - Email must be unique (validated before creation)
     * 
     * @param patientDTO Patient data to create
     * @return Created patient DTO
     * @throws IllegalArgumentException if email already exists
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PatientDTO createPatient(PatientDTO patientDTO) {
        // Validate email uniqueness before creating patient
        if (patientRepository.existsByEmail(patientDTO.getEmail())) {
            throw new IllegalArgumentException("Patient with email " + patientDTO.getEmail() + " already exists");
        }
        
        // Convert DTO to entity and save
        Patient patient = convertToEntity(patientDTO);
        Patient savedPatient = patientRepository.save(patient);
        return convertToDTO(savedPatient);
    }
    
    /**
     * Retrieve patient by ID
     * Read-only transaction for better performance
     * 
     * @param id Patient ID
     * @return Patient DTO
     * @throws IllegalArgumentException if patient not found
     */
    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + id));
        return convertToDTO(patient);
    }
    
    /**
     * Retrieve all patients
     * Read-only transaction for better performance
     * 
     * @return List of all patient DTOs
     */
    @Transactional(readOnly = true)
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Update an existing patient record
     * 
     * Uses pessimistic locking to prevent concurrent modifications
     * 
     * Business Rules:
     * - Email uniqueness is validated if email is being changed
     * 
     * @param id Patient ID to update
     * @param patientDTO Updated patient data
     * @return Updated patient DTO
     * @throws IllegalArgumentException if patient not found or email conflict
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
        // Lock patient record to prevent concurrent modifications
        Patient patient = patientRepository.findByIdWithLock(id)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + id));
        
        // Check if email is being changed and if new email already exists
        // This prevents duplicate emails when updating patient information
        if (!patient.getEmail().equals(patientDTO.getEmail()) && 
            patientRepository.existsByEmail(patientDTO.getEmail())) {
            throw new IllegalArgumentException("Patient with email " + patientDTO.getEmail() + " already exists");
        }
        
        updatePatientFields(patient, patientDTO);
        Patient updatedPatient = patientRepository.save(patient);
        return convertToDTO(updatedPatient);
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new IllegalArgumentException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public PatientDTO getPatientHistory(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + patientId));
        return convertToDTO(patient);
    }
    
    private Patient convertToEntity(PatientDTO dto) {
        Patient patient = new Patient();
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setEmail(dto.getEmail());
        patient.setPhoneNumber(dto.getPhoneNumber());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setAddress(dto.getAddress());
        patient.setMedicalHistory(dto.getMedicalHistory());
        return patient;
    }
    
    private PatientDTO convertToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setEmail(patient.getEmail());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setAddress(patient.getAddress());
        dto.setMedicalHistory(patient.getMedicalHistory());
        return dto;
    }
    
    private void updatePatientFields(Patient patient, PatientDTO dto) {
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setEmail(dto.getEmail());
        patient.setPhoneNumber(dto.getPhoneNumber());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setAddress(dto.getAddress());
        if (dto.getMedicalHistory() != null) {
            patient.setMedicalHistory(dto.getMedicalHistory());
        }
    }
}

