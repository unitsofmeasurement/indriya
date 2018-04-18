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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;
import javax.measure.spi.MetricPrefix;
import tech.units.indriya.unit.Units;

public class DoubleQuantityTest {

  private final DoubleQuantity<ElectricResistance> ONE_OHM = createQuantity(1, Units.OHM);
  private final DoubleQuantity<ElectricResistance> TWO_OHM = createQuantity(2, Units.OHM);
  private final DoubleQuantity<ElectricResistance> MAX_VALUE_OHM = createQuantity(Double.MAX_VALUE, Units.OHM);
  private final DoubleQuantity<ElectricResistance> ONE_MILLIOHM = createQuantity(1, MetricPrefix.MILLI(Units.OHM));

  private <Q extends Quantity<Q>> DoubleQuantity<Q> createQuantity(double d, Unit<Q> unit) {
    return new DoubleQuantity<Q>(Double.valueOf(d).doubleValue(), unit);
  }

  /**
   * Verifies that the addition of two quantities with the same multiples results in a new quantity with the same multiple and the value holding the
   * sum.
   */
  @Test
  public void additionWithSameMultipleKeepsMultiple() {
    Quantity<ElectricResistance> actual = ONE_OHM.add(TWO_OHM);
    DoubleQuantity<ElectricResistance> expected = createQuantity(3, Units.OHM);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that adding a quantity with a larger multiple keeps the result to the smaller multiple.
   */
  @Test
  public void additionWithLargerMultipleKeepsSmallerMultiple() {
    Quantity<ElectricResistance> actual = ONE_MILLIOHM.add(ONE_OHM);
    DoubleQuantity<ElectricResistance> expected = createQuantity(1001, MetricPrefix.MILLI(Units.OHM));
    assertEquals(expected, actual);
  }

  /**
   * Verifies that adding a quantity with a smaller multiple keeps the result to the larger multiple.
   */
  @Test
  public void additionWithSmallerMultipleKeepsLargerMultiple() {
    Quantity<ElectricResistance> actual = ONE_OHM.add(ONE_MILLIOHM);
    DoubleQuantity<ElectricResistance> expected = createQuantity(1.001, Units.OHM);
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
  public void divideTest() {
    Quantity<Length> metre = Quantities.getQuantity(10D, Units.METRE);
    Quantity<Length> result = metre.divide(10D);
    assertTrue(result.getValue().intValue() == 1);
    assertEquals(result.getUnit(), Units.METRE);

    Quantity<Time> day = Quantities.getQuantity(10D, Units.DAY);
    Quantity<Time> dayResult = day.divide(BigDecimal.valueOf(2.5D));
    assertTrue(dayResult.getValue().intValue() == 4);
    assertEquals(dayResult.getUnit(), Units.DAY);
  }

  @Test
  public void multiplyTest() {
    Quantity<Length> metre = Quantities.getQuantity(10D, Units.METRE);
    Quantity<Length> result = metre.multiply(10D);
    assertTrue(result.getValue().intValue() == 100);
    assertEquals(result.getUnit(), Units.METRE);
    @SuppressWarnings("unchecked")
    Quantity<Length> result2 = (Quantity<Length>) metre.multiply(Quantities.getQuantity(10D, Units.HOUR));
    assertTrue(result2.getValue().intValue() == 100);

  }

  @Test
  public void toTest() {
    Quantity<Time> day = Quantities.getQuantity(1D, Units.DAY);
    Quantity<Time> hour = day.to(Units.HOUR);
    assertEquals(hour.getValue().intValue(), 24);
    assertEquals(hour.getUnit(), Units.HOUR);

    Quantity<Time> dayResult = hour.to(Units.DAY);
    assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
    assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
  }

  @Test
  public void inverseTestLength() {
    @SuppressWarnings("unchecked")
    Quantity<Length> metre = (Quantity<Length>) Quantities.getQuantity(10d, Units.METRE).inverse();
    assertEquals(0.1d, metre.getValue());
    assertEquals("1/m", String.valueOf(metre.getUnit()));
  }

  @Test
  public void inverseTestTime() {
    Quantity<?> secInv = Quantities.getQuantity(2d, Units.SECOND).inverse();
    assertEquals(0.5d, secInv.getValue());
    assertEquals("1/s", String.valueOf(secInv.getUnit()));
  }

  @Test
  public void testEquality() throws Exception {
    Quantity<Length> value = Quantities.getQuantity(10D, Units.METRE);
    Quantity<Length> anotherValue = Quantities.getQuantity(10.00D, Units.METRE);
    assertEquals(value, anotherValue);
  }

  @Test
  public void testEqualityFirstDouble() throws Exception {
    Quantity<Length> value = Quantities.getQuantity(new Double(10), Units.METRE);
    Quantity<Length> anotherValue = Quantities.getQuantity(new Long(10), Units.METRE);
    assertEquals(value, anotherValue);
  }

  @Test
  public void testEqualityDifferentPrimitive() throws Exception {
    Quantity<Length> value = Quantities.getQuantity(10, Units.METRE);
    Quantity<Length> anotherValue = Quantities.getQuantity(10.00D, Units.METRE);
    assertEquals(value, anotherValue);
  }
}
