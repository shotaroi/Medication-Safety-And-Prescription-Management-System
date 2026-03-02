package com.shotaroi.medsafety.api.dto.prescription;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DosageScheduleRequest(
        @NotNull @Min(1) Integer doseMg,
        @NotNull @Min(1) Integer timesPerDay,
        @Min(1) Integer intervalHours
) {}
