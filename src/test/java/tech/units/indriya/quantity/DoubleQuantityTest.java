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

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.quantity.Quantities;
import static javax.measure.MetricPrefix.*;
import tech.units.indriya.unit.Units;

public class DoubleQuantityTest {

  private static final Unit<?> SQUARE_OHM = Units.OHM.multiply(Units.OHM);
  private static final DoubleQuantity<ElectricResistance> HALF_AN_OHM = createQuantity(0.5, Units.OHM);
  private static final DoubleQuantity<ElectricResistance> ONE_OHM = createQuantity(1, Units.OHM);
  private static final DoubleQuantity<ElectricResistance> TWO_OHM = createQuantity(2, Units.OHM);
  private static final DoubleQuantity<ElectricResistance> MAX_VALUE_OHM = createQuantity(Double.MAX_VALUE, Units.OHM);
  private static final DoubleQuantity<ElectricResistance> JUST_OVER_HALF_MAX_VALUE_OHM = createQuantity(Double.MAX_VALUE / 1.999999999999999,
      Units.OHM);
  private static final DoubleQuantity<ElectricResistance> ONE_MILLIOHM = createQuantity(1, MILLI(Units.OHM));
  private static final DecimalQuantity<ElectricResistance> ONE_DECIMAL_OHM = new DecimalQuantity<ElectricResistance>(BigDecimal.ONE, Units.OHM);

