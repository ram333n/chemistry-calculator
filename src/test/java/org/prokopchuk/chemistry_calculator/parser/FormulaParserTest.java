package org.prokopchuk.chemistry_calculator.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.prokopchuk.chemistry_calculator.domain.Element;
import org.prokopchuk.chemistry_calculator.exception.InvalidFormulaException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FormulaParserTest {

    private FormulaParser parser;

    @BeforeEach
    void setUp() {
        parser = new FormulaParser();
    }

    @Test
    void singleElement() {
        Map<Element, Integer> result = parser.parse("O");
        assertThat(result).containsExactly(Map.entry(Element.O, 1));
    }

    @Test
    void singleElementWithCount() {
        Map<Element, Integer> result = parser.parse("O2");
        assertThat(result).containsExactly(Map.entry(Element.O, 2));
    }

    @Test
    void water() {
        Map<Element, Integer> result = parser.parse("H2O");
        assertThat(result).containsExactly(
                Map.entry(Element.H, 2),
                Map.entry(Element.O, 1));
    }

    @Test
    void carbonDioxide() {
        Map<Element, Integer> result = parser.parse("CO2");
        assertThat(result).containsExactly(
                Map.entry(Element.C, 1),
                Map.entry(Element.O, 2));
    }

    @Test
    void simpleParentheses() {
        Map<Element, Integer> result = parser.parse("(OH)2");
        assertThat(result).containsExactly(
                Map.entry(Element.O, 2),
                Map.entry(Element.H, 2));
    }

    @Test
    void ironSulfate() {
        Map<Element, Integer> result = parser.parse("Fe2(SO4)3");
        assertThat(result).containsExactly(
                Map.entry(Element.Fe, 2),
                Map.entry(Element.S, 3),
                Map.entry(Element.O, 12));
    }

    @Test
    void calciumPhosphate() {
        Map<Element, Integer> result = parser.parse("Ca3(PO4)2");
        assertThat(result).containsExactly(
                Map.entry(Element.Ca, 3),
                Map.entry(Element.P, 2),
                Map.entry(Element.O, 8));
    }

    @Test
    void nestedParentheses() {
        Map<Element, Integer> result = parser.parse("(NH4)2SO4");
        assertThat(result).containsExactly(
                Map.entry(Element.N, 2),
                Map.entry(Element.H, 8),
                Map.entry(Element.S, 1),
                Map.entry(Element.O, 4));
    }

    @Test
    void repeatedElementsMerged() {
        Map<Element, Integer> result = parser.parse("HCH");
        assertThat(result).containsExactly(
                Map.entry(Element.H, 2),
                Map.entry(Element.C, 1));
    }

    @Test
    void multiDigitCount() {
        Map<Element, Integer> result = parser.parse("H12");
        assertThat(result).containsExactly(Map.entry(Element.H, 12));
    }

    @Test
    void parenthesesWithCountOne() {
        Map<Element, Integer> result = parser.parse("(H2O)");
        assertThat(result).containsExactly(
                Map.entry(Element.H, 2),
                Map.entry(Element.O, 1));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void nullOrBlankThrows(String formula) {
        assertThatThrownBy(() -> parser.parse(formula))
                .isInstanceOf(InvalidFormulaException.class)
                .hasMessageContaining("blank");
    }

    @Test
    void nullThrows() {
        assertThatThrownBy(() -> parser.parse(null))
                .isInstanceOf(InvalidFormulaException.class);
    }

    @Test
    void unknownElementThrows() {
        assertThatThrownBy(() -> parser.parse("Xx"))
                .isInstanceOf(InvalidFormulaException.class)
                .hasMessageContaining("Unknown element");
    }

    @Test
    void lowercaseStartThrows() {
        assertThatThrownBy(() -> parser.parse("h2O"))
                .isInstanceOf(InvalidFormulaException.class);
    }

    @Test
    void zeroCountThrows() {
        assertThatThrownBy(() -> parser.parse("H0"))
                .isInstanceOf(InvalidFormulaException.class)
                .hasMessageContaining("zero");
    }

    @Test
    void unclosedParenthesisThrows() {
        assertThatThrownBy(() -> parser.parse("(H2O"))
                .isInstanceOf(InvalidFormulaException.class)
                .hasMessageContaining("')'");
    }

    @Test
    void unexpectedClosingParenthesisThrows() {
        assertThatThrownBy(() -> parser.parse("H2O)"))
                .isInstanceOf(InvalidFormulaException.class);
    }

    @Test
    void emptyParenthesesThrow() {
        assertThatThrownBy(() -> parser.parse("()"))
                .isInstanceOf(InvalidFormulaException.class);
    }

    @Test
    void trailingInvalidCharacterThrows() {
        assertThatThrownBy(() -> parser.parse("H2O!"))
                .isInstanceOf(InvalidFormulaException.class);
    }
}
