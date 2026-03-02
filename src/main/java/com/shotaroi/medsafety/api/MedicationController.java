package com.shotaroi.medsafety.api;

import com.shotaroi.medsafety.api.dto.medication.MedicationRequest;
import com.shotaroi.medsafety.api.dto.medication.MedicationResponse;
import com.shotaroi.medsafety.application.service.MedicationService;
import com.shotaroi.medsafety.common.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    @PostMapping
    public ResponseEntity<MedicationResponse> create(@Valid @RequestBody MedicationRequest request,
                                                      @RequestHeader(value = "X-User-Id", required = false) String userId) {
        MedicationResponse response = medicationService.create(request, CurrentUser.get(userId, "admin"));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MedicationResponse>> list() {
        return ResponseEntity.ok(medicationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicationResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(medicationService.getById(id));
    }
}
