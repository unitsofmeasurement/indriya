package tech.units.indriya.internal.calc;

public interface NumberSystem {

    Number add(Number x, Number y);
    Number subtract(Number x, Number y);

    Number multiply(Number x, Number y);
    Number divide(Number x, Number y);
    
    Number reciprocal(Number number);
    Number negate(Number number);
    
    Number narrow(Number number);

}
