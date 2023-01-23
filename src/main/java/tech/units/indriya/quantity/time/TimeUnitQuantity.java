/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2023, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Time;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.function.Calculus;
import tech.units.indriya.internal.function.Calculator;
import tech.units.indriya.quantity.Quantities;

/**
 * Class that represents {@link TimeUnit} in Unit-API
 * 
 * @author Otavio Santana
 * @author Werner Keil
 * @author Andi Huber
 * @version 1.2
 * @since 1.0
 */
public final class TimeUnitQuantity extends AbstractQuantity<Time> {

  /**
   * 
   */
  private static final long serialVersionUID = -5840251813363744230L;

  private final TimeUnit timeUnit;

  private final Number value;

  /**
   * creates the {@link TimeUnitQuantity} using {@link TimeUnit} and {@link Long}
   * 
   * @param timeUnit
   *          - time to be used
   * @param value
   *          - value to be used
   */
  TimeUnitQuantity(TimeUnit timeUnit, Number value) {
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
  public static TimeUnitQuantity of(Number number, TimeUnit timeUnit) {
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
    return new TimeUnitQuantity(TimeUnit.SECONDS, seconds.getValue());
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
   * get value expressed in {@link Number}
   * 
   * @return the value
   * @since 1.0
   */
  public Number getValue() {
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
      return Objects.equals(getUnit(), that.getUnit()) && 
              Calculus.currentNumberSystem().compare(value, that.getValue()) == 0;
              
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
      final UnitConverter thisToThat = this.getUnit().getConverterTo(that.getUnit());
      final boolean thatUnitIsSmaller = 
              Calculus.currentNumberSystem().compare(thisToThat.convert(1.), 1.)>0;

      final Unit<Time> preferedUnit = thatUnitIsSmaller ? that.getUnit() : this.getUnit();
      
      final Number thisValueInPreferedUnit = convertedQuantityValue(this, preferedUnit);
      final Number thatValueInPreferedUnit = convertedQuantityValue(that, preferedUnit);
      
      final Number resultValueInPreferedUnit = Calculator.of(thisValueInPreferedUnit)
              .add(thatValueInPreferedUnit)
              .peek();
      
      return Quantities.getQuantity(resultValueInPreferedUnit, preferedUnit);
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
      return applyMultiplicativeQuantityOperation(
              that, (a, b)->Calculator.of(a).divide(b).peek(), Unit::divide);
  }

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<Time> divide(Number that) {
      return applyMultiplicativeNumberOperation(
              that, (a, b)->Calculator.of(a).divide(b).peek());
  }

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<?> multiply(Quantity<?> that) {
      return applyMultiplicativeQuantityOperation(
              that, (a, b)->Calculator.of(a).multiply(b).peek(), Unit::multiply);
  }

  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<Time> multiply(Number that) {
      return applyMultiplicativeNumberOperation(
              that, (a, b)->Calculator.of(a).multiply(b).peek());
  }
  
  /**
   * @since 1.0.1
   */
  @Override
  public ComparableQuantity<Frequency> inverse() {
    return Quantities.getQuantity(
            Calculator.of(value).reciprocal().peek(),
            toUnit(timeUnit).inverse()).asType(Frequency.class);
  }

  /**
   * @since 1.0.2
   */
  @Override
  public Quantity<Time> negate() {
    return of(
            Calculator.of(value).negate().peek(), 
            getTimeUnit());
  }
  
  // -- HELPER
  
  private static <R extends Quantity<R>> Number quantityValue(Quantity<R> that) {
      return convertedQuantityValue(that, that.getUnit());
  }

  private static <R extends Quantity<R>> Number convertedQuantityValue(Quantity<R> that, Unit<R> unit) {
      return that.getUnit().getConverterTo(unit).convert(that.getValue());
  }

  private ComparableQuantity<?> applyMultiplicativeQuantityOperation(
          Quantity<?> that,
          BinaryOperator<Number> valueOperator,
          BinaryOperator<Unit<?>> unitOperator) {

      final Number thisValue = quantityValue(this);
      final Number thatValue = quantityValue(that);
      final Number result = valueOperator.apply(thisValue, thatValue);
      final Unit<?> resultUnit = unitOperator.apply(getUnit(), that.getUnit());
      return Quantities.getQuantity(result, resultUnit);
  }

  private ComparableQuantity<Time> applyMultiplicativeNumberOperation(Number that,
          BinaryOperator<Number> valueOperator) {
      final Number thisValue = this.getValue();
      final Number thatValue = that;
      final Number result = valueOperator.apply(thisValue, thatValue);
      return Quantities.getQuantity(result, getUnit());
  }
}
