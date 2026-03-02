package com.shotaroi.medsafety.domain.entity;

import com.shotaroi.medsafety.domain.enums.InteractionSeverity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Drug interaction rules. atcCodeA and atcCodeB are stored in canonical order
 * (lexicographically) so (A,B) and (B,A) are treated the same.
 */
@Entity
@Table(name = "drug_interaction_rules", indexes = {
        @Index(columnList = "atc_code_a, atc_code_b", unique = true)
})
public class DrugInteractionRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "atc_code_a", nullable = false, length = 20)
    private String atcCodeA;

    @Column(name = "atc_code_b", nullable = false, length = 20)
    private String atcCodeB;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InteractionSeverity severity;

    @Column(nullable = false, length = 500)
    private String message;

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

    public String getAtcCodeA() {
        return atcCodeA;
    }

    public void setAtcCodeA(String atcCodeA) {
        this.atcCodeA = atcCodeA;
    }

    public String getAtcCodeB() {
        return atcCodeB;
    }

    public void setAtcCodeB(String atcCodeB) {
        this.atcCodeB = atcCodeB;
    }

    public InteractionSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(InteractionSeverity severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
