package com.shotaroi.medsafety.api.dto.prescription;

import com.shotaroi.medsafety.domain.enums.InteractionSeverity;

public record InteractionWarning(
        String atcCodeA,
        String atcCodeB,
        InteractionSeverity severity,
        String message
) {}
