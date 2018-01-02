/*
 * Next Generation Units of Measurement Implementation
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
import static tec.units.indriya.unit.Units.SECOND;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Time;

import tec.units.indriya.quantity.Quantities;
import tec.units.indriya.unit.MetricPrefix;
import tec.units.indriya.unit.TransformedUnit;

/**
 * @author Otavio
 * @author Werner
 * @version 1.0
 * @since 1.0
 */
public final class TimeQuantities {

  private TimeQuantities() {
  }

  // Convenience constants outside the unit system (multiples are not held there)

  public static final Unit<Time> MICROSECOND = new TransformedUnit<>("μs", SECOND, SECOND, MetricPrefix.MICRO.getConverter());

  public static final TransformedUnit<Time> MILLISECOND = new TransformedUnit<>("ms", SECOND, SECOND, MetricPrefix.MILLI.getConverter());

  public static final TransformedUnit<Time> NANOSECOND = new TransformedUnit<>("ns", SECOND, SECOND, MetricPrefix.NANO.getConverter());

  /**
   * Creates the {@link Quantity<Time>} based in the difference of the two {@link Temporal}
   * 
   * @param temporalA
   *          - First parameter to range, inclusive
   * @param temporalB
   *          - second parameter to range, exclusive
   * @return the Quantity difference based in {@link Units#DAY}.
   * @throws java.time.temporal.UnsupportedTemporalTypeException
   *           if some temporal doesn't support {@link ChronoUnit#DAYS}
   */
  public static Quantity<Time> getQuantity(Temporal temporalA, Temporal temporalB) {
    long days = ChronoUnit.DAYS.between(temporalA, temporalB);
    return Quantities.getQuantity(days, DAY);
  }

  /**
   * Creates the {@link Quantity<Time>} based in the difference of the two {@link LocalTime}
   * 
   * @param localTimeA
   *          - First parameter to range, inclusive
   * @param localTimeB
   *          - second parameter to range, exclusive
   * @return the Quantity difference based in {@link Units#HOUR}.
   * @throws java.time.temporal.UnsupportedTemporalTypeException
   *           if some temporal doesn't support {@link ChronoUnit#DAYS}
   */
  public static Quantity<Time> getQuantity(LocalTime localTimeA, LocalTime localTimeB) {
    long hours = ChronoUnit.HOURS.between(localTimeA, localTimeB);
    return Quantities.getQuantity(hours, HOUR);
  }

  /**
   * Creates the {@link Quantity<Time>} based in the {@link Temporal} with {@link TemporalAdjuster}
   * 
   * @param temporalA
   *          - temporal
   * @param supplier
   *          the adjust @see {@link TemporalAdjuster}
   * @return The Quantity based in Temporal with TemporalAdjuster in {@link Units#DAY}.
   * @throws java.time.temporal.UnsupportedTemporalTypeException
   *           if some temporal doesn't support {@link ChronoUnit#DAYS}
   */
  public static Quantity<Time> getQuantity(Temporal temporalA, Supplier<TemporalAdjuster> supplier) {
    Temporal temporalB = temporalA.with(supplier.get());
    return getQuantity(temporalA, temporalB);
  }

  /**
   * Creates the {@link Quantity<Time>} based in the {@link Temporal} with {@link Supplier<TemporalAdjuster>}
   * 
   * @param localTimeA
   * @see {@link LocalTime}
   * @param supplier
   *          he adjust @see {@link TemporalAdjuster}
   * @return The Quantity based in Temporal with TemporalAdjuster in {@link Units#DAY}.
   * @throws java.time.temporal.UnsupportedTemporalTypeException
   *           if some temporal doesn't support {@link ChronoUnit#DAYS}
   */
  public static Quantity<Time> getQuantity(LocalTime localTimeA, Supplier<TemporalAdjuster> supplier) {
    LocalTime localTimeB = localTimeA.with(supplier.get());
    return getQuantity(localTimeA, localTimeB);
  }

  /**
   * creates the {@link TimeUnitQuantity} using {@link TimeUnit} and {@link Integer}
   * 
   * @param value
   *          - value to be used
   * @param timeUnit
   *          - time to be used
   */
  public static TimeUnitQuantity getQuantity(Integer number, TimeUnit timeUnit) {
    return new TimeUnitQuantity(Objects.requireNonNull(timeUnit), Objects.requireNonNull(number));
  }

  /**
   * creates the {@link TemporalQuantity} using {@link TemporalUnit} and {@link Integer}
   * 
   * @param value
   *          - value to be used
   * @param timeUnit
   *          - time to be used
   */
  public static TemporalQuantity getQuantity(Integer number, TemporalUnit temporalUnit) {
    return new TemporalQuantity(Objects.requireNonNull(number), Objects.requireNonNull(temporalUnit));
  }

  /**
   * Creates a {@link TimeUnitQuantity} based a {@link Quantity<Time>} converted to {@link Units#SECOND}.
   * 
   * @param quantity
   *          - quantity to be used
   * @return the {@link TimeUnitQuantity} converted be quantity in seconds.
   */
  public static TimeUnitQuantity toTimeUnitSeconds(Quantity<Time> quantity) {
    Quantity<Time> seconds = Objects.requireNonNull(quantity).to(SECOND);
    return new TimeUnitQuantity(TimeUnit.SECONDS, seconds.getValue().intValue());
  }

  /**
   * Creates a {@link TemporalQuantity} based a {@link Quantity<Time>} converted to {@link Units#SECOND}.
   * 
   * @param quantity
   *          - quantity to be used
   * @return the {@link TemporalQuantity} converted be quantity in seconds.
   */
  public static TemporalQuantity toTemporalSeconds(Quantity<Time> quantity) {
    Quantity<Time> seconds = Objects.requireNonNull(quantity).to(SECOND);
    return TemporalQuantity.of(seconds);
  }
}
