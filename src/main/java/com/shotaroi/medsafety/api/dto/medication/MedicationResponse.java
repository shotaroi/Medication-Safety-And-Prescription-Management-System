package com.shotaroi.medsafety.api.dto.medication;

import com.shotaroi.medsafety.domain.enums.MedicationForm;

import java.time.Instant;
import java.util.UUID;

public record MedicationResponse(
        UUID id,
        String atcCode,
        String name,
        MedicationForm form,
        Integer strengthMg,
        Integer maxDailyDoseMg,
        Instant createdAt
) {}
