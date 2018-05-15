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
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;

/**
 * An amount of quantity, consisting of a short and a Unit. ByteQuantity objects are immutable.
 * 
 * @see AbstractQuantity
 * @see Quantity
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @param <Q>
 *          The type of the quantity.
 * @version 0.1, $Date: 2017-05-28 $
 * @since 1.0.7
 */
final class ByteQuantity<Q extends Quantity<Q>> extends AbstractQuantity<Q> {

  /**
     * 
     */
  private static final long serialVersionUID = 6325849816534488248L;

  private final byte value;

  ByteQuantity(byte value, Unit<Q> unit) {
    super(unit);
    this.value = value;
  }

  @Override
  public Byte getValue() {
    return value;
  }

  public double doubleValue(Unit<Q> unit) {
    return getUnit().equals(unit) ? value : super.getUnit().getConverterTo(unit).convert(value);
  }

  @Override
  public long longValue(Unit<Q> unit) {
    double result = doubleValue(unit);
    if (result < Long.MIN_VALUE || result > Long.MAX_VALUE) {
      throw new ArithmeticException("Overflow (" + result + ")");
    }
    return (long) result;
  }

  @Override
  public boolean isBig() {
    return false;
  }

  @Override
  public BigDecimal decimalValue(Unit<Q> unit) {
    return BigDecimal.valueOf(doubleValue(unit));
  }

  private boolean isOverflowing(double value) {
    return value < Byte.MIN_VALUE || value > Byte.MAX_VALUE;
  }

  private ComparableQuantity<Q> addRaw(Number a, Number b, Unit<Q> unit) {
    return NumberQuantity.of(a.byteValue() + b.byteValue(), unit);
  }

  @Override
  public ComparableQuantity<Q> add(Quantity<Q> that) {
    final Quantity<Q> thatConverted = that.to(getUnit());
    final Quantity<Q> thisConverted = this.to(that.getUnit());
    final double resultValueInThisUnit = getValue().doubleValue() + thatConverted.getValue().doubleValue();
    final double resultValueInThatUnit = thisConverted.getValue().doubleValue() + that.getValue().doubleValue();
    final ComparableQuantity<Q> resultInThisUnit = addRaw(getValue(), thatConverted.getValue(), getUnit());
    final ComparableQuantity<Q> resultInThatUnit = addRaw(thisConverted.getValue(), that.getValue(), that.getUnit());
    if (isOverflowing(resultValueInThisUnit)) {
      if (isOverflowing(resultValueInThatUnit)) {
        throw new ArithmeticException();
      } else {
        return resultInThatUnit;
      }
    } else if (isOverflowing(resultValueInThatUnit)) {
      return resultInThisUnit;
    } else if (hasFraction(resultValueInThisUnit)) {
      return resultInThatUnit;
    } else {
      return resultInThisUnit;
    }
  }

  @Override
  public ComparableQuantity<Q> subtract(Quantity<Q> that) {
    final Quantity<Q> thatNegated = NumberQuantity.of(-that.getValue().shortValue(), that.getUnit());
    return add(thatNegated);
  }

  @Override
  public ComparableQuantity<?> divide(Quantity<?> that) {
    return NumberQuantity.of((short) value / that.getValue().byteValue(), getUnit().divide(that.getUnit()));
  }

  @Override
  public ComparableQuantity<Q> divide(Number that) {
    return NumberQuantity.of(value / that.byteValue(), getUnit());
  }

  @Override
  public ComparableQuantity<?> multiply(Quantity<?> multiplier) {
    final double product = getValue().doubleValue() * multiplier.getValue().doubleValue();
    if (isOverflowing(product)) {
      throw new ArithmeticException();
    } else {
      return NumberQuantity.of(value * multiplier.getValue().byteValue(), getUnit().multiply(multiplier.getUnit()));
    }
  }

  @Override
  public ComparableQuantity<Q> multiply(Number multiplier) {
    final double product = getValue().doubleValue() * multiplier.doubleValue();
    if (isOverflowing(product)) {
      throw new ArithmeticException();
    } else {
      return NumberQuantity.of(value * multiplier.byteValue(), getUnit());
    }
  }

  @Override
  public ComparableQuantity<?> inverse() {
    return NumberQuantity.of(1 / value, getUnit().inverse());
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