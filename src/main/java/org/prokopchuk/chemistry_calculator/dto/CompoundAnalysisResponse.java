package org.prokopchuk.chemistry_calculator.dto;

import java.util.List;

public record CompoundAnalysisResponse(
        String formula,
        double molarMass,
        List<ElementAnalysis> elements
) {}
