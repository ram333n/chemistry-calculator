package org.prokopchuk.chemistry_calculator.dto;

import java.util.List;

public record BalancedEquationResponse(
        String equation,
        List<CompoundCoefficient> reactants,
        List<CompoundCoefficient> products
) {}
