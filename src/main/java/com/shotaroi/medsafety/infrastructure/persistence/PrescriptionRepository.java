package com.shotaroi.medsafety.infrastructure.persistence;

import com.shotaroi.medsafety.domain.entity.Prescription;
import com.shotaroi.medsafety.domain.enums.PrescriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    List<Prescription> findByPatientId(UUID patientId);

    @Query("""
        SELECT p FROM Prescription p
        WHERE p.patientId = :patientId
        AND p.medicationId = :medicationId
        AND p.status = :status
        AND (
            (p.startDate IS NULL AND p.endDate IS NULL)
            OR (p.startDate <= :endDate AND (p.endDate IS NULL OR p.endDate >= :startDate))
        )
        """)
    List<Prescription> findOverlappingActive(
            @Param("patientId") UUID patientId,
            @Param("medicationId") UUID medicationId,
            @Param("status") PrescriptionStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT p FROM Prescription p
        WHERE p.patientId = :patientId
        AND p.status = 'ACTIVE'
        """)
    List<Prescription> findActiveByPatientId(@Param("patientId") UUID patientId);
}
