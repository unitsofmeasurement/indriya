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
import java.text.ParsePosition;
import java.util.Objects;

import javax.measure.MeasurementException;
import javax.measure.Quantity;
import javax.measure.Quantity.Scale;
import javax.measure.Unit;
import javax.measure.format.MeasurementParseException;

import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.format.AbstractQuantityFormat;
import tech.units.indriya.format.SimpleQuantityFormat;
import tech.units.indriya.unit.MixedUnit;

/**
 * Singleton class for accessing {@link Quantity} instances.
 * 
 * @version 1.7, March 21, 2019
 * @author werner
 * @author otaviojava
 * @since 1.0
 */
public final class Quantities {
    /**
     * Private singleton constructor.
     */
    private Quantities() {
    }

    /**
     * Returns the {@link #valueOf(java.math.BigDecimal, javax.measure.unit.Unit) decimal} quantity of unknown type corresponding to the specified
     * representation. This method can be used to parse dimensionless quantities.<br>
     * <code>
     *     Quantity<Dimensionless> proportion = Quantities.getQuantity("0.234").asType(Dimensionless.class);
     * </code>
     *
     * <p>
     * Note: This method handles only Locale-neutral quantity formatting and parsing are handled by the {@link AbstractQuantityFormat} class and its
     * subclasses.
     * </p>
     *
     * @param csq
     *            the decimal value and its unit (if any) separated by space(s).
     * @return <code>QuantityFormat.getInstance(LOCALE_NEUTRAL).parse(csq, new ParsePosition(0))</code>
     */
    public static ComparableQuantity<?> getQuantity(CharSequence csq) {
        try {
            return SimpleQuantityFormat.getInstance().parse(csq, new ParsePosition(0));
        } catch (MeasurementParseException e) {
            throw new IllegalArgumentException(e.getParsedString());
        }
    }

    /**
     * Returns the scalar quantity. When the {@link Number} was {@link BigDecimal} or {@link BigInteger} will uses {@link DecimalQuantity}, when the
     * {@link Number} was {@link Double} will {@link DoubleQuantity} otherwise will {@link NumberQuantity}. in the specified unit.
     * 
     * @param value
     *            the measurement value.
     * @param unit
     *            the measurement unit.
     * @param scale
     *            the measurement scale.
     * @return the corresponding <code>numeric</code> quantity.
     * @throws NullPointerException
     *             if value, unit or scale were null
     * @since 2.0
     */
    public static <Q extends Quantity<Q>> ComparableQuantity<Q> getQuantity(Number value, Unit<Q> unit, Scale scale) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(unit);
        Objects.requireNonNull(scale);
        if (Double.class.isInstance(value)) {
            return new DoubleQuantity<>(Double.class.cast(value), unit, scale);
        } else if (Long.class.isInstance(value)) {
            return new LongQuantity<Q>(Long.class.cast(value), unit, scale);
        } else if (Short.class.isInstance(value)) {
            return new ShortQuantity<Q>(Short.class.cast(value), unit, scale);
        } else if (Byte.class.isInstance(value)) {
            return new ByteQuantity<Q>(Byte.class.cast(value), unit, scale);
//        } else if (Integer.class.isInstance(value)) { FIXME IntegerQuantity has issues
//            return new IntegerQuantity<Q>(Integer.class.cast(value), unit);
//        } else if (Float.class.isInstance(value)) { FIXME FloatQuantity has issues
//            return new FloatQuantity<Q>(Float.class.cast(value), unit);
        } else if (BigDecimal.class.isInstance(value)) {
            return new DecimalQuantity<>(BigDecimal.class.cast(value), unit, scale);
        } else if (BigInteger.class.isInstance(value)) {
            return new BigIntegerQuantity<>(BigInteger.class.cast(value), unit, scale);
        }
        return new NumberQuantity<>(value, unit, scale);
    }

    /**
     * Returns the scalar quantity. When the {@link Number} was {@link BigDecimal} or {@link BigInteger} will uses {@link DecimalQuantity}, when the
     * {@link Number} was {@link Double} will {@link DoubleQuantity} otherwise will {@link NumberQuantity}. in the specified unit.
     * 
     * @param value
     *            the measurement value.
     * @param unit
     *            the measurement unit.
     * @return the corresponding <code>numeric</code> quantity.
     * @throws NullPointerException
     *             when value or unit were null
     */
    public static <Q extends Quantity<Q>> ComparableQuantity<Q> getQuantity(Number value, Unit<Q> unit) {
        return getQuantity(value, unit, ABSOLUTE);
    }
    
    /**
     * Returns the mixed quantity. When the {@link Number} was {@link BigDecimal} or {@link BigInteger} will uses {@link DecimalQuantity}, when the
     * {@link Number} was {@link Double} will {@link DoubleQuantity} otherwise will {@link NumberQuantity}. in the specified unit.
     * 
     * @param value
     *            the measurement value.
     * @param unit
     *            the measurement unit.
     * @param scale
     *            the measurement scale.
     * @return the corresponding <code>numeric</code> quantity.
     * @throws NullPointerException
     *             if value or scale were null
     * @throws IllegalArgumentException
     *             if unit is a MixedUnit but the number of given values don't match its parts. 
     * @throws MeasurementException
     *             if unit is not a MixedUnit
     * @since 2.0
     */
    public static <Q extends Quantity<Q>> ComparableQuantity<Q> getMixedQuantity(Number[] values, Unit<Q> unit, Scale scale) {
        if (unit instanceof MixedUnit) {
            final MixedUnit<Q> compUnit =  (MixedUnit<Q>) unit;
            //ComparableQuantity<Q> result = null;
            if (values.length == compUnit.getUnits().size()) {
                return new MixedQuantity<Q>(values, unit, scale);
            } else {
                throw new IllegalArgumentException(String.format("%s values don't match %s in mixed unit", values.length, compUnit.getUnits().size()));
            }
        } else {
            throw new MeasurementException(String.format("%s not a mixed unit", unit));
        }
    }
    
    /**
     * Returns the mixed quantity. When the {@link Number} was {@link BigDecimal} or {@link BigInteger} will uses {@link DecimalQuantity}, when the
     * {@link Number} was {@link Double} will {@link DoubleQuantity} otherwise will {@link NumberQuantity}. in the specified unit.
     * 
     * @param value
     *            the measurement value.
     * @param unit
     *            the measurement unit.
     * @return the corresponding <code>numeric</code> quantity.
     * @throws NullPointerException
     *             if value or unit were null
     * @since 2.0
     */
    public static <Q extends Quantity<Q>> ComparableQuantity<Q> getMixedQuantity(Number[] values, Unit<Q> unit) {
        return getMixedQuantity(values, unit, ABSOLUTE);
    }
}
