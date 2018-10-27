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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.quantity.FloatQuantity;
import tech.units.indriya.quantity.Quantities;
import static javax.measure.MetricPrefix.*;
import tech.units.indriya.unit.Units;

public class FloatQuantityTest {

  private static final Unit<?> SQUARE_OHM = Units.OHM.multiply(Units.OHM);
  private static final FloatQuantity<ElectricResistance> HALF_AN_OHM = createQuantity(0.5F, Units.OHM);
  private static final FloatQuantity<ElectricResistance> ONE_OHM = createQuantity(1, Units.OHM);
  private static final FloatQuantity<ElectricResistance> TWO_OHM = createQuantity(2, Units.OHM);
  private static final FloatQuantity<ElectricResistance> MAX_VALUE_OHM = createQuantity(Float.MAX_VALUE, Units.OHM);
  private static final FloatQuantity<ElectricResistance> JUST_OVER_HALF_MAX_VALUE_OHM = createQuantity(Float.MAX_VALUE / 1.9999999F, Units.OHM);
  private static final FloatQuantity<ElectricResistance> ONE_MILLIOHM = createQuantity(1, MILLI(Units.OHM));
  private static final DoubleQuantity<ElectricResistance> ONE_DOUBLE_OHM = new DoubleQuantity<ElectricResistance>(1D, Units.OHM);

  private static <Q extends Quantity<Q>> FloatQuantity<Q> createQuantity(float f, Unit<Q> unit) {
    return new FloatQuantity<Q>(Float.valueOf(f).floatValue(), unit);
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
    assertEquals(BigDecimal.valueOf(1), ONE_OHM.decimalValue(Units.OHM));
  }

