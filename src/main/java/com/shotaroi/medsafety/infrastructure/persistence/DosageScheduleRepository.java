package com.shotaroi.medsafety.infrastructure.persistence;

import com.shotaroi.medsafety.domain.entity.DosageSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DosageScheduleRepository extends JpaRepository<DosageSchedule, UUID> {

    List<DosageSchedule> findByPrescriptionId(UUID prescriptionId);
}
