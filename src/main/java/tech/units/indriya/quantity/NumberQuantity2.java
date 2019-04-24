/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2019, Units of Measurement project.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 *    and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of JSR-385, Indriya nor the names of their contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tech.units.indriya.quantity;

import static javax.measure.Quantity.Scale.ABSOLUTE;

import java.math.BigDecimal;
import java.util.function.BinaryOperator;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.internal.function.calc.Calculator;

/**
 * Implementation of {@link ComparableQuantity} that holds a Java {@link Number}, 
 * which represented this quantity's amount.
 * <p> 
 * This object is immutable. 
 * <p>
 * TODO[220] rename to NumberQuantity
 *
 * @see AbstractQuantity
 * @see Quantity
 * @see ComparableQuantity
 * @param <Q>
 *          The type of the quantity.
 * @author Andi Huber
 * @since 1.0
 * 
 */
public class NumberQuantity2<Q extends Quantity<Q>> extends AbstractQuantity<Q> {

    private static final long serialVersionUID = -6494337491031528402L;
    
    private final Number value;

    /**
     * @since 2.0
     */
    protected NumberQuantity2(Number number, Unit<Q> unit, Scale sc) {
      super(unit, sc);
      value = number;
    }
    
    protected NumberQuantity2(Number number, Unit<Q> unit) {
        this(number, unit, ABSOLUTE); 
    }

    @Override
    public ComparableQuantity<Q> add(Quantity<Q> that) {
        return addition(that, (thisValueInSystemUnit, thatValueInSystemUnit) ->
        Calculator
            .loadDefault(thisValueInSystemUnit)
            .add(thatValueInSystemUnit)
            .peek());
    }

    @Override
    public ComparableQuantity<Q> subtract(Quantity<Q> that) {
        return addition(that, (thisValueInSystemUnit, thatValueInSystemUnit) -> 
        Calculator
            .loadDefault(thisValueInSystemUnit)
            .subtract(thatValueInSystemUnit)
            .peek());
    }

    @Override
    public ComparableQuantity<?> divide(Quantity<?> that) {
        final Number resultValueInThisUnit = Calculator
                .loadDefault(getValue())
                .divide(that.getValue())
                .peek();
        return Quantities.getQuantity(resultValueInThisUnit, getUnit().divide(that.getUnit()));
    }

    @Override
    public ComparableQuantity<Q> divide(Number divisor) {
        final Number resultValueInThisUnit = Calculator
                .loadDefault(getValue())
                .divide(divisor)
                .peek();
        return Quantities.getQuantity(resultValueInThisUnit, getUnit());
    }

    @Override
    public ComparableQuantity<?> multiply(Quantity<?> that) {
        final Number resultValueInThisUnit = Calculator
                .loadDefault(getValue())
                .multiply(that.getValue())
                .peek();
        return Quantities.getQuantity(resultValueInThisUnit, getUnit().multiply(that.getUnit()));
    }

    @Override
    public ComparableQuantity<Q> multiply(Number multiplier) {
        final Number resultValueInThisUnit = Calculator
                .loadDefault(getValue())
                .multiply(multiplier)
                .peek();
        return Quantities.getQuantity(resultValueInThisUnit, getUnit());
    }

    @Override
    public ComparableQuantity<?> inverse() {
        final Number resultValueInThisUnit = Calculator
                .loadDefault(getValue())
                .reciprocal()
                .peek();
        return Quantities.getQuantity(resultValueInThisUnit, getUnit().inverse());
    }

    @Override
    public Quantity<Q> negate() {
        final Number resultValueInThisUnit = Calculator
                .loadDefault(getValue())
                .negate()
                .peek();
        return Quantities.getQuantity(resultValueInThisUnit, getUnit());
    }

    @Override
    public Number getValue() {
        return value;
    }
    
    // -- HELPER
    
    private ComparableQuantity<Q> addition(Quantity<Q> that, BinaryOperator<Number> operator) {
        
        final Unit<Q> systemUnit = getUnit().getSystemUnit();
        final UnitConverter c1 = this.getUnit().getConverterTo(systemUnit);
        final UnitConverter c2 = that.getUnit().getConverterTo(systemUnit);
        
        final Number thisValueInSystemUnit = c1.convert(this.getValue()); 
        final Number thatValueInSystemUnit = c2.convert(that.getValue()); 
        
        final Number resultValueInSystemUnit = 
                operator.apply(thisValueInSystemUnit, thatValueInSystemUnit);

        final Number resultValueInThisUnit = c1.inverse().convert(resultValueInSystemUnit);

        //TODO[220] scale not handled at all !!!
        return Quantities.getQuantity(resultValueInThisUnit, getUnit());
    }
    
    // -- DEPRECATIONS

    @Override
    public boolean isBig() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal decimalValue(Unit<Q> aUnit) throws ArithmeticException {
        throw new UnsupportedOperationException();
    }

    @Override
    public double doubleValue(Unit<Q> aUnit) throws ArithmeticException {
        throw new UnsupportedOperationException();
    }

}
