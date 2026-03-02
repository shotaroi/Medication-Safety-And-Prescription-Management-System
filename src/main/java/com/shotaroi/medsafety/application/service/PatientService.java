package com.shotaroi.medsafety.application.service;

import com.shotaroi.medsafety.api.dto.patient.PatientRequest;
import com.shotaroi.medsafety.api.dto.patient.PatientResponse;
import com.shotaroi.medsafety.api.mapper.PatientMapper;
import com.shotaroi.medsafety.domain.entity.Patient;
import com.shotaroi.medsafety.domain.enums.AggregateType;
import com.shotaroi.medsafety.domain.enums.AuditAction;
import com.shotaroi.medsafety.domain.exception.DuplicateResourceException;
import com.shotaroi.medsafety.domain.exception.ResourceNotFoundException;
import com.shotaroi.medsafety.infrastructure.persistence.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository repository;
    private final PatientMapper mapper;
    private final AuditService auditService;

    public PatientService(PatientRepository repository, PatientMapper mapper, AuditService auditService) {
        this.repository = repository;
        this.mapper = mapper;
        this.auditService = auditService;
    }

    @Transactional
    public PatientResponse create(PatientRequest request, String performedBy) {
        if (repository.existsByPersonalNumber(request.personalNumber())) {
            throw new DuplicateResourceException("personalNumber", request.personalNumber());
        }
        Patient entity = mapper.toEntity(request);
        entity = repository.save(entity);
        auditService.audit(AggregateType.PATIENT, entity.getId(), AuditAction.CREATED, performedBy,
                Map.of("personalNumber", entity.getPersonalNumber()));
        return mapper.toResponse(entity);
    }

    public PatientResponse getById(UUID id) {
        Patient entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        return mapper.toResponse(entity);
    }

    public Optional<PatientResponse> getByPersonalNumber(String personalNumber) {
        return repository.findByPersonalNumber(personalNumber).map(mapper::toResponse);
    }
}
