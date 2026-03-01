package com.shotaroi.medsafety.api.dto.interaction;

import com.shotaroi.medsafety.api.dto.prescription.InteractionWarning;

import java.util.List;

public record InteractionCheckResponse(
        List<InteractionWarning> warnings
) {}
