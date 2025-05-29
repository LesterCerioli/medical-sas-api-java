package com.medicalapp.patientsservice.service;

import com.medicalapp.patientsservice.model.Patient;
import com.medicalapp.patientsservice.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "MALE", "1234567890", "john.doe@example.com", "123 Main St");
    }

    @Test
    void getAllPatients_shouldReturnListOfPatients() {
        when(patientRepository.findAll()).thenReturn(Collections.singletonList(patient));
        List<Patient> patients = patientService.getAllPatients();
        assertFalse(patients.isEmpty());
        assertEquals(1, patients.size());
        assertEquals("John", patients.get(0).getFirstName());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void getPatientById_shouldReturnPatient_whenFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        Optional<Patient> foundPatient = patientService.getPatientById(1L);
        assertTrue(foundPatient.isPresent());
        assertEquals("John", foundPatient.get().getFirstName());
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void getPatientById_shouldReturnEmpty_whenNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Patient> foundPatient = patientService.getPatientById(1L);
        assertFalse(foundPatient.isPresent());
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void createPatient_shouldReturnSavedPatient() {
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        Patient createdPatient = patientService.createPatient(new Patient()); // pass empty or mock patient
        assertNotNull(createdPatient);
        assertEquals("John", createdPatient.getFirstName());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void updatePatient_shouldReturnUpdatedPatient_whenFound() {
        Patient updatedDetails = new Patient(1L, "Jane", "Doe", LocalDate.of(1990, 1, 1), "FEMALE", "0987654321", "jane.doe@example.com", "456 Oak St");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedDetails); // mock save to return the updated patient

        Optional<Patient> result = patientService.updatePatient(1L, updatedDetails);

        assertTrue(result.isPresent());
        assertEquals("Jane", result.get().getFirstName());
        assertEquals("FEMALE", result.get().getGender());
        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).save(patient); // verify original patient object was mutated and saved
    }

    @Test
    void updatePatient_shouldReturnEmpty_whenNotFound() {
        Patient updatedDetails = new Patient(null, "Jane", "Doe", null, null, null, null, null);
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Patient> result = patientService.updatePatient(1L, updatedDetails);
        assertFalse(result.isPresent());
        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void deletePatient_shouldReturnTrue_whenDeleted() {
        when(patientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(1L);
        boolean deleted = patientService.deletePatient(1L);
        assertTrue(deleted);
        verify(patientRepository, times(1)).existsById(1L);
        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletePatient_shouldReturnFalse_whenNotFound() {
        when(patientRepository.existsById(1L)).thenReturn(false);
        boolean deleted = patientService.deletePatient(1L);
        assertFalse(deleted);
        verify(patientRepository, times(1)).existsById(1L);
        verify(patientRepository, never()).deleteById(1L);
    }
}
