/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2021, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
package tech.units.indriya.internal.function;

import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import javax.measure.Quantity;
import javax.measure.Quantity.Scale;

import org.apiguardian.api.API;

import javax.measure.Unit;
import javax.measure.UnitConverter;

import static javax.measure.Quantity.Scale.ABSOLUTE;
import static javax.measure.Quantity.Scale.RELATIVE;
import static org.apiguardian.api.API.Status.INTERNAL;

import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.function.AbstractConverter;
import tech.units.indriya.quantity.Quantities;

/**
 * Encapsulates scale-honoring quantity arithmetics.
 * 
 * @author Andi Huber
 */
@API(status=INTERNAL)
public final class ScaleHelper {

    public static boolean isAbsolute(final Quantity<?> quantity) {
        return ABSOLUTE == quantity.getScale();
    }

    public static boolean isRelative(final Quantity<?> quantity) {
        return RELATIVE == quantity.getScale();
    }

    public static <Q extends Quantity<Q>> ComparableQuantity<Q> convertTo(
            final Quantity<Q> quantity, 
            final Unit<Q> anotherUnit) {

        final UnitConverter converter = quantity.getUnit().getConverterTo(anotherUnit);
        
        if (isRelative(quantity)) {
            final Number linearFactor = linearFactorOf(converter).orElse(null);
            if(linearFactor==null) {
                throw unsupportedRelativeScaleConversion(quantity, anotherUnit);
            }
            final Number valueInOtherUnit = Calculator.of(linearFactor).multiply(quantity.getValue()).peek();
            return Quantities.getQuantity(valueInOtherUnit, anotherUnit, RELATIVE);
        }
        
        final Number convertedValue = converter.convert(quantity.getValue());
        return Quantities.getQuantity(convertedValue, anotherUnit, ABSOLUTE);
    }

    public static <Q extends Quantity<Q>> ComparableQuantity<Q> addition(
            final Quantity<Q> q1,
            final Quantity<Q> q2, 
            final BinaryOperator<Number> operator) {

        final boolean yieldsRelativeScale = OperandMode.get(q1, q2).isAllRelative(); 

        // converting almost all, except system units and those that are shifted and relative like eg. Δ2°C == Δ2K
        final ToSystemUnitConverter thisConverter = toSystemUnitConverterForAdd(q1, q1);
        final ToSystemUnitConverter thatConverter = toSystemUnitConverterForAdd(q1, q2);

        final Number thisValueInSystemUnit = thisConverter.apply(q1.getValue());
        final Number thatValueInSystemUnit = thatConverter.apply(q2.getValue());

        final Number resultValueInSystemUnit = operator.apply(thisValueInSystemUnit, thatValueInSystemUnit);

        if (yieldsRelativeScale) {
            return Quantities.getQuantity(thisConverter.invert(resultValueInSystemUnit), q1.getUnit(), RELATIVE);
        }

        final boolean needsInvering = !thisConverter.isNoop() || !thatConverter.isNoop();
        final Number resultValueInThisUnit = needsInvering 
                ? q1.getUnit().getConverterTo(q1.getUnit().getSystemUnit()).inverse().convert(resultValueInSystemUnit)
                : resultValueInSystemUnit;

        return Quantities.getQuantity(resultValueInThisUnit, q1.getUnit(), ABSOLUTE);
    }

    public static <Q extends Quantity<Q>> ComparableQuantity<Q> scalarMultiplication(
            final Quantity<Q> quantity, 
            final UnaryOperator<Number> operator) {

        // if operand has scale RELATIVE, multiplication is trivial
        if (isRelative(quantity)) {
            return Quantities.getQuantity(
                    operator.apply(quantity.getValue()), 
                    quantity.getUnit(), 
                    RELATIVE);
        }

        final ToSystemUnitConverter toSystemUnits = toSystemUnitConverterForMul(quantity);

        final Number thisValueWithAbsoluteScale = toSystemUnits.apply(quantity.getValue());
        final Number resultValueInAbsUnits = operator.apply(thisValueWithAbsoluteScale);
        final boolean needsInvering = !toSystemUnits.isNoop();

        final Number resultValueInThisUnit = needsInvering 
                ? quantity.getUnit().getConverterTo(quantity.getUnit().getSystemUnit()).inverse().convert(resultValueInAbsUnits)
                : resultValueInAbsUnits;

        return Quantities.getQuantity(resultValueInThisUnit, quantity.getUnit(), quantity.getScale());
    }

    public static ComparableQuantity<?> multiplication(
            final Quantity<?> q1,
            final Quantity<?> q2, 
            final BinaryOperator<Number> amountOperator, 
            final BinaryOperator<Unit<?>> unitOperator) {
        
        final Quantity<?> absQ1 = toAbsoluteLinear(q1);
        final Quantity<?> absQ2 = toAbsoluteLinear(q2);
        return Quantities.getQuantity(
                amountOperator.apply(absQ1.getValue(), absQ2.getValue()), 
                unitOperator.apply(absQ1.getUnit(), absQ2.getUnit()));
    }

    // -- HELPER

