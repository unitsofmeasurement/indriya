/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2019, Units of Measurement project.
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
package tech.units.indriya.quantity.deprecated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.quantity.deprecated.ByteQuantity;
import tech.units.indriya.quantity.deprecated.NumberQuantity;
import tech.units.indriya.unit.Units;

public class NumberQuantityTest {

  private final NumberQuantity<Length> ONE_METRE = new NumberQuantity<>(1L, Units.METRE);

  /**
   * Verifies that if isBig is set if the constructor receives a BigInteger.
   */
  @Test
  public void instantiationWithBigIntegerSetsIsBig() {
    NumberQuantity<Length> length = new NumberQuantity<>(BigInteger.ONE, Units.METRE);
    assertTrue(length.isBig());
  }

  /**
   * Verifies that if isBig is set if the constructor receives a BigDecimal.
   */
  @Test
  public void instantiationWithBigDecimalSetsIsBig() {
    NumberQuantity<Length> length = new NumberQuantity<>(BigDecimal.ONE, Units.METRE);
    assertTrue(length.isBig());
  }

  /**
   * Verifies that the value is returned without conversion if decimalValue is called with the quantity's unit.
   */
  @Test
  public void decimalValueReturnsValueForSameUnit() {
    assertEquals(BigDecimal.valueOf(1.0), ONE_METRE.decimalValue(Units.METRE));
  }

  /**
   * Verifies that the value is correctly converted if decimalValue is called with the quantity's unit.
   */
  @Test
  public void decimalValueReturnsConvertedValueForOtherUnit() {
    assertEquals(BigDecimal.valueOf(0.001), ONE_METRE.decimalValue(MetricPrefix.KILO(Units.METRE)));
  }

  /**
   * Verifies that a NumberQuantity is equal to itself.
   */
  @Test
  public void numberQuantityIsEqualToItself() {
    assertEquals(ONE_METRE, ONE_METRE);
  }

  /**
   * Verifies that a NumberQuantity is not equal to null.
   */
  @Test
  public void numberQuantityIsNotEqualToNull() {
    assertNotEquals(ONE_METRE, null);
  }

  /**
   * Verifies that a NumberQuantity is not equal to an object of a different type.
   */
  @Test
  public void numberQuantityIsNotEqualToObjectOfDifferentType() {
    assertNotEquals(ONE_METRE, Units.METRE);
  }

  /**
   * Verifies that the factory method using a short value sets the value correctly.
   */
  @Test
  public void factoryMethodForShortSetsValueCorrectly() {
    Quantity<Length> l = NumberQuantity.of((short) 10, Units.METRE);
    assertEquals((short) 10, l.getValue());
  }

  /**
   * Verifies that the factory method using a byte value returns a ByteQuantity.
   */
  @Test
  public void factoryMethodForByteSetsValueCorrectly() {
    Quantity<Length> byteQuantity = NumberQuantity.of((byte) 10, Units.METRE);
    assertEquals(ByteQuantity.class, byteQuantity.getClass());
  }

  @Test
  public void divideTest() {
    Quantity<Length> metre = Quantities.getQuantity(10, Units.METRE);
    Quantity<Length> result = metre.divide(10D);
    assertTrue(result.getValue().intValue() == 1);
    assertEquals(result.getUnit(), Units.METRE);

    Quantity<Time> day = Quantities.getQuantity(10, Units.DAY);
    Quantity<Time> dayResult = day.divide(BigDecimal.valueOf(2.5D));
    assertTrue(dayResult.getValue().intValue() == 4);
    assertEquals(dayResult.getUnit(), Units.DAY);
  }

  @Test
  public void addTest() {
    Quantity<Length> m = Quantities.getQuantity(10, Units.METRE);
    Quantity<Length> m2 = Quantities.getQuantity(12.5F, Units.METRE);
    Quantity<Length> m3 = Quantities.getQuantity(2.5F, Units.METRE);
    Quantity<Length> m4 = Quantities.getQuantity(5L, Units.METRE);
    Quantity<Length> result = m.add(m2).add(m3).add(m4);
    assertTrue(result.getValue().doubleValue() == 30.0);
    assertEquals(Units.METRE, result.getUnit());
  }

  @Test
  public void addQuantityTest() {
    Quantity<Time> day = Quantities.getQuantity(1, Units.DAY);
    Quantity<Time> hours = Quantities.getQuantity(12, Units.HOUR);
    Quantity<Time> result = day.add(hours);
    assertEquals(1.5d, result.getValue().doubleValue());
    assertEquals(Units.DAY, result.getUnit());
  }

