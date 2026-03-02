package com.shotaroi.medsafety.api.mapper;

import com.shotaroi.medsafety.api.dto.prescription.DosageScheduleRequest;
import com.shotaroi.medsafety.api.dto.prescription.DosageScheduleResponse;
import com.shotaroi.medsafety.api.dto.prescription.PrescriptionResponse;
import com.shotaroi.medsafety.domain.entity.DosageSchedule;
import com.shotaroi.medsafety.domain.entity.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PrescriptionMapper {

    @Mapping(target = "schedules", source = "schedules")
    @Mapping(target = "warnings", ignore = true)
    PrescriptionResponse toResponse(Prescription entity, List<DosageScheduleResponse> schedules);

    default PrescriptionResponse toResponse(Prescription entity, List<DosageScheduleResponse> schedules,
                                            List<com.shotaroi.medsafety.api.dto.prescription.InteractionWarning> warnings) {
        PrescriptionResponse base = toResponse(entity, schedules);
        return new PrescriptionResponse(
                base.id(), base.patientId(), base.medicationId(), base.prescribedBy(),
                base.instructions(), base.status(), base.startDate(), base.endDate(),
                base.version(), base.createdAt(), base.updatedAt(), base.schedules(), warnings);
    }

    DosageScheduleResponse toScheduleResponse(DosageSchedule entity);

    DosageSchedule toScheduleEntity(DosageScheduleRequest request);
}
