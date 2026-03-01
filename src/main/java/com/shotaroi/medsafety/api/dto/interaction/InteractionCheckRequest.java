package com.shotaroi.medsafety.api.dto.interaction;

import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record InteractionCheckRequest(
        UUID patientId,
        UUID medicationId,
        @Size(min = 2, max = 10) List<String> atcCodes
) {
    public boolean hasPatientAndMedication() {
        return patientId != null && medicationId != null;
    }

    public boolean hasAtcCodes() {
        return atcCodes != null && atcCodes.size() >= 2;
    }
}
