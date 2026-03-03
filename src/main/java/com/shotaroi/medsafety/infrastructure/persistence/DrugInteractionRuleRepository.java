package com.shotaroi.medsafety.infrastructure.persistence;

import com.shotaroi.medsafety.domain.entity.DrugInteractionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DrugInteractionRuleRepository extends JpaRepository<DrugInteractionRule, UUID> {

    /**
     * Find interaction between two ATC codes.
     * Rules are stored with atc_code_a <= atc_code_b (canonical order).
     */
    @Query("""
        SELECT r FROM DrugInteractionRule r
        WHERE (r.atcCodeA = :codeA AND r.atcCodeB = :codeB)
        """)
    List<DrugInteractionRule> findInteractionBetween(
            @Param("codeA") String atcCodeA,
            @Param("codeB") String atcCodeB);

    @Query("""
        SELECT r FROM DrugInteractionRule r
        WHERE r.atcCodeA IN :codes OR r.atcCodeB IN :codes
        """)
    List<DrugInteractionRule> findAllInvolvingCodes(@Param("codes") List<String> atcCodes);
}
