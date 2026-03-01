package com.shotaroi.medsafety.api.dto.druginteraction;

import com.shotaroi.medsafety.domain.enums.InteractionSeverity;

import java.time.Instant;
import java.util.UUID;

public record DrugInteractionRuleResponse(
        UUID id,
        String atcCodeA,
        String atcCodeB,
        InteractionSeverity severity,
        String message,
        Instant createdAt
) {}
