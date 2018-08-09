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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Objects;
import java.util.function.BiFunction;

import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.function.Calculus;

/**
 * Abstract class extending {@link AbstractQuantity}, the superclass to all quantity classes that hold a Java {@link Number} to store the value.
 * 
 * @param <Q>
 *          The type of the quantity.
 */
abstract class JavaNumberQuantity<Q extends Quantity<Q>> extends AbstractQuantity<Q> {

  private static final long serialVersionUID = -772486200565856789L;

  private static final BigDecimal LONG_MAX_VALUE = new BigDecimal(Long.MAX_VALUE);
  private static final BigDecimal LONG_MIN_VALUE = new BigDecimal(Long.MIN_VALUE);

  /**
   * Constructor calling the superclass's constructor.
   * 
   * @param unit
   *          The unit.
   */
  protected JavaNumberQuantity(Unit<Q> unit) {
    super(unit);
  }

  /**
   * Indicates whether the quantity holds a decimal value {@link Number} type.
   * 
   * @return True if the value type is decimal.
   */
  abstract boolean isDecimal();

  /**
   * Returns the size of the {@link Number} type of the quantity, or zero if the {@link Number} type doesn't have a defined size.
   * 
   * @return The size of the {@link Number} type of the quantity, or zero if a size is not applicable to the {@link Number} type.
   */
  abstract int getSize();

  /**
   * Returns the {@link Number} type of the quantity.
   * 
   * @return The {@link Number} type.
   */
  abstract Class<?> getNumberType();

  /**
   * Casts a value from BigDecimal to the quantity's number type.
   * 
   * @param value
   *          The value to be case.
   * @return The value in the quantity's number type.
   */
  abstract Number castFromBigDecimal(BigDecimal value);

  /**
   * Returns whether a value is outside the valid range for the number type.
   * 
   * @param value
   *          The value to be checked.
   * @return True if the value is outside the range of the number type; false if the number type is big or the value is inside the range of the number
   *         type.
   */
  abstract boolean isOverflowing(BigDecimal value);

  @Override
  public double doubleValue(Unit<Q> unit) {
    final double result = getUnit().getConverterTo(unit).convert(getValue()).doubleValue();
    if (Double.isInfinite(result)) {
      throw new ArithmeticException();
    } else {
      return result;
    }
  }

  @Override
  protected long longValue(Unit<Q> unit) {
    final BigDecimal result = (BigDecimal) getUnit().getConverterTo(unit).convert(numberAsBigDecimal(getValue()));
    if (result.compareTo(LONG_MIN_VALUE) < 0 || result.compareTo(LONG_MAX_VALUE) > 0) {
      throw new ArithmeticException("Overflow (" + result + ")");
    } else {
      return result.longValue();
    }
  }

  @Override
  public BigDecimal decimalValue(Unit<Q> unit) {
    if (getUnit().equals(unit)) {
      return numberAsBigDecimal(getValue());
    } else {
      return (BigDecimal) getUnit().getConverterTo(unit).convert(numberAsBigDecimal(getValue()));
    }
  }

  @Override
  public ComparableQuantity<Q> add(Quantity<Q> that) {
    if (canWidenTo(that)) {
      return widenTo((JavaNumberQuantity<Q>) that).add(that);
    }
    final BigDecimal thisValueInThisUnit = decimalValue(getUnit());
    final BigDecimal thatValueInThisUnit = convertedQuantityValueAsBigDecimal(that, this.getUnit());
    final BigDecimal thisValueInThatUnit = decimalValue(that.getUnit());
    final BigDecimal thatValueInThatUnit = quantityValueAsBigDecimal(that);
    final BigDecimal resultValueInThisUnit = thisValueInThisUnit.add(thatValueInThisUnit, Calculus.MATH_CONTEXT);
    final BigDecimal resultValueInThatUnit = thisValueInThatUnit.add(thatValueInThatUnit, Calculus.MATH_CONTEXT);
    final ComparableQuantity<Q> resultInThisUnit = createTypedQuantity(getNumberType(), castFromBigDecimal(resultValueInThisUnit), getUnit());
    final ComparableQuantity<Q> resultInThatUnit = createTypedQuantity(getNumberType(), castFromBigDecimal(resultValueInThatUnit), that.getUnit());
    if (isOverflowing(resultValueInThisUnit)) {
      if (isOverflowing(resultValueInThatUnit)) {
        throw new ArithmeticException();
      } else {
        return resultInThatUnit;
      }
    } else if (isOverflowing(resultValueInThatUnit)) {
      return resultInThisUnit;
    } else if (!isDecimal() && hasFraction(resultValueInThisUnit)) {
      return resultInThatUnit;
    } else {
      return resultInThisUnit;
    }
  }

  @Override
  public ComparableQuantity<Q> subtract(Quantity<Q> that) {
    return add(that.negate());
  }

  @FunctionalInterface
  private interface TriFunction<R, A, B, C> {
    R apply(A a, B b, C c);
  }

