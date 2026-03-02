package com.shotaroi.medsafety.api.dto.prescription;

import java.time.Instant;
import java.util.UUID;

public record DosageScheduleResponse(
        UUID id,
        UUID prescriptionId,
        Integer doseMg,
        Integer timesPerDay,
        Integer intervalHours,
        Instant createdAt
) {}
