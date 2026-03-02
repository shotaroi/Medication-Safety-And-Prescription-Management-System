package com.shotaroi.medsafety.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shotaroi.medsafety.api.dto.prescription.DosageScheduleRequest;
import com.shotaroi.medsafety.api.dto.prescription.DosageScheduleResponse;
import com.shotaroi.medsafety.api.dto.prescription.PrescriptionResponse;
import com.shotaroi.medsafety.api.dto.prescription.PrescriptionRequest;
import com.shotaroi.medsafety.application.service.IdempotencyService;
import com.shotaroi.medsafety.application.service.PrescriptionService;
import com.shotaroi.medsafety.common.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";

    private final PrescriptionService prescriptionService;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    public PrescriptionController(PrescriptionService prescriptionService,
                                  IdempotencyService idempotencyService,
                                  ObjectMapper objectMapper) {
        this.prescriptionService = prescriptionService;
        this.idempotencyService = idempotencyService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PrescriptionRequest request,
                                   @RequestHeader(value = "X-User-Id", required = false) String userId,
                                   @RequestHeader(value = IDEMPOTENCY_KEY_HEADER, required = false) String idempotencyKey) throws IOException {
        String requestBody = objectMapper.writeValueAsString(request);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var existing = idempotencyService.findExisting(idempotencyKey, requestBody);
            if (existing.isPresent()) {
                var result = existing.get();
                return ResponseEntity
                        .status(result.status())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.readValue(result.body(), PrescriptionResponse.class));
            }
        }

        PrescriptionResponse response = prescriptionService.create(request, CurrentUser.get(userId, "doctor"));

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyService.store(idempotencyKey, requestBody, HttpStatus.CREATED.value(),
                    objectMapper.writeValueAsString(response));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(prescriptionService.getById(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<PrescriptionResponse> cancel(@PathVariable UUID id,
                                                      @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return ResponseEntity.ok(prescriptionService.cancel(id, CurrentUser.get(userId, "pharmacist")));
    }

    @PostMapping("/{id}/schedule")
    public ResponseEntity<DosageScheduleResponse> addSchedule(@PathVariable UUID id,
                                                              @Valid @RequestBody DosageScheduleRequest request,
                                                              @RequestHeader(value = "X-User-Id", required = false) String userId) {
        DosageScheduleResponse response = prescriptionService.addSchedule(id, request, CurrentUser.get(userId, "doctor"));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
