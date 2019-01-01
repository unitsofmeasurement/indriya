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
package tech.units.indriya.quantity;

import static javax.measure.MetricPrefix.MILLI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.unit.Units;

public class BigIntegerQuantityTest {

  private static final Unit<?> SQUARE_OHM = Units.OHM.multiply(Units.OHM);
  private final BigIntegerQuantity<ElectricResistance> ONE_OHM = createQuantity(1L, Units.OHM);
  private final BigIntegerQuantity<ElectricResistance> TWO_OHM = createQuantity(2L, Units.OHM);
  private final BigIntegerQuantity<ElectricResistance> ONE_MILLIOHM = createQuantity(1L, MILLI(Units.OHM));
  private static final FloatQuantity<ElectricResistance> ONE_FLOAT_OHM = new FloatQuantity<ElectricResistance>(1F, Units.OHM);

  private static <Q extends Quantity<Q>> BigIntegerQuantity<Q> createQuantity(long l, Unit<Q> unit) {
    return new BigIntegerQuantity<Q>(l, unit);
  }

  /**
   * Verifies that the multiplication of two quantities multiplies correctly.
   */
  @Test
  public void quantityMultiplicationMultipliesCorrectly() {
    Quantity<?> actual = TWO_OHM.multiply(TWO_OHM);
    BigIntegerQuantity<?> expected = createQuantity(4L, SQUARE_OHM);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the multiplication with a number multiplies correctly.
   */
  @Test
  public void numberMultiplicationMultipliesCorrectly() {
    Quantity<?> actual = TWO_OHM.multiply(2L);
    BigIntegerQuantity<ElectricResistance> expected = createQuantity(4L, Units.OHM);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the division of two quantities divides correctly.
   */
  @Test
  public void quantityDivisionDividesCorrectly() {
    Quantity<?> actual = TWO_OHM.divide(TWO_OHM);
    BigIntegerQuantity<Dimensionless> expected = createQuantity(1L, AbstractUnit.ONE);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the division with a number divides correctly.
   */
  @Test
  public void numberDivisionDividesCorrectly() {
    Quantity<?> actual = TWO_OHM.divide(2L);
    assertEquals(ONE_OHM, actual);
  }

  /**
   * Verifies that the inverse returns the correct reciprocal for a unit quantity.
   */
  @Test
  public void inverseReturnsUnitQuantityForUnitQuantity() {
    Quantity<?> actual = ONE_OHM.inverse();
    BigIntegerQuantity<?> expected = createQuantity(1L, Units.OHM.inverse());
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the inverse returns the correct reciprocal for a quantity larger than a unit quantity.
   */
  @Test
  public void inverseReturnsZeroQuantityForLargerThanUnitQuantity() {
    Quantity<?> actual = TWO_OHM.inverse();
    BigIntegerQuantity<?> expected = createQuantity(0L, Units.OHM.inverse());
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the inverse throws an exception for a zero quantity.
   */
  @Test
  public void inverseThrowsExceptionForZeroQuantity() {
    assertThrows(ArithmeticException.class, () -> {
      createQuantity(0L, Units.OHM).inverse();
    });
  }

  /**
   * Verifies that a BigIntegerQuantity is big.
   */
  @Test
  public void bigIntegerQuantityIsBig() {
    assertTrue(ONE_OHM.isBig());
  }

  /**
   * Verifies that a BigIntegerQuantity is not decimal.
   */
  @Test
  public void bigIntegerQuantityIsNotDecimal() {
    assertFalse(ONE_OHM.isDecimal());
  }

  /**
   * Verifies the getSize method for BigIntegerQuantity returns 0.
   */
  @Test
  public void bigIntegerQuantityGetSizeReturnsZero() {
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
    assertEquals(BigDecimal.valueOf(1), ONE_OHM.decimalValue(Units.OHM));
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
    assertEquals(BigDecimal.valueOf(1000), ONE_OHM.decimalValue(MILLI(Units.OHM)));
  }

  /**
   * Verifies that the value is returned without conversion if longValue is called with the quantity's unit.
   */
  @Test
  public void longValueReturnsValueForSameUnit() {
    assertEquals(1, ONE_OHM.longValue(Units.OHM));
  }

  /**
   * Verifies that longValue can handle Long.MAX_VALUE.
   */
  @Test
  public void longValueReturnsLongMaxValue() {
    assertEquals(Long.MAX_VALUE, new BigIntegerQuantity<ElectricResistance>(BigInteger.valueOf(Long.MAX_VALUE), Units.OHM).longValue(Units.OHM));
  }

  /**
   * Verifies that longValue can handle Long.MIN_VALUE.
   */
  @Test
  public void longValueReturnsLongMinValue() {
    assertEquals(Long.MIN_VALUE, new BigIntegerQuantity<ElectricResistance>(BigInteger.valueOf(Long.MIN_VALUE), Units.OHM).longValue(Units.OHM));
  }

  /**
   * Verifies longValue throws an exception if the value is larger than Long.MAX_VALUE.
   */
  @Test
  public void longValueThrowsExceptionOnPositiveOverflow() {
    assertThrows(ArithmeticException.class, () -> {
      new BigIntegerQuantity<ElectricResistance>(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE), Units.OHM).longValue(Units.OHM);
    });
  }

  /**
   * Verifies that the value is correctly converted if longValue is called with the quantity's unit.
   */
  @Test
  public void longValueReturnsConvertedValueForOtherUnit() {
    assertEquals(0, ONE_MILLIOHM.longValue(Units.OHM));
  }

  /**
   * Verifies longValue throws an exception if the value is smaller than Long.MIN_VALUE.
   */
  @Test
  public void longValueThrowsExceptionOnNegativeOverflow() {
    assertThrows(ArithmeticException.class, () -> {
      new BigIntegerQuantity<ElectricResistance>(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE), Units.OHM).longValue(Units.OHM);
    });
  }

  /**
   * Verifies that an exception is thrown if the conversion for longValue results in a positive overflow.
   */
  @Test
  public void longValueThrowsExceptionOnPositiveOverflowAfterConversion() {
    assertThrows(ArithmeticException.class, () -> {
      createQuantity(Long.MAX_VALUE / 10L + 117L, MetricPrefix.DEKA(Units.OHM)).longValue(Units.OHM);
    });
  }

  /**
   * Verifies that an exception is thrown if the conversion for longValue results in a negative overflow.
   */
  @Test
  public void longValueThrowsExceptionOnNegativeOverflowAfterConversion() {
    assertThrows(ArithmeticException.class, () -> {
      createQuantity(Long.MIN_VALUE / 10L - 117L, MetricPrefix.DEKA(Units.OHM)).longValue(Units.OHM);
    });
  }

  /**
   * Verifies that a quantity isn't equal to null.
   */
  @Test
  public void bigIntegerQuantityIsNotEqualToNull() {
    assertFalse(ONE_OHM.equals(null));
  }

  /**
   * Verifies that a quantity is equal to itself.
   */
  @Test
  public void bigIntegerQuantityIsEqualToItself() {
    assertTrue(ONE_OHM.equals(ONE_OHM));
  }

  /**
   * Verifies that a quantity is equal to another instance with the same value and unit.
   */
  @Test
  public void bigIntegerQuantityIsEqualToIdenticalInstance() {
    assertTrue(ONE_OHM.equals(createQuantity(1, Units.OHM)));
  }

  /**
   * Verifies that a quantity is equal to another instance with the same value and unit using another primitive.
   */
  @Test
  public void bigIntegerQuantityIsEqualToIdenticalInstanceWithAnotherPrimitive() {
    assertTrue(ONE_OHM.equals(new DoubleQuantity<ElectricResistance>(Double.valueOf(1).doubleValue(), Units.OHM)));
  }

  /**
   * Verifies that a quantity is not equal to a quantity with a different value.
   */
  @Test
  public void bigIntegerQuantityIsNotEqualToQuantityWithDifferentValue() {
    assertFalse(ONE_OHM.equals(TWO_OHM));
  }

  /**
   * Verifies that a quantity is not equal to a quantity with a different unit.
   */
  @Test
  public void bigIntegerQuantityIsNotEqualToQuantityWithDifferentUnit() {
    assertFalse(ONE_OHM.equals(ONE_MILLIOHM));
  }

  /**
   * Verifies that a quantity is not equal to an object of a different class.
   */
  @Test
  public void bigIntegerQuantityIsNotEqualToObjectOfDifferentClass() {
    assertFalse(ONE_OHM.equals(SQUARE_OHM));
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
  public void additionMustProduceCorrectResultIfSameUnits() {
    BigIntegerQuantity<ElectricResistance> twoOhm = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(2).longValue(), Units.OHM);
    BigIntegerQuantity<ElectricResistance> expected = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(3).longValue(), Units.OHM);
    Quantity<ElectricResistance> actual = ONE_OHM.add(twoOhm);
    assertEquals(expected, actual);
  }

  @Test
  public void additionMustConvertToLowestPrefix() {
    for (MetricPrefix prefix : MetricPrefix.values()) {
      final String msg = String.format("testing 1 Ω + 1 %sΩ", prefix.getSymbol());
      final BigIntegerQuantity<ElectricResistance> operand = new BigIntegerQuantity<ElectricResistance>(1L, Units.OHM.prefix(prefix));
      final BigIntegerQuantity<ElectricResistance> expected;
      if (prefix.getExponent() > 0) {
        expected = new BigIntegerQuantity<ElectricResistance>(BigInteger.TEN.pow(prefix.getExponent()).add(BigInteger.ONE), Units.OHM);
      } else {
        expected = new BigIntegerQuantity<ElectricResistance>(BigInteger.TEN.pow(-prefix.getExponent()).add(BigInteger.ONE),
            Units.OHM.prefix(prefix));
      }
      assertEquals(expected, ONE_OHM.add(operand), msg);
      assertEquals(expected, operand.add(ONE_OHM), msg);
    }
  }

  @Test
  @DisplayName("1 Ω - 1001 mΩ should be -1 mΩ")
  public void subtractionMustProduceCorrectResult() {
    final BigIntegerQuantity<ElectricResistance> operand = new BigIntegerQuantity<ElectricResistance>(1001L, MILLI(Units.OHM));
    final BigIntegerQuantity<ElectricResistance> expected = new BigIntegerQuantity<ElectricResistance>(-1L, MILLI(Units.OHM));

    assertEquals(expected, ONE_OHM.subtract(operand));
    assertEquals(expected, operand.subtract(ONE_OHM).multiply(-1));
  }

  /**
   * Verifies that addition with BigIntegerQuantity returns a BigIntegerQuantity.
   */
  @Test
  public void additionWithBigIntegerQuantityDoesNotWiden() {
    assertEquals(BigIntegerQuantity.class, ONE_OHM.add(ONE_OHM).getClass());
  }

  /**
   * Verifies that addition with FloatQuantity widens to FloatQuantity.
   */
  @Test
  public void additionWithFloatQuantityWidensToFloatQuantity() {
    assertEquals(FloatQuantity.class, ONE_OHM.add(ONE_FLOAT_OHM).getClass());
  }

  /**
   * Verifies that subtraction with BigIntegerQuantity returns a BigIntegerQuantity.
   */
  @Test
  public void subtractionWithBigIntegerQuantityDoesNotWiden() {
    assertEquals(BigIntegerQuantity.class, ONE_OHM.subtract(ONE_OHM).getClass());
  }

  /**
   * Verifies that subtraction with FloatQuantity widens to FloatQuantity.
   */
  @Test
  public void subtractionWithFloatQuantityWidensToFloatQuantity() {
    assertEquals(FloatQuantity.class, ONE_OHM.subtract(ONE_FLOAT_OHM).getClass());
  }

  /**
   * Verifies that multiplication with BigIntegerQuantity returns a BigIntegerQuantity.
   */
  @Test
  public void multiplicationWithBigIntegerQuantityDoesNotWiden() {
    assertEquals(BigIntegerQuantity.class, ONE_OHM.multiply(ONE_OHM).getClass());
  }

  /**
   * Verifies that multiplication with FloatQuantity widens to FloatQuantity.
   */
  @Test
  public void multiplicationWithFloatQuantityWidensToFloatQuantity() {
    assertEquals(FloatQuantity.class, ONE_OHM.multiply(ONE_FLOAT_OHM).getClass());
  }

  /**
   * Verifies that division with BigIntegerQuantity returns a BigIntegerQuantity.
   */
  @Test
  public void divisionWithBigIntegerQuantityDoesNotWiden() {
    assertEquals(BigIntegerQuantity.class, ONE_OHM.divide(ONE_OHM).getClass());
  }

  /**
   * Verifies that division with FloatQuantity widens to FloatQuantity.
   */
  @Test
  public void divisionWithFloatQuantityWidensToFloatQuantity() {
    assertEquals(FloatQuantity.class, ONE_OHM.divide(ONE_FLOAT_OHM).getClass());
  }

  /**
   * Tests negate() of BigIntegerQuantity.
   */
  @Test
  public void negateTest() {
    assertEquals(BigInteger.ONE.negate(), ONE_OHM.negate().getValue());
  }
}
