package com.shotaroi.medsafety.application.service;

import com.shotaroi.medsafety.api.dto.prescription.InteractionWarning;
import com.shotaroi.medsafety.domain.entity.DrugInteractionRule;
import com.shotaroi.medsafety.domain.entity.Medication;
import com.shotaroi.medsafety.domain.enums.InteractionSeverity;
import com.shotaroi.medsafety.domain.exception.HighSeverityInteractionException;
import com.shotaroi.medsafety.domain.exception.ResourceNotFoundException;
import com.shotaroi.medsafety.infrastructure.persistence.DrugInteractionRuleRepository;
import com.shotaroi.medsafety.infrastructure.persistence.MedicationRepository;
import com.shotaroi.medsafety.infrastructure.persistence.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InteractionService {

    private final DrugInteractionRuleRepository interactionRepository;
    private final MedicationRepository medicationRepository;
    private final PrescriptionRepository prescriptionRepository;

    public InteractionService(DrugInteractionRuleRepository interactionRepository,
                              MedicationRepository medicationRepository,
                              PrescriptionRepository prescriptionRepository) {
        this.interactionRepository = interactionRepository;
        this.medicationRepository = medicationRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    /**
     * Check interactions between new medication and existing active prescriptions for patient.
     * Returns warnings for LOW/MEDIUM. Throws for HIGH severity.
     */
    public List<InteractionWarning> checkForPatient(UUID patientId, UUID medicationId, boolean blockOnHigh) {
        Medication newMed = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Medication", medicationId));
        String newAtc = newMed.getAtcCode();

        List<String> existingAtcCodes = prescriptionRepository.findActiveByPatientId(patientId)
                .stream()
                .map(p -> medicationRepository.findById(p.getMedicationId()))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get().getAtcCode())
                .filter(atc -> !atc.equals(newAtc))
                .distinct()
                .collect(Collectors.toList());

        List<InteractionWarning> warnings = new ArrayList<>();
        for (String existingAtc : existingAtcCodes) {
            List<DrugInteractionRule> rules = findInteractionBetween(newAtc, existingAtc);
            for (DrugInteractionRule rule : rules) {
                InteractionWarning w = new InteractionWarning(
                        rule.getAtcCodeA(), rule.getAtcCodeB(), rule.getSeverity(), rule.getMessage());
                warnings.add(w);
                if (rule.getSeverity() == InteractionSeverity.HIGH && blockOnHigh) {
                    throw new HighSeverityInteractionException(
                            warnings.stream().map(InteractionWarning::message).toList());
                }
            }
        }
        return warnings;
    }

    /**
     * Check interactions between a list of ATC codes.
     */
    public List<InteractionWarning> checkForAtcCodes(List<String> atcCodes) {
        List<InteractionWarning> warnings = new ArrayList<>();
        for (int i = 0; i < atcCodes.size(); i++) {
            for (int j = i + 1; j < atcCodes.size(); j++) {
                String a = atcCodes.get(i);
                String b = atcCodes.get(j);
                List<DrugInteractionRule> rules = findInteractionBetween(a, b);
                for (DrugInteractionRule rule : rules) {
                    warnings.add(new InteractionWarning(
                            rule.getAtcCodeA(), rule.getAtcCodeB(), rule.getSeverity(), rule.getMessage()));
                }
            }
        }
        return warnings;
    }

    private List<DrugInteractionRule> findInteractionBetween(String atc1, String atc2) {
        String a = atc1.compareTo(atc2) <= 0 ? atc1 : atc2;
        String b = atc1.compareTo(atc2) <= 0 ? atc2 : atc1;
        return interactionRepository.findInteractionBetween(a, b);
    }
}
