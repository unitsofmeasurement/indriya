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
package tech.units.indriya.quantity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.quantity.FloatQuantity;
import tech.units.indriya.quantity.NumberQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.MetricPrefix;
import tech.units.indriya.unit.Units;

public class FloatQuantityTest {

  private final FloatQuantity<ElectricResistance> ONE_OHM = createQuantity(1, Units.OHM);
  private final FloatQuantity<ElectricResistance> TWO_OHM = createQuantity(2, Units.OHM);
  private final FloatQuantity<ElectricResistance> MAX_VALUE_OHM = createQuantity(Float.MAX_VALUE, Units.OHM);
  private final FloatQuantity<ElectricResistance> ONE_MILLIOHM = createQuantity(1, MetricPrefix.MILLI(Units.OHM));

  private <Q extends Quantity<Q>> FloatQuantity<Q> createQuantity(float f, Unit<Q> unit) {
    return new FloatQuantity<Q>(Float.valueOf(f).floatValue(), unit);
  }

  @Test
  public void divideTest() {
    FloatQuantity<ElectricResistance> quantity1 = new FloatQuantity<ElectricResistance>(Float.valueOf(3).floatValue(), Units.OHM);
    FloatQuantity<ElectricResistance> quantity2 = new FloatQuantity<ElectricResistance>(Float.valueOf(2).floatValue(), Units.OHM);
    Quantity<?> result = quantity1.divide(quantity2);
    assertEquals(Float.valueOf(1.5f), result.getValue());
  }

  /**
   * Verifies that the addition of two quantities with the same multiples results in a new quantity with the same multiple and the value holding the
   * sum.
   */
  @Test
  public void additionWithSameMultipleKeepsMultiple() {
    Quantity<ElectricResistance> actual = ONE_OHM.add(TWO_OHM);
    FloatQuantity<ElectricResistance> expected = createQuantity(3, Units.OHM);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that adding a quantity with a larger multiple keeps the result to the smaller multiple.
   */
  @Test
  public void additionWithLargerMultipleKeepsSmallerMultiple() {
    Quantity<ElectricResistance> actual = ONE_MILLIOHM.add(ONE_OHM);
    FloatQuantity<ElectricResistance> expected = createQuantity(1001, MetricPrefix.MILLI(Units.OHM));
    assertEquals(expected, actual);
  }

  /**
   * Verifies that adding a quantity with a smaller multiple keeps the result to the larger multiple.
   */
  @Test
  public void additionWithSmallerMultipleKeepsLargerMultiple() {
    Quantity<ElectricResistance> actual = ONE_OHM.add(ONE_MILLIOHM);
    FloatQuantity<ElectricResistance> expected = createQuantity(1.001f, Units.OHM);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the addition of two quantities with the same multiples resulting in an overflow throws an exception.
   */
  @Test
  public void additionWithSameMultipleResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      MAX_VALUE_OHM.add(MAX_VALUE_OHM);
    });
  }

  /**
   * Verifies that adding a quantity with a larger overflowing multiple casts the result to the larger multiple.
   */
  @Test
  public void additionWithLargerOverflowingMultipleCastsToLargerMultiple() {
    Quantity<ElectricResistance> actual = ONE_MILLIOHM.add(MAX_VALUE_OHM);
    assertEquals(MAX_VALUE_OHM, actual);
  }

  /**
   * Verifies that subtraction subtracts the argument from the target object.
   */
  @Test
  public void subtractionSubtractsArgumentFromTargetObject() {
    Quantity<ElectricResistance> actual = TWO_OHM.subtract(ONE_OHM);
    assertEquals(ONE_OHM, actual);
  }

  @Test
  public void multiplyQuantityTest() {
    FloatQuantity<ElectricResistance> quantity1 = new FloatQuantity<ElectricResistance>(Float.valueOf(3).floatValue(), Units.OHM);
    FloatQuantity<ElectricResistance> quantity2 = new FloatQuantity<ElectricResistance>(Float.valueOf(2).floatValue(), Units.OHM);
    Quantity<?> result = quantity1.multiply(quantity2);
    assertEquals(Float.valueOf(6L), result.getValue());
  }

  @Test
  public void longValueTest() {
    FloatQuantity<Time> day = new FloatQuantity<Time>(3F, Units.DAY);
    long hours = day.longValue(Units.HOUR);
    assertEquals(72L, hours);
  }

  @Test
  public void doubleValueTest() {
    FloatQuantity<Time> day = new FloatQuantity<Time>(3F, Units.DAY);
    double hours = day.doubleValue(Units.HOUR);
    assertEquals(72D, hours);
  }

  @Test
  public void toTest() {
    Quantity<Time> day = Quantities.getQuantity(1D, Units.DAY);
    Quantity<Time> hour = day.to(Units.HOUR);
    assertEquals(Double.valueOf(24), hour.getValue());
    assertEquals(hour.getUnit(), Units.HOUR);

    Quantity<Time> dayResult = hour.to(Units.DAY);
    assertEquals(dayResult.getValue(), day.getValue());
    assertEquals(dayResult.getUnit(), day.getUnit());
  }

  @Test
  public void inverseTest() {
    AbstractQuantity<Length> l = NumberQuantity.of(Float.valueOf(10f).floatValue(), Units.METRE);
    assertEquals(Float.valueOf(1f / 10f), l.inverse().getValue());
  }

  @Test
  public void testEquality() throws Exception {
    Quantity<Length> value = Quantities.getQuantity(new Float(10), Units.METRE);
    Quantity<Length> anotherValue = Quantities.getQuantity(new Float(10.0F), Units.METRE);
    assertEquals(value, anotherValue);
  }
}
