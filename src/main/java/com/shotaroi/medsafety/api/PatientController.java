package com.shotaroi.medsafety.api;

import com.shotaroi.medsafety.api.dto.patient.PatientRequest;
import com.shotaroi.medsafety.api.dto.patient.PatientResponse;
import com.shotaroi.medsafety.application.service.PatientService;
import com.shotaroi.medsafety.application.service.PrescriptionService;
import com.shotaroi.medsafety.common.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final PrescriptionService prescriptionService;

    public PatientController(PatientService patientService, PrescriptionService prescriptionService) {
        this.patientService = patientService;
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    public ResponseEntity<PatientResponse> create(@Valid @RequestBody PatientRequest request,
                                                  @RequestHeader(value = "X-User-Id", required = false) String userId) {
        PatientResponse response = patientService.create(request, CurrentUser.get(userId, "system"));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getById(@PathVariable UUID id) {
        PatientResponse response = patientService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getByPersonalNumber(@RequestParam(required = false) String personalNumber) {
        if (personalNumber == null || personalNumber.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Optional<PatientResponse> response = patientService.getByPersonalNumber(personalNumber);
        return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/prescriptions")
    public ResponseEntity<List<com.shotaroi.medsafety.api.dto.prescription.PrescriptionResponse>> getPrescriptions(@PathVariable UUID id) {
        List<com.shotaroi.medsafety.api.dto.prescription.PrescriptionResponse> prescriptions = prescriptionService.findByPatientId(id);
        return ResponseEntity.ok(prescriptions);
    }
}