  /**
   * Verifies that the value is correctly converted if decimalValue is called with the quantity's unit.
   */
  @Test
  public void decimalValueReturnsConvertedValueForOtherUnit() {
    assertEquals(BigDecimal.valueOf(0.001), ONE_MILLIOHM.decimalValue(Units.OHM));
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
      createQuantity(Long.MAX_VALUE + 550000000000.0F, Units.OHM).longValue(Units.OHM);
    });
  }

  /**
   * Verifies that an exception is thrown if the conversion for longValue results in a negative overflow.
   */
  @Test
  public void longValueThrowsExceptionOnNegativeOverflow() {
    assertThrows(ArithmeticException.class, () -> {
      createQuantity(Long.MIN_VALUE - 550000000000.0F, Units.OHM).longValue(Units.OHM);
    });
  }

  /**
   * Verifies that the multiplication of two quantities multiplies correctly.
   */
  @Test
  public void quantityMultiplicationMultipliesCorrectly() {
    Quantity<?> actual = TWO_OHM.multiply(TWO_OHM);
    FloatQuantity<?> expected = createQuantity(4, SQUARE_OHM);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the multiplication of two quantities resulting in an overflow throws an exception.
   */
  @Test
  public void quantityMultiplicationResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      JUST_OVER_HALF_MAX_VALUE_OHM.multiply(TWO_OHM);
    });
  }

  /**
   * Verifies that the multiplication with a number multiplies correctly.
   */
  @Test
  public void numberMultiplicationMultipliesCorrectly() {
    Quantity<?> actual = TWO_OHM.multiply(2);
    FloatQuantity<ElectricResistance> expected = createQuantity(4, Units.OHM);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the multiplication with a number resulting in an overflow throws an exception.
   */
  @Test
  public void numberMultiplicationResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      JUST_OVER_HALF_MAX_VALUE_OHM.multiply(2F);
    });
  }

  /**
   * Verifies that the division of two quantities divides correctly.
   */
  @Test
  public void quantityDivisionDividesCorrectly() {
    Quantity<?> actual = TWO_OHM.divide(TWO_OHM);
    FloatQuantity<Dimensionless> expected = createQuantity(1, AbstractUnit.ONE);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the division of two quantities resulting in an overflow throws an exception.
   */
  @Test
  public void quantityDivisionResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      JUST_OVER_HALF_MAX_VALUE_OHM.divide(HALF_AN_OHM);
    });
  }

  /**
   * Verifies that the division with a number divides correctly.
   */
  @Test
  public void numberDivisionDividesCorrectly() {
    Quantity<?> actual = TWO_OHM.divide(2);
    assertEquals(ONE_OHM, actual);
  }

  /**
   * Verifies that the division with a number resulting in an overflow throws an exception.
   */
  @Test
  public void numberDivisionResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      JUST_OVER_HALF_MAX_VALUE_OHM.divide(0.5F);
    });
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

  /**
   * Tests negate()
   */
  @Test
  public void negateTest() {
    assertEquals(-1f, ONE_OHM.negate().getValue());
  }

  /**
   * Verifies that the inverse returns the correct reciprocal for a unit quantity.
   */
  @Test
  public void inverseReturnsUnitQuantityForUnitQuantity() {
    Quantity<?> actual = ONE_OHM.inverse();
    FloatQuantity<?> expected = createQuantity(1, Units.OHM.inverse());
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the inverse returns the correct reciprocal for a quantity larger than a unit quantity.
   */
  @Test
  public void inverseReturnsZeroQuantityForLargerThanUnitQuantity() {
    Quantity<?> actual = TWO_OHM.inverse();
    FloatQuantity<?> expected = createQuantity(0.5F, Units.OHM.inverse());
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the inverse throws an exception for a zero quantity.
   */
  @Test
  public void inverseReturnsInfinityQuantityForZeroQuantity() {
    Quantity<?> actual = createQuantity(0, Units.OHM).inverse();
    FloatQuantity<?> expected = createQuantity(Float.POSITIVE_INFINITY, Units.OHM.inverse());
    assertEquals(expected, actual);
  }

  /**
   * Verifies that a FloatQuantity isn't big.
   */
  @Test
  public void floatQuantityIsNotBig() {
    assertFalse(ONE_OHM.isBig());
  }

  /**
   * Verifies that a FloatQuantity is decimal.
   */
  @Test
  public void floatQuantityIsDecimal() {
    assertTrue(ONE_OHM.isDecimal());
  }

  /**
   * Verifies that a FloatQuantity has the size of Float.
   */
  @Test
  public void floatQuantityHasByteSize() {
    assertEquals(Float.SIZE, ONE_OHM.getSize());
  }

  /**
   * Verifies that a quantity isn't equal to null.
   */
  @Test
  public void floatQuantityIsNotEqualToNull() {
    assertFalse(ONE_OHM.equals(null));
  }

  /**
   * Verifies that a quantity is equal to itself.
   */
  @Test
  public void floatQuantityIsEqualToItself() {
    assertTrue(ONE_OHM.equals(ONE_OHM));
  }

  /**
   * Verifies that a quantity is equal to another instance with the same value and unit.
   */
  @Test
  public void floatQuantityIsEqualToIdenticalInstance() {
    assertTrue(ONE_OHM.equals(createQuantity(1, Units.OHM)));
  }

  /**
   * Verifies that a quantity is equal to another instance with the same value and unit using another primitive.
   */
  @Test
  public void floatQuantityIsEqualToIdenticalInstanceWithAnotherPrimitive() {
    assertTrue(ONE_OHM.equals(new LongQuantity<ElectricResistance>(Long.valueOf(1L).longValue(), Units.OHM)));
  }

  /**
   * Verifies that a quantity is not equal to a quantity with a different value.
   */
  @Test
  public void floatQuantityIsNotEqualToQuantityWithDifferentValue() {
    assertFalse(ONE_OHM.equals(TWO_OHM));
  }

  /**
   * Verifies that a quantity is not equal to a quantity with a different unit.
   */
  @Test
  public void floatQuantityIsNotEqualToQuantityWithDifferentUnit() {
    assertFalse(ONE_OHM.equals(ONE_MILLIOHM));
  }

  /**
   * Verifies that a quantity is not equal to an object of a different class.
   */
  @Test
  public void floatQuantityIsNotEqualToObjectOfDifferentClass() {
    assertFalse(ONE_OHM.equals(SQUARE_OHM));
  }

  /**
   * Verifies that addition with FloatQuantity returns a FloatQuantity.
   */
  @Test
  public void additionWithFloatQuantityDoesNotWiden() {
    assertEquals(FloatQuantity.class, ONE_OHM.add(ONE_OHM).getClass());
  }

  /**
   * Verifies that addition with DoubleQuantity widens to DoubleQuantity.
   */
  @Test
  public void additionWithDoubleQuantityWidensToDoubleQuantity() {
    assertEquals(DoubleQuantity.class, ONE_OHM.add(ONE_DOUBLE_OHM).getClass());
  }

  /**
   * Verifies that subtraction with FloatQuantity returns a FloatQuantity.
   */
  @Test
  public void subtractionWithFloatQuantityDoesNotWiden() {
    assertEquals(FloatQuantity.class, ONE_OHM.subtract(ONE_OHM).getClass());
  }

  /**
   * Verifies that subtraction with DoubleQuantity widens to DoubleQuantity.
   */
  @Test
  public void subtractionWithDoubleQuantityWidensToDoubleQuantity() {
    assertEquals(DoubleQuantity.class, ONE_OHM.subtract(ONE_DOUBLE_OHM).getClass());
  }

  /**
   * Verifies that multiplication with FloatQuantity returns a FloatQuantity.
   */
  @Test
  public void multiplicationWithFloatQuantityDoesNotWiden() {
    assertEquals(FloatQuantity.class, ONE_OHM.multiply(ONE_OHM).getClass());
  }

  /**
   * Verifies that multiplication with DoubleQuantity widens to DoubleQuantity.
   */
  @Test
  public void multiplicationWithDoubleQuantityWidensToDoubleQuantity() {
    assertEquals(DoubleQuantity.class, ONE_OHM.multiply(ONE_DOUBLE_OHM).getClass());
  }

  /**
   * Verifies that division with FloatQuantity returns a FloatQuantity.
   */
  @Test
  public void divisionWithFloatQuantityDoesNotWiden() {
    assertEquals(FloatQuantity.class, ONE_OHM.divide(ONE_OHM).getClass());
  }

  /**
   * Verifies that division with DoubleQuantity widens to DoubleQuantity.
   */
  @Test
  public void divisionWithDoubleQuantityWidensToDoubleQuantity() {
    assertEquals(DoubleQuantity.class, ONE_OHM.divide(ONE_DOUBLE_OHM).getClass());
  }
}
