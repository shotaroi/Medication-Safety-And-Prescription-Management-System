package com.shotaroi.medsafety.application.service;

import com.shotaroi.medsafety.common.CorrelationIdHolder;
import com.shotaroi.medsafety.domain.entity.AuditEvent;
import com.shotaroi.medsafety.domain.enums.AggregateType;
import com.shotaroi.medsafety.domain.enums.AuditAction;
import com.shotaroi.medsafety.infrastructure.persistence.AuditEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class AuditService {

    private final AuditEventRepository repository;

    public AuditService(AuditEventRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void audit(AggregateType aggregateType, UUID aggregateId, AuditAction action,
                      String performedBy, Map<String, Object> payload) {
        AuditEvent event = new AuditEvent();
        event.setAggregateType(aggregateType);
        event.setAggregateId(aggregateId);
        event.setAction(action);
        event.setPerformedBy(performedBy);
        event.setCorrelationId(CorrelationIdHolder.get());
        event.setPayloadJson(payload);
        repository.save(event);
    }
}