    private static <Q extends Quantity<Q>> Quantity<Q> toAbsoluteLinear(Quantity<Q> quantity) {
        final Unit<Q> systemUnit = quantity.getUnit().getSystemUnit();
        final UnitConverter toSystemUnit = quantity.getUnit().getConverterTo(systemUnit);
        if(toSystemUnit.isLinear()) {
            if(isAbsolute(quantity)) {
                return quantity;
            }
            return Quantities.getQuantity(quantity.getValue(), quantity.getUnit());
        }
        // convert to system units
        if(isAbsolute(quantity)) {
            return Quantities.getQuantity(toSystemUnit.convert(quantity.getValue()), systemUnit, Scale.ABSOLUTE);
        } else {
            final Number linearFactor = linearFactorOf(toSystemUnit).orElse(null);
            if(linearFactor==null) {
                throw unsupportedRelativeScaleConversion(quantity, systemUnit);
            }
            final Number valueInSystemUnits = Calculator.of(linearFactor).multiply(quantity.getValue()).peek();
            return Quantities.getQuantity(valueInSystemUnits, systemUnit, ABSOLUTE);
        }
    }

    // used for addition, honors RELATIVE scale
    private static <Q extends Quantity<Q>> ToSystemUnitConverter toSystemUnitConverterForAdd(
            final Quantity<Q> q1,
            final Quantity<Q> q2) {
        final Unit<Q> systemUnit = q1.getUnit().getSystemUnit();
        return ToSystemUnitConverter.forQuantity(q2, systemUnit);
    }

    // used for multiplication, honors RELATIVE scale
    private static <T extends Quantity<T>> 
    ToSystemUnitConverter toSystemUnitConverterForMul(Quantity<T> quantity) {
        final Unit<T> systemUnit = quantity.getUnit().getSystemUnit();
        return ToSystemUnitConverter.forQuantity(quantity, systemUnit);
    }

    private static Optional<Number> linearFactorOf(UnitConverter converter) {
        return (converter instanceof AbstractConverter)
                ? ((AbstractConverter)converter).linearFactor()
                : Optional.empty();
    }

    // honors RELATIVE scale
    private static class ToSystemUnitConverter implements UnaryOperator<Number> {
        private final UnaryOperator<Number> unaryOperator;
        private final UnaryOperator<Number> inverseOperator;

        public static <Q extends Quantity<Q>>  
        ToSystemUnitConverter forQuantity(Quantity<Q> quantity, Unit<Q> systemUnit) {
            if(quantity.getUnit().equals(systemUnit)) {
                return ToSystemUnitConverter.noop(); // no conversion required
            }

            final UnitConverter converter = quantity.getUnit().getConverterTo(systemUnit);

            if(isAbsolute(quantity)) {

                return ToSystemUnitConverter.of(converter::convert); // convert to system units

            } else {
                final Number linearFactor = linearFactorOf(converter).orElse(null);
                if(linearFactor!=null) {
                    // conversion by factor required ... Δ2°C -> Δ2K , Δ2°F -> 5/9 * Δ2K
                    return ToSystemUnitConverter.factor(linearFactor); 
                }
                // convert any other cases of RELATIVE scale to system unit (ABSOLUTE) ...
                throw unsupportedConverter(converter, quantity.getUnit());
            }
        }

        public Number invert(Number x) {
            return isNoop() 
                    ? x
                    : inverseOperator.apply(x); 
        }

        public static ToSystemUnitConverter of(UnaryOperator<Number> unaryOperator) {
            return new ToSystemUnitConverter(unaryOperator, null);
        }
        public static ToSystemUnitConverter noop() {
            return new ToSystemUnitConverter(null, null);
        }
        public static ToSystemUnitConverter factor(Number factor) {
            return new ToSystemUnitConverter(
                    number->Calculator.of(number).multiply(factor).peek(),
                    number->Calculator.of(number).divide(factor).peek());
        }
        private ToSystemUnitConverter(
                UnaryOperator<Number> unaryOperator, 
                UnaryOperator<Number> inverseOperator) {
            this.unaryOperator = unaryOperator;
            this.inverseOperator = inverseOperator;
        }
        public boolean isNoop() {
            return unaryOperator==null;
        }
        @Override
        public Number apply(Number x) {
            return isNoop() 
                    ? x
                    : unaryOperator.apply(x); 
        }

    }
    
    // -- OPERANDS
    
    private static enum OperandMode {
        ALL_ABSOLUTE,
        ALL_RELATIVE,
        MIXED;
        public static OperandMode get(
                final Quantity<?> q1,
                final Quantity<?> q2) {
            if(q1.getScale()!=q2.getScale()) {
                return OperandMode.MIXED;
            }
            return isAbsolute(q1)
                    ? OperandMode.ALL_ABSOLUTE
                    : OperandMode.ALL_RELATIVE;
        }
//        public boolean isAllAbsolute() {
//            return this==ALL_ABSOLUTE;
//        }
        public boolean isAllRelative() {
            return this==ALL_RELATIVE;
        }
    }
        
    // -- EXCEPTIONS
    
    private static <Q extends Quantity<Q>> UnsupportedOperationException unsupportedRelativeScaleConversion(
            Quantity<Q> quantity, 
            Unit<Q> anotherUnit) {
        return new UnsupportedOperationException(
                String.format(
                        "Conversion of Quantitity %s to Unit %s is not supported for realtive scale.", 
                        quantity, anotherUnit));
    }
    
    private static UnsupportedOperationException unsupportedConverter(UnitConverter converter, Unit<?> unit) {
        return new UnsupportedOperationException(
                String.format(
                        "Scale conversion from RELATIVE to ABSOLUTE for Unit %s having Converter %s is not implemented.", 
                        unit, converter));
    }	
}
