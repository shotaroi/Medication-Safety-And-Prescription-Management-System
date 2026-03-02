package com.shotaroi.medsafety.api.mapper;

import com.shotaroi.medsafety.api.dto.patient.PatientRequest;
import com.shotaroi.medsafety.api.dto.patient.PatientResponse;
import com.shotaroi.medsafety.domain.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientResponse toResponse(Patient entity);

    Patient toEntity(PatientRequest request);

    void updateEntity(PatientRequest request, @MappingTarget Patient entity);
}
