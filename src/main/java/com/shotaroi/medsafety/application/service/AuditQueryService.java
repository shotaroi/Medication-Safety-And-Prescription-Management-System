package com.shotaroi.medsafety.application.service;

import com.shotaroi.medsafety.api.dto.audit.AuditEventResponse;
import com.shotaroi.medsafety.api.mapper.AuditEventMapper;
import com.shotaroi.medsafety.domain.enums.AggregateType;
import com.shotaroi.medsafety.infrastructure.persistence.AuditEventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuditQueryService {

    private final AuditEventRepository repository;
    private final AuditEventMapper mapper;

    public AuditQueryService(AuditEventRepository repository, AuditEventMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<AuditEventResponse> find(AggregateType aggregateType, UUID aggregateId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        if (aggregateType != null && aggregateId != null) {
            return repository.findByAggregateTypeAndAggregateId(aggregateType, aggregateId, pageable)
                    .map(mapper::toResponse);
        }
        if (aggregateType != null) {
            return repository.findByAggregateType(aggregateType, pageable).map(mapper::toResponse);
        }
        return repository.findAll(pageable).map(mapper::toResponse);
    }
}
