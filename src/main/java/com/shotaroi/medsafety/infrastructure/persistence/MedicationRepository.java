package com.shotaroi.medsafety.infrastructure.persistence;

import com.shotaroi.medsafety.domain.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, UUID> {

    Optional<Medication> findByAtcCode(String atcCode);

    boolean existsByAtcCode(String atcCode);
}
