package com.shotaroi.medsafety.infrastructure.persistence;

import com.shotaroi.medsafety.domain.entity.AuditEvent;
import com.shotaroi.medsafety.domain.enums.AggregateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {

    Page<AuditEvent> findByAggregateTypeAndAggregateId(
            AggregateType aggregateType,
            UUID aggregateId,
            Pageable pageable);

    Page<AuditEvent> findByAggregateType(AggregateType aggregateType, Pageable pageable);

    Page<AuditEvent> findByCorrelationId(String correlationId, Pageable pageable);
}
