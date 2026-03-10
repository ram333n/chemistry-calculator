package org.prokopchuk.chemistry_calculator.dto;

public record ElementAnalysis(
        String symbol,
        int count,
        double atomicMass,
        double totalMass,
        double massFraction
) {}
