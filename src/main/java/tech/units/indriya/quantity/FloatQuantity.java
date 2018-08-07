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
import tech.units.indriya.function.Calculus;

/**
 * An amount of quantity, consisting of a float and a Unit. FloatQuantity objects are immutable.
 * 
 * @see AbstractQuantity
 * @see Quantity
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @author Otavio de Santana
 * @param <Q>
 *          The type of the quantity.
 * @version 0.6, $Date: 2018-07-20 $
 * @since 1.0
 */
final class FloatQuantity<Q extends Quantity<Q>> extends JavaNumberQuantity<Q> {

  private static final long serialVersionUID = 5992028803791009345L;

  private static final BigDecimal FLOAT_MIN_VALUE = new BigDecimal(Float.MIN_VALUE);
  private static final BigDecimal FLOAT_MAX_VALUE = new BigDecimal(Float.MAX_VALUE);

  final float value;

  public FloatQuantity(float value, Unit<Q> unit) {
    super(unit);
    this.value = value;
  }

  @Override
  public Float getValue() {
    return value;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private ComparableQuantity<Q> addRaw(Number a, Number b, Unit<Q> unit) {
    return new FloatQuantity(a.floatValue() + b.floatValue(), unit);
  }

  public ComparableQuantity<Q> add(Quantity<Q> that) {
    if (canWidenTo(that)) {
      return widenTo((JavaNumberQuantity<Q>) that).add(that);
    }
    final Quantity<Q> thatConverted = that.to(getUnit());
    final Quantity<Q> thisConverted = this.to(that.getUnit());
    final float resultValueInThisUnit = getValue().floatValue() + thatConverted.getValue().floatValue();
    final float resultValueInThatUnit = thisConverted.getValue().floatValue() + that.getValue().floatValue();
    final ComparableQuantity<Q> resultInThisUnit = addRaw(getValue(), thatConverted.getValue(), getUnit());
    final ComparableQuantity<Q> resultInThatUnit = addRaw(thisConverted.getValue(), that.getValue(), that.getUnit());
    if (Float.isInfinite(resultValueInThisUnit) && Float.isInfinite(resultValueInThatUnit)) {
      throw new ArithmeticException();
    } else if (Float.isInfinite(resultValueInThisUnit)) {
      return resultInThatUnit;
    } else {
      return resultInThisUnit;
    }
  }

  @Override
  public ComparableQuantity<Q> multiply(Number that) {
    final float product = value * that.floatValue();
    if (Float.isInfinite(product)) {
      throw new ArithmeticException();
    } else {
      return new FloatQuantity<Q>(product, getUnit());
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public ComparableQuantity<Q> inverse() {
    return (AbstractQuantity<Q>) new FloatQuantity(1f / value, getUnit().inverse());
  }

  @Override
  public ComparableQuantity<Q> divide(Number that) {
    final float quotient = value / that.floatValue();
    if (Float.isInfinite(quotient)) {
      throw new ArithmeticException();
    } else {
      return new FloatQuantity<Q>(quotient, getUnit());
    }
  }

  @Override
  public boolean isBig() {
    return false;
  }

  @Override
  public BigDecimal decimalValue(Unit<Q> unit) throws ArithmeticException {
    final BigDecimal decimal = BigDecimal.valueOf(value);
    return Calculus.toBigDecimal(getUnit().getConverterTo(unit).convert(decimal));
  }

  @Override
  public boolean isDecimal() {
    return true;
  }

  @Override
  public int getSize() {
    return Float.SIZE;
  }

  @Override
  public Class<?> getNumberType() {
    return float.class;
  }
  
  @Override
  Number castFromBigDecimal(BigDecimal value) {
    return (float) value.doubleValue();
  }

  @Override
  boolean isOverflowing(BigDecimal value) {
    return value.compareTo(FLOAT_MIN_VALUE) < 0 || value.compareTo(FLOAT_MAX_VALUE) > 0;
  }

  @Override
  public Quantity<Q> negate() {
    return new FloatQuantity<Q>(-value, getUnit());
  }
}
