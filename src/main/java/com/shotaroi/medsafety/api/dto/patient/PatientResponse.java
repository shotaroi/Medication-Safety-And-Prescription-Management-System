package com.shotaroi.medsafety.api.dto.patient;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PatientResponse(
        UUID id,
        String personalNumber,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Instant createdAt
) {}
