package com.shotaroi.medsafety.application.service;

import com.shotaroi.medsafety.api.dto.medication.MedicationRequest;
import com.shotaroi.medsafety.api.dto.medication.MedicationResponse;
import com.shotaroi.medsafety.api.mapper.MedicationMapper;
import com.shotaroi.medsafety.domain.entity.Medication;
import com.shotaroi.medsafety.domain.enums.AggregateType;
import com.shotaroi.medsafety.domain.enums.AuditAction;
import com.shotaroi.medsafety.domain.exception.DuplicateResourceException;
import com.shotaroi.medsafety.domain.exception.ResourceNotFoundException;
import com.shotaroi.medsafety.infrastructure.persistence.MedicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MedicationService {

    private final MedicationRepository repository;
    private final MedicationMapper mapper;
    private final AuditService auditService;

    public MedicationService(MedicationRepository repository, MedicationMapper mapper, AuditService auditService) {
        this.repository = repository;
        this.mapper = mapper;
        this.auditService = auditService;
    }

    @Transactional
    public MedicationResponse create(MedicationRequest request, String performedBy) {
        if (repository.existsByAtcCode(request.atcCode())) {
            throw new DuplicateResourceException("atcCode", request.atcCode());
        }
        Medication entity = mapper.toEntity(request);
        entity = repository.save(entity);
        auditService.audit(AggregateType.MEDICATION, entity.getId(), AuditAction.CREATED, performedBy,
                Map.of("atcCode", entity.getAtcCode()));
        return mapper.toResponse(entity);
    }

    public List<MedicationResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    public MedicationResponse getById(UUID id) {
        Medication entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication", id));
        return mapper.toResponse(entity);
    }
}
