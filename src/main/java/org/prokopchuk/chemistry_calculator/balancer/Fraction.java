package org.prokopchuk.chemistry_calculator.balancer;

class Fraction {

    static final Fraction ZERO = of(0);
    static final Fraction ONE = of(1);

    final long numerator;
    final long denominator;

    private Fraction(long numerator, long denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    static Fraction of(long value) {
        return of(value, 1);
    }

    static Fraction of(long numerator, long denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("Denominator cannot be zero");
        }
        if (numerator == 0) {
            return new Fraction(0, 1);
        }
        long sign = (denominator < 0) ? -1 : 1;
        long absNum = Math.abs(numerator);
        long absDen = Math.abs(denominator);
        long g = gcd(absNum, absDen);
        return new Fraction(sign * numerator / g, sign * denominator / g);
    }

    Fraction add(Fraction other) {
        return of(this.numerator * other.denominator + other.numerator * this.denominator,
                this.denominator * other.denominator);
    }

    Fraction subtract(Fraction other) {
        return of(this.numerator * other.denominator - other.numerator * this.denominator,
                this.denominator * other.denominator);
    }

    Fraction multiply(Fraction other) {
        return of(this.numerator * other.numerator, this.denominator * other.denominator);
    }

    Fraction divide(Fraction other) {
        return of(this.numerator * other.denominator, this.denominator * other.numerator);
    }

    Fraction negate() {
        return of(-numerator, denominator);
    }

    Fraction abs() {
        return numerator < 0 ? negate() : this;
    }

    boolean isZero() {
        return numerator == 0;
    }

    static long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            long t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    static long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }
}
