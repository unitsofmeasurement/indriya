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
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.function.Calculus;

/**
 * An amount of quantity, implementation of {@link ComparableQuantity} that uses {@link Double} as implementation of {@link Number}, this object is
 * immutable. Note: all operations which involves {@link Number}, this implementation will convert to {@link Double}.
 *
 * @param <Q>
 *          The type of the quantity.
 * @param <Q>
 *          The type of the quantity.
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @author Otavio de Santana
 * @version 0.4, $Date: 2017-05-28 $
 * @see AbstractQuantity
 * @see Quantity
 * @see ComparableQuantity
 * @since 1.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
final class DoubleQuantity<Q extends Quantity<Q>> extends AbstractQuantity<Q> implements Serializable {

  private static final long serialVersionUID = 8660843078156312278L;

  private final double value;

  public DoubleQuantity(double value, Unit<Q> unit) {
    super(unit);
    this.value = value;
  }

  @Override
  public Double getValue() {
    return value;
  }

  @Override
  public double doubleValue(Unit<Q> unit) {
    final double result = getUnit().getConverterTo(unit).convert(value);
    if (Double.isInfinite(result)) {
      throw new ArithmeticException();
    } else {
      return result;
    }
  }

  @Override
  public BigDecimal decimalValue(Unit<Q> unit) throws ArithmeticException {
    final BigDecimal decimal = BigDecimal.valueOf(value);
    return Calculus.toBigDecimal(getUnit().getConverterTo(unit).convert(decimal));
  }

  @Override
  public long longValue(Unit<Q> unit) {
    final double result = getUnit().getConverterTo(unit).convert(value);
    if (result < Long.MIN_VALUE || result > Long.MAX_VALUE) {
      throw new ArithmeticException("Overflow (" + result + ")");
    }
    return (long) result;
  }

  private ComparableQuantity<Q> addRaw(Number a, Number b, Unit<Q> unit) {
    return NumberQuantity.of(a.doubleValue() + b.doubleValue(), unit);
  }

  @Override
  public ComparableQuantity<Q> add(Quantity<Q> that) {
    final Quantity<Q> thatConverted = that.to(getUnit());
    final Quantity<Q> thisConverted = this.to(that.getUnit());
    final double resultValueInThisUnit = getValue().doubleValue() + thatConverted.getValue().doubleValue();
    final double resultValueInThatUnit = thisConverted.getValue().doubleValue() + that.getValue().doubleValue();
    final ComparableQuantity<Q> resultInThisUnit = addRaw(getValue(), thatConverted.getValue(), getUnit());
    final ComparableQuantity<Q> resultInThatUnit = addRaw(thisConverted.getValue(), that.getValue(), that.getUnit());
    if (Double.isInfinite(resultValueInThisUnit) && Double.isInfinite(resultValueInThatUnit)) {
      throw new ArithmeticException();
    } else if (Double.isInfinite(resultValueInThisUnit)) {
      return resultInThatUnit;
    } else {
      return resultInThisUnit;
    }
  }

  @Override
  public ComparableQuantity<Q> subtract(Quantity<Q> that) {
    final Quantity<Q> thatNegated = NumberQuantity.of(-that.getValue().doubleValue(), that.getUnit());
    return add(thatNegated);
  }

  @Override
  public ComparableQuantity<?> multiply(Quantity<?> that) {
    final double product = value * that.getValue().doubleValue();
    if (Double.isInfinite(product)) {
      throw new ArithmeticException();
    } else {
      return new DoubleQuantity(product, getUnit().multiply(that.getUnit()));
    }
  }

  @Override
  public ComparableQuantity<Q> multiply(Number that) {
    final double product = value * that.doubleValue();
    if (Double.isInfinite(product)) {
      throw new ArithmeticException();
    } else {
      return new DoubleQuantity(product, getUnit());
    }
  }

  @Override
  public ComparableQuantity<?> divide(Quantity<?> that) {
    return new DoubleQuantity(value / that.getValue().doubleValue(), getUnit().divide(that.getUnit()));
  }

  @Override
  public ComparableQuantity<Q> divide(Number that) {
    return Quantities.getQuantity(value / that.doubleValue(), getUnit());
  }

  @Override
  public AbstractQuantity<Q> inverse() {
    return (AbstractQuantity<Q>) Quantities.getQuantity(1d / value, getUnit().inverse());
  }

  @Override
  public boolean isBig() {
    return false;
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