  @Test
  public void subtractTest() {
    Quantity<Length> m = Quantities.getQuantity(10, Units.METRE);
    Quantity<Length> m2 = Quantities.getQuantity(12.5, Units.METRE);
    Quantity<Length> result = m.subtract(m2);
    assertTrue(result.getValue().doubleValue() == -2.5);
    assertEquals(result.getUnit(), Units.METRE);
  }

  @Test
  public void subtractQuantityTest() {
    Quantity<Time> day = Quantities.getQuantity(1, Units.DAY);
    Quantity<Time> hours = Quantities.getQuantity(12, Units.HOUR);
    Quantity<Time> result = day.subtract(hours);
    assertEquals(0.5d, result.getValue().doubleValue());
    assertEquals(result.getUnit(), Units.DAY);
  }

  @Test
  public void multiplyTest() {
    Quantity<Length> metre = Quantities.getQuantity(10, Units.METRE);
    Quantity<Length> result = metre.multiply(10D);
    assertTrue(result.getValue().intValue() == 100);
    assertEquals(result.getUnit(), Units.METRE);
    @SuppressWarnings("unchecked")
    Quantity<Length> result2 = (Quantity<Length>) metre.multiply(Quantities.getQuantity(10, Units.METRE));
    assertTrue(result2.getValue().intValue() == 100);
  }

  @Test
  public void toTest() {
    Quantity<Time> day = Quantities.getQuantity(1, Units.DAY);
    Quantity<Time> hour = day.to(Units.HOUR);
    assertEquals(hour.getValue().intValue(), 24);
    assertEquals(hour.getUnit(), Units.HOUR);

    Quantity<Time> dayResult = hour.to(Units.DAY);
    assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
    assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
  }

  @Test
  public void doubleValueTest() {
    NumberQuantity<Time> day = new NumberQuantity<Time>(Double.valueOf(3), Units.DAY);
    double hours = day.doubleValue(Units.HOUR);
    assertEquals(72, hours);
  }

  @Test
  public void intValueTest() {
    NumberQuantity<Time> day = new NumberQuantity<Time>(Double.valueOf(3), Units.DAY);
    int hours = day.intValue(Units.HOUR);
    assertEquals(72, hours);
  }

  @Test
  public void inverseTestLength() {
    @SuppressWarnings("unchecked")
    Quantity<Length> metre = (Quantity<Length>) Quantities.getQuantity(10, Units.METRE).inverse();
    assertEquals(Float.valueOf(0.1F), Float.valueOf(metre.getValue().floatValue()));
    assertEquals("1/m", String.valueOf(metre.getUnit()));
  }

  @Test
  public void inverseTestTime() {
    Quantity<?> secInv = Quantities.getQuantity(2, Units.SECOND).inverse();
    assertEquals(Float.valueOf(0.5F), Float.valueOf(secInv.getValue().floatValue()));
    assertEquals("1/s", String.valueOf(secInv.getUnit()));
  }

  @Test
  public void testEqualityAtomic() throws Exception {
    Quantity<Length> value = Quantities.getQuantity(new AtomicInteger(10), Units.METRE);
    Quantity<Length> anotherValue = Quantities.getQuantity(new AtomicLong(10), Units.METRE);
    assertEquals(value, anotherValue);
  }

  @Test
  public void testEqualityBig() throws Exception {
    Quantity<Length> value = Quantities.getQuantity(BigInteger.valueOf(20), Units.METRE);
    Quantity<Length> anotherValue = Quantities.getQuantity(BigDecimal.valueOf(20), Units.METRE);
    assertEquals(value, anotherValue);
  }

  @Test
  public void testEqualityWithNull() throws Exception {
    final Quantity<Length> value = Quantities.getQuantity(BigInteger.valueOf(20), Units.METRE);
    assertThrows(NullPointerException.class, () -> {
      final Quantity<Length> anotherValue = Quantities.getQuantity(null, Units.METRE);
      assertEquals(value, anotherValue);
    });
  }

  @Test
  public void testNegateBig() throws Exception {
    final Quantity<Length> value = Quantities.getQuantity(BigInteger.valueOf(20), Units.METRE);
    assertEquals(BigInteger.valueOf(20).negate(), value.negate().getValue());
  }

  @Test
  public void testNegateDouble() throws Exception {
    final Quantity<Length> value = Quantities.getQuantity(30d, Units.METRE);
    assertEquals(-30d, value.negate().getValue());
  }
}
