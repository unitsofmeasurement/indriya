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
import tech.units.indriya.function.RationalNumber;
import tech.units.indriya.unit.Units;

/**
 * Test for Decimal comparable quantities 
 * @author Werner
 *
 */
public class DecimalComparableQuantityTest {

  @Test
  public void divideOperationsTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    ComparableQuantity<Time> time = Quantities.getQuantity(BigDecimal.TEN, Units.SECOND);
    ComparableQuantity<Speed> speed = metre.divide(time, Speed.class);

    assertEquals(Integer.valueOf(speed.getValue().intValue()), Integer.valueOf(1));
    assertEquals(Units.METRE_PER_SECOND, speed.getUnit());
  }

  @Test
  public void divideOperationsExceptionTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    ComparableQuantity<Time> time = Quantities.getQuantity(BigDecimal.TEN, Units.SECOND);
    assertThrows(ClassCastException.class, () -> {
    	@SuppressWarnings("unused")
    	ComparableQuantity<Area> area = metre.divide(time, Area.class);
    });
  }

  @Test
  public void multiplyOperationsTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    ComparableQuantity<Area> area = metre.multiply(metre, Area.class);

    assertEquals(Integer.valueOf(area.getValue().intValue()), Integer.valueOf(100));
    assertEquals(Units.SQUARE_METRE, area.getUnit());
  }

  @Test
  public void multiplyOperationsExceptionTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    assertThrows(ClassCastException.class, () -> {
    	@SuppressWarnings("unused")
    	ComparableQuantity<Speed> speed = metre.multiply(metre, Speed.class);
    });
  }

  @Test
  public void multiplyOperationsException2Test() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    @SuppressWarnings("unused")
    Quantity<Area> speed = metre.multiply(metre).asType(Area.class);
  }

  @Test
  public void divideTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    ComparableQuantity<Length> result = metre.divide(10D);
    assertTrue(result.getValue().intValue() == 1);
    assertEquals(result.getUnit(), Units.METRE);

    ComparableQuantity<Time> day = Quantities.getQuantity(BigDecimal.TEN, Units.DAY);
    ComparableQuantity<Time> dayResult = day.divide(BigDecimal.valueOf(2.5D));
    assertTrue(dayResult.getValue().intValue() == 4);
    assertEquals(dayResult.getUnit(), Units.DAY);
  }

  @Test
  public void addTest() {
    ComparableQuantity<Length> m = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    ComparableQuantity<Length> m2 = Quantities.getQuantity(BigDecimal.valueOf(12.5), Units.METRE);
    ComparableQuantity<Length> m3 = Quantities.getQuantity(2.5, Units.METRE);
    ComparableQuantity<Length> m4 = Quantities.getQuantity(5L, Units.METRE);
    ComparableQuantity<Length> result = m.add(m2).add(m3).add(m4);
    assertTrue(result.getValue().doubleValue() == 30.0);
    assertEquals(result.getUnit(), Units.METRE);
  }

  @Test
  public void addQuantityTest() {
    ComparableQuantity<Time> day = Quantities.getQuantity(BigDecimal.ONE, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(BigDecimal.valueOf(12), Units.HOUR);
    ComparableQuantity<Time> result = day.add(hours);
    assertTrue(result.getValue().doubleValue() == 1.5);
    assertEquals(result.getUnit(), Units.DAY);
  }

  @Test
  public void subtractTest() {
    ComparableQuantity<Length> m = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    ComparableQuantity<Length> m2 = Quantities.getQuantity(12.5, Units.METRE);
    ComparableQuantity<Length> result = m.subtract(m2);
    assertTrue(result.getValue().doubleValue() == -2.5);
    assertEquals(result.getUnit(), Units.METRE);
  }

  @Test
  public void subtractQuantityTest() {
    ComparableQuantity<Time> day = Quantities.getQuantity(BigDecimal.ONE, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(BigDecimal.valueOf(12), Units.HOUR);
    ComparableQuantity<Time> result = day.subtract(hours);
    assertTrue(result.getValue().doubleValue() == 0.5);
    assertEquals(result.getUnit(), Units.DAY);
  }

  @Test
  public void multiplyTest() {
    ComparableQuantity<Length> metre = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    ComparableQuantity<Length> result = metre.multiply(10D);
    assertTrue(result.getValue().intValue() == 100);
    assertEquals(result.getUnit(), Units.METRE);
    @SuppressWarnings("unchecked")
    ComparableQuantity<Length> result2 = (ComparableQuantity<Length>) metre.multiply(Quantities.getQuantity(BigDecimal.TEN, Units.METRE));
    assertTrue(result2.getValue().intValue() == 100);
  }

  @Test
  public void toTest() {
    ComparableQuantity<Time> day = Quantities.getQuantity(BigDecimal.ONE, Units.DAY);
    ComparableQuantity<Time> hour = day.to(Units.HOUR);
    assertEquals(hour.getValue().intValue(), 24);
    assertEquals(hour.getUnit(), Units.HOUR);

    ComparableQuantity<Time> dayResult = hour.to(Units.DAY);
    assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
    assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
  }

  @Test
  public void inverseTestLength() {
    @SuppressWarnings("unchecked")
    ComparableQuantity<Length> metre = (ComparableQuantity<Length>) Quantities.getQuantity(BigDecimal.TEN, Units.METRE).inverse();
    assertNumberEquals(RationalNumber.of(1,  10), metre.getValue(), 1E-12);
    assertEquals("1/m", String.valueOf(metre.getUnit()));
  }

  @Test
  public void inverseTestTime() {
    ComparableQuantity<?> secInv = Quantities.getQuantity(BigDecimal.valueOf(2d), Units.SECOND).inverse();
    assertNumberEquals(RationalNumber.of(1,  2), secInv.getValue(), 1E-12);
    assertEquals("1/s", String.valueOf(secInv.getUnit()));
  }

  @Test
  public void isGreaterThanTest() {
    ComparableQuantity<Time> day = Quantities.getQuantity(BigDecimal.ONE, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(BigDecimal.valueOf(12), Units.HOUR);
    ComparableQuantity<Time> minutes = Quantities.getQuantity(BigDecimal.valueOf(40), Units.MINUTE);

    ComparableQuantity<Time> dayInHour = Quantities.getQuantity(BigDecimal.valueOf(24), Units.HOUR);
    ComparableQuantity<Time> dayInMinutes = Quantities.getQuantity(BigDecimal.valueOf(1440), Units.MINUTE);

    ComparableQuantity<Time> daysInHour = Quantities.getQuantity(BigDecimal.valueOf(48), Units.HOUR);

    assertTrue(day.isGreaterThan(hours));
    assertTrue(day.isGreaterThan(minutes));

    assertFalse(day.isGreaterThan(dayInHour));
    assertFalse(day.isGreaterThan(daysInHour));
    assertFalse(day.isGreaterThan(dayInMinutes));

  }

  @Test
  public void isGreaterThanOrEqualToTest() {

    ComparableQuantity<Time> day = Quantities.getQuantity(BigDecimal.ONE, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(BigDecimal.valueOf(12), Units.HOUR);
    ComparableQuantity<Time> dayInHour = Quantities.getQuantity(BigDecimal.valueOf(24), Units.HOUR);
    ComparableQuantity<Time> daysInHour = Quantities.getQuantity(BigDecimal.valueOf(48), Units.HOUR);

    assertTrue(day.isGreaterThanOrEqualTo(hours));
    assertTrue(day.isGreaterThanOrEqualTo(dayInHour));
    assertFalse(day.isGreaterThanOrEqualTo(daysInHour));
  }

  @Test
  public void isLessThanTest() {

    ComparableQuantity<Time> day = Quantities.getQuantity(BigDecimal.ONE, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(BigDecimal.valueOf(12), Units.HOUR);
    ComparableQuantity<Time> dayInHour = Quantities.getQuantity(BigDecimal.valueOf(24), Units.HOUR);
    ComparableQuantity<Time> daysInHour = Quantities.getQuantity(BigDecimal.valueOf(48), Units.HOUR);

    assertFalse(day.isLessThan(day));
    assertFalse(day.isLessThan(hours));
    assertFalse(day.isLessThan(dayInHour));
    assertTrue(day.isLessThan(daysInHour));
  }

  @Test
  public void isLessThanOrEqualToTest() {

    ComparableQuantity<Time> day = Quantities.getQuantity(BigDecimal.ONE, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(BigDecimal.valueOf(12), Units.HOUR);
    ComparableQuantity<Time> dayInHour = Quantities.getQuantity(BigDecimal.valueOf(24), Units.HOUR);
    ComparableQuantity<Time> daysInHour = Quantities.getQuantity(BigDecimal.valueOf(48), Units.HOUR);

    assertTrue(day.isLessThanOrEqualTo(day));
    assertFalse(day.isLessThanOrEqualTo(hours));
    assertTrue(day.isLessThanOrEqualTo(dayInHour));
    assertTrue(day.isLessThanOrEqualTo(daysInHour));

  }

  @Test
  public void isEquivalentTest() {

    ComparableQuantity<Time> day = Quantities.getQuantity(BigDecimal.ONE, Units.DAY);
    ComparableQuantity<Time> hours = Quantities.getQuantity(BigDecimal.valueOf(12), Units.HOUR);
    ComparableQuantity<Time> dayInHour = Quantities.getQuantity(BigDecimal.valueOf(24), Units.HOUR);
    ComparableQuantity<Time> daysInHour = Quantities.getQuantity(BigDecimal.valueOf(48), Units.HOUR);

    assertTrue(day.isLessThanOrEqualTo(day));
    assertFalse(day.isEquivalentTo(hours));
    assertTrue(day.isEquivalentTo(dayInHour));
    assertFalse(day.isEquivalentTo(daysInHour));
  }
}
