package com.shotaroi.medsafety.api.mapper;

import com.shotaroi.medsafety.api.dto.medication.MedicationRequest;
import com.shotaroi.medsafety.api.dto.medication.MedicationResponse;
import com.shotaroi.medsafety.domain.entity.Medication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicationMapper {

    MedicationResponse toResponse(Medication entity);

    Medication toEntity(MedicationRequest request);
}
