package org.prokopchuk.chemistry_calculator.balancer;

import org.prokopchuk.chemistry_calculator.domain.Element;
import org.prokopchuk.chemistry_calculator.exception.InvalidEquationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SequencedSet;

@Component
public class EquationBalancer {

    public int[] balance(List<Map<Element, Integer>> reactants, List<Map<Element, Integer>> products) {
        List<Element> elements = collectElements(reactants, products);
        int numElements = elements.size();
        int numCompounds = reactants.size() + products.size();

        Fraction[][] matrix = buildMatrix(elements, numElements, numCompounds, reactants, products);
        rref(matrix, numElements, numCompounds);

        return extractCoefficients(matrix, numElements, numCompounds);
    }

    private List<Element> collectElements(List<Map<Element, Integer>> reactants, List<Map<Element, Integer>> products) {
        SequencedSet<Element> set = new LinkedHashSet<>();
        for (Map<Element, Integer> compound : reactants) {
            set.addAll(compound.keySet());
        }
        for (Map<Element, Integer> compound : products) {
            set.addAll(compound.keySet());
        }
        return new ArrayList<>(set);
    }

    private Fraction[][] buildMatrix(List<Element> elements, int numElements, int numCompounds,
                                     List<Map<Element, Integer>> reactants, List<Map<Element, Integer>> products) {
        Fraction[][] m = new Fraction[numElements][numCompounds];
        for (int i = 0; i < numElements; i++) {
            for (int j = 0; j < numCompounds; j++) {
                m[i][j] = Fraction.ZERO;
            }
        }

        for (int col = 0; col < reactants.size(); col++) {
            Map<Element, Integer> compound = reactants.get(col);
            for (int row = 0; row < numElements; row++) {
                Integer count = compound.get(elements.get(row));
                if (count != null) {
                    m[row][col] = Fraction.of(count);
                }
            }
        }

        for (int i = 0; i < products.size(); i++) {
            int col = reactants.size() + i;
            Map<Element, Integer> compound = products.get(i);
            for (int row = 0; row < numElements; row++) {
                Integer count = compound.get(elements.get(row));
                if (count != null) {
                    m[row][col] = Fraction.of(-count);
                }
            }
        }

        return m;
    }

    private void rref(Fraction[][] m, int rows, int cols) {
        int pivotRow = 0;
        for (int col = 0; col < cols && pivotRow < rows; col++) {
            int nonZeroRow = -1;
            for (int row = pivotRow; row < rows; row++) {
                if (!m[row][col].isZero()) {
                    nonZeroRow = row;
                    break;
                }
            }
            if (nonZeroRow == -1) {
                continue;
            }

            Fraction[] temp = m[pivotRow];
            m[pivotRow] = m[nonZeroRow];
            m[nonZeroRow] = temp;

            Fraction pivotVal = m[pivotRow][col];
            for (int j = 0; j < cols; j++) {
                m[pivotRow][j] = m[pivotRow][j].divide(pivotVal);
            }

            for (int row = 0; row < rows; row++) {
                if (row != pivotRow && !m[row][col].isZero()) {
                    Fraction factor = m[row][col];
                    for (int j = 0; j < cols; j++) {
                        m[row][j] = m[row][j].subtract(factor.multiply(m[pivotRow][j]));
                    }
                }
            }

            pivotRow++;
        }
    }

    private int[] extractCoefficients(Fraction[][] m, int rows, int cols) {
        int[] pivotCol = new int[rows];
        boolean[] isPivot = new boolean[cols];
        java.util.Arrays.fill(pivotCol, -1);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (!m[row][col].isZero()) {
                    pivotCol[row] = col;
                    isPivot[col] = true;
                    break;
                }
            }
        }

        List<Integer> freeCols = new ArrayList<>();
        for (int col = 0; col < cols; col++) {
            if (!isPivot[col]) {
                freeCols.add(col);
            }
        }

        if (freeCols.isEmpty()) {
            throw new InvalidEquationException("Equation cannot be balanced");
        }
        if (freeCols.size() > 1) {
            throw new InvalidEquationException("Equation is underdetermined");
        }

        int freeCol = freeCols.get(0);
        Fraction[] solution = new Fraction[cols];
        for (int i = 0; i < cols; i++) {
            solution[i] = Fraction.ZERO;
        }
        solution[freeCol] = Fraction.ONE;

        for (int row = 0; row < rows; row++) {
            int pc = pivotCol[row];
            if (pc != -1) {
                solution[pc] = m[row][freeCol].negate();
            }
        }

        long lcmVal = 1;
        for (Fraction f : solution) {
            lcmVal = Fraction.lcm(lcmVal, f.denominator);
        }

        int[] coefficients = new int[cols];
        for (int i = 0; i < cols; i++) {
            coefficients[i] = (int) (solution[i].numerator * (lcmVal / solution[i].denominator));
        }

        if (java.util.Arrays.stream(coefficients).anyMatch(c -> c <= 0)) {
            for (int i = 0; i < coefficients.length; i++) {
                coefficients[i] = -coefficients[i];
            }
        }

        if (java.util.Arrays.stream(coefficients).anyMatch(c -> c <= 0)) {
            throw new InvalidEquationException("Equation cannot be balanced with positive coefficients");
        }

        return coefficients;
    }
}