  @SuppressWarnings({ "unchecked" })
  private ComparableQuantity<?> applyMultiplicativeQuantityOperation(Quantity<?> that,
      BiFunction<ComparableQuantity<Q>, Quantity<?>, ComparableQuantity<?>> thisOperator,
      TriFunction<BigDecimal, BigDecimal, BigDecimal, MathContext> valueOperator, BiFunction<Unit<?>, Unit<?>, Unit<?>> unitOperator) {
    if (canWidenTo(that)) {
      return thisOperator.apply(widenTo((JavaNumberQuantity<Q>) that), that);
    }
    final BigDecimal thisValue = decimalValue(getUnit());
    final BigDecimal thatValue = quantityValueAsBigDecimal(that);
    final BigDecimal result = valueOperator.apply(thisValue, thatValue, Calculus.MATH_CONTEXT);
    if (isOverflowing(result)) {
      throw new ArithmeticException();
    }
    final Unit<?> resultUnit = unitOperator.apply(getUnit(), that.getUnit());
    return createQuantity(getNumberType(), castFromBigDecimal(result), resultUnit);
  }

  @Override
  public ComparableQuantity<?> multiply(Quantity<?> that) {
    return applyMultiplicativeQuantityOperation(that, ComparableQuantity<Q>::multiply, BigDecimal::multiply, Unit::multiply);
  }

  @Override
  public ComparableQuantity<?> divide(Quantity<?> that) {
    return applyMultiplicativeQuantityOperation(that, ComparableQuantity<Q>::divide, BigDecimal::divide, Unit::divide);
  }

  private ComparableQuantity<Q> applyMultiplicativeNumberOperation(Number that,
      TriFunction<BigDecimal, BigDecimal, BigDecimal, MathContext> valueOperator) {
    final BigDecimal thisValue = decimalValue(getUnit());
    final BigDecimal thatValue = numberAsBigDecimal(that);
    final BigDecimal result = valueOperator.apply(thisValue, thatValue, Calculus.MATH_CONTEXT);
    if (isOverflowing(result)) {
      throw new ArithmeticException();
    }
    return createTypedQuantity(getNumberType(), castFromBigDecimal(result), getUnit());
  }

  @Override
  public ComparableQuantity<Q> multiply(Number that) {
    return applyMultiplicativeNumberOperation(that, BigDecimal::multiply);
  }

  @Override
  public ComparableQuantity<Q> divide(Number that) {
    return applyMultiplicativeNumberOperation(that, BigDecimal::divide);
  }

  private <R extends Quantity<R>> BigDecimal quantityValueAsBigDecimal(Quantity<R> that) {
    return convertedQuantityValueAsBigDecimal(that, that.getUnit());
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private <R extends Quantity<R>> BigDecimal convertedQuantityValueAsBigDecimal(Quantity<R> that, Unit<R> unit) {
    if (that instanceof JavaNumberQuantity) {
      return ((JavaNumberQuantity) that).decimalValue(unit);
    } else {
      return (BigDecimal) that.getUnit().getConverterTo(unit).convert(numberAsBigDecimal(that.getValue()));
    }
  }

  private BigDecimal numberAsBigDecimal(Number that) {
    if (that instanceof BigDecimal) {
      return (BigDecimal) that;
    } else if (that instanceof BigInteger) {
      return new BigDecimal((BigInteger) that);
    } else if (that instanceof Double || that instanceof Float) {
      return new BigDecimal(that.doubleValue());
    } else {
      return new BigDecimal(that.longValue());
    }
  }

  boolean canWidenTo(Quantity<?> target) {
    if (target instanceof JavaNumberQuantity) {
      JavaNumberQuantity<?> jnTarget = (JavaNumberQuantity<?>) target;
      if (jnTarget.isDecimal() && !isDecimal()) {
        return true;
      }
      if (isDecimal() && !jnTarget.isDecimal()) {
        return false;
      }
      if (jnTarget.isBig() && !isBig()) {
        return true;
      }
      return getSize() != 0 && jnTarget.getSize() > getSize();
    }
    return false;
  }

  ComparableQuantity<Q> widenTo(JavaNumberQuantity<Q> target) {
    return createTypedQuantity(target.getNumberType(), widenValue(this, target), getUnit());
  }

  private ComparableQuantity<?> createQuantity(Class<?> numberType, Number value, Unit<?> unit) {
    try {
      Method m = NumberQuantity.class.getDeclaredMethod("of", numberType, Unit.class);
      return (ComparableQuantity<?>) m.invoke(null, value, unit);
    } catch (Exception e) {
      return null;
    }
  }

  @SuppressWarnings({ "unchecked" })
  private ComparableQuantity<Q> createTypedQuantity(Class<?> numberType, Number value, Unit<Q> unit) {
    return (ComparableQuantity<Q>) createQuantity(numberType, value, unit);
  }

  private Number widenValue(JavaNumberQuantity<Q> source, JavaNumberQuantity<?> target) {
    Number value = source.getValue();
    if (target.isBig() && !source.isBig()) {
      if (target.getNumberType().equals(BigInteger.class)) {
        value = BigInteger.valueOf(value.longValue());
      } else {
        value = BigDecimal.valueOf(value.doubleValue());
      }
    } else if (source.getNumberType().equals(BigInteger.class) && !target.isBig()) {
      if (target.getNumberType().equals(float.class)) {
        value = source.getValue().floatValue();
      } else {
        value = source.getValue().doubleValue();
      }
    } else if (source.getNumberType().equals(BigInteger.class) && target.getNumberType().equals(BigDecimal.class)) {
      value = new BigDecimal((BigInteger) value);
    }
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (obj instanceof Quantity<?>) {
      Quantity<?> that = (Quantity<?>) obj;
      return Objects.equals(getUnit(), that.getUnit()) && Equalizer.hasEquality(getValue(), that.getValue());
    }
    return false;
  }
}
