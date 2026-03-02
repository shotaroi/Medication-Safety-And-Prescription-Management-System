package com.shotaroi.medsafety.domain.entity;

import com.shotaroi.medsafety.domain.enums.MedicationForm;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "medications", indexes = @Index(unique = true, columnList = "atc_code"))
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "atc_code", nullable = false, unique = true, length = 20)
    private String atcCode;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MedicationForm form;

    @Column(name = "strength_mg", nullable = false)
    private Integer strengthMg;

    @Column(name = "max_daily_dose_mg", nullable = false)
    private Integer maxDailyDoseMg;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAtcCode() {
        return atcCode;
    }

    public void setAtcCode(String atcCode) {
        this.atcCode = atcCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MedicationForm getForm() {
        return form;
    }

    public void setForm(MedicationForm form) {
        this.form = form;
    }

    public Integer getStrengthMg() {
        return strengthMg;
    }

    public void setStrengthMg(Integer strengthMg) {
        this.strengthMg = strengthMg;
    }

    public Integer getMaxDailyDoseMg() {
        return maxDailyDoseMg;
    }

    public void setMaxDailyDoseMg(Integer maxDailyDoseMg) {
        this.maxDailyDoseMg = maxDailyDoseMg;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
