/*
 * Next Generation Units of Measurement Implementation
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
 * 3. Neither the name of JSR-363, Indriya nor the names of their contributors may be used to endorse or promote products
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
package tec.units.indriya.quantity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import tec.units.indriya.AbstractQuantity;
import tec.units.indriya.ComparableQuantity;

/**
 * An amount of quantity, implementation of {@link ComparableQuantity} that keep {@link Number} as possible otherwise converts to
 * {@link DecimalQuantity}, this object is immutable.
 *
 * @see AbstractQuantity
 * @see Quantity
 * @see ComparableQuantity
 * @param <Q>
 *          The type of the quantity.
 * @author otaviojava
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.0.1, $Date: 2017-05-28 $
 * @since 1.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class NumberQuantity<Q extends Quantity<Q>> extends AbstractQuantity<Q> implements Serializable {

  private static final long serialVersionUID = 7312161895652321241L;

  private final Number value;

  /**
   * Indicates if this quantity is big.
   */
  private final boolean isBig;

  protected NumberQuantity(Number number, Unit<Q> unit) {
    super(unit);
    value = number;
    isBig = number instanceof BigDecimal || number instanceof BigInteger;
  }

  @Override
  public double doubleValue(Unit<Q> unit) {
    Unit<Q> myUnit = getUnit();
    try {
      UnitConverter converter = myUnit.getConverterTo(unit);
      return converter.convert(getValue().doubleValue());
    } catch (UnconvertibleException e) {
      throw e;
    }
  }

  @Override
  public Number getValue() {
    return value;
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

  @Override
  public ComparableQuantity<Q> inverse() {

    return new NumberQuantity((getValue() instanceof BigDecimal ? BigDecimal.ONE.divide((BigDecimal) getValue()) : 1d / getValue().doubleValue()),
        getUnit().inverse());
  }

  @Override
  public BigDecimal decimalValue(Unit<Q> unit, MathContext ctx) throws ArithmeticException {
    if (value instanceof BigDecimal) {
      return (BigDecimal) value;
    }
    if (value instanceof BigInteger) {
      return new BigDecimal((BigInteger) value);
    }
    return BigDecimal.valueOf(value.doubleValue());
  }

  @Override
  public ComparableQuantity<Q> subtract(Quantity<Q> that) {
    return toDecimalQuantity().subtract(that);
  }

  private DecimalQuantity<Q> toDecimalQuantity() {
    return new DecimalQuantity<>(BigDecimal.valueOf(value.doubleValue()), getUnit());
  }

  /**
   * Returns the scalar quantity for the specified <code>long</code> stated in the specified unit.
   *
   * @param longValue
   *          the quantity value.
   * @param unit
   *          the measurement unit.
   * @return the corresponding <code>int</code> quantity.
   */
  public static <Q extends Quantity<Q>> AbstractQuantity<Q> of(long longValue, Unit<Q> unit) {
    return new LongQuantity<Q>(longValue, unit);
  }

  /**
   * Returns the scalar quantity for the specified <code>int</code> stated in the specified unit.
   *
   * @param intValue
   *          the quantity value.
   * @param unit
   *          the measurement unit.
   * @return the corresponding <code>int</code> quantity.
   */
  public static <Q extends Quantity<Q>> AbstractQuantity<Q> of(int intValue, Unit<Q> unit) {
    return new IntegerQuantity<Q>(intValue, unit);
  }

  /**
   * Returns the scalar quantity for the specified <code>short</code> stated in the specified unit.
   *
   * @param value
   *          the quantity value.
   * @param unit
   *          the measurement unit.
   * @return the corresponding <code>short</code> quantity.
   */
  public static <Q extends Quantity<Q>> AbstractQuantity<Q> of(short value, Unit<Q> unit) {
    return new ShortQuantity<Q>(value, unit);
  }

  /**
   * Returns the scalar quantity for the specified <code>byte</code> stated in the specified unit.
   *
   * @param value
   *          the quantity value.
   * @param unit
   *          the measurement unit.
   * @return the corresponding <code>byte</code> quantity.
   */
  public static <Q extends Quantity<Q>> AbstractQuantity<Q> of(byte value, Unit<Q> unit) {
    return new ByteQuantity<Q>(value, unit);
  }

  /**
   * Returns the scalar quantity for the specified <code>float</code> stated in the specified unit.
   *
   * @param floatValue
   *          the measurement value.
   * @param unit
   *          the measurement unit.
   * @return the corresponding <code>float</code> quantity.
   */
  public static <Q extends Quantity<Q>> AbstractQuantity<Q> of(float floatValue, Unit<Q> unit) {
    return new FloatQuantity<Q>(floatValue, unit);
  }

  /**
   * Returns the scalar quantity for the specified <code>double</code> stated in the specified unit.
   *
   * @param doubleValue
   *          the measurement value.
   * @param unit
   *          the measurement unit.
   * @return the corresponding <code>double</code> quantity.
   */
  public static <Q extends Quantity<Q>> AbstractQuantity<Q> of(double doubleValue, Unit<Q> unit) {
    return new DoubleQuantity<Q>(doubleValue, unit);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Quantity<?>) {
      Quantity<?> that = (Quantity<?>) obj;
      return Objects.equals(getUnit(), that.getUnit()) && Equalizer.hasEquality(value, that.getValue());
    }
    return false;
  }

}
