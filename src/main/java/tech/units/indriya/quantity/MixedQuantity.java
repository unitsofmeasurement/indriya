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
import java.math.BigInteger;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.function.Calculus;
import tech.units.indriya.unit.MixedUnit;

/**
 * An implementation of {@link Quantity} that represents multi-radix quantities (like "1 hour:20 min:45 sec" or "6 ft, 4 in").
 * This object is immutable.
 *
 * @see AbstractQuantity
 * @see Quantity
 * @see ComparableQuantity
 * @see MixedUnit
 * @param <Q>
 *            The type of the quantity.
 * @author <a href="mailto:werner@units.tech">Werner Keil</a>
 * @version 1.4, $Date: 2019-03-22 $
 * @since 2.0
 */
public class MixedQuantity<Q extends Quantity<Q>> extends AbstractQuantity<Q> {

    /**
     * 
     */
    private static final long serialVersionUID = 3353669444479095073L;

    private final Number[] values;

    /**
     * Indicates if this quantity is big.
     */
    private final boolean isBig;

    /**
     * @since 2.0
     */
    protected MixedQuantity(Number[] numbers, Unit<Q> unit, Scale sc) {
        super(unit, sc);
        Objects.requireNonNull(numbers);
        values = numbers;
        boolean tmpBig = false;
        for (Number number : numbers) {
            if (number instanceof BigDecimal || number instanceof BigInteger)
                tmpBig = true;
        }
        isBig = tmpBig;
    }

    /**
     * @since 2.0
     */
    protected MixedQuantity(Number[] numbers, Unit<Q> unit) {
        this(numbers, unit, ABSOLUTE);
    }

    @Override
    public double doubleValue(Unit<Q> unit) {
        UnitConverter converter = getUnit().getConverterTo(unit);
        return converter.convert(getValue().doubleValue());
    }

    /**
     * Returns the numeric value of the quantity represented in the {@link MixedUnit#getReferenceUnit() reference unit} (first in the array/list) of this {@link MixedUnit mixed unit}.
     * @since 2.0
     */
    @Override
    public Number getValue() {
        if (getUnit() instanceof MixedUnit) {
            final MixedUnit<Q> mixUnit =  (MixedUnit<Q>) getUnit();
            ComparableQuantity<Q> result = null;
            if (values.length == mixUnit.getUnits().size()) {
                for (int i = 0; i < values.length; i++) {
                    Number value = values[i];
                    if (result == null) {
                        result = Quantities.getQuantity(value, mixUnit.getUnits().get(i), getScale());
                    } else {
                        result = result.add(Quantities.getQuantity(value, mixUnit.getUnits().get(i), getScale()));
                    }
                }
                return result.getValue();
            } else {
                throw new IllegalArgumentException(String.format("%s values don't match %s in mixed unit", values.length, mixUnit.getUnits().size()));
            }
        } else {
            throw new IllegalArgumentException(String.format("%s not a mixed unit", getUnit()));
        }
    }

    /**
     * Returns the array of numeric values of the quantity corresponding to the {@link MixedUnit mixed unit}.
     * 
     * @return the array of numeric values; not null
     * @since 2.0
     */
    public Number[] getValues() {
        return values;
    }

    /**
     * Indicates if this measured amount is a big number, i.E. BigDecimal or BigInteger. In all other cases this would be false.
     *
     * @return <code>true</code> if this quantity is big; <code>false</code> otherwise.
     */
    @Override
    public boolean isBig() {
        return isBig;
    }

    @Override
    public ComparableQuantity<Q> add(Quantity<Q> that) {
        return toDecimalQuantity().add(that);
    }

    @Override
    public ComparableQuantity<?> multiply(Quantity<?> that) {
        return toDecimalQuantity().multiply(that);
    }

    @Override
    public ComparableQuantity<Q> multiply(Number that) {
        return toDecimalQuantity().multiply(that);
    }

    @Override
    public ComparableQuantity<?> divide(Quantity<?> that) {
        return toDecimalQuantity().divide(that);
    }