  private static <Q extends Quantity<Q>> DoubleQuantity<Q> createQuantity(double d, Unit<Q> unit) {
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
    DoubleQuantity<ElectricResistance> expected = createQuantity(1001, MILLI(Units.OHM));
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
    Quantity<ElectricResistance> largeValueOhm = MAX_VALUE_OHM.divide(2);
    Quantity<ElectricResistance> actual = ONE_MILLIOHM.add(largeValueOhm);
    assertEquals(largeValueOhm, actual);
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
   * Verifies that the multiplication of two quantities multiplies correctly.
   */
  @Test
  public void quantityMultiplicationMultipliesCorrectly() {
    Quantity<?> actual = TWO_OHM.multiply(TWO_OHM);
    DoubleQuantity<?> expected = createQuantity(4, SQUARE_OHM);
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
    DoubleQuantity<ElectricResistance> expected = createQuantity(4, Units.OHM);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the multiplication with a number resulting in an overflow throws an exception.
   */
  @Test
  public void numberMultiplicationResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      JUST_OVER_HALF_MAX_VALUE_OHM.multiply(2);
    });
  }

  /**
   * Verifies that the division of two quantities divides correctly.
   */
  @Test
  public void quantityDivisionDividesCorrectly() {
    Quantity<?> actual = TWO_OHM.divide(TWO_OHM);
    DoubleQuantity<Dimensionless> expected = createQuantity(1, AbstractUnit.ONE);
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
      JUST_OVER_HALF_MAX_VALUE_OHM.divide(0.5);
    });
  }

  /**
   * Verifies that the inverse returns the correct reciprocal for a unit quantity.
   */
  @Test
  public void inverseReturnsUnitQuantityForUnitQuantity() {
    Quantity<?> actual = ONE_OHM.inverse();
    DoubleQuantity<?> expected = createQuantity(1, Units.OHM.inverse());
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the inverse returns the correct reciprocal for a quantity larger than a unit quantity.
   */
  @Test
  public void inverseReturnsZeroQuantityForLargerThanUnitQuantity() {
    Quantity<?> actual = TWO_OHM.inverse();
    DoubleQuantity<?> expected = createQuantity(0.5, Units.OHM.inverse());
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the inverse throws an exception for a zero quantity.
   */
  @Test
  public void inverseReturnsInfinityQuantityForZeroQuantity() {
    Quantity<?> actual = createQuantity(0, Units.OHM).inverse();
    DoubleQuantity<?> expected = createQuantity(Double.POSITIVE_INFINITY, Units.OHM.inverse());
    assertEquals(expected, actual);
  }

  /**
   * Verifies that a DoubleQuantity isn't big.
   */
  @Test
  public void doubleQuantityIsNotBig() {
    assertFalse(ONE_OHM.isBig());
  }

  /**
   * Verifies that a DoubleQuantity is decimal.
   */
  @Test
  public void doubleQuantityIsDecimal() {
    assertTrue(ONE_OHM.isDecimal());
  }

  /**
   * Verifies that a DoubleQuantity has the size of Double.
   */
  @Test
  public void doubleQuantityHasByteSize() {
    assertEquals(Double.SIZE, ONE_OHM.getSize());
  }

  /**
   * Verifies that a quantity isn't equal to null.
   */
  @Test
  public void doubleQuantityIsNotEqualToNull() {
    assertFalse(ONE_OHM.equals(null));
  }

  /**
   * Verifies that a quantity is equal to itself.
   */
  @Test
  public void doubleQuantityIsEqualToItself() {
    assertTrue(ONE_OHM.equals(ONE_OHM));
  }

  /**
   * Verifies that a quantity is equal to another instance with the same value and unit.
   */
  @Test
  public void doubleQuantityIsEqualToIdenticalInstance() {
    assertTrue(ONE_OHM.equals(createQuantity(1, Units.OHM)));
  }

  /**
   * Verifies that a quantity is equal to another instance with the same value and unit using another primitive.
   */
  @Test
  public void doubleQuantityIsEqualToIdenticalInstanceWithAnotherPrimitive() {
    assertTrue(ONE_OHM.equals(new LongQuantity<ElectricResistance>(Long.valueOf(1L).longValue(), Units.OHM)));
  }

  /**
   * Verifies that a quantity is not equal to a quantity with a different value.
   */
  @Test
  public void doubleQuantityIsNotEqualToQuantityWithDifferentValue() {
    assertFalse(ONE_OHM.equals(TWO_OHM));
  }

  /**
   * Verifies that a quantity is not equal to a quantity with a different unit.
   */
  @Test
  public void doubleQuantityIsNotEqualToQuantityWithDifferentUnit() {
    assertFalse(ONE_OHM.equals(ONE_MILLIOHM));
  }

  /**
   * Verifies that a quantity is not equal to an object of a different class.
   */
  @Test
  public void doubleQuantityIsNotEqualToObjectOfDifferentClass() {
    assertFalse(ONE_OHM.equals(SQUARE_OHM));
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
   * Verifies that an exception is thrown if the conversion for doubleValue results in overflow.
   */
  @Test
  public void doubleValueThrowsExceptionOnOverflow() {
    assertThrows(ArithmeticException.class, () -> {
      MAX_VALUE_OHM.doubleValue(MILLI(Units.OHM));
    });
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
      createQuantity(Long.MAX_VALUE + 1025.0, Units.OHM).longValue(Units.OHM);
    });
  }

  /**
   * Verifies that an exception is thrown if the conversion for longValue results in a negative overflow.
   */
  @Test
  public void longValueThrowsExceptionOnNegativeOverflow() {
    assertThrows(ArithmeticException.class, () -> {
      createQuantity(Long.MIN_VALUE - 1025.0, Units.OHM).longValue(Units.OHM);
    });
  }

  /**
   * Verifies that addition with DoubleQuantity returns a DoubleQuantity.
   */
  @Test
  public void additionWithDoubleQuantityDoesNotWiden() {
    assertEquals(DoubleQuantity.class, ONE_OHM.add(ONE_OHM).getClass());
  }

  /**
   * Verifies that addition with DecimalQuantity widens to DecimalQuantity.
   */
  @Test
  public void additionWithDecimalQuantityWidensToDecimalQuantity() {
    assertEquals(DecimalQuantity.class, ONE_OHM.add(ONE_DECIMAL_OHM).getClass());
  }

  /**
   * Verifies that subtraction with DoubleQuantity returns a DoubleQuantity.
   */
  @Test
  public void subtractionWithDoubleQuantityDoesNotWiden() {
    assertEquals(DoubleQuantity.class, ONE_OHM.subtract(ONE_OHM).getClass());
  }

  /**
   * Verifies that subtraction with DecimalQuantity widens to DecimalQuantity.
   */
  @Test
  public void subtractionWithDecimalQuantityWidensToDecimalQuantity() {
    assertEquals(DecimalQuantity.class, ONE_OHM.subtract(ONE_DECIMAL_OHM).getClass());
  }

  /**
   * Verifies that multiplication with DoubleQuantity returns a DoubleQuantity.
   */
  @Test
  public void multiplicationWithDoubleQuantityDoesNotWiden() {
    assertEquals(DoubleQuantity.class, ONE_OHM.multiply(ONE_OHM).getClass());
  }

  /**
   * Verifies that multiplication with DecimalQuantity widens to DecimalQuantity.
   */
  @Test
  public void multiplicationWithDecimalQuantityWidensToDecimalQuantity() {
    assertEquals(DecimalQuantity.class, ONE_OHM.multiply(ONE_DECIMAL_OHM).getClass());
  }

  /**
   * Verifies that division with DoubleQuantity returns a DoubleQuantity.
   */
  @Test
  public void divisionWithDoubleQuantityDoesNotWiden() {
    assertEquals(DoubleQuantity.class, ONE_OHM.divide(ONE_OHM).getClass());
  }

  /**
   * Verifies that division with DecimalQuantity widens to DecimalQuantity.
   */
  @Test
  public void divisionWithDecimalQuantityWidensToDecimalQuantity() {
    assertEquals(DecimalQuantity.class, ONE_OHM.divide(ONE_DECIMAL_OHM).getClass());
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

  /**
   * Tests negate()
   */
  @Test
  public void negateTest() {
    assertEquals(-1d, ONE_OHM.negate().getValue());
  }
}
