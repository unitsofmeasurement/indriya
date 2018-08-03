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

import static javax.measure.MetricPrefix.MILLI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.unit.Units;

public class DecimalQuantityTest {

  private static final Unit<?> SQUARE_OHM = Units.OHM.multiply(Units.OHM);
  private final DecimalQuantity<ElectricResistance> ONE_OHM = createQuantity(1L, Units.OHM);
  private final DecimalQuantity<ElectricResistance> TWO_OHM = createQuantity(2L, Units.OHM);
  private final DecimalQuantity<ElectricResistance> ONE_MILLIOHM = createQuantity(1L, MILLI(Units.OHM));

  private <Q extends Quantity<Q>> DecimalQuantity<Q> createQuantity(long l, Unit<Q> unit) {
    return new DecimalQuantity<Q>(l, unit);
  }

  @Test
  public void divideTest() {
    Quantity<Length> metre = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    Quantity<Length> result = metre.divide(10D);
    assertTrue(result.getValue().intValue() == 1);
    assertEquals(result.getUnit(), Units.METRE);

    Quantity<Time> day = Quantities.getQuantity(BigDecimal.TEN, Units.DAY);
    Quantity<Time> dayResult = day.divide(BigDecimal.valueOf(2.5D));
    assertTrue(dayResult.getValue().intValue() == 4);
    assertEquals(dayResult.getUnit(), Units.DAY);
  }

  @Test
  public void addTest() {
    Quantity<Length> m = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    Quantity<Length> m2 = Quantities.getQuantity(BigDecimal.valueOf(12.5), Units.METRE);
    Quantity<Length> m3 = Quantities.getQuantity(2.5, Units.METRE);
    Quantity<Length> m4 = Quantities.getQuantity(5L, Units.METRE);
    Quantity<Length> result = m.add(m2).add(m3).add(m4);
    assertTrue(result.getValue().doubleValue() == 30.0);
    assertEquals(result.getUnit(), Units.METRE);
  }

  @Test
  public void addQuantityTest() {
    Quantity<Time> day = Quantities.getQuantity(BigDecimal.ONE, Units.DAY);
    Quantity<Time> hours = Quantities.getQuantity(BigDecimal.valueOf(12), Units.HOUR);
    Quantity<Time> result = day.add(hours);
    assertTrue(result.getValue().doubleValue() == 1.5);
    assertEquals(result.getUnit(), Units.DAY);
  }

  @Test
  public void subtractTest() {
    Quantity<Length> m = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    Quantity<Length> m2 = Quantities.getQuantity(12.5, Units.METRE);
    Quantity<Length> result = m.subtract(m2);
    assertTrue(result.getValue().doubleValue() == -2.5);
    assertEquals(result.getUnit(), Units.METRE);
  }

  @Test
  public void subtractQuantityTest() {
    Quantity<Time> day = Quantities.getQuantity(BigDecimal.ONE, Units.DAY);
    Quantity<Time> hours = Quantities.getQuantity(BigDecimal.valueOf(12), Units.HOUR);
    Quantity<Time> result = day.subtract(hours);
    assertTrue(result.getValue().doubleValue() == 0.5);
    assertEquals(result.getUnit(), Units.DAY);
  }

  @Test
  public void multiplyTest() {
    Quantity<Length> metre = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    Quantity<Length> result = metre.multiply(10D);
    assertTrue(result.getValue().intValue() == 100);
    assertEquals(result.getUnit(), Units.METRE);
    @SuppressWarnings("unchecked")
    Quantity<Length> result2 = (Quantity<Length>) metre.multiply(Quantities.getQuantity(BigDecimal.TEN, Units.METRE));
    assertTrue(result2.getValue().intValue() == 100);
  }

  @Test
  public void toTest() {
    Quantity<Time> day = Quantities.getQuantity(BigDecimal.ONE, Units.DAY);
    Quantity<Time> hour = day.to(Units.HOUR);
    assertEquals(hour.getValue().intValue(), 24);
    assertEquals(hour.getUnit(), Units.HOUR);

    Quantity<Time> dayResult = hour.to(Units.DAY);
    assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
    assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
  }

  /**
   * Tests negate() of DecimalQuantity.
   */
  @Test
  public void negateTest() {
    assertEquals(BigDecimal.valueOf(1.0d).negate(), ONE_OHM.negate().getValue());
  }

  @Test
  public void inverseTestLength() {
    @SuppressWarnings("unchecked")
    Quantity<Length> metre = (Quantity<Length>) Quantities.getQuantity(BigDecimal.TEN, Units.METRE).inverse();
    assertEquals(BigDecimal.valueOf(0.1d), metre.getValue());
    assertEquals("1/m", String.valueOf(metre.getUnit()));
  }

  @Test
  public void inverseTestTime() {
    Quantity<?> secInv = Quantities.getQuantity(BigDecimal.valueOf(2d), Units.SECOND).inverse();
    assertEquals(BigDecimal.valueOf(0.5d), secInv.getValue());
    assertEquals("1/s", String.valueOf(secInv.getUnit()));
  }

  /**
   * Verifies that a DecimalQuantity is big.
   */
  @Test
  public void decimalQuantityIsBig() {
    assertTrue(ONE_OHM.isBig());
  }

