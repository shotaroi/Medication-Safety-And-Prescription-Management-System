package com.shotaroi.medsafety.api.dto.audit;

import com.shotaroi.medsafety.domain.enums.AggregateType;
import com.shotaroi.medsafety.domain.enums.AuditAction;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AuditEventResponse(
        UUID id,
        AggregateType aggregateType,
        UUID aggregateId,
        AuditAction action,
        String performedBy,
        String correlationId,
        Map<String, Object> payloadJson,
        Instant createdAt
) {}
