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
package tech.units.indriya.quantity.time;

import static tech.units.indriya.unit.Units.DAY;
import static tech.units.indriya.unit.Units.HOUR;
import static tech.units.indriya.unit.Units.MINUTE;
import static tech.units.indriya.unit.Units.SECOND;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Time;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.function.Calculus;
import tech.units.indriya.quantity.Quantities;

/**
 * Class that represents {@link TimeUnit} in Unit-API
 * 
 * @author otaviojava
 * @author keilw
 * @version 1.0.2
 * @since 1.0
 */
public final class TimeUnitQuantity extends AbstractQuantity<Time> {

  /**
   * 
   */
  private static final long serialVersionUID = -5840251813363744230L;

  private static final BigDecimal LONG_MAX_VALUE = new BigDecimal(Long.MAX_VALUE);
  private static final BigDecimal LONG_MIN_VALUE = new BigDecimal(Long.MIN_VALUE);

  private final TimeUnit timeUnit;

  private final Long value;

  /**
   * creates the {@link TimeUnitQuantity} using {@link TimeUnit} and {@link Long}
   * 
   * @param timeUnit
   *          - time to be used
   * @param value
   *          - value to be used
   */
  TimeUnitQuantity(TimeUnit timeUnit, Long value) {
    super(toUnit(timeUnit));
    this.timeUnit = timeUnit;
    this.value = value;
  }

  /**
   * creates the {@link TimeUnitQuantity} using {@link TimeUnit} and {@link Long}
   * 
   * @param timeUnit
   *          - time to be used
   * @param value
   *          - value to be used
   * @since 1.0.9
   */
  public static TimeUnitQuantity of(Long number, TimeUnit timeUnit) {
    return new TimeUnitQuantity(Objects.requireNonNull(timeUnit), Objects.requireNonNull(number));
  }

  /**
   * creates the {@link TimeUnitQuantity} using {@link TimeUnit} and {@link Integer}
   * 
   * @param timeUnit
   *          - time to be used
   * @param value
   *          - value to be used
   * @since 1.0.9
   */
  public static TimeUnitQuantity of(Integer number, TimeUnit timeUnit) {
    return new TimeUnitQuantity(Objects.requireNonNull(timeUnit), Objects.requireNonNull(number).longValue());
  }

  /**
   * creates the {@link TimeUnitQuantity} using {@link TimeUnit} and {@link Integer}
   * 
   * @param timeUnit
   *          - time to be used
   * @param value
   *          - value to be used
   * @since 1.0
   * @deprecated use #of(Integer, TimeUnit)
   */
  @Deprecated
  public static TimeUnitQuantity of(TimeUnit timeUnit, Integer number) {
    return of(number, timeUnit);
  }

  /**
   * Creates a {@link TimeUnitQuantity} based a {@link Quantity<Time>} converted to {@link SI#SECOND}.
   * 
   * @param quantity
   *          - quantity to be used
   * @return the {@link TimeUnitQuantity} converted be quantity in seconds.
   * @since 1.0
   */
  public static TimeUnitQuantity of(Quantity<Time> quantity) {
    Quantity<Time> seconds = Objects.requireNonNull(quantity).to(SECOND);
    return new TimeUnitQuantity(TimeUnit.SECONDS, seconds.getValue().longValue());
  }

  /**
   * get to {@link TimeUnit}
   * 
   * @return the TimeUnit
   * @since 1.0
   */
  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  /**
   * get value expressed in {@link Long}
   * 
   * @return the value
   * @since 1.0
   */
  public Long getValue() {
    return value;
  }

  /**
   * converts the {@link TimeUnit} to {@link Unit}
   * 
   * @return the {@link TimeUnitQuantity#getTimeUnit()} converted to Unit
   * @since 1.0
   */
  public Unit<Time> toUnit() {
    return toUnit(timeUnit);
  }

  /**
   * Converts the {@link TimeUnitQuantity} to {@link Quantity<Time>}
   * 
   * @return this class converted to Quantity
   * @since 1.0
   */
  public Quantity<Time> toQuantity() {
    return Quantities.getQuantity(value, toUnit());
  }

  public TimeUnitQuantity to(TimeUnit aTimeUnit) {
    Quantity<Time> time = toQuantity().to(toUnit(aTimeUnit));
    return new TimeUnitQuantity(aTimeUnit, time.getValue().longValue());
  }

  private static Unit<Time> toUnit(TimeUnit timeUnit) {
    switch (timeUnit) {
      case MICROSECONDS:
        return TimeQuantities.MICROSECOND;
      case MILLISECONDS:
        return TimeQuantities.MILLISECOND;
      case NANOSECONDS:
        return TimeQuantities.NANOSECOND;
      case SECONDS:
        return SECOND;
      case MINUTES:
        return MINUTE;
      case HOURS:
        return HOUR;
      case DAYS:
        return DAY;
      default:
        throw new IllegalStateException("In TimeUnitQuantity just supports DAYS, HOURS, MICROSECONDS, MILLISECONDS, MINUTES, NANOSECONDS, SECONDS ");
    }
  }

  /**
   * @since 1.0
   */
  @Override
  public int hashCode() {
    return Objects.hash(timeUnit, value);
  }

