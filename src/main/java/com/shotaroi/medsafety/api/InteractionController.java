package com.shotaroi.medsafety.api;

import com.shotaroi.medsafety.api.dto.interaction.InteractionCheckRequest;
import com.shotaroi.medsafety.api.dto.interaction.InteractionCheckResponse;
import com.shotaroi.medsafety.application.service.InteractionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/interactions")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping("/check")
    public ResponseEntity<InteractionCheckResponse> check(@Valid @RequestBody InteractionCheckRequest request) {
        List<com.shotaroi.medsafety.api.dto.prescription.InteractionWarning> warnings;
        if (request.hasPatientAndMedication()) {
            warnings = interactionService.checkForPatient(request.patientId(), request.medicationId(), false);
        } else if (request.hasAtcCodes()) {
            warnings = interactionService.checkForAtcCodes(request.atcCodes());
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new InteractionCheckResponse(warnings));
    }
}
