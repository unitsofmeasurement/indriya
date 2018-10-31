/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2018, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParsePosition;
import java.util.Objects;

import javax.measure.LevelOfMeasurement;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.MeasurementParseException;

import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.format.AbstractQuantityFormat;
import tech.units.indriya.format.SimpleQuantityFormat;

/**
 * Singleton class for accessing {@link Quantity} instances.
 * 
 * @version 1.2
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
     * @param level
     *            the measurement level.
     * @return the corresponding <code>numeric</code> quantity.
     * @throws NullPointerException
     *             if value, unit or level were null
     * @since 2.0
     */
    public static <Q extends Quantity<Q>> ComparableQuantity<Q> getQuantity(Number value, Unit<Q> unit, LevelOfMeasurement level) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(unit);
        Objects.requireNonNull(level);
        if (Double.class.isInstance(value)) {
            return new DoubleQuantity<>(Double.class.cast(value), unit, level);
        } else if (Long.class.isInstance(value)) {
            return new LongQuantity<Q>(Long.class.cast(value), unit, level);
        } else if (Short.class.isInstance(value)) {
            return new ShortQuantity<Q>(Short.class.cast(value), unit, level);
        } else if (Byte.class.isInstance(value)) {
            return new ByteQuantity<Q>(Byte.class.cast(value), unit, level);
//        } else if (Integer.class.isInstance(value)) { FIXME IntegerQuantity has issues
//            return new IntegerQuantity<Q>(Integer.class.cast(value), unit);
//        } else if (Float.class.isInstance(value)) { FIXME FloatQuantity has issues
//            return new FloatQuantity<Q>(Float.class.cast(value), unit);
        } else if (BigDecimal.class.isInstance(value)) {
            return new DecimalQuantity<>(BigDecimal.class.cast(value), unit, level);
        } else if (BigInteger.class.isInstance(value)) {
            return new BigIntegerQuantity<>(BigInteger.class.cast(value), unit, level);
        }
        return new NumberQuantity<>(value, unit, level);
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
        return getQuantity(value, unit, LevelOfMeasurement.RATIO); // TODO we use RATIO for now, should be replaced by some Unit to Level mapping for known cases (e.g. Fahrenheit or Celsius)
    }
}
