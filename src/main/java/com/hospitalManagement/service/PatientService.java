package com.hospitalManagement.service;

import com.hospitalManagement.entity.Patient;
import com.hospitalManagement.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public Patient getPetientById(Long id){
        Patient p1 = patientRepository.findById(id).orElseThrow();
        return p1;
    }
}
