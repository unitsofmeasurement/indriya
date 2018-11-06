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
package tech.units.indriya.quantity.time;

import static tech.units.indriya.unit.Units.DAY;
import static tech.units.indriya.unit.Units.HOUR;
import static tech.units.indriya.unit.Units.MINUTE;
import static tech.units.indriya.unit.Units.SECOND;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Time;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.function.Calculus;
import tech.units.indriya.quantity.NumberQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/**
 * Class that represents {@link TemporalUnit} in Unit-API
 * 
 * @author Werner Keil
 * @version 1.0.2
 * @since 1.0
 */
public final class TemporalQuantity extends AbstractQuantity<Time> {
  /**
   * 
   */
  private static final long serialVersionUID = 6835738653744691425L;

  private static final BigDecimal LONG_MAX_VALUE = new BigDecimal(Long.MAX_VALUE);
  private static final BigDecimal LONG_MIN_VALUE = new BigDecimal(Long.MIN_VALUE);

  private final TemporalUnit timeUnit;
  private final Long value;
  private final TemporalAmount amount;

  /**
   * creates the {@link TemporalQuantity} using {@link TemporalUnit} and {@link Integer}
   * 
   * @param timeUnit
   *          - time to be used
   * @param value
   *          - value to be used
   */
  TemporalQuantity(Long value, TemporalUnit timeUnit) {
    super(toUnit(timeUnit));
    this.timeUnit = timeUnit;
    this.amount = Duration.of(value, timeUnit);
    this.value = value;
  }

  /**
   * creates the {@link TemporalQuantity} using {@link TemporalUnit} and {@link Long}
   * 
   * @param value
   *          - value to be used
   * @param timeUnit
   *          - time to be used
   */
  public static TemporalQuantity of(Long number, TemporalUnit timeUnit) {
    return new TemporalQuantity(Objects.requireNonNull(number), Objects.requireNonNull(timeUnit));
  }

  /**
   * creates the {@link TemporalQuantity} using {@link TemporalUnit} and {@link Integer}
   * 
   * @param value
   *          - value to be used
   * @param timeUnit
   *          - time to be used
   */
  public static TemporalQuantity of(Integer number, TemporalUnit timeUnit) {
    return new TemporalQuantity(Objects.requireNonNull(number).longValue(), Objects.requireNonNull(timeUnit));
  }

  /**
   * Creates a {@link TemporalQuantity} based a {@link Quantity<Time>} converted to {@link Units#SECOND}.
   * 
   * @param quantity
   *          - quantity to be used
   * @return the {@link TemporalQuantity} converted be quantity in seconds.
   */
  public static TemporalQuantity of(Quantity<Time> quantity) {
    Quantity<Time> seconds = Objects.requireNonNull(quantity).to(SECOND);
    return new TemporalQuantity(seconds.getValue().longValue(), ChronoUnit.SECONDS);
  }

  /**
   * get to {@link TemporalAmount}
   * 
   * @return the TemporalAmount
   */
  public TemporalAmount getTemporalAmount() {
    return amount;
  }

  /**
   * get to {@link TemporalUnit}
   * 
   * @return the TemporalUnit
   */
  public TemporalUnit getTemporalUnit() {
    return timeUnit;
  }

  /**
   * get value expressed in {@link Long}
   * 
   * @return the value
   */
  public Long getValue() {
    return value;
  }

  /**
   * converts the {@link TemporalUnit} to {@link Unit}
   * 
   * @return the {@link TemporalQuantity#getTemporalUnit()} converted to Unit
   */
  public Unit<Time> toUnit() {
    return toUnit(timeUnit);
  }

  /**
   * Converts the {@link TemporalQuantity} to {@link Quantity<Time>}
   * 
   * @return this class converted to Quantity
   */
  public Quantity<Time> toQuantity() {
    return Quantities.getQuantity(value, toUnit());
  }

  public TemporalQuantity to(TemporalUnit aTimeUnit) {
    Quantity<Time> time = toQuantity().to(toUnit(aTimeUnit));
    return new TemporalQuantity(time.getValue().longValue(), aTimeUnit);
  }

  private static Unit<Time> toUnit(TemporalUnit timeUnit) {
    if (timeUnit instanceof ChronoUnit) {
      ChronoUnit chronoUnit = (ChronoUnit) timeUnit;
      switch (chronoUnit) {
        case MICROS:
          return TimeQuantities.MICROSECOND;
        case MILLIS:
          return TimeQuantities.MILLISECOND;
        case NANOS:
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
          throw new IllegalArgumentException("TemporalQuantity only supports DAYS, HOURS, MICROS, MILLIS, MINUTES, NANOS, SECONDS ");
      }
    }
    throw new IllegalArgumentException("TemporalQuantity only supports temporal units of type ChronoUnit");
  }

