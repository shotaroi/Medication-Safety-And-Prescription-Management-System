-- Medication Safety & Prescription Management System - Initial Schema
-- Flyway migration V1

-- Patients
CREATE TABLE patients (
    id UUID PRIMARY KEY,
    personal_number VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_patients_personal_number ON patients(personal_number);

-- Medications
CREATE TABLE medications (
    id UUID PRIMARY KEY,
    atc_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    form VARCHAR(20) NOT NULL,
    strength_mg INTEGER NOT NULL,
    max_daily_dose_mg INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_medications_atc_code ON medications(atc_code);

-- Prescriptions (with optimistic locking)
CREATE TABLE prescriptions (
    id UUID PRIMARY KEY,
    patient_id UUID NOT NULL REFERENCES patients(id),
    medication_id UUID NOT NULL REFERENCES medications(id),
    prescribed_by VARCHAR(100) NOT NULL,
    instructions VARCHAR(1000),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    start_date DATE,
    end_date DATE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_prescriptions_patient_id ON prescriptions(patient_id);
CREATE INDEX idx_prescriptions_medication_id ON prescriptions(medication_id);
CREATE INDEX idx_prescriptions_status ON prescriptions(status);
CREATE INDEX idx_prescriptions_patient_medication_dates ON prescriptions(patient_id, medication_id, start_date, end_date);

-- Dosage schedules
CREATE TABLE dosage_schedules (
    id UUID PRIMARY KEY,
    prescription_id UUID NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    dose_mg INTEGER NOT NULL,
    times_per_day INTEGER NOT NULL,
    interval_hours INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_dosage_schedules_prescription_id ON dosage_schedules(prescription_id);

-- Drug interaction rules (canonical order: atc_code_a <= atc_code_b lexicographically)
CREATE TABLE drug_interaction_rules (
    id UUID PRIMARY KEY,
    atc_code_a VARCHAR(20) NOT NULL,
    atc_code_b VARCHAR(20) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    message VARCHAR(500) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_atc_order CHECK (atc_code_a <= atc_code_b),
    CONSTRAINT uq_interaction_pair UNIQUE (atc_code_a, atc_code_b)
);

CREATE INDEX idx_drug_interaction_rules_codes ON drug_interaction_rules(atc_code_a, atc_code_b);

-- Audit events (append-only)
CREATE TABLE audit_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(20) NOT NULL,
    aggregate_id UUID NOT NULL,
    action VARCHAR(20) NOT NULL,
    performed_by VARCHAR(100) NOT NULL,
    correlation_id VARCHAR(64),
    payload_json JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_events_aggregate ON audit_events(aggregate_type, aggregate_id);
CREATE INDEX idx_audit_events_correlation_id ON audit_events(correlation_id);
CREATE INDEX idx_audit_events_created_at ON audit_events(created_at);

-- Idempotency keys for prescription creation
CREATE TABLE idempotency_keys (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(128) NOT NULL UNIQUE,
    request_hash VARCHAR(64) NOT NULL,
    response_status INTEGER,
    response_body TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_idempotency_keys_key ON idempotency_keys(idempotency_key);
