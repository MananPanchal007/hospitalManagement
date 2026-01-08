//package com.hospitalManagement.controller;
//
//import com.hospitalManagement.service.PatientService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/patients")
//@RequiredArgsConstructor
//public class PatientController {
//
//    private final PatientService patientService;
//    private final AppointmentService appointmentService;
//
//    @PostMapping("/appointments")
//    public ResponseEntity<AppointmentResponseDto> createNewAppointment(@RequestBody CreateAppointmentRequestDto createAppointmentRequestDto) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createNewAppointment(createAppointmentRequestDto));
//    }
//
//    @GetMapping("/profile")
//    private ResponseEntity<PatientResponseDto> getPatientProfile() {
//        Long patientId = 4L;
//        return ResponseEntity.ok(patientService.getPatientById(patientId));
//    }