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

import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;

/**
 * An amount of quantity, consisting of a long and a Unit. LongQuantity objects are immutable.
 * 
 * @see AbstractQuantity
 * @see Quantity
 * @author <a href="mailto:werner@uom.technology">Werner Keil</a>
 * @param <Q>
 *          The type of the quantity.
 * @version 0.4, $Date: 2018-07-21 $
 * @since 1.0.7
 */
final class LongQuantity<Q extends Quantity<Q>> extends JavaNumberQuantity<Q> {

  private static final BigDecimal LONG_MAX_VALUE_AS_BIG_DECIMAL = new BigDecimal(Long.MAX_VALUE);
  private static final BigDecimal LONG_MIN_VALUE_AS_BIG_DECIMAL = new BigDecimal(Long.MIN_VALUE);

  private static final long serialVersionUID = 3092808554937634365L;

  private final long value;

  public LongQuantity(long value, Unit<Q> unit) {
    super(unit);
    this.value = value;
  }

  @Override
  public Long getValue() {
    return value;
  }

  private boolean isOverflowing(BigDecimal value) {
    return value.compareTo(LONG_MAX_VALUE_AS_BIG_DECIMAL) > 0 || value.compareTo(LONG_MIN_VALUE_AS_BIG_DECIMAL) < 0;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private ComparableQuantity<Q> addRaw(Number a, Number b, Unit<Q> unit) {
    return new LongQuantity(a.longValue() + b.longValue(), unit);
  }

  public ComparableQuantity<Q> add(Quantity<Q> that) {
    if (canWidenTo(that)) {
      return widenTo((JavaNumberQuantity<Q>) that).add(that);
    }
    final Quantity<Q> thatConverted = that.to(getUnit());
    final Quantity<Q> thisConverted = this.to(that.getUnit());
    final BigDecimal resultValueInThisUnit = new BigDecimal(getValue()).add(new BigDecimal(thatConverted.getValue().doubleValue()));
    final BigDecimal resultValueInThatUnit = new BigDecimal(thisConverted.getValue().doubleValue())
        .add(new BigDecimal(that.getValue().doubleValue()));
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

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public ComparableQuantity<Q> subtract(Quantity<Q> that) {
    if (canWidenTo(that)) {
      return widenTo((JavaNumberQuantity<Q>) that).subtract(that);
    }
    final Quantity<Q> thatNegated = new LongQuantity(-that.getValue().longValue(), that.getUnit());
    return add(thatNegated);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public ComparableQuantity<?> multiply(Quantity<?> that) {
    if (canWidenTo(that)) {
      return widenTo((JavaNumberQuantity<Q>) that).multiply(that);
    }
    final BigDecimal product = new BigDecimal(getValue()).multiply(new BigDecimal(that.getValue().longValue()));
    if (isOverflowing(product)) {
      throw new ArithmeticException();
    } else {
      return new LongQuantity(product.longValue(), getUnit().multiply(that.getUnit()));
    }
  }

  public ComparableQuantity<Q> multiply(Number that) {
    final BigDecimal product = new BigDecimal(getValue()).multiply(new BigDecimal(that.longValue()));
    if (isOverflowing(product)) {
      throw new ArithmeticException();
    } else {
      return NumberQuantity.of(product.longValue(), getUnit());
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public ComparableQuantity<?> divide(Quantity<?> that) {
    if (canWidenTo(that)) {
      return widenTo((JavaNumberQuantity<Q>) that).divide(that);
    }
    return new LongQuantity((long) (value / that.getValue().doubleValue()), getUnit().divide(that.getUnit()));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public ComparableQuantity<Q> inverse() {
    return (AbstractQuantity<Q>) new LongQuantity(1 / value, getUnit().inverse());
  }

  public ComparableQuantity<Q> divide(Number that) {
    return NumberQuantity.of(value / that.doubleValue(), getUnit());
  }

  @Override
  public boolean isBig() {
    return false;
  }

  @Override
  public BigDecimal decimalValue(Unit<Q> unit) {
    return BigDecimal.valueOf(doubleValue(unit));
  }

  @Override
  public boolean isDecimal() {
    return false;
  }

  @Override
  public int getSize() {
    return Long.SIZE;
  }

  @Override
  public Class<?> getNumberType() {
    return long.class;
  }

  @Override
  public Quantity<Q> negate() {
    return new LongQuantity<Q>(-value, getUnit());
  }
}
