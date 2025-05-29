package com.medicalapp.patientsservice.service;

import com.medicalapp.patientsservice.model.Patient;
import com.medicalapp.patientsservice.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    @Transactional
    public Patient createPatient(Patient patient) {
        // Add any validation or business logic before saving
        return patientRepository.save(patient);
    }

    @Transactional
    public Optional<Patient> updatePatient(Long id, Patient patientDetails) {
        return patientRepository.findById(id)
            .map(existingPatient -> {
                existingPatient.setFirstName(patientDetails.getFirstName());
                existingPatient.setLastName(patientDetails.getLastName());
                existingPatient.setDateOfBirth(patientDetails.getDateOfBirth());
                existingPatient.setGender(patientDetails.getGender());
                existingPatient.setContactNumber(patientDetails.getContactNumber());
                existingPatient.setEmail(patientDetails.getEmail());
                existingPatient.setAddress(patientDetails.getAddress());
                return patientRepository.save(existingPatient);
            });
    }

    @Transactional
    public boolean deletePatient(Long id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
