/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2020, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
import static javax.measure.Quantity.Scale.RELATIVE;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.internal.function.Calculator;

/**
 * Implementation of {@link ComparableQuantity} that holds a Java {@link Number}, 
 * which represented this quantity's amount.
 * <p> 
 * This object is immutable. 
 * <p>
 *
 * @see AbstractQuantity
 * @see Quantity
 * @see ComparableQuantity
 * @param <Q>
 *          The type of the quantity.
 * @author Andi Huber
 * @author Werner Keil
 * @version 1.4
 * @since 1.0
 * 
 */
public class NumberQuantity<Q extends Quantity<Q>> extends AbstractQuantity<Q> {

    private static final long serialVersionUID = -6494337491031528402L;
    
    private final Number value;

    /**
     * @since 2.0
     */
    protected NumberQuantity(Number number, Unit<Q> unit, Scale sc) {
      super(unit, sc);
      value = Calculator.of(number).peek(); // takes care of invalid number values (infinity, ...)
    }
    
    protected NumberQuantity(Number number, Unit<Q> unit) {
        this(number, unit, ABSOLUTE); 
    }

    @Override
    public ComparableQuantity<Q> add(Quantity<Q> that) {
        return addition(that, 
                (thisValue, thatValue) -> Calculator.of(thisValue).add(thatValue).peek());
    }

    @Override
    public ComparableQuantity<Q> subtract(Quantity<Q> that) {
        return addition(that, 
                (thisValue, thatValue) -> Calculator.of(thisValue).subtract(thatValue).peek());
    }

    @Override
    public ComparableQuantity<?> divide(Quantity<?> that) {
        return multiplication(that, 
                (thisValue, thatValue) -> Calculator.of(thisValue).divide(thatValue).peek(),
                (thisUnit, thatUnit) -> thisUnit.divide(thatUnit));
    }

    @Override
    public ComparableQuantity<Q> divide(Number divisor) {
        return scalarMultiplication(thisValue -> 
                Calculator.of(thisValue).divide(divisor).peek());
    }

    @Override
    public ComparableQuantity<?> multiply(Quantity<?> that) {
        return multiplication(that, 
                (thisValue, thatValue) -> Calculator.of(thisValue).multiply(thatValue).peek(),
                (thisUnit, thatUnit) -> thisUnit.multiply(thatUnit));
    }

    @Override
    public ComparableQuantity<Q> multiply(Number factor) {
        return scalarMultiplication(thisValue -> 
                Calculator.of(thisValue).multiply(factor).peek());
    }

    @Override
    public ComparableQuantity<?> inverse() {
        final Number resultValueInThisUnit = Calculator
                .of(getValue())
                .reciprocal()
                .peek();
        return Quantities.getQuantity(resultValueInThisUnit, getUnit().inverse());
    }

    @Override
    public Quantity<Q> negate() {
        final Number resultValueInThisUnit = Calculator
                .of(getValue())
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
	    
	    final boolean yieldsRelativeScale = RELATIVE.equals(this.getScale()) && RELATIVE.equals(that.getScale());
	    
		// converting almost all, except system units and those that are shifted and relative like eg. Δ2°C == Δ2K
		final ToSystemUnitConverter thisConverter = toSystemUnitConverterForAdd(this);
		final ToSystemUnitConverter thatConverter = toSystemUnitConverterForAdd(that);
		
		final Number thisValueInSystemUnit = thisConverter.apply(this.getValue());
		final Number thatValueInSystemUnit = thatConverter.apply(that.getValue());

		final Number resultValueInSystemUnit = operator.apply(thisValueInSystemUnit, thatValueInSystemUnit);
		
        if (yieldsRelativeScale) {
            return Quantities.getQuantity(thisConverter.invert(resultValueInSystemUnit), this.getUnit(), RELATIVE);
        }
		
        final boolean needsInvering = !thisConverter.isNoop() || !thatConverter.isNoop();
		final Number resultValueInThisUnit = needsInvering 
		        ? this.getUnit().getConverterTo(this.getUnit().getSystemUnit()).inverse().convert(resultValueInSystemUnit)
		        : resultValueInSystemUnit;
		
		return Quantities.getQuantity(resultValueInThisUnit, this.getUnit(), ABSOLUTE);
	}

	private ComparableQuantity<Q> scalarMultiplication(UnaryOperator<Number> operator) {

	    // if operands has scale RELATIVE, multiplication is trivial
        if (RELATIVE.equals(this.getScale())) {
            return Quantities.getQuantity(operator.apply(this.getValue()), this.getUnit(), RELATIVE);
        }
	    
	    final ToSystemUnitConverter thisConverter = toSystemUnitConverterForMul(this);
        
        final Number thisValueWithAbsoluteScale = thisConverter.apply(this.getValue());
        final Number resultValueInAbsUnits = operator.apply(thisValueWithAbsoluteScale);
        final boolean needsInvering = !thisConverter.isNoop();
        
        final Number resultValueInThisUnit = needsInvering 
                ? this.getUnit().getConverterTo(this.getUnit().getSystemUnit()).inverse().convert(resultValueInAbsUnits)
                : resultValueInAbsUnits;
        
	    return Quantities.getQuantity(resultValueInThisUnit, this.getUnit(), this.getScale());
	}
	
    private ComparableQuantity<?> multiplication(
            Quantity<?> that, 
            BinaryOperator<Number> amountOperator, 
            BinaryOperator<Unit<?>> unitOperator) {
	    
	    final ToSystemUnitConverter thisConverter = toSystemUnitConverterForMul(this);
        final ToSystemUnitConverter thatConverter = toSystemUnitConverterForMul(that);
        
        final Number thisValueWithAbsoluteScale = thisConverter.apply(this.getValue());
        final Number thatValueWithAbsoluteScale = thatConverter.apply(that.getValue());

        final Number resultValueInSystemUnits = amountOperator.apply(thisValueWithAbsoluteScale, thatValueWithAbsoluteScale);
        
        final Unit<Q> thisAbsUnit = thisConverter.isNoop() ? this.getUnit() : this.getUnit().getSystemUnit();
        final Unit<?> thatAbsUnit = thatConverter.isNoop() ? that.getUnit() : that.getUnit().getSystemUnit();
        
        return Quantities.getQuantity(
                resultValueInSystemUnits, 
                unitOperator.apply(thisAbsUnit, thatAbsUnit));
	}
	
	// -- HELPER - LOW LEVEL
    
    // used for addition, also honors RELATIVE scale
    private ToSystemUnitConverter toSystemUnitConverterForAdd(Quantity<Q> quantity) {
        final Unit<Q> systemUnit = this.getUnit().getSystemUnit();
        return ToSystemUnitConverter.forQuantity(quantity, systemUnit);
    }

    // used for multiplication, also honors RELATIVE scale
    private static <T extends Quantity<T>> 
    ToSystemUnitConverter toSystemUnitConverterForMul(Quantity<T> quantity) {
        final Unit<T> systemUnit = quantity.getUnit().getSystemUnit();
        return ToSystemUnitConverter.forQuantity(quantity, systemUnit);
    }
    
    

}
