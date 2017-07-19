/*
 * Next Generation Units of Measurement Implementation
 * Copyright (c) 2005-2017, Jean-Marie Dautelle, Werner Keil, V2COM.
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
package tec.units.indriya.quantity.time;

import static tec.units.indriya.unit.Units.DAY;
import static tec.units.indriya.unit.Units.HOUR;
import static tec.units.indriya.unit.Units.MINUTE;
import static tec.units.indriya.unit.Units.SECOND;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Time;

import tec.units.indriya.AbstractQuantity;
import tec.units.indriya.ComparableQuantity;
import tec.units.indriya.quantity.Quantities;

/**
 * Class that represents {@link TimeUnit} in Unit-API
 * 
 * @author otaviojava
 * @author keilw
 * @version 1.0.1
 * @since 1.0
 */
public final class TimeUnitQuantity extends AbstractQuantity<Time> {

  /**
     * 
     */
  private static final long serialVersionUID = -5840251813363744230L;

  private final TimeUnit timeUnit;

  private final Integer value;

  /**
   * creates the {@link TimeUnitQuantity} using {@link TimeUnit} and {@link Integer}
   * 
   * @param timeUnit
   *          - time to be used
   * @param value
   *          - value to be used
   */
  TimeUnitQuantity(TimeUnit timeUnit, Integer value) {
    super(toUnit(timeUnit));
    this.timeUnit = timeUnit;
    this.value = value;
  }

  /**
   * creates the {@link TimeUnitQuantity} using {@link TimeUnit} and {@link Integer}
   * 
   * @param timeUnit
   *          - time to be used
   * @param value
   *          - value to be used
   * @since 1.0
   */
  public static TimeUnitQuantity of(TimeUnit timeUnit, Integer number) {
    return new TimeUnitQuantity(Objects.requireNonNull(timeUnit), Objects.requireNonNull(number));
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
    return new TimeUnitQuantity(TimeUnit.SECONDS, seconds.getValue().intValue());
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
   * get value expressed in {@link Integer}
   * 
   * @return the value
   * @since 1.0
   */
  public Integer getValue() {
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

  public TimeUnitQuantity to(TimeUnit timeUnit) {
    Quantity<Time> time = toQuantity().to(toUnit(timeUnit));
    return new TimeUnitQuantity(timeUnit, time.getValue().intValue());
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

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<Time> add(Quantity<Time> that) {
    if (getUnit().equals(that.getUnit())) {
      return TimeQuantities.getQuantity(value + that.getValue().intValue(), timeUnit);
    }
    Quantity<Time> converted = that.to(getUnit());
    return TimeQuantities.getQuantity(value + converted.getValue().intValue(), timeUnit);
  }

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<Time> subtract(Quantity<Time> that) {
    if (getUnit().equals(that.getUnit())) {
      return TimeQuantities.getQuantity(value - that.getValue().intValue(), timeUnit);
    }
    Quantity<Time> converted = that.to(getUnit());
    return TimeQuantities.getQuantity(value - converted.getValue().intValue(), timeUnit);
  }

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<?> divide(Quantity<?> that) {
    if (getUnit().equals(that.getUnit())) {
      return TimeQuantities.getQuantity(value / that.getValue().intValue(), timeUnit);
    }
    Unit<?> divUnit = getUnit().divide(that.getUnit());
    UnitConverter conv;
    try {
      conv = getUnit().getConverterToAny(divUnit);
      return TimeQuantities.getQuantity(value / conv.convert(that.getValue()).intValue(), timeUnit);
    } catch (UnconvertibleException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return TimeQuantities.getQuantity(value / that.getValue().intValue(), timeUnit);
    } catch (IncommensurableException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return TimeQuantities.getQuantity(value / that.getValue().intValue(), timeUnit);
    }
  }

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<Time> divide(Number that) {
    return TimeQuantities.getQuantity(value / that.intValue(), timeUnit);
  }

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<?> multiply(Quantity<?> multiplier) {
    if (getUnit().equals(multiplier.getUnit())) {
      return TimeQuantities.getQuantity(value * multiplier.getValue().intValue(), timeUnit);
    }
    Unit<?> mulUnit = getUnit().multiply(multiplier.getUnit());
    UnitConverter conv;
    try {
      conv = getUnit().getConverterToAny(mulUnit);
      return TimeQuantities.getQuantity(value * conv.convert(multiplier.getValue()).intValue(), timeUnit);
    } catch (UnconvertibleException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return TimeQuantities.getQuantity(value * multiplier.getValue().intValue(), timeUnit);
    } catch (IncommensurableException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return TimeQuantities.getQuantity(value * multiplier.getValue().intValue(), timeUnit);
    }
  }

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<Time> multiply(Number multiplier) {
    return TimeQuantities.getQuantity(value * multiplier.intValue(), timeUnit);
  }

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<?> inverse() {
    return TimeQuantities.getQuantity(1 / value, timeUnit);
  }

  /**
   * @since 1.0.1
   */
  @Override
  public boolean isBig() {
    return false;
  }

  /**
   * @since 1.0.1
   */
  @Override
  public BigDecimal decimalValue(Unit<Time> unit, MathContext ctx) throws ArithmeticException {
    return BigDecimal.valueOf(value.doubleValue());
  }

  /**
   * @since 1.0.1
   */
  @Override
  public double doubleValue(Unit<Time> unit) throws ArithmeticException {
    return value.doubleValue();
  }
}
