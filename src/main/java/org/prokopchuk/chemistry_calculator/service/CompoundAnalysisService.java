package org.prokopchuk.chemistry_calculator.service;

import org.prokopchuk.chemistry_calculator.domain.Element;
import org.prokopchuk.chemistry_calculator.dto.CompoundAnalysisResponse;
import org.prokopchuk.chemistry_calculator.dto.ElementAnalysis;
import org.prokopchuk.chemistry_calculator.parser.FormulaParser;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class CompoundAnalysisService {

    private final FormulaParser formulaParser;

    public CompoundAnalysisService(FormulaParser formulaParser) {
        this.formulaParser = formulaParser;
    }

    public CompoundAnalysisResponse analyze(String formula) {
        Map<Element, Integer> elementCounts = formulaParser.parse(formula);

        double molarMass = elementCounts.entrySet().stream()
                .mapToDouble(e -> e.getKey().getAtomicMass() * e.getValue())
                .sum();

        List<ElementAnalysis> elements = elementCounts.entrySet().stream()
                .map(e -> {
                    Element el = e.getKey();
                    int count = e.getValue();
                    double totalMass = el.getAtomicMass() * count;
                    double massFraction = round((totalMass / molarMass) * 100, 4);
                    return new ElementAnalysis(el.name(), count, el.getAtomicMass(), totalMass, massFraction);
                })
                .toList();

        return new CompoundAnalysisResponse(formula, round(molarMass, 4), elements);
    }

    private double round(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }
}
