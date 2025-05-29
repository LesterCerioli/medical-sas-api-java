package com.medicalapp.patientsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicalapp.patientsservice.model.Patient;
import com.medicalapp.patientsservice.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Use @WebMvcTest for testing the controller layer
// Specify the controller class to test
@WebMvcTest(PatientController.class)
// We don't need @ExtendWith(MockitoExtension.class) when using @WebMvcTest, as it includes Mockito support
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Use @MockBean to add mock PatientService to ApplicationContext
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "MALE", "1234567890", "john.doe@example.com", "123 Main St");
    }

    @Test
    void getAllPatients_shouldReturnListOfPatients() throws Exception {
        when(patientService.getAllPatients()).thenReturn(Collections.singletonList(patient));

        mockMvc.perform(get("/api/v1/patients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void getPatientById_shouldReturnPatient_whenFound() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(Optional.of(patient));

        mockMvc.perform(get("/api/v1/patients/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getPatientById_shouldReturnNotFound_whenMissing() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/patients/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPatient_shouldReturnCreatedPatient() throws Exception {
        when(patientService.createPatient(any(Patient.class))).thenReturn(patient);

        mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void updatePatient_shouldReturnUpdatedPatient_whenFound() throws Exception {
        Patient updatedDetails = new Patient(1L, "Jane", "Doe", LocalDate.of(1990, 1, 1), "FEMALE", "0987654321", "jane.doe@example.com", "456 Oak St");
        when(patientService.updatePatient(anyLong(), any(Patient.class))).thenReturn(Optional.of(updatedDetails));

        mockMvc.perform(put("/api/v1/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void updatePatient_shouldReturnNotFound_whenMissing() throws Exception {
        Patient updatedDetails = new Patient(1L, "Jane", "Doe", null, null, null, null, null);
        when(patientService.updatePatient(anyLong(), any(Patient.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePatient_shouldReturnNoContent_whenDeleted() throws Exception {
        when(patientService.deletePatient(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/patients/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePatient_shouldReturnNotFound_whenMissing() throws Exception {
        when(patientService.deletePatient(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/patients/1"))
                .andExpect(status().isNotFound());
    }
}
