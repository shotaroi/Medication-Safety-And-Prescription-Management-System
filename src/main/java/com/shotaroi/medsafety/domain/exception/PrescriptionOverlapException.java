package com.shotaroi.medsafety.domain.exception;

import java.util.UUID;

public class PrescriptionOverlapException extends DomainException {

    private final UUID patientId;
    private final UUID medicationId;

    public PrescriptionOverlapException(UUID patientId, UUID medicationId) {
        super("Active prescription already exists for this patient and medication with overlapping dates");
        this.patientId = patientId;
        this.medicationId = medicationId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getMedicationId() {
        return medicationId;
    }
}
