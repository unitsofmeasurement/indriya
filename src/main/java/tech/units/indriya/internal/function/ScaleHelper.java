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
package tech.units.indriya.internal.function;

import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import javax.measure.Quantity;
import javax.measure.Quantity.Scale;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import static javax.measure.Quantity.Scale.ABSOLUTE;
import static javax.measure.Quantity.Scale.RELATIVE;

import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.function.AbstractConverter;
import tech.units.indriya.quantity.Quantities;

/**
 * Encapsulates scale-honoring quantity arithmetics.
 * 
 * @author Andi Huber
 */
public final class ScaleHelper {

    public static enum OperandMode {
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
        public boolean isAllRelative() {
            return this==ALL_RELATIVE;
        }
    }
    
    public static boolean isAbsolute(final Quantity<?> quantity) {
        return ABSOLUTE==quantity.getScale();
    }

    public static boolean isRelative(final Quantity<?> quantity) {
        return RELATIVE==quantity.getScale();
    }

    public static <Q extends Quantity<Q>> ComparableQuantity<Q> convertTo(
            final Quantity<Q> quantity, 
            final Unit<Q> anotherUnit) {

        if (isRelative(quantity)) {
            final Unit<Q> systemUnit = quantity.getUnit().getSystemUnit();
            final ToSystemUnitConverter converter = ToSystemUnitConverter.forQuantity(quantity, systemUnit);
            final Number valueInSystemUnit = converter.apply(quantity.getValue());
            // not if the calculation must yield RELATIVE UNITS
            final Number valueInOtherUnit = systemUnit.getConverterTo(anotherUnit).convert(valueInSystemUnit);
            return Quantities.getQuantity(valueInOtherUnit, anotherUnit, Scale.RELATIVE);
        }
        final Number convertedValue = quantity.getUnit().getConverterTo(anotherUnit).convert(quantity.getValue());
        return Quantities.getQuantity(convertedValue, anotherUnit);
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

        // if operands has scale RELATIVE, multiplication is trivial
        if (RELATIVE.equals(quantity.getScale())) {
            return Quantities.getQuantity(operator.apply(quantity.getValue()), quantity.getUnit(), RELATIVE);
        }

        final ToSystemUnitConverter thisConverter = toSystemUnitConverterForMul(quantity);

        final Number thisValueWithAbsoluteScale = thisConverter.apply(quantity.getValue());
        final Number resultValueInAbsUnits = operator.apply(thisValueWithAbsoluteScale);
        final boolean needsInvering = !thisConverter.isNoop();

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

        final ToSystemUnitConverter thisConverter = toSystemUnitConverterForMul(q1);
        final ToSystemUnitConverter thatConverter = toSystemUnitConverterForMul(q2);

        final Number thisValueWithAbsoluteScale = thisConverter.apply(q1.getValue());
        final Number thatValueWithAbsoluteScale = thatConverter.apply(q2.getValue());

        final Number resultValueInSystemUnits = amountOperator.apply(thisValueWithAbsoluteScale, thatValueWithAbsoluteScale);

        final Unit<?> thisAbsUnit = thisConverter.isNoop() ? q1.getUnit() : q1.getUnit().getSystemUnit();
        final Unit<?> thatAbsUnit = thatConverter.isNoop() ? q2.getUnit() : q2.getUnit().getSystemUnit();

        return Quantities.getQuantity(
                resultValueInSystemUnits, 
                unitOperator.apply(thisAbsUnit, thatAbsUnit));
    }

    // -- HELPER - LOW LEVEL

    // used for addition, also honors RELATIVE scale
    private static <Q extends Quantity<Q>> ToSystemUnitConverter toSystemUnitConverterForAdd(
            final Quantity<Q> q1,
            final Quantity<Q> quantity) {
        final Unit<Q> systemUnit = q1.getUnit().getSystemUnit();
        return ToSystemUnitConverter.forQuantity(quantity, systemUnit);
    }

    // used for multiplication, also honors RELATIVE scale
    private static <T extends Quantity<T>> 
    ToSystemUnitConverter toSystemUnitConverterForMul(Quantity<T> quantity) {
        final Unit<T> systemUnit = quantity.getUnit().getSystemUnit();
        return ToSystemUnitConverter.forQuantity(quantity, systemUnit);
    }


    // also honors RELATIVE scale
    private static class ToSystemUnitConverter implements UnaryOperator<Number> {
        private final UnaryOperator<Number> unaryOperator;
        private final UnaryOperator<Number> inverseOperator;

        public static <Q extends Quantity<Q>>  
        ToSystemUnitConverter forQuantity(Quantity<Q> quantity, Unit<Q> systemUnit) {
            if(quantity.getUnit().equals(systemUnit)) {
                return ToSystemUnitConverter.noop(); // no conversion required
            }

            final UnitConverter converter = quantity.getUnit().getConverterTo(systemUnit);

            if(ABSOLUTE.equals(quantity.getScale())) {

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
        private ToSystemUnitConverter(UnaryOperator<Number> unaryOperator, UnaryOperator<Number> inverseOperator) {
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

        private static Optional<Number> linearFactorOf(UnitConverter converter) {
            return (converter instanceof AbstractConverter)
                    ? ((AbstractConverter)converter).linearFactor()
                            : Optional.empty();
        }

        private static UnsupportedOperationException unsupportedConverter(UnitConverter converter, Unit<?> unit) {
            return new UnsupportedOperationException(
                    String.format(
                            "Scale conversion from RELATIVE to ABSOLUTE for Unit %s having Converter %s is not implemented.", 
                            unit, converter));
        }

    }

}
