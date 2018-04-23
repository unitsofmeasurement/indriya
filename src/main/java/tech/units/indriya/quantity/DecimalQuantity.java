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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.Calculus;
import tech.units.indriya.ComparableQuantity;

/**
 * An amount of quantity, implementation of {@link ComparableQuantity} that uses {@link BigDecimal} as implementation of {@link Number}, this object
 * is immutable. Note: all operations which involves {@link Number}, this implementation will convert to {@link BigDecimal}, and all operation of
 * BigDecimal will use {@link MathContext#DECIMAL128}.
 *
 * @param <Q>
 *          The type of the quantity.
 * @author otaviojava
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @see AbstractQuantity
 * @see Quantity
 * @see ComparableQuantity
 * @version 1.0.2
 * @since 1.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
final class DecimalQuantity<Q extends Quantity<Q>> extends AbstractQuantity<Q> implements Serializable {

  private static final long serialVersionUID = 6504081836032983882L;

  private final BigDecimal value;

  public DecimalQuantity(BigDecimal value, Unit<Q> unit) {
    super(unit);
    this.value = value;
  }

  public DecimalQuantity(double value, Unit<Q> unit) {
    super(unit);
    this.value = BigDecimal.valueOf(value);
  }

  @Override
  public BigDecimal getValue() {
    return value;
  }

  @Override
  public double doubleValue(Unit<Q> unit) {
    return (getUnit().equals(unit)) 
    		? value.doubleValue() 
    		: getUnit().getConverterTo(unit).convert(value.doubleValue());
  }

  @Override
  public BigDecimal decimalValue(Unit<Q> unit) throws ArithmeticException {
    return Calculus.toBigDecimal(super.getUnit().getConverterTo(unit).convert(value));
  }

  @Override
  public ComparableQuantity<Q> add(Quantity<Q> that) {
    if (getUnit().equals(that.getUnit())) {
      return Quantities.getQuantity(value.add(Calculus.toBigDecimal(that.getValue()), Calculus.DEFAULT_MATH_CONTEXT), getUnit());
    }
    Quantity<Q> converted = that.to(getUnit());
    return Quantities.getQuantity(value.add(Calculus.toBigDecimal(converted.getValue())), getUnit());
  }

  @Override
  public ComparableQuantity<Q> subtract(Quantity<Q> that) {
    if (getUnit().equals(that.getUnit())) {
      return Quantities.getQuantity(value.subtract(Calculus.toBigDecimal(that.getValue()), Calculus.DEFAULT_MATH_CONTEXT), getUnit());
    }
    Quantity<Q> converted = that.to(getUnit());
    return Quantities.getQuantity(value.subtract(Calculus.toBigDecimal(converted.getValue()), Calculus.DEFAULT_MATH_CONTEXT), getUnit());
  }

  @Override
  public ComparableQuantity<?> multiply(Quantity<?> that) {
    return new DecimalQuantity(value.multiply(Calculus.toBigDecimal(that.getValue()), Calculus.DEFAULT_MATH_CONTEXT), getUnit().multiply(that.getUnit()));
  }

  @Override
  public ComparableQuantity<Q> multiply(Number that) {
    return Quantities.getQuantity(value.multiply(Calculus.toBigDecimal(that), Calculus.DEFAULT_MATH_CONTEXT), getUnit());
  }

  @Override
  public ComparableQuantity<Q> divide(Number that) {
    return Quantities.getQuantity(value.divide(Calculus.toBigDecimal(that), Calculus.DEFAULT_MATH_CONTEXT), getUnit());
  }

  @Override
  public ComparableQuantity<Q> inverse() {
    return (ComparableQuantity<Q>) Quantities.getQuantity(BigDecimal.ONE.divide(value), getUnit().inverse());
  }

  @Override
  protected long longValue(Unit<Q> unit) {
    double result = doubleValue(unit);
    if ((result < Long.MIN_VALUE) || (result > Long.MAX_VALUE)) {
      throw new ArithmeticException("Overflow (" + result + ")");
    }
    return (long) result;
  }

  @Override
  public boolean isBig() {
    return true;
  }

  @Override
  public ComparableQuantity<?> divide(Quantity<?> that) {
    return new DecimalQuantity(value.divide(Calculus.toBigDecimal(that.getValue()), Calculus.DEFAULT_MATH_CONTEXT), getUnit().divide(that.getUnit()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see AbstractQuantity#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (obj instanceof Quantity<?>) {
      Quantity<?> that = (Quantity<?>) obj;
      return Objects.equals(getUnit(), that.getUnit()) && Equalizer.hasEquality(value, that.getValue());
    }
    return false;
  }
}
