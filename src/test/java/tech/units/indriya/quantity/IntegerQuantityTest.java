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
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.quantity.IntegerQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public class IntegerQuantityTest {

  private static final Unit<?> SQUARE_OHM = Units.OHM.multiply(Units.OHM);
  private final IntegerQuantity<ElectricResistance> ONE_OHM = createQuantity(1, Units.OHM);
  private final IntegerQuantity<ElectricResistance> TWO_OHM = createQuantity(2, Units.OHM);
  private final IntegerQuantity<ElectricResistance> MIN_VALUE_OHM = createQuantity(Integer.MIN_VALUE, Units.OHM);
  private final IntegerQuantity<ElectricResistance> MAX_VALUE_OHM = createQuantity(Integer.MAX_VALUE, Units.OHM);
  private final IntegerQuantity<ElectricResistance> ONE_MILLIOHM = createQuantity(1, MetricPrefix.MILLI(Units.OHM));
  private final IntegerQuantity<ElectricResistance> ONE_KILOOHM = createQuantity(1, MetricPrefix.KILO(Units.OHM));
  private final IntegerQuantity<ElectricResistance> ONE_YOTTAOHM = createQuantity(1, MetricPrefix.YOTTA(Units.OHM));
  private static final LongQuantity<ElectricResistance> ONE_LONG_OHM = new LongQuantity<ElectricResistance>(1L, Units.OHM);

  private <Q extends Quantity<Q>> IntegerQuantity<Q> createQuantity(int i, Unit<Q> unit) {
    return new IntegerQuantity<Q>(Integer.valueOf(i).intValue(), unit);
  }

  /**
   * Verifies that the addition of two quantities with the same multiples results in a new quantity with the same multiple and the value holding the
   * sum.
   */
  @Test
  public void additionWithSameMultipleKeepsMultiple() {
    Quantity<ElectricResistance> actual = ONE_OHM.add(TWO_OHM);
    IntegerQuantity<ElectricResistance> expected = createQuantity(3, Units.OHM);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the addition of two quantities with the same multiples resulting in an overflow throws an exception.
   */
  @Test
  public void additionWithSameMultipleResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      ONE_OHM.add(MAX_VALUE_OHM);
    });
  }

  /**
   * Verifies that adding a quantity with a larger multiple keeps the result to the smaller multiple.
   */
  @Test
  public void additionWithLargerMultipleKeepsSmallerMultiple() {
    Quantity<ElectricResistance> actual = ONE_MILLIOHM.add(ONE_OHM);
    IntegerQuantity<ElectricResistance> expected = createQuantity(1001, MetricPrefix.MILLI(Units.OHM));
    assertEquals(expected, actual);
  }

  /**
   * Verifies that adding a quantity with a smaller multiple casts the result to the smaller multiple.
   */
  @Test
  public void additionWithSmallerMultipleCastsToSmallerMultipleIfNeeded() {
    Quantity<ElectricResistance> actual = ONE_OHM.add(ONE_MILLIOHM);
    IntegerQuantity<ElectricResistance> expected = createQuantity(1001, MetricPrefix.MILLI(Units.OHM));
    assertEquals(expected, actual);
  }

  /**
   * Verifies that adding a quantity with a larger overflowing multiple casts the result to the larger multiple.
   */
  @Test
  public void additionWithLargerOverflowingMultipleCastsToLargerMultiple() {
    Quantity<ElectricResistance> actual = ONE_OHM.add(ONE_YOTTAOHM);
    assertEquals(ONE_YOTTAOHM, actual);
  }

  /**
   * Verifies that adding a quantity with a larger multiple resulting in an overflowing sum casts the result to the larger multiple.
   */
  @Test
  public void additionWithLargerMultipleAndOverflowingResultCastsToLargerMultiple() {
    IntegerQuantity<ElectricResistance> almost_max_value_ohm = createQuantity(Integer.MAX_VALUE - 999, Units.OHM);
    Quantity<ElectricResistance> actual = almost_max_value_ohm.add(ONE_KILOOHM);
    IntegerQuantity<ElectricResistance> expected = createQuantity(Integer.MAX_VALUE / 1000, MetricPrefix.KILO(Units.OHM));
    assertEquals(expected, actual);
  }

  /**
   * Verifies that adding a quantity with a larger multiple resulting in an overflowing sum casts the result to the larger multiple.
   */
  @Test
  public void additionWithLargerMultipleButNotOverflowingResultKeepsSmallerMultiple() {
    IntegerQuantity<ElectricResistance> almost_max_value_ohm = createQuantity(Integer.MAX_VALUE - 1000, Units.OHM);
    Quantity<ElectricResistance> actual = almost_max_value_ohm.add(ONE_KILOOHM);
    assertEquals(MAX_VALUE_OHM, actual);
  }

  /**
   * Verifies that adding a quantity with a smaller underflowing multiple keeps the result at the larger multiple.
   */
  @Test
  public void additionWithSmallerUnderflowingMultipleKeepsAtLargerMultiple() {
    Quantity<ElectricResistance> actual = ONE_YOTTAOHM.add(ONE_OHM);
    assertEquals(ONE_YOTTAOHM, actual);
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
   * Verifies that the subtraction of two quantities with the same multiples resulting in a negative overflow throws an exception.
   */
  @Test
  public void subtractionWithSameMultipleResultingInNegativeOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      MIN_VALUE_OHM.subtract(ONE_OHM);
    });
  }

  /**
   * Verifies that the subtraction of two quantities with the same multiples almost resulting in a negative overflow doesn't an exception.
   */
  @Test
  public void subtractionWithSameMultipleAlmostResultingInNegativeDoesNotThrowException() {
    Quantity<ElectricResistance> actual = createQuantity(Integer.MIN_VALUE + 1, Units.OHM).subtract(ONE_OHM);
    assertEquals(MIN_VALUE_OHM, actual);
  }

  /**
   * Verifies that the multiplication of two quantities multiplies correctly.
   */
  @Test
  public void quantityMultiplicationMultipliesCorrectly() {
    Quantity<?> actual = TWO_OHM.multiply(TWO_OHM);
    IntegerQuantity<?> expected = createQuantity(4, SQUARE_OHM);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the multiplication of two quantities resulting in an overflow throws an exception.
   */
  @Test
  public void quantityMultiplicationResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      Quantity<ElectricResistance> halfMaxValuePlusOne = createQuantity(1 + Integer.MAX_VALUE / 2, Units.OHM);
      halfMaxValuePlusOne.multiply(TWO_OHM);
    });
  }

  /**
   * Verifies that the multiplication with a number multiplies correctly.
   */
  @Test
  public void numberMultiplicationMultipliesCorrectly() {
    Quantity<?> actual = TWO_OHM.multiply(2);
    IntegerQuantity<ElectricResistance> expected = createQuantity(4, Units.OHM);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the multiplication with a number resulting in an overflow throws an exception.
   */
  @Test
  public void numberMultiplicationResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      Quantity<ElectricResistance> halfMaxValuePlusOne = createQuantity(1 + Integer.MAX_VALUE / 2, Units.OHM);
      halfMaxValuePlusOne.multiply(2);
    });
  }

  /**
   * Verifies that the division of two quantities divides correctly.
   */
  @Test
  public void quantityDivisionDividesCorrectly() {
    Quantity<?> actual = TWO_OHM.divide(TWO_OHM);
    IntegerQuantity<Dimensionless> expected = createQuantity(1, AbstractUnit.ONE);
    assertEquals(expected, actual);
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
   * Verifies that the inverse returns the correct reciprocal for a unit quantity.
   */
  @Test
  public void inverseReturnsUnitQuantityForUnitQuantity() {
    Quantity<?> actual = ONE_OHM.inverse();
    IntegerQuantity<?> expected = createQuantity(1, Units.OHM.inverse());
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the inverse returns the correct reciprocal for a quantity larger than a unit quantity.
   */
  @Test
  public void inverseReturnsZeroQuantityForLargerThanUnitQuantity() {
    Quantity<?> actual = TWO_OHM.inverse();
    IntegerQuantity<?> expected = createQuantity(0, Units.OHM.inverse());
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the inverse throws an exception for a zero quantity.
   */
  @Test
  public void inverseThrowsExceptionForZeroQuantity() {
    assertThrows(ArithmeticException.class, () -> {
      createQuantity(0, Units.OHM).inverse();
    });
  }

  /**
   * Verifies that a IntegerQuantity isn't big.
   */
  @Test
  public void integerQuantityIsNotBig() {
    assertFalse(ONE_OHM.isBig());
  }

  /**
   * Verifies that a IntegerQuantity isn't decimal.
   */
  @Test
  public void integerQuantityIsNotDecimal() {
    assertFalse(ONE_OHM.isDecimal());
  }

  /**
   * Verifies that a IntegerQuantity has the size of Integer.
   */
  @Test
  public void integerQuantityHasByteSize() {
    assertEquals(Integer.SIZE, ONE_OHM.getSize());
  }

  /**
   * Verifies that a quantity isn't equal to null.
   */
  @Test
  public void integerQuantityIsNotEqualToNull() {
    assertFalse(ONE_OHM.equals(null));
  }

  /**
   * Verifies that a quantity is equal to itself.
   */
  @Test
  public void integerQuantityIsEqualToItself() {
    assertTrue(ONE_OHM.equals(ONE_OHM));
  }

  /**
   * Verifies that a quantity is equal to another instance with the same value and unit.
   */
  @Test
  public void integerQuantityIsEqualToIdenticalInstance() {
    assertTrue(ONE_OHM.equals(createQuantity(1, Units.OHM)));
  }

  /**
   * Verifies that a quantity is equal to another instance with the same value and unit using another primitive.
   */
  @Test
  public void integerQuantityIsEqualToIdenticalInstanceWithAnotherPrimitive() {
    assertTrue(ONE_OHM.equals(new DoubleQuantity<ElectricResistance>(Double.valueOf(1).doubleValue(), Units.OHM)));
  }

  /**
   * Verifies that a quantity is not equal to a quantity with a different value.
   */
  @Test
  public void integerQuantityIsNotEqualToQuantityWithDifferentValue() {
    assertFalse(ONE_OHM.equals(TWO_OHM));
  }

  /**
   * Verifies that a quantity is not equal to a quantity with a different unit.
   */
  @Test
  public void integerQuantityIsNotEqualToQuantityWithDifferentUnit() {
    assertFalse(ONE_OHM.equals(ONE_KILOOHM));
  }

  /**
   * Verifies that a quantity is not equal to an object of a different class.
   */
  @Test
  public void integerQuantityIsNotEqualToObjectOfDifferentClass() {
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
   * Verifies that the value is returned without conversion if decimalValue is called with the quantity's unit.
   */
  @Test
  public void decimalValueReturnsValueForSameUnit() {
    assertEquals(BigDecimal.valueOf(1.0), ONE_OHM.decimalValue(Units.OHM));
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
      createQuantity(9223373, MetricPrefix.TERA(Units.OHM)).longValue(Units.OHM);
    });
  }

  /**
   * Verifies that an exception is thrown if the conversion for longValue results in a negative overflow.
   */
  @Test
  public void longValueThrowsExceptionOnNegativeOverflow() {
    assertThrows(ArithmeticException.class, () -> {
      createQuantity(-9223373, MetricPrefix.TERA(Units.OHM)).longValue(Units.OHM);
    });
  }
  
  /**
   * Verifies that addition with IntegerQuantity returns a IntegerQuantity.
   */
  @Test
  public void additionWithIntegerQuantityDoesNotWiden() {
    assertEquals(IntegerQuantity.class, ONE_OHM.add(ONE_OHM).getClass());
  }

  /**
   * Verifies that addition with LongQuantity widens to LongQuantity.
   */
  @Test
  public void additionWithLongQuantityWidensToLongQuantity() {
    assertEquals(LongQuantity.class, ONE_OHM.add(ONE_LONG_OHM).getClass());
  }

  /**
   * Verifies that subtraction with IntegerQuantity returns a IntegerQuantity.
   */
  @Test
  public void subtractionWithIntegerQuantityDoesNotWiden() {
    assertEquals(IntegerQuantity.class, ONE_OHM.subtract(ONE_OHM).getClass());
  }

  /**
   * Verifies that subtraction with LongQuantity widens to LongQuantity.
   */
  @Test
  public void subtractionWithLongQuantityWidensToLongQuantity() {
    assertEquals(LongQuantity.class, ONE_OHM.subtract(ONE_LONG_OHM).getClass());
  }

  /**
   * Verifies that multiplication with IntegerQuantity returns a IntegerQuantity.
   */
  @Test
  public void multiplicationWithIntegerQuantityDoesNotWiden() {
    assertEquals(IntegerQuantity.class, ONE_OHM.multiply(ONE_OHM).getClass());
  }

  /**
   * Verifies that multiplication with LongQuantity widens to LongQuantity.
   */
  @Test
  public void multiplicationWithLongQuantityWidensToLongQuantity() {
    assertEquals(LongQuantity.class, ONE_OHM.multiply(ONE_LONG_OHM).getClass());
  }

  /**
   * Verifies that division with IntegerQuantity returns a IntegerQuantity.
   */
  @Test
  public void divisionWithIntegerQuantityDoesNotWiden() {
    assertEquals(IntegerQuantity.class, ONE_OHM.divide(ONE_OHM).getClass());
  }

  /**
   * Verifies that division with LongQuantity widens to LongQuantity.
   */
  @Test
  public void divisionWithLongQuantityWidensToLongQuantity() {
    assertEquals(LongQuantity.class, ONE_OHM.divide(ONE_LONG_OHM).getClass());
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
  public void testEquality() throws Exception {
    Quantity<Length> value = Quantities.getQuantity(new Integer(10), Units.METRE);
    Quantity<Length> anotherValue = Quantities.getQuantity(new Integer(10), Units.METRE);
    assertEquals(value, anotherValue);
  }

}
