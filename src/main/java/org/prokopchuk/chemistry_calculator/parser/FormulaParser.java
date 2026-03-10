package org.prokopchuk.chemistry_calculator.parser;

import org.prokopchuk.chemistry_calculator.domain.Element;
import org.prokopchuk.chemistry_calculator.exception.InvalidFormulaException;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class FormulaParser {

    public Map<Element, Integer> parse(String formula) {
        if (formula == null || formula.isBlank()) {
            throw new InvalidFormulaException("Formula must not be blank");
        }
        return new Parse(formula).run();
    }

    private static class Parse {

        private final String formula;
        private int pos;

        Parse(String formula) {
            this.formula = formula;
        }

        Map<Element, Integer> run() {
            Map<Element, Integer> result = parseFormula();
            if (pos != formula.length()) {
                throw new InvalidFormulaException("Unexpected character at position " + pos + ": '" + formula.charAt(pos) + "'");
            }
            return result;
        }

        private Map<Element, Integer> parseFormula() {
            Map<Element, Integer> result = new LinkedHashMap<>();

            while (pos < formula.length() && formula.charAt(pos) != ')') {
                Map<Element, Integer> group = parseGroup();
                mergeInto(result, group, 1);
            }

            if (result.isEmpty()) {
                throw new InvalidFormulaException("Empty formula or group at position " + pos);
            }

            return result;
        }

        private Map<Element, Integer> parseGroup() {
            if (pos < formula.length() && formula.charAt(pos) == '(') {
                pos++; // consume '('
                Map<Element, Integer> inner = parseFormula();
                if (pos >= formula.length() || formula.charAt(pos) != ')') {
                    throw new InvalidFormulaException("Expected ')' at position " + pos);
                }
                pos++; // consume ')'
                int count = parseCount();
                Map<Element, Integer> result = new LinkedHashMap<>();
                mergeInto(result, inner, count);
                return result;
            } else {
                String symbol = parseElementSymbol();
                Element element = resolveElement(symbol);
                int count = parseCount();
                Map<Element, Integer> result = new LinkedHashMap<>();
                result.put(element, count);
                return result;
            }
        }

        private String parseElementSymbol() {
            if (pos >= formula.length() || !Character.isUpperCase(formula.charAt(pos))) {
                throw new InvalidFormulaException("Expected element symbol at position " + pos);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(formula.charAt(pos++));
            if (pos < formula.length() && Character.isLowerCase(formula.charAt(pos))) {
                sb.append(formula.charAt(pos++));
            }
            return sb.toString();
        }

        private Element resolveElement(String symbol) {
            try {
                return Element.valueOf(symbol);
            } catch (IllegalArgumentException e) {
                throw new InvalidFormulaException("Unknown element: " + symbol);
            }
        }

        private int parseCount() {
            if (pos >= formula.length() || !Character.isDigit(formula.charAt(pos))) {
                return 1;
            }
            int start = pos;
            while (pos < formula.length() && Character.isDigit(formula.charAt(pos))) {
                pos++;
            }
            int count = Integer.parseInt(formula.substring(start, pos));
            if (count == 0) {
                throw new InvalidFormulaException("Element count cannot be zero at position " + start);
            }
            return count;
        }

        private void mergeInto(Map<Element, Integer> dst, Map<Element, Integer> src, int factor) {
            for (Map.Entry<Element, Integer> entry : src.entrySet()) {
                dst.merge(entry.getKey(), entry.getValue() * factor, Integer::sum);
            }
        }
    }
}
