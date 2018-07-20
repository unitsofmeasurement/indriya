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
import tech.units.indriya.quantity.ByteQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public class ByteQuantityTest {

  private static final ShortQuantity<ElectricResistance> ONE_SHORT_OHM = new ShortQuantity<ElectricResistance>((short) 1, Units.OHM);
  private static final Unit<?> SQUARE_OHM = Units.OHM.multiply(Units.OHM);
  private final ByteQuantity<ElectricResistance> ONE_OHM = createQuantity((byte) 1, Units.OHM);
  private final ByteQuantity<ElectricResistance> TWO_OHM = createQuantity((byte) 2, Units.OHM);
  private final ByteQuantity<ElectricResistance> MIN_VALUE_OHM = createQuantity(Byte.MIN_VALUE, Units.OHM);
  private final ByteQuantity<ElectricResistance> MAX_VALUE_OHM = createQuantity(Byte.MAX_VALUE, Units.OHM);
  private final ByteQuantity<ElectricResistance> ONE_DEKAOHM = createQuantity((byte) 1, MetricPrefix.DEKA(Units.OHM));
  private final ByteQuantity<ElectricResistance> ONE_DECIOHM = createQuantity((byte) 1, MetricPrefix.DECI(Units.OHM));
  private final ByteQuantity<ElectricResistance> ONE_YOTTAOHM = createQuantity((byte) 1, MetricPrefix.YOTTA(Units.OHM));

  private <Q extends Quantity<Q>> ByteQuantity<Q> createQuantity(byte b, Unit<Q> unit) {
    return new ByteQuantity<Q>(Byte.valueOf(b).byteValue(), unit);
  }

  /**
   * Verifies that the addition of two quantities with the same multiples results in a new quantity with the same multiple and the value holding the
   * sum.
   */
  @Test
  public void additionWithSameMultipleKeepsMultiple() {
    Quantity<ElectricResistance> actual = ONE_OHM.add(TWO_OHM);
    ByteQuantity<ElectricResistance> expected = createQuantity((byte) 3, Units.OHM);
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
    Quantity<ElectricResistance> actual = ONE_DECIOHM.add(ONE_OHM);
    ByteQuantity<ElectricResistance> expected = createQuantity((byte) 11, MetricPrefix.DECI(Units.OHM));
    assertEquals(expected, actual);
  }

  /**
   * Verifies that adding a quantity with a smaller multiple casts the result to the smaller multiple.
   */
  @Test
  public void additionWithSmallerMultipleCastsToSmallerMultipleIfNeeded() {
    Quantity<ElectricResistance> actual = ONE_OHM.add(ONE_DECIOHM);
    ByteQuantity<ElectricResistance> expected = createQuantity((byte) 11, MetricPrefix.DECI(Units.OHM));
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
    ByteQuantity<ElectricResistance> almost_max_value_ohm = createQuantity((byte) (Byte.MAX_VALUE - 9), Units.OHM);
    Quantity<ElectricResistance> actual = almost_max_value_ohm.add(ONE_DEKAOHM);
    ByteQuantity<ElectricResistance> expected = createQuantity((byte) (Byte.MAX_VALUE / 10), MetricPrefix.DEKA(Units.OHM));
    assertEquals(expected, actual);
  }

  /**
   * Verifies that adding a quantity with a larger multiple resulting in an overflowing sum casts the result to the larger multiple.
   */
  @Test
  public void additionWithLargerMultipleButNotOverflowingResultKeepsSmallerMultiple() {
    ByteQuantity<ElectricResistance> almost_max_value_ohm = createQuantity((byte) (Byte.MAX_VALUE - 10), Units.OHM);
    Quantity<ElectricResistance> actual = almost_max_value_ohm.add(ONE_DEKAOHM);
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
    Quantity<ElectricResistance> actual = createQuantity((byte) (Byte.MIN_VALUE + 1), Units.OHM).subtract(ONE_OHM);
    assertEquals(MIN_VALUE_OHM, actual);
  }

  /**
   * Verifies that the multiplication of two quantities multiplies correctly.
   */
  @Test
  public void quantityMultiplicationMultipliesCorrectly() {
    Quantity<?> actual = TWO_OHM.multiply(TWO_OHM);
    ByteQuantity<?> expected = createQuantity((byte) 4, SQUARE_OHM);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the multiplication of two quantities resulting in an overflow throws an exception.
   */
  @Test
  public void quantityMultiplicationResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      Quantity<ElectricResistance> halfMaxValuePlusOne = createQuantity((byte) (1 + Byte.MAX_VALUE / 2), Units.OHM);
      halfMaxValuePlusOne.multiply(TWO_OHM);
    });
  }

  /**
   * Verifies that the multiplication with a number multiplies correctly.
   */
  @Test
  public void numberMultiplicationMultipliesCorrectly() {
    Quantity<?> actual = TWO_OHM.multiply(2);
    ByteQuantity<ElectricResistance> expected = createQuantity((byte) 4, Units.OHM);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the multiplication with a number resulting in an overflow throws an exception.
   */
  @Test
  public void numberMultiplicationResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      Quantity<ElectricResistance> halfMaxValuePlusOne = createQuantity((byte) (1 + Byte.MAX_VALUE / 2), Units.OHM);
      halfMaxValuePlusOne.multiply(2);
    });
  }

  /**
   * Verifies that the division of two quantities divides correctly.
   */
  @Test
  public void quantityDivisionDividesCorrectly() {
    Quantity<?> actual = TWO_OHM.divide(TWO_OHM);
    ByteQuantity<Dimensionless> expected = createQuantity((byte) 1, AbstractUnit.ONE);
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
    ByteQuantity<?> expected = createQuantity((byte) 1, Units.OHM.inverse());
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the inverse returns the correct reciprocal for a quantity larger than a unit quantity.
   */
  @Test
  public void inverseReturnsZeroQuantityForLargerThanUnitQuantity() {
    Quantity<?> actual = TWO_OHM.inverse();
    ByteQuantity<?> expected = createQuantity((byte) 0, Units.OHM.inverse());
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the inverse throws an exception for a zero quantity.
   */
  @Test
  public void inverseThrowsExceptionForZeroQuantity() {
    assertThrows(ArithmeticException.class, () -> {
      createQuantity((byte) 0, Units.OHM).inverse();
    });
  }

  /**
   * Verifies that a ByteQuantity isn't big.
   */
  @Test
  public void byteQuantityIsNotBig() {
    assertFalse(ONE_OHM.isBig());
  }

  /**
   * Verifies that a ByteQuantity isn't decimal.
   */
  @Test
  public void byteQuantityIsNotDecimal() {
    assertFalse(ONE_OHM.isDecimal());
  }
  
  /**
   * Verifies that a ByteQuantity has the size of Byte.
   */
  @Test
  public void byteQuantityHasByteSize() {
    assertEquals(Byte.SIZE, ONE_OHM.getSize());
  }

  /**
   * Verifies that a quantity isn't equal to null.
   */
  @Test
  public void byteQuantityIsNotEqualToNull() {
    assertFalse(ONE_OHM.equals(null));
  }

  /**
   * Verifies that a quantity is equal to itself.
   */
  @Test
  public void byteQuantityIsEqualToItself() {
    assertTrue(ONE_OHM.equals(ONE_OHM));
  }

  /**
   * Verifies that a quantity is equal to another instance with the same value and unit.
   */
  @Test
  public void byteQuantityIsEqualToIdenticalInstance() {
    assertTrue(ONE_OHM.equals(createQuantity((byte) 1, Units.OHM)));
  }

  /**
   * Verifies that a quantity is equal to another instance with the same value and unit using another primitive.
   */
  @Test
  public void byteQuantityIsEqualToIdenticalInstanceWithAnotherPrimitive() {
    assertTrue(ONE_OHM.equals(new DoubleQuantity<ElectricResistance>(Double.valueOf(1).doubleValue(), Units.OHM)));
  }

  /**
   * Verifies that a quantity is not equal to a quantity with a different value.
   */
  @Test
  public void byteQuantityIsNotEqualToQuantityWithDifferentValue() {
    assertFalse(ONE_OHM.equals(TWO_OHM));
  }

  /**
   * Verifies that a quantity is not equal to a quantity with a different unit.
   */
  @Test
  public void byteQuantityIsNotEqualToQuantityWithDifferentUnit() {
    assertFalse(ONE_OHM.equals(ONE_DEKAOHM));
  }

  /**
   * Verifies that a quantity is not equal to an object of a different class.
   */
  @Test
  public void byteQuantityIsNotEqualToObjectOfDifferentClass() {
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
    assertEquals(0.1, ONE_DECIOHM.doubleValue(Units.OHM));
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
    assertEquals(BigDecimal.valueOf(0.1), ONE_DECIOHM.decimalValue(Units.OHM));
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
    assertEquals(0, ONE_DECIOHM.longValue(Units.OHM));
  }

  /**
   * Verifies that an exception is thrown if the conversion for longValue results in a positive overflow.
   */
  @Test
  public void longValueThrowsExceptionOnPositiveOverflow() {
    assertThrows(ArithmeticException.class, () -> {
      createQuantity((byte) 10, MetricPrefix.EXA(Units.OHM)).longValue(Units.OHM);
    });
  }

  /**
   * Verifies that an exception is thrown if the conversion for longValue results in a negative overflow.
   */
  @Test
  public void longValueThrowsExceptionOnNegativeOverflow() {
    assertThrows(ArithmeticException.class, () -> {
      createQuantity((byte) -10, MetricPrefix.EXA(Units.OHM)).longValue(Units.OHM);
    });
  }

  /**
   * Verifies that addition with ByteQuantity returns a ByteQuantity.
   */
  @Test
  public void additionWithByteQuantityDoesNotWiden() {
    assertEquals(ByteQuantity.class, ONE_OHM.add(ONE_OHM).getClass());
  }

  /**
   * Verifies that addition with ShortQuantity widens to ShortQuantity.
   */
  @Test
  public void additionWithShortQuantityWidensToShortQuantity() {
    assertEquals(ShortQuantity.class, ONE_OHM.add(ONE_SHORT_OHM).getClass());
  }

  /**
   * Verifies that subtraction with ByteQuantity returns a ByteQuantity.
   */
  @Test
  public void subtractionWithByteQuantityDoesNotWiden() {
    assertEquals(ByteQuantity.class, ONE_OHM.subtract(ONE_OHM).getClass());
  }

  /**
   * Verifies that subtraction with ShortQuantity widens to ShortQuantity.
   */
  @Test
  public void subtractionWithShortQuantityWidensToShortQuantity() {
    assertEquals(ShortQuantity.class, ONE_OHM.subtract(ONE_SHORT_OHM).getClass());
  }

  /**
   * Verifies that multiplication with ByteQuantity returns a ByteQuantity.
   */
  @Test
  public void multiplicationWithByteQuantityDoesNotWiden() {
    assertEquals(ByteQuantity.class, ONE_OHM.multiply(ONE_OHM).getClass());
  }

  /**
   * Verifies that multiplication with ShortQuantity widens to ShortQuantity.
   */
  @Test
  public void multiplicationWithShortQuantityWidensToShortQuantity() {
    assertEquals(ShortQuantity.class, ONE_OHM.multiply(ONE_SHORT_OHM).getClass());
  }

  /**
   * Verifies that division with ByteQuantity returns a ByteQuantity.
   */
  @Test
  public void divisionWithByteQuantityDoesNotWiden() {
    assertEquals(ByteQuantity.class, ONE_OHM.divide(ONE_OHM).getClass());
  }

  /**
   * Verifies that division with ShortQuantity widens to ShortQuantity.
   */
  @Test
  public void divisionWithShortQuantityWidensToShortQuantity() {
    assertEquals(ShortQuantity.class, ONE_OHM.divide(ONE_SHORT_OHM).getClass());
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
    assertEquals((byte)-1, ONE_OHM.negate().getValue());
  }

  @Test
  public void testEquality() throws Exception {
    Quantity<Length> value = Quantities.getQuantity(Byte.valueOf("1"), Units.METRE);
    Quantity<Length> anotherValue = Quantities.getQuantity(Byte.valueOf("1"), Units.METRE);
    assertEquals(value, anotherValue);
  }
}
