package org.prokopchuk.chemistry_calculator.service;

import org.prokopchuk.chemistry_calculator.balancer.EquationBalancer;
import org.prokopchuk.chemistry_calculator.dto.BalancedEquationResponse;
import org.prokopchuk.chemistry_calculator.dto.CompoundCoefficient;
import org.prokopchuk.chemistry_calculator.parser.EquationParser;
import org.prokopchuk.chemistry_calculator.parser.EquationParser.ParsedEquation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquationBalancingService {

    private final EquationParser equationParser;
    private final EquationBalancer equationBalancer;

    public EquationBalancingService(EquationParser equationParser, EquationBalancer equationBalancer) {
        this.equationParser = equationParser;
        this.equationBalancer = equationBalancer;
    }

    public BalancedEquationResponse balance(String equation) {
        ParsedEquation parsed = equationParser.parse(equation);
        int[] coefficients = equationBalancer.balance(parsed.reactantMaps(), parsed.productMaps());

        int numReactants = parsed.reactantFormulas().size();

        List<CompoundCoefficient> reactants = new ArrayList<>();
        for (int i = 0; i < numReactants; i++) {
            reactants.add(new CompoundCoefficient(parsed.reactantFormulas().get(i), coefficients[i]));
        }

        List<CompoundCoefficient> products = new ArrayList<>();
        for (int i = 0; i < parsed.productFormulas().size(); i++) {
            products.add(new CompoundCoefficient(parsed.productFormulas().get(i), coefficients[numReactants + i]));
        }

        String equationString = renderEquation(reactants, products);
        return new BalancedEquationResponse(equationString, reactants, products);
    }

    private String renderEquation(List<CompoundCoefficient> reactants, List<CompoundCoefficient> products) {
        return renderSide(reactants) + " -> " + renderSide(products);
    }

    private String renderSide(List<CompoundCoefficient> compounds) {
        return compounds.stream()
                .map(c -> c.coefficient() == 1 ? c.formula() : c.coefficient() + c.formula())
                .collect(Collectors.joining(" + "));
    }
}
