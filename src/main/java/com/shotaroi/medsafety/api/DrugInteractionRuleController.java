package com.shotaroi.medsafety.api;

import com.shotaroi.medsafety.api.dto.druginteraction.DrugInteractionRuleRequest;
import com.shotaroi.medsafety.api.dto.druginteraction.DrugInteractionRuleResponse;
import com.shotaroi.medsafety.application.service.DrugInteractionRuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/drug-interactions")
public class DrugInteractionRuleController {

    private final DrugInteractionRuleService drugInteractionRuleService;

    public DrugInteractionRuleController(DrugInteractionRuleService drugInteractionRuleService) {
        this.drugInteractionRuleService = drugInteractionRuleService;
    }

    @PostMapping
    public ResponseEntity<DrugInteractionRuleResponse> create(@Valid @RequestBody DrugInteractionRuleRequest request) {
        DrugInteractionRuleResponse response = drugInteractionRuleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrugInteractionRuleResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(drugInteractionRuleService.getById(id));
    }
}
