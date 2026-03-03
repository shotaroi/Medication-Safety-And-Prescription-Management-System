package com.shotaroi.medsafety.infrastructure.persistence;

import com.shotaroi.medsafety.domain.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByPersonalNumber(String personalNumber);

    boolean existsByPersonalNumber(String personalNumber);
}
