package com.shotaroi.medsafety.api.mapper;

import com.shotaroi.medsafety.api.dto.druginteraction.DrugInteractionRuleRequest;
import com.shotaroi.medsafety.api.dto.druginteraction.DrugInteractionRuleResponse;
import com.shotaroi.medsafety.domain.entity.DrugInteractionRule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DrugInteractionRuleMapper {

    DrugInteractionRuleResponse toResponse(DrugInteractionRule entity);

    DrugInteractionRule toEntity(DrugInteractionRuleRequest request);
}
