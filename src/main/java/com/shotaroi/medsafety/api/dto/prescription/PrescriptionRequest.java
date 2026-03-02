package com.shotaroi.medsafety.api.dto.prescription;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record PrescriptionRequest(
        @NotNull java.util.UUID patientId,
        @NotNull java.util.UUID medicationId,
        @NotBlank @Size(max = 100) String prescribedBy,
        @Size(max = 1000) String instructions,
        LocalDate startDate,
        LocalDate endDate,
        @Valid List<DosageScheduleRequest> schedules  // optional; can add via POST /prescriptions/{id}/schedule later
) {}
