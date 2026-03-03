package com.shotaroi.medsafety.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shotaroi.medsafety.IntegrationTestBase;
import com.shotaroi.medsafety.api.dto.medication.MedicationRequest;
import com.shotaroi.medsafety.api.dto.patient.PatientRequest;
import com.shotaroi.medsafety.api.dto.prescription.PrescriptionRequest;
import com.shotaroi.medsafety.domain.enums.MedicationForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
class OverlapIntegrationTest extends IntegrationTestBase {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    String doctorToken;
    String patientId;
    String medicationId;

    @BeforeEach
    void setUp() throws Exception {
        doctorToken = obtainToken("doctor", "DOCTOR");
        patientId = createPatient();
        medicationId = createMedication();
    }

    private String obtainToken(String user, String role) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/token").param("user", user).param("role", role))
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    private String createPatient() throws Exception {
        String pnr = "19900101-" + String.format("%04d", (int)(Math.random() * 10000));
        PatientRequest req = new PatientRequest(pnr, "Test", "Patient", LocalDate.of(1990, 1, 1));
        MvcResult result = mockMvc.perform(post("/api/patients").header("Authorization", "Bearer " + doctorToken)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    private String createMedication() throws Exception {
        String atc = "N05AX" + String.format("%02d", (int)(Math.random() * 99));
        MedicationRequest req = new MedicationRequest(atc, "TestMed", MedicationForm.TABLET, 25, 100);
        MvcResult result = mockMvc.perform(post("/api/medications").header("Authorization", "Bearer " + obtainToken("admin", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    void createOverlappingPrescription_returns409() throws Exception {
        PrescriptionRequest req = new PrescriptionRequest(
                UUID.fromString(patientId), UUID.fromString(medicationId), "Dr. Smith", "Take daily",
                LocalDate.now(), LocalDate.now().plusDays(30), null);

        mockMvc.perform(post("/api/prescriptions").header("Authorization", "Bearer " + doctorToken)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/prescriptions").header("Authorization", "Bearer " + doctorToken)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Prescription Overlap"));
    }
}
