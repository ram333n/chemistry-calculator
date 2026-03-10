package org.prokopchuk.chemistry_calculator.parser;

import org.prokopchuk.chemistry_calculator.domain.Element;
import org.prokopchuk.chemistry_calculator.exception.InvalidEquationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EquationParser {

    public record ParsedEquation(
            List<String> reactantFormulas,
            List<Map<Element, Integer>> reactantMaps,
            List<String> productFormulas,
            List<Map<Element, Integer>> productMaps
    ) {}

    private final FormulaParser formulaParser;

    public EquationParser(FormulaParser formulaParser) {
        this.formulaParser = formulaParser;
    }

    public ParsedEquation parse(String equation) {
        if (equation == null || equation.isBlank()) {
            throw new InvalidEquationException("Equation must not be blank");
        }

        String[] sides = equation.split("->", -1);
        if (sides.length != 2) {
            throw new InvalidEquationException("Equation must contain exactly one '->' separator");
        }

        List<String> reactantFormulas = new ArrayList<>();
        List<Map<Element, Integer>> reactantMaps = new ArrayList<>();
        List<String> productFormulas = new ArrayList<>();
        List<Map<Element, Integer>> productMaps = new ArrayList<>();

        parseSide(sides[0], reactantFormulas, reactantMaps);
        parseSide(sides[1], productFormulas, productMaps);

        return new ParsedEquation(reactantFormulas, reactantMaps, productFormulas, productMaps);
    }

    private void parseSide(String side, List<String> formulas, List<Map<Element, Integer>> maps) {
        String[] tokens = side.split("\\+");
        for (String token : tokens) {
            String trimmed = token.trim();
            if (trimmed.isEmpty()) {
                throw new InvalidEquationException("Empty compound in equation");
            }
            formulas.add(trimmed);
            maps.add(formulaParser.parse(trimmed));
        }
    }
}
