package org.prokopchuk.chemistry_calculator.balancer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FractionTest {

    @Test
    void ofLong() {
        Fraction f = Fraction.of(3);
        assertThat(f.numerator).isEqualTo(3);
        assertThat(f.denominator).isEqualTo(1);
    }

    @Test
    void reducesToLowestTerms() {
        Fraction f = Fraction.of(6, 4);
        assertThat(f.numerator).isEqualTo(3);
        assertThat(f.denominator).isEqualTo(2);
    }

    @Test
    void negativeNumerator() {
        Fraction f = Fraction.of(-3, 4);
        assertThat(f.numerator).isEqualTo(-3);
        assertThat(f.denominator).isEqualTo(4);
    }

    @Test
    void negativeDenominatorNormalized() {
        Fraction f = Fraction.of(3, -4);
        assertThat(f.numerator).isEqualTo(-3);
        assertThat(f.denominator).isEqualTo(4);
    }

    @Test
    void bothNegativeNormalized() {
        Fraction f = Fraction.of(-3, -4);
        assertThat(f.numerator).isEqualTo(3);
        assertThat(f.denominator).isEqualTo(4);
    }

    @Test
    void zero() {
        Fraction f = Fraction.of(0, 5);
        assertThat(f.numerator).isEqualTo(0);
        assertThat(f.denominator).isEqualTo(1);
        assertThat(f.isZero()).isTrue();
    }

    @Test
    void zeroDenominatorThrows() {
        assertThatThrownBy(() -> Fraction.of(1, 0))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void add() {
        Fraction result = Fraction.of(1, 2).add(Fraction.of(1, 3));
        assertThat(result.numerator).isEqualTo(5);
        assertThat(result.denominator).isEqualTo(6);
    }

    @Test
    void subtract() {
        Fraction result = Fraction.of(3, 4).subtract(Fraction.of(1, 4));
        assertThat(result.numerator).isEqualTo(1);
        assertThat(result.denominator).isEqualTo(2);
    }

    @Test
    void multiply() {
        Fraction result = Fraction.of(2, 3).multiply(Fraction.of(3, 4));
        assertThat(result.numerator).isEqualTo(1);
        assertThat(result.denominator).isEqualTo(2);
    }

    @Test
    void divide() {
        Fraction result = Fraction.of(2, 3).divide(Fraction.of(4, 3));
        assertThat(result.numerator).isEqualTo(1);
        assertThat(result.denominator).isEqualTo(2);
    }

    @Test
    void negate() {
        Fraction result = Fraction.of(3, 4).negate();
        assertThat(result.numerator).isEqualTo(-3);
        assertThat(result.denominator).isEqualTo(4);
    }

    @Test
    void absPositive() {
        Fraction result = Fraction.of(3, 4).abs();
        assertThat(result.numerator).isEqualTo(3);
        assertThat(result.denominator).isEqualTo(4);
    }

    @Test
    void absNegative() {
        Fraction result = Fraction.of(-3, 4).abs();
        assertThat(result.numerator).isEqualTo(3);
        assertThat(result.denominator).isEqualTo(4);
    }

    @Test
    void isZeroTrue() {
        assertThat(Fraction.ZERO.isZero()).isTrue();
    }

    @Test
    void isZeroFalse() {
        assertThat(Fraction.ONE.isZero()).isFalse();
    }

    @Test
    void gcd() {
        assertThat(Fraction.gcd(12, 8)).isEqualTo(4);
        assertThat(Fraction.gcd(7, 3)).isEqualTo(1);
        assertThat(Fraction.gcd(0, 5)).isEqualTo(5);
    }

    @Test
    void lcm() {
        assertThat(Fraction.lcm(4, 6)).isEqualTo(12);
        assertThat(Fraction.lcm(3, 5)).isEqualTo(15);
    }
}
