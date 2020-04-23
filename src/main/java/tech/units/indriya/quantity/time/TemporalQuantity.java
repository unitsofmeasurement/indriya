/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2020, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
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
import tech.units.indriya.unit.Units;

/**
 * Class that represents {@link TemporalUnit} in Unit-API
 * 
 * @author Werner Keil
 * @author Filip van Laenen
 * @author Andi Huber
 * @version 1.3, Jun 4, 2019
 * @since 1.0
 */
public final class TemporalQuantity extends AbstractQuantity<Time> {
  
  private static final long serialVersionUID = -707159906206272775L;
  
  private final Object $lock1 = new Object[0]; // serializable lock for 'amount'
  
  private final TemporalUnit timeUnit;
  private final Number value;
  private transient TemporalAmount amount;

  /**
   * creates the {@link TemporalQuantity} using {@link TemporalUnit} and {@link Number}
   * 
   * @param timeUnit
   *          - time to be used
   * @param value
   *          - value to be used
   */
  TemporalQuantity(Number value, TemporalUnit timeUnit) {
    super(toUnit(timeUnit));
    this.timeUnit = timeUnit;
    this.value = value;
  }

  /**
   * creates the {@link TemporalQuantity} using {@link TemporalUnit} and {@link Number}
   * 
   * @param value
   *          - value to be used
   * @param timeUnit
   *          - time to be used
   */
  public static TemporalQuantity of(Number number, TemporalUnit timeUnit) {
    return new TemporalQuantity(Objects.requireNonNull(number), Objects.requireNonNull(timeUnit));
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
    return new TemporalQuantity(seconds.getValue(), ChronoUnit.SECONDS);
  }

  /**
   * Returns the {@link TemporalAmount} of this {@code TemporalQuantity}, which may involve rounding or truncation.
   * 
   * @return the TemporalAmount
   * @throws ArithmeticException when the {@code value} of this {@code TemporalQuantity} cannot be converted to long
   */
  public TemporalAmount getTemporalAmount() {
    synchronized ($lock1) {
        if(amount==null) {
            
            long longValue = value.longValue();
            
            Number error = Calculator.of(value)
            .subtract(longValue)
            .abs()
            .peek();

            //TODO[220] we should try to switch to smaller units to minimize the error
            if(Calculus.currentNumberSystem().compare(error, 1)>0) {
                String msg = String.format("cannot round number %s to long", "" + value);
                throw new ArithmeticException(msg);
            }
            amount = Duration.of(longValue, timeUnit);
            
        }
    }
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
   * get value expressed in {@link Number}
   * 
   * @return the value
   */
  public Number getValue() {
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
      return Objects.equals(getUnit(), that.getUnit()) && 
              Calculus.currentNumberSystem().compare(value, that.getValue()) == 0;
    }
    return super.equals(obj);
  }

  @Override
  public String toString() {
    return "Temporal unit:" + timeUnit + " value: " + value;
  }
  
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

  @Override
  public ComparableQuantity<Time> subtract(Quantity<Time> that) {
    return add(that.negate());
  }

  @Override
  public ComparableQuantity<?> divide(Quantity<?> that) {
    return applyMultiplicativeQuantityOperation(
            that, (a, b)->Calculator.of(a).divide(b).peek(), Unit::divide);
  }

  @Override
  public ComparableQuantity<Time> divide(Number that) {
    return applyMultiplicativeNumberOperation(
            that, (a, b)->Calculator.of(a).divide(b).peek());
  }

  @Override
  public ComparableQuantity<?> multiply(Quantity<?> that) {
    return applyMultiplicativeQuantityOperation(
            that, (a, b)->Calculator.of(a).multiply(b).peek(), Unit::multiply);
  }

  @Override
  public ComparableQuantity<Time> multiply(Number that) {
    return applyMultiplicativeNumberOperation(
            that, (a, b)->Calculator.of(a).multiply(b).peek());
  }

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
    return of(Calculator.of(value).negate().peek(), getTemporalUnit());
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
