/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2020, Units of Measurement project.
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

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MICROS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.units.indriya.NumberAssertions.assertNumberEquals;
import static tech.units.indriya.unit.Units.DAY;
import static tech.units.indriya.unit.Units.HERTZ;
import static tech.units.indriya.unit.Units.HOUR;
import static tech.units.indriya.unit.Units.MINUTE;
import static tech.units.indriya.unit.Units.SECOND;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.TimeUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public class TimeQuantitiesTest {

  @Test
  public void ofTest() {
    TemporalQuantity day = TemporalQuantity.of(1, DAYS);
    TemporalQuantity hour = TemporalQuantity.of(1, HOURS);
    TemporalQuantity minute = TemporalQuantity.of(1, MINUTES);
    TemporalQuantity second = TemporalQuantity.of(1, SECONDS);
    TemporalQuantity microSecond = TemporalQuantity.of(1, MICROS);
    TemporalQuantity milliSecond = TemporalQuantity.of(1, MILLIS);
    TemporalQuantity nanoSecond = TemporalQuantity.of(1, NANOS);

    assertEquals(DAYS, day.getTemporalUnit());
    assertNumberEquals(1, day.getValue(), 1E-12);

    assertEquals(HOURS, hour.getTemporalUnit());
    assertNumberEquals(1, hour.getValue(), 1E-12);

    assertEquals(MINUTES, minute.getTemporalUnit());
    assertNumberEquals(1, minute.getValue(), 1E-12);

    assertEquals(SECONDS, second.getTemporalUnit());
    assertNumberEquals(1, second.getValue(), 1E-12);

    assertEquals(MICROS, microSecond.getTemporalUnit());
    assertNumberEquals(1, microSecond.getValue(), 1E-12);

    assertEquals(MILLIS, milliSecond.getTemporalUnit());
    assertNumberEquals(1, milliSecond.getValue(), 1E-12);

    assertEquals(NANOS, nanoSecond.getTemporalUnit());
    assertNumberEquals(1, nanoSecond.getValue(), 1E-12);
  }

  @Test
  public void ofQuantityTest() {
    Quantity<Time> hour = Quantities.getQuantity(1, Units.HOUR);
    TemporalQuantity timeQuantity = TemporalQuantity.of(hour);

    assertEquals(SECONDS, timeQuantity.getTemporalUnit());
    assertEquals(SECOND, timeQuantity.toUnit());
    assertNumberEquals(3600, timeQuantity.getValue(), 1E-12);
  }

  @Test
  public void toUnitTest() {
    TemporalQuantity day = TemporalQuantity.of(1, DAYS);
    TemporalQuantity hour = TemporalQuantity.of(1, HOURS);
    TemporalQuantity minute = TemporalQuantity.of(1, MINUTES);
    TemporalQuantity second = TemporalQuantity.of(1, SECONDS);
    TemporalQuantity microSecond = TemporalQuantity.of(1, MICROS);
    TemporalQuantity milliSecond = TemporalQuantity.of(1, MILLIS);
    TemporalQuantity nanoSecond = TemporalQuantity.of(1, NANOS);

    assertEquals(DAY, day.toUnit());
    assertEquals(HOUR, hour.toUnit());
    assertEquals(MINUTE, minute.toUnit());
    assertEquals(SECOND, second.toUnit());
    assertEquals(TimeQuantities.MICROSECOND, microSecond.toUnit());
    assertEquals(TimeQuantities.MILLISECOND, milliSecond.toUnit());
    assertEquals(TimeQuantities.NANOSECOND, nanoSecond.toUnit());
  }

  @Test
  public void toQuantityTest() {
    TemporalQuantity day = TemporalQuantity.of(1, DAYS);
    TemporalQuantity hour = TemporalQuantity.of(1, HOURS);
    TemporalQuantity minute = TemporalQuantity.of(1, MINUTES);
    TemporalQuantity second = TemporalQuantity.of(1, SECONDS);
    TemporalQuantity microSecond = TemporalQuantity.of(1, MICROS);
    TemporalQuantity milliSecond = TemporalQuantity.of(1, MILLIS);
    TemporalQuantity nanoSecond = TemporalQuantity.of(1, NANOS);

    verifyQuantity(day.toQuantity(), DAY, 1);
    verifyQuantity(hour.toQuantity(), HOUR, 1);
    verifyQuantity(minute.toQuantity(), MINUTE, 1);
    verifyQuantity(second.toQuantity(), SECOND, 1);
    verifyQuantity(microSecond.toQuantity(), TimeQuantities.MICROSECOND, 1);
    verifyQuantity(milliSecond.toQuantity(), TimeQuantities.MILLISECOND, 1);
    verifyQuantity(nanoSecond.toQuantity(), TimeQuantities.NANOSECOND, 1);

  }

  @Test
  public void convertTest() {
    TemporalQuantity day = TemporalQuantity.of(1, DAYS);
    TemporalQuantity hours = day.to(HOURS);

    assertEquals(HOURS, hours.getTemporalUnit());
    assertEquals(Long.valueOf(24), hours.getValue());

    TemporalQuantity oneDay = hours.to(DAYS);
    assertEquals(DAYS, oneDay.getTemporalUnit());
    assertEquals(Long.valueOf(1), oneDay.getValue());
  }

  private static void verifyQuantity(Quantity<Time> quantity, Unit<Time> unit, Number number) {
    assertEquals(unit, quantity.getUnit());
    assertEquals(Integer.valueOf(number.intValue()), Integer.valueOf(quantity.getValue().intValue()));
  }

  @Test
  public void ofTemporalTest() {
    LocalDate a = Year.of(2015).atMonth(Month.JANUARY).atDay(9);
    LocalDate b = Year.of(2015).atMonth(Month.JANUARY).atDay(10);
    Quantity<Time> time = TimeQuantities.getQuantity(a, b);
    assertEquals(Integer.valueOf(1), Integer.valueOf(time.getValue().intValue()));
    assertEquals(Units.DAY, time.getUnit());
  }

  @Test
  public void ofLocalTimeTest() {
    LocalTime a = LocalTime.of(0, 0);
    LocalTime b = LocalTime.of(12, 0);
    Quantity<Time> time = TimeQuantities.getQuantity(a, b);
    assertEquals(Double.valueOf(12.0), Double.valueOf(time.getValue().doubleValue()));
    assertEquals(Units.HOUR, time.getUnit());
  }

  @Test
    public void ofTemporalAdjustTest() {
	LocalDate a = Year.of(2015).atMonth(Month.JANUARY).atDay(9);

	Quantity<Time> time = TimeQuantities.getQuantity(a, () -> TemporalAdjusters.next(DayOfWeek.SUNDAY));
	assertEquals(Integer.valueOf(2), Integer.valueOf(time.getValue().intValue()));
	assertEquals(Units.DAY, time.getUnit());
    }

  @Test
    public void ofLocalTimeTemporalAdjustTest() {
	LocalTime a = LocalTime.MIDNIGHT;
	TemporalAdjuster temporalAdjuster = (temporal) -> temporal.plus(12L, ChronoUnit.HOURS);

	Quantity<Time> time = TimeQuantities.getQuantity(a, () -> temporalAdjuster);
	assertEquals(Integer.valueOf(12), Integer.valueOf(time.getValue().intValue()));
	assertEquals(Units.HOUR, time.getUnit());
    }

  @Test
  public void inverseTest() {
    TimeUnitQuantity tenSeconds = TimeUnitQuantity.of(10, TimeUnit.SECONDS);
    Quantity<Frequency> perTenSeconds = tenSeconds.inverse();
    assertNumberEquals(new BigDecimal("0.1"), perTenSeconds.getValue(), 1E-12);
    assertEquals(HERTZ.getConverterTo(perTenSeconds.getUnit()), perTenSeconds.getUnit().getConverterTo(HERTZ));
  }

  @Test
  public void inverseTemporalTest() {
    final TemporalQuantity tenSeconds = TemporalQuantity.of(10, SECONDS);
    Quantity<Frequency> perTenSeconds = tenSeconds.inverse();
    assertNumberEquals(new BigDecimal("0.1"), perTenSeconds.getValue(), 1E-12);
    assertEquals(HERTZ.getConverterTo(perTenSeconds.getUnit()), perTenSeconds.getUnit().getConverterTo(HERTZ));
  }
  
  @Test
  public void negateTest() {
    final TimeUnitQuantity tenSeconds = TimeUnitQuantity.of(10, TimeUnit.SECONDS);
    final Quantity<Time> negated = tenSeconds.negate();
    assertNumberEquals(-10, negated.getValue(), 1E-12);
  }
  
  @Test
  public void negateTemporalTest() {
	final TemporalQuantity tenSeconds = TemporalQuantity.of(10, SECONDS);
    final Quantity<Time> negated = tenSeconds.negate();
    assertNumberEquals(-10, negated.getValue(), 1E-12);
  }
}
