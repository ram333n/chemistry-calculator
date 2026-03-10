package org.prokopchuk.chemistry_calculator.balancer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.prokopchuk.chemistry_calculator.domain.Element;
import org.prokopchuk.chemistry_calculator.exception.InvalidEquationException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EquationBalancerTest {

    private EquationBalancer balancer;

    @BeforeEach
    void setUp() {
        balancer = new EquationBalancer();
    }

    @Test
    void waterCombustion() {
        // H2 + O2 -> H2O  =>  [2, 1, 2]
        List<Map<Element, Integer>> reactants = List.of(
                Map.of(Element.H, 2),
                Map.of(Element.O, 2)
        );
        List<Map<Element, Integer>> products = List.of(
                Map.of(Element.H, 2, Element.O, 1)
        );
        assertThat(balancer.balance(reactants, products)).containsExactly(2, 1, 2);
    }

    @Test
    void ironOxide() {
        // Fe + O2 -> Fe2O3  =>  [4, 3, 2]
        List<Map<Element, Integer>> reactants = List.of(
                Map.of(Element.Fe, 1),
                Map.of(Element.O, 2)
        );
        List<Map<Element, Integer>> products = List.of(
                Map.of(Element.Fe, 2, Element.O, 3)
        );
        assertThat(balancer.balance(reactants, products)).containsExactly(4, 3, 2);
    }

    @Test
    void propaneCombustion() {
        // C3H8 + O2 -> CO2 + H2O  =>  [1, 5, 3, 4]
        List<Map<Element, Integer>> reactants = List.of(
                Map.of(Element.C, 3, Element.H, 8),
                Map.of(Element.O, 2)
        );
        List<Map<Element, Integer>> products = List.of(
                Map.of(Element.C, 1, Element.O, 2),
                Map.of(Element.H, 2, Element.O, 1)
        );
        assertThat(balancer.balance(reactants, products)).containsExactly(1, 5, 3, 4);
    }

    @Test
    void ironSulfateReaction() {
        // Fe + H2SO4 -> FeSO4 + H2  =>  [1, 1, 1, 1]
        List<Map<Element, Integer>> reactants = List.of(
                Map.of(Element.Fe, 1),
                Map.of(Element.H, 2, Element.S, 1, Element.O, 4)
        );
        List<Map<Element, Integer>> products = List.of(
                Map.of(Element.Fe, 1, Element.S, 1, Element.O, 4),
                Map.of(Element.H, 2)
        );
        assertThat(balancer.balance(reactants, products)).containsExactly(1, 1, 1, 1);
    }

    @Test
    void sodiumChloride() {
        // Na + Cl2 -> NaCl  =>  [2, 1, 2]
        List<Map<Element, Integer>> reactants = List.of(
                Map.of(Element.Na, 1),
                Map.of(Element.Cl, 2)
        );
        List<Map<Element, Integer>> products = List.of(
                Map.of(Element.Na, 1, Element.Cl, 1)
        );
        assertThat(balancer.balance(reactants, products)).containsExactly(2, 1, 2);
    }

    @Test
    void complexDichromateOxidation() {
        // (Cr(N2H4CO)6)4(Cr(CN)6)3 + KMnO4 + H2SO4 -> K2Cr2O7 + MnSO4 + CO2 + KNO3 + K2SO4 + H2O
        // (Cr(N2H4CO)6)4(Cr(CN)6)3: Cr=7, N=66, H=96, C=42, O=24
        // Expected: [10, 1176, 1399, 35, 1176, 420, 660, 223, 1879]
        List<Map<Element, Integer>> reactants = List.of(
                Map.of(Element.Cr, 7, Element.N, 66, Element.H, 96, Element.C, 42, Element.O, 24),
                Map.of(Element.K, 1, Element.Mn, 1, Element.O, 4),
                Map.of(Element.H, 2, Element.S, 1, Element.O, 4)
        );
        List<Map<Element, Integer>> products = List.of(
                Map.of(Element.K, 2, Element.Cr, 2, Element.O, 7),
                Map.of(Element.Mn, 1, Element.S, 1, Element.O, 4),
                Map.of(Element.C, 1, Element.O, 2),
                Map.of(Element.K, 1, Element.N, 1, Element.O, 3),
                Map.of(Element.K, 2, Element.S, 1, Element.O, 4),
                Map.of(Element.H, 2, Element.O, 1)
        );
        assertThat(balancer.balance(reactants, products))
                .containsExactly(10, 1176, 1399, 35, 1176, 420, 660, 223, 1879);
    }

    @Test
    void impossibleEquationThrows() {
        // H2 -> O2 (impossible)
        List<Map<Element, Integer>> reactants = List.of(Map.of(Element.H, 2));
        List<Map<Element, Integer>> products = List.of(Map.of(Element.O, 2));
        assertThatThrownBy(() -> balancer.balance(reactants, products))
                .isInstanceOf(InvalidEquationException.class);
    }
}
