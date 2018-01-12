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
package tec.units.indriya.quantity.time;

import static java.time.temporal.ChronoUnit.*;
import static tec.units.indriya.unit.Units.DAY;
import static tec.units.indriya.unit.Units.HOUR;
import static tec.units.indriya.unit.Units.MINUTE;
import static tec.units.indriya.unit.Units.SECOND;
import static tec.units.indriya.unit.Units.HERTZ;

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

import org.junit.Assert;
import org.junit.Test;

import tec.units.indriya.quantity.Quantities;
import tec.units.indriya.quantity.time.TemporalQuantity;
import tec.units.indriya.quantity.time.TimeQuantities;
import tec.units.indriya.unit.Units;

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

    Assert.assertEquals(DAYS, day.getTemporalUnit());
    Assert.assertEquals(Integer.valueOf(1), day.getValue());

    Assert.assertEquals(HOURS, hour.getTemporalUnit());
    Assert.assertEquals(Integer.valueOf(1), hour.getValue());

    Assert.assertEquals(MINUTES, minute.getTemporalUnit());
    Assert.assertEquals(Integer.valueOf(1), minute.getValue());

    Assert.assertEquals(SECONDS, second.getTemporalUnit());
    Assert.assertEquals(Integer.valueOf(1), second.getValue());

    Assert.assertEquals(MICROS, microSecond.getTemporalUnit());
    Assert.assertEquals(Integer.valueOf(1), microSecond.getValue());

    Assert.assertEquals(MILLIS, milliSecond.getTemporalUnit());
    Assert.assertEquals(Integer.valueOf(1), milliSecond.getValue());

    Assert.assertEquals(NANOS, nanoSecond.getTemporalUnit());
    Assert.assertEquals(Integer.valueOf(1), nanoSecond.getValue());
  }

  @Test
  public void ofQuantityTest() {
    Quantity<Time> hour = Quantities.getQuantity(1, Units.HOUR);
    TemporalQuantity timeQuantity = TemporalQuantity.of(hour);

    Assert.assertEquals(SECONDS, timeQuantity.getTemporalUnit());
    Assert.assertEquals(SECOND, timeQuantity.toUnit());
    Assert.assertEquals(Integer.valueOf(3600), timeQuantity.getValue());
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

    Assert.assertEquals(DAY, day.toUnit());
    Assert.assertEquals(HOUR, hour.toUnit());
    Assert.assertEquals(MINUTE, minute.toUnit());
    Assert.assertEquals(SECOND, second.toUnit());
    Assert.assertEquals(TimeQuantities.MICROSECOND, microSecond.toUnit());
    Assert.assertEquals(TimeQuantities.MILLISECOND, milliSecond.toUnit());
    Assert.assertEquals(TimeQuantities.NANOSECOND, nanoSecond.toUnit());
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

    Assert.assertEquals(HOURS, hours.getTemporalUnit());
    Assert.assertEquals(Integer.valueOf(24), hours.getValue());

    TemporalQuantity oneDay = hours.to(DAYS);
    Assert.assertEquals(DAYS, oneDay.getTemporalUnit());
    Assert.assertEquals(Integer.valueOf(1), oneDay.getValue());
  }

  private void verifyQuantity(Quantity<Time> quantity, Unit<Time> unit, Number number) {
    Assert.assertEquals(unit, quantity.getUnit());
    Assert.assertEquals(Integer.valueOf(number.intValue()), Integer.valueOf(quantity.getValue().intValue()));
  }

  @Test
  public void ofTemporalTest() {
    LocalDate a = Year.of(2015).atMonth(Month.JANUARY).atDay(9);
    LocalDate b = Year.of(2015).atMonth(Month.JANUARY).atDay(10);
    Quantity<Time> time = TimeQuantities.getQuantity(a, b);
    Assert.assertEquals(Integer.valueOf(1), Integer.valueOf(time.getValue().intValue()));
    Assert.assertEquals(Units.DAY, time.getUnit());
  }

  @Test
  public void ofLocalTimeTest() {
    LocalTime a = LocalTime.of(0, 0);
    LocalTime b = LocalTime.of(12, 0);
    Quantity<Time> time = TimeQuantities.getQuantity(a, b);
    Assert.assertEquals(Double.valueOf(12.0), Double.valueOf(time.getValue().doubleValue()));
    Assert.assertEquals(Units.HOUR, time.getUnit());
  }

  @Test
    public void ofTemporalAdjustTest() {
	LocalDate a = Year.of(2015).atMonth(Month.JANUARY).atDay(9);

	Quantity<Time> time = TimeQuantities.getQuantity(a, () -> TemporalAdjusters.next(DayOfWeek.SUNDAY));
	Assert.assertEquals(Integer.valueOf(2), Integer.valueOf(time.getValue().intValue()));
	Assert.assertEquals(Units.DAY, time.getUnit());
    }

  @Test
    public void ofLocalTimeTemporalAdjustTest() {
	LocalTime a = LocalTime.MIDNIGHT;
	TemporalAdjuster temporalAdjuster = (temporal) -> temporal.plus(12L, ChronoUnit.HOURS);

	Quantity<Time> time = TimeQuantities.getQuantity(a, () -> temporalAdjuster);
	Assert.assertEquals(Integer.valueOf(12), Integer.valueOf(time.getValue().intValue()));
	Assert.assertEquals(Units.HOUR, time.getUnit());
    }

  @Test
  public void inverseTest() {
    TimeUnitQuantity tenSeconds = TimeUnitQuantity.of(10, TimeUnit.SECONDS);
    Quantity<Frequency> perTenSeconds = tenSeconds.inverse();
    Assert.assertEquals(Double.valueOf(0.1d), perTenSeconds.getValue());
    Assert.assertEquals(HERTZ.getConverterTo(perTenSeconds.getUnit()), perTenSeconds.getUnit().getConverterTo(HERTZ));
  }

  @Test
  public void inverseTemporalTest() {
    TemporalQuantity tenSeconds = TemporalQuantity.of(10, SECONDS);
    Quantity<Frequency> perTenSeconds = tenSeconds.inverse();
    Assert.assertEquals(0.1d, perTenSeconds.getValue());
    Assert.assertEquals(HERTZ.getConverterTo(perTenSeconds.getUnit()), perTenSeconds.getUnit().getConverterTo(HERTZ));
  }
}
