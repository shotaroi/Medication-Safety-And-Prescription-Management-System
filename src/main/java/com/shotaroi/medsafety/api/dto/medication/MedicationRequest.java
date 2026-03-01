package com.shotaroi.medsafety.api.dto.medication;

import com.shotaroi.medsafety.domain.enums.MedicationForm;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MedicationRequest(
        @NotBlank @Size(max = 20) String atcCode,
        @NotBlank @Size(max = 200) String name,
        @NotNull MedicationForm form,
        @NotNull @Min(1) Integer strengthMg,
        @NotNull @Min(1) Integer maxDailyDoseMg
) {}
