package com.shotaroi.medsafety.api.mapper;

import com.shotaroi.medsafety.api.dto.audit.AuditEventResponse;
import com.shotaroi.medsafety.domain.entity.AuditEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditEventMapper {

    AuditEventResponse toResponse(AuditEvent entity);
}
