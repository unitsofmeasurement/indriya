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
package tec.units.indriya.quantity;

import java.math.BigDecimal;

import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Time;

import org.junit.Assert;
import org.junit.Test;

import tec.units.indriya.ComparableQuantity;
import tec.units.indriya.quantity.Quantities;
import tec.units.indriya.unit.Units;

public class NumberComparableQuantityTest {

  @Test
  public void divideOperationsTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(10, Units.METRE);
    ComparableQuantity<Time> time = Quantities.getQuantity(10, Units.SECOND);
    ComparableQuantity<Speed> speed = metre.divide(time, Speed.class);

    Assert.assertEquals(Integer.valueOf(speed.getValue().intValue()), Integer.valueOf(1));
    Assert.assertEquals(Units.METRE_PER_SECOND, speed.getUnit());
  }

  @Test(expected = ClassCastException.class)
  public void divideOperationsExceptionTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(10, Units.METRE);
    ComparableQuantity<Time> time = Quantities.getQuantity(10, Units.SECOND);
    @SuppressWarnings("unused")
    ComparableQuantity<Area> area = metre.divide(time, Area.class);
  }

  @Test
  public void multiplyOperationsTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(10, Units.METRE);
    ComparableQuantity<Area> area = metre.multiply(metre, Area.class);

    Assert.assertEquals(Integer.valueOf(area.getValue().intValue()), Integer.valueOf(100));
    Assert.assertEquals(Units.SQUARE_METRE, area.getUnit());
  }

  @Test(expected = ClassCastException.class)
  public void multiplyOperationsExceptionTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(10, Units.METRE);
    @SuppressWarnings("unused")
    ComparableQuantity<Speed> speed = metre.multiply(metre, Speed.class);
  }

  @Test
  public void divideTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(10, Units.METRE);
    ComparableQuantity<Length> result = metre.divide(10D);
    Assert.assertTrue(result.getValue().intValue() == 1);
    Assert.assertEquals(result.getUnit(), Units.METRE);

    ComparableQuantity<Time> day = Quantities.getQuantity(10, Units.DAY);
    ComparableQuantity<Time> dayResult = day.divide(BigDecimal.valueOf(2.5D));
    Assert.assertTrue(dayResult.getValue().intValue() == 4);
    Assert.assertEquals(dayResult.getUnit(), Units.DAY);
  }

  @Test
  public void addTest() {
    ComparableQuantity<Length> m = Quantities.getQuantity(10, Units.METRE);
    ComparableQuantity<Length> m2 = Quantities.getQuantity(12.5F, Units.METRE);
    ComparableQuantity<Length> m3 = Quantities.getQuantity(2.5F, Units.METRE);
    ComparableQuantity<Length> m4 = Quantities.getQuantity(5L, Units.METRE);
    ComparableQuantity<Length> result = m.add(m2).add(m3).add(m4);
    Assert.assertTrue(result.getValue().doubleValue() == 30.0);
    Assert.assertEquals(result.getUnit(), Units.METRE);
  }

  @Test
  public void addQuantityTest() {
    ComparableQuantity<Time> day = Quantities.getQuantity(1, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(12, Units.HOUR);
    ComparableQuantity<Time> result = day.add(hours);
    Assert.assertTrue(result.getValue().doubleValue() == 1.5);
    Assert.assertEquals(result.getUnit(), Units.DAY);
  }

  @Test
  public void subtractTest() {
    ComparableQuantity<Length> m = Quantities.getQuantity(10, Units.METRE);
    ComparableQuantity<Length> m2 = Quantities.getQuantity(12.5, Units.METRE);
    ComparableQuantity<Length> result = m.subtract(m2);
    Assert.assertTrue(result.getValue().doubleValue() == -2.5);
    Assert.assertEquals(result.getUnit(), Units.METRE);
  }

  @Test
  public void subtractQuantityTest() {
    ComparableQuantity<Time> day = Quantities.getQuantity(1, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(12, Units.HOUR);
    ComparableQuantity<Time> result = day.subtract(hours);
    Assert.assertTrue(result.getValue().doubleValue() == 0.5);
    Assert.assertEquals(result.getUnit(), Units.DAY);
  }

  @Test
  public void multiplyTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(10, Units.METRE);
    ComparableQuantity<Length> result = metre.multiply(10D);
    Assert.assertTrue(result.getValue().intValue() == 100);
    Assert.assertEquals(result.getUnit(), Units.METRE);
    @SuppressWarnings("unchecked")
    ComparableQuantity<Length> result2 = (ComparableQuantity<Length>) metre.multiply(Quantities.getQuantity(10, Units.METRE));
    Assert.assertTrue(result2.getValue().intValue() == 100);
  }

  @Test
  public void toTest() {
    ComparableQuantity<Time> day = Quantities.getQuantity(1, Units.DAY);
    ComparableQuantity<Time> hour = day.to(Units.HOUR);
    Assert.assertEquals(hour.getValue().intValue(), 24);
    Assert.assertEquals(hour.getUnit(), Units.HOUR);

    ComparableQuantity<Time> dayResult = hour.to(Units.DAY);
    Assert.assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
    Assert.assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
  }

  @Test
  public void inverseTestLength() {
    @SuppressWarnings("unchecked")
    ComparableQuantity<Length> metre = (ComparableQuantity<Length>) Quantities.getQuantity(10, Units.METRE).inverse();
    Assert.assertEquals(Float.valueOf(0.1F), Float.valueOf(metre.getValue().floatValue()));
    Assert.assertEquals("1/m", String.valueOf(metre.getUnit()));
  }

  @Test
  public void inverseTestTime() {
    ComparableQuantity<?> secInv = Quantities.getQuantity(2, Units.SECOND).inverse();
    Assert.assertEquals(Float.valueOf(0.5F), Float.valueOf(secInv.getValue().floatValue()));
    Assert.assertEquals("1/s", String.valueOf(secInv.getUnit()));
  }

  @Test
  public void isGreaterThanTest() {
    ComparableQuantity<Time> day = Quantities.getQuantity(1, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(12, Units.HOUR);
    ComparableQuantity<Time> minutes = Quantities.getQuantity(40, Units.MINUTE);

    ComparableQuantity<Time> dayInHour = Quantities.getQuantity(24, Units.HOUR);
    ComparableQuantity<Time> dayInMinutes = Quantities.getQuantity(1440, Units.MINUTE);

    ComparableQuantity<Time> daysInHour = Quantities.getQuantity(48, Units.HOUR);

    Assert.assertTrue(day.isGreaterThan(hours));
    Assert.assertTrue(day.isGreaterThan(minutes));

    Assert.assertFalse(day.isGreaterThan(dayInHour));
    Assert.assertFalse(day.isGreaterThan(daysInHour));
    Assert.assertFalse(day.isGreaterThan(dayInMinutes));

  }

  @Test
  public void isGreaterThanOrEqualToTest() {
    ComparableQuantity<Time> day = Quantities.getQuantity(1, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(12, Units.HOUR);
    ComparableQuantity<Time> dayInHour = Quantities.getQuantity(24, Units.HOUR);
    ComparableQuantity<Time> daysInHour = Quantities.getQuantity(48, Units.HOUR);

    Assert.assertTrue(day.isGreaterThanOrEqualTo(hours));
    Assert.assertTrue(day.isGreaterThanOrEqualTo(dayInHour));
    Assert.assertFalse(day.isGreaterThanOrEqualTo(daysInHour));
  }

  @Test
  public void isLessThanTest() {
    ComparableQuantity<Time> day = Quantities.getQuantity(1, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(12, Units.HOUR);
    ComparableQuantity<Time> dayInHour = Quantities.getQuantity(24, Units.HOUR);
    ComparableQuantity<Time> daysInHour = Quantities.getQuantity(48, Units.HOUR);

    Assert.assertFalse(day.isLessThan(day));
    Assert.assertFalse(day.isLessThan(hours));
    Assert.assertFalse(day.isLessThan(dayInHour));
    Assert.assertTrue(day.isLessThan(daysInHour));
  }

  @Test
  public void isLessThanOrEqualToTest() {
    ComparableQuantity<Time> day = Quantities.getQuantity(1, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(12, Units.HOUR);
    ComparableQuantity<Time> dayInHour = Quantities.getQuantity(24, Units.HOUR);
    ComparableQuantity<Time> daysInHour = Quantities.getQuantity(48, Units.HOUR);

    Assert.assertTrue(day.isLessThanOrEqualTo(day));
    Assert.assertFalse(day.isLessThanOrEqualTo(hours));
    Assert.assertTrue(day.isLessThanOrEqualTo(dayInHour));
    Assert.assertTrue(day.isLessThanOrEqualTo(daysInHour));
  }

  @Test
  public void isEquivalentOfTest() {
    ComparableQuantity<Time> day = Quantities.getQuantity(1, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(12, Units.HOUR);
    ComparableQuantity<Time> dayInHour = Quantities.getQuantity(24, Units.HOUR);
    ComparableQuantity<Time> daysInHour = Quantities.getQuantity(48, Units.HOUR);

    Assert.assertTrue(day.isLessThanOrEqualTo(day));
    Assert.assertFalse(day.isEquivalentOf(hours));
    Assert.assertTrue(day.isEquivalentOf(dayInHour));
    Assert.assertFalse(day.isEquivalentOf(daysInHour));
  }
}
