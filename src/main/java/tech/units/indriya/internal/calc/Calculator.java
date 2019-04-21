package tech.units.indriya.internal.calc;

import java.util.Objects;

import tech.units.indriya.function.Calculus;

public class Calculator {

    public static Calculator getDefault() {
        return new Calculator(Calculus.NUMBER_SYSTEM);
    }

    public static Calculator loadDefault(Number number) {
        return getDefault().load(number);
    }

    private final NumberSystem ns;
    private Number acc = 0;
    
    private Calculator(NumberSystem ns) {
        this.ns = ns;
    }

    public Calculator load(Number number) {
        Objects.requireNonNull(number);
        this.acc = number;
        return this;
    }
    
    public Calculator add(Number number) {
        Objects.requireNonNull(number);
        acc = ns.add(acc, number);    
        return this;
    }
    
    public Calculator subtract(Number number) {
        Objects.requireNonNull(number);
        acc = ns.subtract(acc, number);
        return this;
    }
    
    public Calculator multiply(Number number) {
        acc = ns.multiply(acc, number);    
        return this;
    }

    public Calculator divide(Number number) {
        acc = ns.divide(acc, number);    
        return this;
    }

    public Calculator negate() {
        acc = ns.negate(acc);
        return this;
    }

    public Calculator reciprocal() {
        acc = ns.reciprocal(acc);
        return this;
    }
    
    public Number peek() {
        return ns.narrow(acc);
    }
  
    
}