    @Override
    public ComparableQuantity<Q> divide(Number that) {
        return toDecimalQuantity().divide(that);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public ComparableQuantity<Q> inverse() {
        Number[] inv = new Number[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof BigDecimal) {
                inv[i] = BigDecimal.ONE.divide((BigDecimal) values[i]);
            } else if (values[i] instanceof BigInteger) {
                BigDecimal.ONE.divide(new BigDecimal((BigInteger) values[i]));
            } else {
                inv[i] = 1d / values[i].doubleValue();
            }
        }
        return new MixedQuantity(inv, getUnit().inverse());
    }

    @Override
    public BigDecimal decimalValue(Unit<Q> unit) throws ArithmeticException {
        return Calculus.toBigDecimal(getUnit().getConverterTo(unit).convert(getValue()));
    }

    @Override
    public ComparableQuantity<Q> subtract(Quantity<Q> that) {
        return toDecimalQuantity().subtract(that);
    }

    /**
     * <p>
     * Returns a {@code NumberQuantity} with same Unit, but whose getValue() is {@code(-this.getValue())}. </p>
     * 
     * @return {@code -this}.
     */
    public Quantity<Q> negate() {
        if (BigDecimal.class.isInstance(getValue())) {
            return new DecimalQuantity<>(BigDecimal.class.cast(getValue()).negate(), getUnit());
        } else if (BigInteger.class.isInstance(getValue())) {
            return new DecimalQuantity<>(new BigDecimal(BigInteger.class.cast(getValue())).negate(), getUnit());
        } else if (Long.class.isInstance(getValue())) {
            return new IntegerQuantity<>(-getValue().intValue(), getUnit());
        } else if (Integer.class.isInstance(getValue())) {
            return new IntegerQuantity<>(-getValue().intValue(), getUnit());
        } else if (Float.class.isInstance(getValue())) {
            return new FloatQuantity<>(-getValue().floatValue(), getUnit());
        } else if (Short.class.isInstance(getValue())) {
            return new ShortQuantity<>((short) -getValue().shortValue(), getUnit());
        } else if (Byte.class.isInstance(getValue())) {
            return new ByteQuantity<>((byte) -getValue().byteValue(), getUnit());
        } else {
            return new DoubleQuantity<>(-getValue().doubleValue(), getUnit());
        }
    }

    private DecimalQuantity<Q> toDecimalQuantity() {
        return new DecimalQuantity<>(BigDecimal.valueOf(getValue().doubleValue()), getUnit());
    }

    /**
     * Returns the mixed quantity for the specified <code>Number</code> array stated in the specified unit.
     *
     * @param values
     *            the measurement values.
     * @param unit
     *            the measurement unit.
     * @return the corresponding mixed quantity.
     */
    public static <Q extends Quantity<Q>> AbstractQuantity<Q> of(Number[] values, Unit<Q> unit) {
        return new MixedQuantity<Q>(values, unit);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Quantity<?>) {
            Quantity<?> that = (Quantity<?>) obj;
            return Objects.equals(getUnit(), that.getUnit()) && Equalizer.hasEquality(getValue(), that.getValue());
        }
        return false;
    }

    /**
     * Returns this quantity after conversion to specified unit. The default implementation returns
     * <code>NumberQuantity.of(doubleValue(unit), unit)</code> . If this quantity is already stated in the specified unit, then this quantity is
     * returned and no conversion is performed.
     *
     * @param anotherUnit
     *            the unit in which the returned measure is stated.
     * @return this quantity or a new quantity equivalent to this quantity stated in the specified unit.
     * @throws ArithmeticException
     *             if the result is inexact and the quotient has a non-terminating decimal expansion.
     */
    @Override
    public ComparableQuantity<Q> to(Unit<Q> anotherUnit) {
        if (anotherUnit instanceof MixedUnit) {
            // TODO test for the array size of the target MixedUnit compared to this one.
        }
        return super.to(anotherUnit);
    }
}
