package com.hospitalManagement.controller;

import com.hospitalManagement.dto.ApiResponse;
import com.hospitalManagement.dto.PatientDTO;
import com.hospitalManagement.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Management", description = "APIs for managing patient records")
public class PatientController {
    
    private final PatientService patientService;
    
    @PostMapping
    @Operation(summary = "Create a new patient", description = "Creates a new patient record with validation")
    public ResponseEntity<ApiResponse<PatientDTO>> createPatient(
            @Valid @RequestBody PatientDTO patientDTO) {
        try {
            PatientDTO createdPatient = patientService.createPatient(patientDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdPatient, "Patient created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID", description = "Retrieves a patient record by their ID")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatient(
            @Parameter(description = "Patient ID") @PathVariable Long id) {
        try {
            PatientDTO patient = patientService.getPatientById(id);
            return ResponseEntity.ok(ApiResponse.success(patient, "Patient retrieved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all patients", description = "Retrieves all patient records")
    public ResponseEntity<ApiResponse<List<PatientDTO>>> getAllPatients() {
        List<PatientDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(ApiResponse.success(patients, "Patients retrieved successfully"));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update patient", description = "Updates an existing patient record")
    public ResponseEntity<ApiResponse<PatientDTO>> updatePatient(
            @Parameter(description = "Patient ID") @PathVariable Long id,
            @Valid @RequestBody PatientDTO patientDTO) {
        try {
            PatientDTO updatedPatient = patientService.updatePatient(id, patientDTO);
            return ResponseEntity.ok(ApiResponse.success(updatedPatient, "Patient updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete patient", description = "Deletes a patient record")
    public ResponseEntity<ApiResponse<Void>> deletePatient(
            @Parameter(description = "Patient ID") @PathVariable Long id) {
        try {
            patientService.deletePatient(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Patient deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/history")
    @Operation(summary = "Get patient history", description = "Retrieves patient history and records")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatientHistory(
            @Parameter(description = "Patient ID") @PathVariable Long id) {
        try {
            PatientDTO patient = patientService.getPatientHistory(id);
            return ResponseEntity.ok(ApiResponse.success(patient, "Patient history retrieved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}

