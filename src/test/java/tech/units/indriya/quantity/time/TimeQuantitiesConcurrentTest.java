/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2022, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.units.indriya.NumberAssertions.assertNumberEquals;
import static tech.units.indriya.unit.Units.DAY;
import static tech.units.indriya.unit.Units.HOUR;
import static tech.units.indriya.unit.Units.MINUTE;
import static tech.units.indriya.unit.Units.SECOND;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.quantity.time.TimeQuantities;
import tech.units.indriya.quantity.time.TimeUnitQuantity;

public class TimeQuantitiesConcurrentTest {

  @Test
  public void ofTest() {
    TimeUnitQuantity day = TimeUnitQuantity.of(1, DAYS);
    TimeUnitQuantity hour = TimeUnitQuantity.of(1, HOURS);
    TimeUnitQuantity minute = TimeUnitQuantity.of(1, MINUTES);
    TimeUnitQuantity second = TimeUnitQuantity.of(1, SECONDS);
    TimeUnitQuantity microSecond = TimeUnitQuantity.of(1, MICROSECONDS);
    TimeUnitQuantity milliSecond = TimeUnitQuantity.of(1, MILLISECONDS);
    TimeUnitQuantity nanoSecond = TimeUnitQuantity.of(1, NANOSECONDS);

    assertEquals(DAYS, day.getTimeUnit());
    assertNumberEquals(1, day.getValue(), 1E-12);

    assertEquals(HOURS, hour.getTimeUnit());
    assertNumberEquals(1, hour.getValue(), 1E-12);

    assertEquals(MINUTES, minute.getTimeUnit());
    assertNumberEquals(1, minute.getValue(), 1E-12);

    assertEquals(SECONDS, second.getTimeUnit());
    assertNumberEquals(1, second.getValue(), 1E-12);

    assertEquals(MICROSECONDS, microSecond.getTimeUnit());
    assertNumberEquals(1, microSecond.getValue(), 1E-12);

    assertEquals(MILLISECONDS, milliSecond.getTimeUnit());
    assertNumberEquals(1, milliSecond.getValue(), 1E-12);

    assertEquals(NANOSECONDS, nanoSecond.getTimeUnit());
    assertNumberEquals(1, nanoSecond.getValue(), 1E-12);
  }

  @Test
  public void ofQuantityTest() {
    Quantity<Time> hour = Quantities.getQuantity(1, HOUR);
    TimeUnitQuantity timeQuantity = TimeUnitQuantity.of(hour);

    assertEquals(SECONDS, timeQuantity.getTimeUnit());
    assertEquals(SECOND, timeQuantity.toUnit());
    assertNumberEquals(3600, timeQuantity.getValue(), 1E-12);
  }

  @Test
  public void toUnitTest() {
    TimeUnitQuantity day = TimeUnitQuantity.of(1, DAYS);
    TimeUnitQuantity hour = TimeUnitQuantity.of(1, HOURS);
    TimeUnitQuantity minute = TimeUnitQuantity.of(1, MINUTES);
    TimeUnitQuantity second = TimeUnitQuantity.of(1, SECONDS);
    TimeUnitQuantity microSecond = TimeUnitQuantity.of(1, MICROSECONDS);
    TimeUnitQuantity milliSecond = TimeUnitQuantity.of(1, MILLISECONDS);
    TimeUnitQuantity nanoSecond = TimeUnitQuantity.of(1, NANOSECONDS);

    assertEquals(DAY, day.toUnit());
    assertEquals(HOUR, hour.toUnit());
    assertEquals(MINUTE, minute.toUnit());

    assertEquals(SECOND, second.toUnit());

    assertEquals(TimeQuantities.MICROSECOND, microSecond.toUnit());
    assertEquals(TimeQuantities.MILLISECOND, milliSecond.toUnit());
    assertEquals(TimeQuantities.NANOSECOND, nanoSecond.toUnit());
  }

  @Test
  public void toQuanityTest() {
    TimeUnitQuantity day = TimeUnitQuantity.of(1, DAYS);
    TimeUnitQuantity hour = TimeUnitQuantity.of(1, HOURS);
    TimeUnitQuantity minute = TimeUnitQuantity.of(1, MINUTES);
    TimeUnitQuantity second = TimeUnitQuantity.of(1, SECONDS);
    TimeUnitQuantity microSecond = TimeUnitQuantity.of(1, MICROSECONDS);
    TimeUnitQuantity milliSecond = TimeUnitQuantity.of(1, MILLISECONDS);
    TimeUnitQuantity nanoSecond = TimeUnitQuantity.of(1, NANOSECONDS);

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
    TimeUnitQuantity day = TimeUnitQuantity.of(1, DAYS);
    TimeUnitQuantity hours = day.to(HOURS);

    assertEquals(HOURS, hours.getTimeUnit());
    assertEquals(Long.valueOf(24), hours.getValue());

    TimeUnitQuantity oneDay = hours.to(DAYS);
    assertEquals(DAYS, oneDay.getTimeUnit());
    assertEquals(Long.valueOf(1), oneDay.getValue());
  }

  private static void verifyQuantity(Quantity<Time> quantity, Unit<Time> unit, Number number) {
    assertEquals(unit, quantity.getUnit());
    assertEquals(Integer.valueOf(number.intValue()), Integer.valueOf(quantity.getValue().intValue()));
  }
}
