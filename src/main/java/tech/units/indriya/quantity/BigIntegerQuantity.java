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
import java.math.BigInteger;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import tech.units.indriya.AbstractConverter;
import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.function.Calculus;

/**
 * An amount of quantity, implementation of {@link ComparableQuantity} that uses {@link BigInteger} as implementation of {@link Number}, this object
 * is immutable. Note: all operations which involves {@link Number}, this implementation will convert to {@link BigInteger}.
 *
 * @param <Q>
 *          The type of the quantity.
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @see AbstractQuantity
 * @see Quantity
 * @see ComparableQuantity
 * @version 0.4
 * @since 2.0
 */
final class BigIntegerQuantity<Q extends Quantity<Q>> extends AbstractQuantity<Q> implements Serializable, JavaNumberQuantity<Q> {

  private static final long serialVersionUID = -593014349777834846L;
  private final BigInteger value;

  public BigIntegerQuantity(BigInteger value, Unit<Q> unit) {
    super(unit);
    this.value = value;
  }

  public BigIntegerQuantity(long value, Unit<Q> unit) {
    super(unit);
    this.value = BigInteger.valueOf(value);
  }

  /**
   * <p>
   * Returns a {@code BigIntegerQuantity} with same Unit, but whose value is {@code(-this.getValue())}.
   * </p>
   * 
   * @return {@code -this}.
   */
  public BigIntegerQuantity<Q> negate() {
    return new BigIntegerQuantity<Q>(value.negate(), getUnit());
  }

  @Override
  public BigInteger getValue() {
    return value;
  }

  @Override
  public double doubleValue(Unit<Q> unit) {
    if (getUnit().equals(unit)) {
      return value.doubleValue();
    } else {
      return getUnit().getConverterTo(unit).convert(value.doubleValue());
    }
  }

  @Override
  public BigDecimal decimalValue(Unit<Q> unit) throws ArithmeticException {
    if (getUnit().equals(unit)) {
      return new BigDecimal(value);
    } else {
      final Number converted = ((AbstractConverter) getUnit().getConverterTo(unit)).convert(value);
      if (converted instanceof BigDecimal) {
        return (BigDecimal) converted;
      } else if (converted instanceof BigInteger) {
        return new BigDecimal((BigInteger) converted);
      } else {
        return BigDecimal.valueOf(converted.doubleValue());
      }
    }
  }

  @Override
  public ComparableQuantity<Q> add(Quantity<Q> that) {
    if (NumberQuantity.canWiden(this, that)) {
      return NumberQuantity.widen(this, (JavaNumberQuantity<Q>) that).add(that);
    }
    if (getUnit().equals(that.getUnit())) {
      return new BigIntegerQuantity<Q>(value.add(Calculus.toBigInteger(that.getValue())), getUnit());
    }
    // we need to decide which of the 2 units to pick for the result
    // 1) this.getUnit()
    // 2) that.getUnit()
    // we pick the one, that yields higher precision

    final UnitConverter thisUnitToThatUnitConverter = this.getUnit().getConverterTo(that.getUnit());

    boolean thisUnitLargerThanThatUnit = Math.abs(thisUnitToThatUnitConverter.convert(1.0)) > 1.0;

    final Unit<Q> pickedUnit;
    final BigInteger sumValue;

    if (thisUnitLargerThanThatUnit) {
      pickedUnit = that.getUnit();
      BigInteger thisValueConverted = Calculus.toBigInteger(thisUnitToThatUnitConverter.convert(getValue()));
      sumValue = Calculus.toBigInteger(that.getValue()).add(thisValueConverted);
    } else {
      pickedUnit = this.getUnit();
      UnitConverter thatUnitToThisUnitConverter = that.getUnit().getConverterTo(this.getUnit());
      BigInteger thatValueConverted = Calculus.toBigInteger(thatUnitToThisUnitConverter.convert(that.getValue()));
      sumValue = value.add(thatValueConverted);
    }

    return Quantities.getQuantity(sumValue, pickedUnit);
  }

  @Override
  public ComparableQuantity<Q> subtract(Quantity<Q> that) {
    if (NumberQuantity.canWiden(this, that)) {
      return NumberQuantity.widen(this, (JavaNumberQuantity<Q>) that).subtract(that);
    }
    if (that instanceof BigIntegerQuantity) {
      return add(((BigIntegerQuantity<Q>) that).negate());
    }
    return add(Quantities.getQuantity(Calculus.negate(that.getValue()), that.getUnit()));
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public ComparableQuantity<?> multiply(Quantity<?> that) {
    if (NumberQuantity.canWiden(this, that)) {
      return NumberQuantity.widen(this, (JavaNumberQuantity<Q>) that).multiply(that);
    }
    return new BigIntegerQuantity(value.multiply(Calculus.toBigDecimal(that.getValue()).toBigInteger()), getUnit().multiply(that.getUnit()));
  }

  @Override
  public ComparableQuantity<Q> multiply(Number that) {
    return Quantities.getQuantity(value.multiply(Calculus.toBigInteger(that)), getUnit());
  }

  @Override
  public ComparableQuantity<Q> divide(Number that) {
    return Quantities.getQuantity(value.divide(Calculus.toBigDecimal(that).toBigInteger()), getUnit());
  }

  @SuppressWarnings({ "unchecked" })
  @Override
  public ComparableQuantity<Q> inverse() {
    return (ComparableQuantity<Q>) Quantities.getQuantity(BigInteger.ONE.divide(value), getUnit().inverse());
  }

  @Override
  protected long longValue(Unit<Q> unit) {
    double result = doubleValue(unit);
    if (result < Long.MIN_VALUE || result > Long.MAX_VALUE) {
      throw new ArithmeticException("Overflow (" + result + ")");
    }
    return (long) result;
  }

  @Override
  public boolean isBig() {
    return true;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public ComparableQuantity<?> divide(Quantity<?> that) {
    if (NumberQuantity.canWiden(this, that)) {
      return NumberQuantity.widen(this, (JavaNumberQuantity<Q>) that).divide(that);
    }
    return new BigIntegerQuantity(value.divide(Calculus.toBigInteger(that.getValue())), getUnit().divide(that.getUnit()));
  }

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

  @Override
  public boolean isDecimal() {
    return false;
  }

  @Override
  public int getSize() {
    return 0;
  }

  @Override
  public Class<?> getNumberType() {
    return BigInteger.class;
  }
}
