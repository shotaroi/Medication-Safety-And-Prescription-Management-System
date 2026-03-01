package com.shotaroi.medsafety.api.dto.druginteraction;

import com.shotaroi.medsafety.domain.enums.InteractionSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DrugInteractionRuleRequest(
        @NotBlank @Size(max = 20) String atcCodeA,
        @NotBlank @Size(max = 20) String atcCodeB,
        @NotNull InteractionSeverity severity,
        @NotBlank @Size(max = 500) String message
) {}
