package com.hospitalManagement.service;

import com.hospitalManagement.dto.DoctorDTO;
import com.hospitalManagement.entity.Doctor;
import com.hospitalManagement.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {
    
    private final DoctorRepository doctorRepository;
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        if (doctorRepository.existsByEmail(doctorDTO.getEmail())) {
            throw new IllegalArgumentException("Doctor with email " + doctorDTO.getEmail() + " already exists");
        }
        
        Doctor doctor = convertToEntity(doctorDTO);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return convertToDTO(savedDoctor);
    }
    
    @Transactional(readOnly = true)
    public DoctorDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + id));
        return convertToDTO(doctor);
    }
    
    @Transactional(readOnly = true)
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<DoctorDTO> getAvailableDoctors() {
        return doctorRepository.findByIsAvailableTrue().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DoctorDTO updateDoctor(Long id, DoctorDTO doctorDTO) {
        Doctor doctor = doctorRepository.findByIdWithLock(id)
            .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + id));
        
        // Check if email is being changed and if new email already exists
        if (!doctor.getEmail().equals(doctorDTO.getEmail()) && 
            doctorRepository.existsByEmail(doctorDTO.getEmail())) {
            throw new IllegalArgumentException("Doctor with email " + doctorDTO.getEmail() + " already exists");
        }
        
        updateDoctorFields(doctor, doctorDTO);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        return convertToDTO(updatedDoctor);
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new IllegalArgumentException("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Boolean checkDoctorAvailability(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + doctorId));
        return doctor.getIsAvailable();
    }
    
    private Doctor convertToEntity(DoctorDTO dto) {
        Doctor doctor = new Doctor();
        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
        doctor.setEmail(dto.getEmail());
        doctor.setPhoneNumber(dto.getPhoneNumber());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setDepartment(dto.getDepartment());
        doctor.setConsultationFee(dto.getConsultationFee());
        doctor.setIsAvailable(dto.getIsAvailable() != null ? dto.getIsAvailable() : true);
        return doctor;
    }
    
    private DoctorDTO convertToDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setEmail(doctor.getEmail());
        dto.setPhoneNumber(doctor.getPhoneNumber());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setDepartment(doctor.getDepartment());
        dto.setConsultationFee(doctor.getConsultationFee());
        dto.setIsAvailable(doctor.getIsAvailable());
        return dto;
    }
    
    private void updateDoctorFields(Doctor doctor, DoctorDTO dto) {
        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
        doctor.setEmail(dto.getEmail());
        doctor.setPhoneNumber(dto.getPhoneNumber());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setDepartment(dto.getDepartment());
        doctor.setConsultationFee(dto.getConsultationFee());
        if (dto.getIsAvailable() != null) {
            doctor.setIsAvailable(dto.getIsAvailable());
        }
    }
}

