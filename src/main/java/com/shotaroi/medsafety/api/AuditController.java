package com.shotaroi.medsafety.api;

import com.shotaroi.medsafety.api.dto.audit.AuditEventResponse;
import com.shotaroi.medsafety.application.service.AuditQueryService;
import com.shotaroi.medsafety.domain.enums.AggregateType;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditQueryService auditQueryService;

    public AuditController(AuditQueryService auditQueryService) {
        this.auditQueryService = auditQueryService;
    }

    @GetMapping
    public ResponseEntity<Page<AuditEventResponse>> list(
            @RequestParam(required = false) AggregateType aggregateType,
            @RequestParam(required = false) UUID aggregateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AuditEventResponse> result = auditQueryService.find(aggregateType, aggregateId, page, size);
        return ResponseEntity.ok(result);
    }
}
