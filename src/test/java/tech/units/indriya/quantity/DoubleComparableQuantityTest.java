/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2021, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
package tech.units.indriya.quantity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.NumberAssertions.assertNumberEquals;

import java.math.BigDecimal;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.unit.Units;

public class DoubleComparableQuantityTest {

  @Test
  public void divideOperationsTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(10D, Units.METRE);
    ComparableQuantity<Time> time = Quantities.getQuantity(10D, Units.SECOND);
    ComparableQuantity<Speed> speed = metre.divide(time, Speed.class);

    assertEquals(Integer.valueOf(speed.getValue().intValue()), Integer.valueOf(1));
    assertEquals(Units.METRE_PER_SECOND, speed.getUnit());
  }

  @Test
  public void divideOperationsExceptionTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(10D, Units.METRE);
    ComparableQuantity<Time> time = Quantities.getQuantity(10D, Units.SECOND);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      ComparableQuantity<Area> area = metre.divide(time, Area.class);
    });
  }

  @Test
  public void multiplyOperationsTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(10D, Units.METRE);
    ComparableQuantity<Area> area = metre.multiply(metre, Area.class);

    assertEquals(Integer.valueOf(area.getValue().intValue()), Integer.valueOf(100));
    assertEquals(Units.SQUARE_METRE, area.getUnit());
  }

  @Test
  public void multiplyOperationsExceptionTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(10D, Units.METRE);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      ComparableQuantity<Speed> speed = metre.multiply(metre, Speed.class);
    });
  }

  @Test
  public void divideTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(10D, Units.METRE);
    ComparableQuantity<Length> result = metre.divide(10D);
    assertTrue(result.getValue().intValue() == 1);
    assertEquals(result.getUnit(), Units.METRE);

    ComparableQuantity<Time> day = Quantities.getQuantity(10D, Units.DAY);
    ComparableQuantity<Time> dayResult = day.divide(BigDecimal.valueOf(2.5D));
    assertTrue(dayResult.getValue().intValue() == 4);
    assertEquals(dayResult.getUnit(), Units.DAY);
  }

  @Test
  public void addTest() {
    ComparableQuantity<Length> m = Quantities.getQuantity(10D, Units.METRE);
    ComparableQuantity<Length> m2 = Quantities.getQuantity(BigDecimal.valueOf(12.5), Units.METRE);
    ComparableQuantity<Length> m3 = Quantities.getQuantity(2.5, Units.METRE);
    ComparableQuantity<Length> m4 = Quantities.getQuantity(5L, Units.METRE);
    ComparableQuantity<Length> result = m.add(m2).add(m3).add(m4);
    assertTrue(result.getValue().doubleValue() == 30.0);
    assertEquals(result.getUnit(), Units.METRE);
  }

  @Test
  public void addQuantityTest() {
    ComparableQuantity<Time> day = Quantities.getQuantity(1, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(12D, Units.HOUR);
    ComparableQuantity<Time> result = day.add(hours);
    assertTrue(result.getValue().doubleValue() == 1.5);
    assertEquals(result.getUnit(), Units.DAY);
  }

  @Test
  public void subtractTest() {
    ComparableQuantity<Length> m = Quantities.getQuantity(10D, Units.METRE);
    ComparableQuantity<Length> m2 = Quantities.getQuantity(12.5, Units.METRE);
    ComparableQuantity<Length> result = m.subtract(m2);
    assertTrue(result.getValue().doubleValue() == -2.5);
    assertEquals(result.getUnit(), Units.METRE);
  }

  @Test
  public void subtractQuantityTest() {
    ComparableQuantity<Time> day = Quantities.getQuantity(1, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(12F, Units.HOUR);
    ComparableQuantity<Time> result = day.subtract(hours);
    assertTrue(result.getValue().doubleValue() == 0.5);
    assertEquals(result.getUnit(), Units.DAY);
  }

  @Test
  public void multiplyTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(10D, Units.METRE);
    ComparableQuantity<Length> result = metre.multiply(10D);
    assertEquals(100, result.getValue().intValue());
    assertEquals(result.getUnit(), Units.METRE);
    @SuppressWarnings("unchecked")
    ComparableQuantity<Length> result2 = (ComparableQuantity<Length>) metre.multiply(Quantities.getQuantity(10D, Units.HOUR));
    assertEquals("100 m·h", result2.toString()); 
  }

  @Test
  public void toTest() {
    ComparableQuantity<Time> day = Quantities.getQuantity(1D, Units.DAY);
    ComparableQuantity<Time> hour = day.to(Units.HOUR);
    assertEquals(hour.getValue().intValue(), 24);
    assertEquals(hour.getUnit(), Units.HOUR);

    ComparableQuantity<Time> dayResult = hour.to(Units.DAY);
    assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
    assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
  }

  @Test
  public void inverseTestLength() {
    Quantity<?> perMetre = Quantities.getQuantity(10d, Units.METRE).inverse();
    assertTrue(perMetre instanceof ComparableQuantity);
    assertNumberEquals(0.1d, perMetre.getValue(), 1E-12);
    assertEquals("1/m", String.valueOf(perMetre.getUnit()));
  }

  @Test
  public void inverseTestTime() {
    Quantity<?> perSec = Quantities.getQuantity(2d, Units.SECOND).inverse();
    assertTrue(perSec instanceof ComparableQuantity);
    assertNumberEquals(0.5d, perSec.getValue(), 1E-12);
    assertEquals("1/s", String.valueOf(perSec.getUnit()));
  }

  @Test
  public void isGreaterThanTest() {
    ComparableQuantity<Time> day = Quantities.getQuantity(1D, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(12D, Units.HOUR);
    ComparableQuantity<Time> dayInHour = Quantities.getQuantity(24D, Units.HOUR);
    ComparableQuantity<Time> daysInHour = Quantities.getQuantity(46D, Units.HOUR);

    assertFalse(day.isGreaterThan(day));
    assertTrue(day.isGreaterThan(hours));
    assertFalse(day.isGreaterThan(dayInHour));
    assertFalse(day.isGreaterThan(daysInHour));
  }

  @Test
  public void isGreaterThanOrEqualToTest() {

    ComparableQuantity<Time> day = Quantities.getQuantity(1D, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(12D, Units.HOUR);
    ComparableQuantity<Time> dayInHour = Quantities.getQuantity(24D, Units.HOUR);
    ComparableQuantity<Time> daysInHour = Quantities.getQuantity(46D, Units.HOUR);

    assertTrue(day.isLessThanOrEqualTo(day));
    assertTrue(day.isGreaterThanOrEqualTo(hours));
    assertTrue(day.isGreaterThanOrEqualTo(dayInHour));
    assertFalse(day.isGreaterThanOrEqualTo(daysInHour));
  }

  @Test
  public void isLessThanTest() {

    ComparableQuantity<Time> day = Quantities.getQuantity(1D, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(12D, Units.HOUR);
    ComparableQuantity<Time> dayInHour = Quantities.getQuantity(24D, Units.HOUR);
    ComparableQuantity<Time> daysInHour = Quantities.getQuantity(46D, Units.HOUR);

    assertFalse(day.isLessThan(day));
    assertFalse(day.isLessThan(hours));
    assertFalse(day.isLessThan(dayInHour));
    assertTrue(day.isLessThan(daysInHour));
  }

  @Test
  public void isLessThanOrEqualToTest() {

    ComparableQuantity<Time> day = Quantities.getQuantity(1D, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(12D, Units.HOUR);
    ComparableQuantity<Time> dayInHour = Quantities.getQuantity(24D, Units.HOUR);
    ComparableQuantity<Time> daysInHour = Quantities.getQuantity(46D, Units.HOUR);

    assertTrue(day.isLessThanOrEqualTo(day));
    assertFalse(day.isLessThanOrEqualTo(hours));
    assertTrue(day.isLessThanOrEqualTo(dayInHour));
    assertTrue(day.isLessThanOrEqualTo(daysInHour));

  }

  @Test
  public void isEquivalentOfTest() {

    ComparableQuantity<Time> day = Quantities.getQuantity(1D, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(12D, Units.HOUR);
    ComparableQuantity<Time> dayInHour = Quantities.getQuantity(24D, Units.HOUR);
    ComparableQuantity<Time> daysInHour = Quantities.getQuantity(46D, Units.HOUR);

    assertTrue(day.isEquivalentTo(day));
    assertFalse(day.isEquivalentTo(hours));
    assertTrue(day.isEquivalentTo(dayInHour));
    assertFalse(day.isEquivalentTo(daysInHour));
  }
}