  /**
   * Verifies that a DecimalQuantity is decimal.
   */
  @Test
  public void decimalQuantityIsDecimal() {
    assertTrue(ONE_OHM.isDecimal());
  }

  /**
   * Verifies the getSize method for DecimalQuantity returns 0.
   */
  @Test
  public void decimalQuantityGetSizeReturnsZero() {
    assertEquals(0, ONE_OHM.getSize());
  }

  /**
   * Verifies that the value is returned without conversion if doubleValue is called with the quantity's unit.
   */
  @Test
  public void doubleValueReturnsValueForSameUnit() {
    assertEquals(1, ONE_OHM.doubleValue(Units.OHM));
  }

  /**
   * Verifies that the value is correctly converted if doubleValue is called with the quantity's unit.
   */
  @Test
  public void doubleValueReturnsConvertedValueForOtherUnit() {
    assertEquals(0.001, ONE_MILLIOHM.doubleValue(Units.OHM));
  }

  /**
   * Verifies that the value is returned without conversion if decimalValue is called with the quantity's unit.
   */
  @Test
  public void decimalValueReturnsValueForSameUnit() {
    assertEquals(BigDecimal.valueOf(1.0), ONE_OHM.decimalValue(Units.OHM));
  }

  /**
   * Verifies that the value is correctly converted to a BigDecimal if decimalValue is called with the quantity's unit.
   */
  @Test
  public void decimalValueReturnsConvertedDecimalValueForOtherUnit() {
    assertEquals(BigDecimal.valueOf(0.001), ONE_MILLIOHM.decimalValue(Units.OHM));
  }

  /**
   * Verifies that the value is correctly converted to a BigDecimal through BigInteger if decimalValue is called with the quantity's unit.
   */
  @Test
  public void decimalValueReturnsConvertedIntegerValueForOtherUnit() {
    assertEquals(BigDecimal.valueOf(1000.0), ONE_OHM.decimalValue(MILLI(Units.OHM)));
  }

  /**
   * Verifies that the value is returned without conversion if longValue is called with the quantity's unit.
   */
  @Test
  public void longValueReturnsValueForSameUnit() {
    assertEquals(1, ONE_OHM.longValue(Units.OHM));
  }

  /**
   * Verifies that the value is correctly converted if longValue is called with the quantity's unit.
   */
  @Test
  public void longValueReturnsConvertedValueForOtherUnit() {
    assertEquals(0, ONE_MILLIOHM.longValue(Units.OHM));
  }

  /**
   * Verifies that an exception is thrown if the conversion for longValue results in a positive overflow.
   */
  @Test
  public void longValueThrowsExceptionOnPositiveOverflow() {
    assertThrows(ArithmeticException.class, () -> {
      createQuantity(Long.MAX_VALUE / 10L + 117L, MetricPrefix.DEKA(Units.OHM)).longValue(Units.OHM);
    });
  }

  /**
   * Verifies that an exception is thrown if the conversion for longValue results in a negative overflow.
   */
  @Test
  public void longValueThrowsExceptionOnNegativeOverflow() {
    assertThrows(ArithmeticException.class, () -> {
      createQuantity(Long.MIN_VALUE / 10L - 117L, MetricPrefix.DEKA(Units.OHM)).longValue(Units.OHM);
    });
  }

  /**
   * Verifies that a quantity isn't equal to null.
   */
  @Test
  public void decimalQuantityIsNotEqualToNull() {
    assertFalse(ONE_OHM.equals(null));
  }

  /**
   * Verifies that a quantity is equal to itself.
   */
  @Test
  public void decimalQuantityIsEqualToItself() {
    assertTrue(ONE_OHM.equals(ONE_OHM));
  }

  /**
   * Verifies that a quantity is equal to another instance with the same value and unit.
   */
  @Test
  public void decimalQuantityIsEqualToIdenticalInstance() {
    assertTrue(ONE_OHM.equals(createQuantity(1, Units.OHM)));
  }

  /**
   * Verifies that a quantity is equal to another instance with the same value and unit using another primitive.
   */
  @Test
  public void decimalQuantityIsEqualToIdenticalInstanceWithAnotherPrimitive() {
    assertTrue(ONE_OHM.equals(new DoubleQuantity<ElectricResistance>(Double.valueOf(1).doubleValue(), Units.OHM)));
  }

  /**
   * Verifies that a quantity is not equal to a quantity with a different value.
   */
  @Test
  public void decimalQuantityIsNotEqualToQuantityWithDifferentValue() {
    assertFalse(ONE_OHM.equals(TWO_OHM));
  }

  /**
   * Verifies that a quantity is not equal to a quantity with a different unit.
   */
  @Test
  public void decimalQuantityIsNotEqualToQuantityWithDifferentUnit() {
    assertFalse(ONE_OHM.equals(ONE_MILLIOHM));
  }

  /**
   * Verifies that a quantity is not equal to an object of a different class.
   */
  @Test
  public void decimalQuantityIsNotEqualToObjectOfDifferentClass() {
    assertFalse(ONE_OHM.equals(SQUARE_OHM));
  }

}
