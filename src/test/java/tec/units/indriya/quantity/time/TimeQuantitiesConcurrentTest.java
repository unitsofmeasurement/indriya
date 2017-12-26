/*
 * Next Generation Units of Measurement Implementation
 * Copyright (c) 2005-2017, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static tec.units.indriya.unit.Units.DAY;
import static tec.units.indriya.unit.Units.HOUR;
import static tec.units.indriya.unit.Units.MINUTE;
import static tec.units.indriya.unit.Units.SECOND;

import java.util.concurrent.TimeUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Time;

import org.junit.Assert;
import org.junit.Test;

import tec.units.indriya.quantity.Quantities;
import tec.units.indriya.quantity.time.TimeQuantities;
import tec.units.indriya.quantity.time.TimeUnitQuantity;
import tec.units.indriya.unit.Units;

public class TimeQuantitiesConcurrentTest {

  @Test
  public void ofTest() {

    TimeUnitQuantity day = TimeUnitQuantity.of(DAYS, 1);
    TimeUnitQuantity hour = TimeUnitQuantity.of(HOURS, 1);
    TimeUnitQuantity minute = TimeUnitQuantity.of(MINUTES, 1);
    TimeUnitQuantity second = TimeUnitQuantity.of(SECONDS, 1);
    TimeUnitQuantity microSecond = TimeUnitQuantity.of(MICROSECONDS, 1);
    TimeUnitQuantity milliSecond = TimeUnitQuantity.of(MILLISECONDS, 1);
    TimeUnitQuantity nanoSecond = TimeUnitQuantity.of(NANOSECONDS, 1);

    Assert.assertEquals(DAYS, day.getTimeUnit());
    Assert.assertEquals(Integer.valueOf(1), day.getValue());

    Assert.assertEquals(HOURS, hour.getTimeUnit());
    Assert.assertEquals(Integer.valueOf(1), hour.getValue());

    Assert.assertEquals(MINUTES, minute.getTimeUnit());
    Assert.assertEquals(Integer.valueOf(1), minute.getValue());

    Assert.assertEquals(SECONDS, second.getTimeUnit());
    Assert.assertEquals(Integer.valueOf(1), second.getValue());

    Assert.assertEquals(MICROSECONDS, microSecond.getTimeUnit());
    Assert.assertEquals(Integer.valueOf(1), microSecond.getValue());

    Assert.assertEquals(MILLISECONDS, milliSecond.getTimeUnit());
    Assert.assertEquals(Integer.valueOf(1), milliSecond.getValue());

    Assert.assertEquals(NANOSECONDS, nanoSecond.getTimeUnit());
    Assert.assertEquals(Integer.valueOf(1), nanoSecond.getValue());
  }

  @Test
  public void ofQuantityTest() {
    Quantity<Time> hour = Quantities.getQuantity(1, Units.HOUR);
    TimeUnitQuantity timeQuantity = TimeUnitQuantity.of(hour);

    Assert.assertEquals(TimeUnit.SECONDS, timeQuantity.getTimeUnit());
    Assert.assertEquals(SECOND, timeQuantity.toUnit());
    Assert.assertEquals(Integer.valueOf(3600), timeQuantity.getValue());
  }

  @Test
  public void toUnitTest() {

    TimeUnitQuantity day = TimeUnitQuantity.of(DAYS, 1);
    TimeUnitQuantity hour = TimeUnitQuantity.of(HOURS, 1);
    TimeUnitQuantity minute = TimeUnitQuantity.of(MINUTES, 1);
    TimeUnitQuantity second = TimeUnitQuantity.of(SECONDS, 1);
    TimeUnitQuantity microSecond = TimeUnitQuantity.of(MICROSECONDS, 1);
    TimeUnitQuantity milliSecond = TimeUnitQuantity.of(MILLISECONDS, 1);
    TimeUnitQuantity nanoSecond = TimeUnitQuantity.of(NANOSECONDS, 1);

    Assert.assertEquals(DAY, day.toUnit());
    Assert.assertEquals(HOUR, hour.toUnit());
    Assert.assertEquals(MINUTE, minute.toUnit());

    Assert.assertEquals(SECOND, second.toUnit());

    Assert.assertEquals(TimeQuantities.MICROSECOND, microSecond.toUnit());
    Assert.assertEquals(TimeQuantities.MILLISECOND, milliSecond.toUnit());
    Assert.assertEquals(TimeQuantities.NANOSECOND, nanoSecond.toUnit());
  }

  @Test
  public void toQuanityTest() {
    TimeUnitQuantity day = TimeUnitQuantity.of(DAYS, 1);
    TimeUnitQuantity hour = TimeUnitQuantity.of(HOURS, 1);
    TimeUnitQuantity minute = TimeUnitQuantity.of(MINUTES, 1);
    TimeUnitQuantity second = TimeUnitQuantity.of(SECONDS, 1);
    TimeUnitQuantity microSecond = TimeUnitQuantity.of(MICROSECONDS, 1);
    TimeUnitQuantity milliSecond = TimeUnitQuantity.of(MILLISECONDS, 1);
    TimeUnitQuantity nanoSecond = TimeUnitQuantity.of(NANOSECONDS, 1);

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
    TimeUnitQuantity day = TimeUnitQuantity.of(DAYS, 1);
    TimeUnitQuantity hours = day.to(TimeUnit.HOURS);

    Assert.assertEquals(TimeUnit.HOURS, hours.getTimeUnit());
    Assert.assertEquals(Integer.valueOf(24), hours.getValue());

    TimeUnitQuantity oneDay = hours.to(TimeUnit.DAYS);
    Assert.assertEquals(TimeUnit.DAYS, oneDay.getTimeUnit());
    Assert.assertEquals(Integer.valueOf(1), oneDay.getValue());
  }

  private void verifyQuantity(Quantity<Time> quantity, Unit<Time> unit, Number number) {
    Assert.assertEquals(unit, quantity.getUnit());
    Assert.assertEquals(Integer.valueOf(number.intValue()), Integer.valueOf(quantity.getValue().intValue()));
  }
}
