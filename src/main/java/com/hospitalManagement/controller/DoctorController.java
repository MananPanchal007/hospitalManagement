package com.hospitalManagement.controller;

import com.hospitalManagement.dto.ApiResponse;
import com.hospitalManagement.dto.DoctorDTO;
import com.hospitalManagement.service.DoctorService;
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
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Management", description = "APIs for managing doctor records and availability")
public class DoctorController {
    
    private final DoctorService doctorService;
    
    @PostMapping
    @Operation(summary = "Create a new doctor", description = "Creates a new doctor record with validation")
    public ResponseEntity<ApiResponse<DoctorDTO>> createDoctor(
            @Valid @RequestBody DoctorDTO doctorDTO) {
        try {
            DoctorDTO createdDoctor = doctorService.createDoctor(doctorDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdDoctor, "Doctor created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get doctor by ID", description = "Retrieves a doctor record by their ID")
    public ResponseEntity<ApiResponse<DoctorDTO>> getDoctor(
            @Parameter(description = "Doctor ID") @PathVariable Long id) {
        try {
            DoctorDTO doctor = doctorService.getDoctorById(id);
            return ResponseEntity.ok(ApiResponse.success(doctor, "Doctor retrieved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all doctors", description = "Retrieves all doctor records")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getAllDoctors() {
        List<DoctorDTO> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(ApiResponse.success(doctors, "Doctors retrieved successfully"));
    }
    
    @GetMapping("/available")
    @Operation(summary = "Get available doctors", description = "Retrieves all doctors who are currently available")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getAvailableDoctors() {
        List<DoctorDTO> doctors = doctorService.getAvailableDoctors();
        return ResponseEntity.ok(ApiResponse.success(doctors, "Available doctors retrieved successfully"));
    }
    
    @GetMapping("/specialization/{specialization}")
    @Operation(summary = "Get doctors by specialization", description = "Retrieves doctors filtered by specialization")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getDoctorsBySpecialization(
            @Parameter(description = "Doctor specialization") @PathVariable String specialization) {
        List<DoctorDTO> doctors = doctorService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(ApiResponse.success(doctors, "Doctors retrieved successfully"));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update doctor", description = "Updates an existing doctor record")
    public ResponseEntity<ApiResponse<DoctorDTO>> updateDoctor(
            @Parameter(description = "Doctor ID") @PathVariable Long id,
            @Valid @RequestBody DoctorDTO doctorDTO) {
        try {
            DoctorDTO updatedDoctor = doctorService.updateDoctor(id, doctorDTO);
            return ResponseEntity.ok(ApiResponse.success(updatedDoctor, "Doctor updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete doctor", description = "Deletes a doctor record")
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(
            @Parameter(description = "Doctor ID") @PathVariable Long id) {
        try {
            doctorService.deleteDoctor(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Doctor deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/availability")
    @Operation(summary = "Check doctor availability", description = "Checks if a doctor is available for appointments")
    public ResponseEntity<ApiResponse<Boolean>> checkDoctorAvailability(
            @Parameter(description = "Doctor ID") @PathVariable Long id) {
        try {
            Boolean isAvailable = doctorService.checkDoctorAvailability(id);
            return ResponseEntity.ok(ApiResponse.success(isAvailable, "Doctor availability checked"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}

