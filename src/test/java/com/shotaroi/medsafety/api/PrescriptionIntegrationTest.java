package com.shotaroi.medsafety.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shotaroi.medsafety.IntegrationTestBase;
import com.shotaroi.medsafety.api.dto.medication.MedicationRequest;
import com.shotaroi.medsafety.api.dto.patient.PatientRequest;
import com.shotaroi.medsafety.api.dto.prescription.DosageScheduleRequest;
import com.shotaroi.medsafety.api.dto.prescription.PrescriptionRequest;
import com.shotaroi.medsafety.domain.enums.MedicationForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
class PrescriptionIntegrationTest extends IntegrationTestBase {

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
        MvcResult result = mockMvc.perform(post("/api/auth/token")
                        .param("user", user)
                        .param("role", role))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    private String createPatient() throws Exception {
        String pnr = "19900101-" + String.format("%04d", (int) (Math.random() * 10000));
        PatientRequest req = new PatientRequest(pnr, "Test", "Patient", LocalDate.of(1990, 1, 1));
        MvcResult result = mockMvc.perform(post("/api/patients")
                        .header("Authorization", "Bearer " + doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    private String createMedication() throws Exception {
        String atc = "N05AH" + String.format("%02d", (int) (Math.random() * 99));
        MedicationRequest req = new MedicationRequest(atc, "TestMed-" + UUID.randomUUID().toString().substring(0, 8), MedicationForm.TABLET, 25, 100);
        MvcResult result = mockMvc.perform(post("/api/medications")
                        .header("Authorization", "Bearer " + obtainToken("admin", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    void createPrescription_withExcessiveDose_returns422() throws Exception {
        PrescriptionRequest req = new PrescriptionRequest(
                java.util.UUID.fromString(patientId),
                java.util.UUID.fromString(medicationId),
                "Dr. Smith",
                "Take as directed",
                LocalDate.now(),
                null,
                List.of(new DosageScheduleRequest(50, 3, null))  // 150 mg/day > 100 max
        );

        mockMvc.perform(post("/api/prescriptions")
                        .header("Authorization", "Bearer " + doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Max Daily Dose Exceeded"))
                .andExpect(jsonPath("$.extensions.dailyDoseMg").value(150))
                .andExpect(jsonPath("$.extensions.maxDailyDoseMg").value(100));
    }

    @Test
    void createPrescription_idempotency_sameKeySameRequest_returnsCached() throws Exception {
        PrescriptionRequest req = new PrescriptionRequest(
                java.util.UUID.fromString(patientId),
                java.util.UUID.fromString(medicationId),
                "Dr. Smith",
                "Take once daily",
                LocalDate.now(),
                null,
                List.of(new DosageScheduleRequest(25, 1, 24))
        );
        String body = objectMapper.writeValueAsString(req);
        String key = "idem-" + System.currentTimeMillis();

        MvcResult first = mockMvc.perform(post("/api/prescriptions")
                        .header("Authorization", "Bearer " + doctorToken)
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        String firstId = objectMapper.readTree(first.getResponse().getContentAsString()).get("id").asText();

        MvcResult second = mockMvc.perform(post("/api/prescriptions")
                        .header("Authorization", "Bearer " + doctorToken)
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        String secondId = objectMapper.readTree(second.getResponse().getContentAsString()).get("id").asText();
        assert firstId.equals(secondId);
    }
}
