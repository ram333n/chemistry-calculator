package org.prokopchuk.chemistry_calculator.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.prokopchuk.chemistry_calculator.domain.Element;
import org.prokopchuk.chemistry_calculator.exception.InvalidEquationException;
import org.prokopchuk.chemistry_calculator.exception.InvalidFormulaException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EquationParserTest {

    private EquationParser parser;

    @BeforeEach
    void setUp() {
        parser = new EquationParser(new FormulaParser());
    }

    @Test
    void noSpaces() {
        EquationParser.ParsedEquation result = parser.parse("H2+O2->H2O");
        assertThat(result.reactantFormulas()).containsExactly("H2", "O2");
        assertThat(result.productFormulas()).containsExactly("H2O");
        assertThat(result.reactantMaps()).containsExactly(
                Map.of(Element.H, 2),
                Map.of(Element.O, 2)
        );
        assertThat(result.productMaps()).containsExactly(
                Map.of(Element.H, 2, Element.O, 1)
        );
    }

    @Test
    void withSpaces() {
        EquationParser.ParsedEquation result = parser.parse("H2 + O2 -> H2O");
        assertThat(result.reactantFormulas()).containsExactly("H2", "O2");
        assertThat(result.productFormulas()).containsExactly("H2O");
    }

    @Test
    void missingArrowThrows() {
        assertThatThrownBy(() -> parser.parse("H2+O2H2O"))
                .isInstanceOf(InvalidEquationException.class)
                .hasMessageContaining("->");
    }

    @Test
    void multipleArrowsThrow() {
        assertThatThrownBy(() -> parser.parse("H2->O2->H2O"))
                .isInstanceOf(InvalidEquationException.class)
                .hasMessageContaining("->");
    }

    @Test
    void emptyReactantSideThrows() {
        assertThatThrownBy(() -> parser.parse("->H2O"))
                .isInstanceOf(InvalidEquationException.class);
    }

    @Test
    void emptyProductSideThrows() {
        assertThatThrownBy(() -> parser.parse("H2+O2->"))
                .isInstanceOf(InvalidEquationException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void blankEquationThrows(String equation) {
        assertThatThrownBy(() -> parser.parse(equation))
                .isInstanceOf(InvalidEquationException.class);
    }

    @Test
    void nullEquationThrows() {
        assertThatThrownBy(() -> parser.parse(null))
                .isInstanceOf(InvalidEquationException.class);
    }

    @Test
    void unknownElementPropagatesAsInvalidFormulaException() {
        assertThatThrownBy(() -> parser.parse("Xx+O2->XxO"))
                .isInstanceOf(InvalidFormulaException.class)
                .hasMessageContaining("Unknown element");
    }
}