  /**
   * @since 1.0
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (TimeUnitQuantity.class.isInstance(obj)) {
      TimeUnitQuantity other = TimeUnitQuantity.class.cast(obj);
      return Objects.equals(timeUnit, other.timeUnit) && Objects.equals(value, other.value);
    }
    if (obj instanceof Quantity<?>) {
      Quantity<?> that = (Quantity<?>) obj;
      return Objects.equals(getUnit(), that.getUnit()) && Equalizer.hasEquality(value, that.getValue());
    }
    return super.equals(obj);
  }

  @Override
  public String toString() {
    return "Time unit:" + timeUnit + " value: " + value;
  }

  private static BigDecimal numberAsBigDecimal(Number that) {
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

  boolean isOverflowing(BigDecimal aValue) {
    return aValue.compareTo(LONG_MIN_VALUE) < 0 || aValue.compareTo(LONG_MAX_VALUE) > 0;
  }

  private static <R extends Quantity<R>> BigDecimal quantityValueAsBigDecimal(Quantity<R> that) {
    return convertedQuantityValueAsBigDecimal(that, that.getUnit());
  }

  private static <R extends Quantity<R>> BigDecimal convertedQuantityValueAsBigDecimal(Quantity<R> that, Unit<R> unit) {
    return (BigDecimal) that.getUnit().getConverterTo(unit).convert(numberAsBigDecimal(that.getValue()));
  }

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<Time> add(Quantity<Time> that) {
    final BigDecimal thisValueInSystemUnit = convertedQuantityValueAsBigDecimal(this, SECOND);
    final BigDecimal thatValueInSystemUnit = convertedQuantityValueAsBigDecimal(that, SECOND);
    final BigDecimal resultValueInSystemUnit = thisValueInSystemUnit.add(thatValueInSystemUnit, Calculus.MATH_CONTEXT);
    final BigDecimal resultValueInThisUnit = numberAsBigDecimal(SECOND.getConverterTo(getUnit()).convert(resultValueInSystemUnit));
    final BigDecimal resultValueInThatUnit = numberAsBigDecimal(SECOND.getConverterTo(that.getUnit()).convert(resultValueInSystemUnit));
    final TimeUnitQuantity resultInThisUnit = TimeQuantities.getQuantity(resultValueInThisUnit.longValue(), timeUnit);
    final ComparableQuantity<Time> resultInThatUnit = Quantities.getQuantity(resultValueInThatUnit.longValue(), that.getUnit());
    if (isOverflowing(resultValueInThisUnit)) {
      if (isOverflowing(resultValueInThatUnit))
        throw new ArithmeticException();
      return resultInThatUnit;
    } else if (isOverflowing(resultValueInThatUnit)) {
      return resultInThisUnit;
    } else if (hasFraction(resultValueInThisUnit)) {
      return resultInThatUnit;
    } else {
      return resultInThisUnit;
    }
  }

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<Time> subtract(Quantity<Time> that) {
    return add(that.negate());
  }

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<?> divide(Quantity<?> that) {
    return applyMultiplicativeQuantityOperation(that, BigDecimal::divide, Unit::divide);
  }

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<Time> divide(Number that) {
    return applyMultiplicativeNumberOperation(that, BigDecimal::divide);
  }

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<?> multiply(Quantity<?> that) {
    return applyMultiplicativeQuantityOperation(that, BigDecimal::multiply, Unit::multiply);
  }

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<Time> multiply(Number that) {
    return applyMultiplicativeNumberOperation(that, BigDecimal::multiply);
  }

  @FunctionalInterface
  private interface TriFunction<R, A, B, C> {
    R apply(A a, B b, C c);
  }

  private ComparableQuantity<?> applyMultiplicativeQuantityOperation(Quantity<?> that,
      TriFunction<BigDecimal, BigDecimal, BigDecimal, MathContext> valueOperator, BiFunction<Unit<?>, Unit<?>, Unit<?>> unitOperator) {
    final BigDecimal thisValue = quantityValueAsBigDecimal(this);
    final BigDecimal thatValue = quantityValueAsBigDecimal(that);
    final BigDecimal result = valueOperator.apply(thisValue, thatValue, Calculus.MATH_CONTEXT);
    if (isOverflowing(result)) {
      throw new ArithmeticException();
    }
    final Unit<?> resultUnit = unitOperator.apply(getUnit(), that.getUnit());
    return Quantities.getQuantity(result.longValue(), resultUnit);
  }

  private ComparableQuantity<Time> applyMultiplicativeNumberOperation(Number that,
      TriFunction<BigDecimal, BigDecimal, BigDecimal, MathContext> valueOperator) {
    final BigDecimal thisValue = numberAsBigDecimal(this.value);
    final BigDecimal thatValue = numberAsBigDecimal(that);
    final BigDecimal result = valueOperator.apply(thisValue, thatValue, Calculus.MATH_CONTEXT);
    if (isOverflowing(result)) {
      throw new ArithmeticException();
    }
    return Quantities.getQuantity(result.longValue(), getUnit());
  }

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<Frequency> inverse() {
    return Quantities.getQuantity(1.0 / value, toUnit(timeUnit).inverse()).asType(Frequency.class);
  }

  /**
   * @since 1.0.1
   */
  @Override
  public boolean isBig() {
    return false;
  }

  /**
   * @since 1.0.2
   */
  @Override
  public Quantity<Time> negate() {
    return of(-value, getTimeUnit());
  }
}
