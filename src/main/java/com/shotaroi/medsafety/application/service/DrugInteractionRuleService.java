package com.shotaroi.medsafety.application.service;

import com.shotaroi.medsafety.api.dto.druginteraction.DrugInteractionRuleRequest;
import com.shotaroi.medsafety.api.dto.druginteraction.DrugInteractionRuleResponse;
import com.shotaroi.medsafety.api.mapper.DrugInteractionRuleMapper;
import com.shotaroi.medsafety.domain.entity.DrugInteractionRule;
import com.shotaroi.medsafety.domain.exception.ResourceNotFoundException;
import com.shotaroi.medsafety.infrastructure.persistence.DrugInteractionRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Canonical order: atcCodeA <= atcCodeB (lexicographically).
 */
@Service
public class DrugInteractionRuleService {

    private final DrugInteractionRuleRepository repository;
    private final DrugInteractionRuleMapper mapper;

    public DrugInteractionRuleService(DrugInteractionRuleRepository repository, DrugInteractionRuleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public DrugInteractionRuleResponse create(DrugInteractionRuleRequest request) {
        String a = request.atcCodeA();
        String b = request.atcCodeB();
        if (a.compareTo(b) > 0) {
            String tmp = a;
            a = b;
            b = tmp;
        }
        DrugInteractionRule entity = new DrugInteractionRule();
        entity.setAtcCodeA(a);
        entity.setAtcCodeB(b);
        entity.setSeverity(request.severity());
        entity.setMessage(request.message());
        entity = repository.save(entity);
        return mapper.toResponse(entity);
    }

    public DrugInteractionRuleResponse getById(UUID id) {
        DrugInteractionRule entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DrugInteractionRule", id));
        return mapper.toResponse(entity);
    }
}
