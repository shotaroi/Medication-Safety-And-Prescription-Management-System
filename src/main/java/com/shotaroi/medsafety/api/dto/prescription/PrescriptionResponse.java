package com.shotaroi.medsafety.api.dto.prescription;

import com.shotaroi.medsafety.domain.enums.PrescriptionStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PrescriptionResponse(
        UUID id,
        UUID patientId,
        UUID medicationId,
        String prescribedBy,
        String instructions,
        PrescriptionStatus status,
        LocalDate startDate,
        LocalDate endDate,
        Long version,
        Instant createdAt,
        Instant updatedAt,
        List<DosageScheduleResponse> schedules,
        List<InteractionWarning> warnings
) {
    public PrescriptionResponse(UUID id, UUID patientId, UUID medicationId, String prescribedBy,
                                String instructions, PrescriptionStatus status, LocalDate startDate,
                                LocalDate endDate, Long version, Instant createdAt, Instant updatedAt,
                                List<DosageScheduleResponse> schedules) {
        this(id, patientId, medicationId, prescribedBy, instructions, status, startDate, endDate,
                version, createdAt, updatedAt, schedules, List.of());
    }
}