  @Override
  public int hashCode() {
    return Objects.hash(timeUnit, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (TemporalQuantity.class.isInstance(obj)) {
      TemporalQuantity other = TemporalQuantity.class.cast(obj);
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
    return "Temporal unit:" + timeUnit + " value: " + value;
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

  @Override
  public ComparableQuantity<Time> add(Quantity<Time> that) {
    final BigDecimal thisValueInSystemUnit = decimalValue(Units.SECOND);
    final BigDecimal thatValueInSystemUnit = (BigDecimal) that.getUnit().getConverterTo(Units.SECOND).convert(numberAsBigDecimal(that.getValue()));
    final BigDecimal resultValueInSystemUnit = thisValueInSystemUnit.add(thatValueInSystemUnit, Calculus.MATH_CONTEXT);
    final BigDecimal resultValueInThisUnit = numberAsBigDecimal(Units.SECOND.getConverterTo(getUnit()).convert(resultValueInSystemUnit));
    final BigDecimal resultValueInThatUnit = numberAsBigDecimal(Units.SECOND.getConverterTo(that.getUnit()).convert(resultValueInSystemUnit));
    final TemporalQuantity resultInThisUnit = TimeQuantities.getQuantity(resultValueInThisUnit.longValue(), timeUnit);
    final ComparableQuantity<Time> resultInThatUnit = NumberQuantity.of(resultValueInThatUnit.longValue(), that.getUnit());
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

  @Override
  public ComparableQuantity<Time> subtract(Quantity<Time> that) {
    return add(that.negate());
  }

  @Override
  public ComparableQuantity<?> divide(Quantity<?> that) {
    if (getUnit().equals(that.getUnit())) {
      return TimeQuantities.getQuantity(value / that.getValue().longValue(), timeUnit);
    }
    Unit<?> divUnit = getUnit().divide(that.getUnit());
    UnitConverter conv;
    try {
      conv = getUnit().getConverterToAny(divUnit);
      return TimeQuantities.getQuantity(value / conv.convert(that.getValue()).longValue(), timeUnit);
    } catch (UnconvertibleException e) {
      e.printStackTrace();
      return TimeQuantities.getQuantity(value / that.getValue().longValue(), timeUnit);
    } catch (IncommensurableException e) {
      e.printStackTrace();
      return TimeQuantities.getQuantity(value / that.getValue().longValue(), timeUnit);
    }
  }

  @Override
  public ComparableQuantity<Time> divide(Number that) {
    return TimeQuantities.getQuantity(value / that.longValue(), timeUnit);
  }

  @Override
  public ComparableQuantity<?> multiply(Quantity<?> multiplier) {
    if (getUnit().equals(multiplier.getUnit())) {
      return TimeQuantities.getQuantity(value * multiplier.getValue().longValue(), timeUnit);
    }
    Unit<?> mulUnit = getUnit().multiply(multiplier.getUnit());
    UnitConverter conv;
    try {
      conv = getUnit().getConverterToAny(mulUnit);
      return TimeQuantities.getQuantity(value * conv.convert(multiplier.getValue()).longValue(), timeUnit);
    } catch (UnconvertibleException e) {
      e.printStackTrace();
      return TimeQuantities.getQuantity(value * multiplier.getValue().longValue(), timeUnit);
    } catch (IncommensurableException e) {
      e.printStackTrace();
      return TimeQuantities.getQuantity(value * multiplier.getValue().longValue(), timeUnit);
    }
  }

  @Override
  public ComparableQuantity<Time> multiply(Number multiplier) {
    return TimeQuantities.getQuantity(value * multiplier.longValue(), timeUnit);
  }

  @Override
  public ComparableQuantity<Frequency> inverse() {
    return Quantities.getQuantity(1d / value.doubleValue(), toUnit(timeUnit).inverse()).asType(Frequency.class);
  }

  @Override
  public boolean isBig() {
    return false; // Duration backed by long
  }

  @Override
  public BigDecimal decimalValue(Unit<Time> unit) throws ArithmeticException {
    return (BigDecimal) getUnit().getConverterTo(unit).convert(BigDecimal.valueOf(value));
  }

  @Override
  public double doubleValue(Unit<Time> unit) throws ArithmeticException {
    final double result = getUnit().getConverterTo(unit).convert(getValue()).doubleValue();
    if (Double.isInfinite(result))
      throw new ArithmeticException();
    return result;
  }

  /**
   * @since 1.0.2
   */
  @Override
  public Quantity<Time> negate() {
    return of(-value, getTemporalUnit());
  }
}
