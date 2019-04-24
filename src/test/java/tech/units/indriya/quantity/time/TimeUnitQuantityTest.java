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
package tech.units.indriya.quantity.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/**
 * Unit tests on the {@code TimeUnitQuantity} class.
 */
public class TimeUnitQuantityTest {

  private static final Unit<?> SQUARE_MINUTE = Units.MINUTE.multiply(Units.MINUTE);

  private static final TimeUnitQuantity FORTY_TWO_MINUTES = TimeUnitQuantity.of(42L, TimeUnit.MINUTES);
  private static final TimeUnitQuantity ONE_SECOND = TimeUnitQuantity.of(1L, TimeUnit.SECONDS);
  private static final TimeUnitQuantity ONE_MILLISECOND = TimeUnitQuantity.of(1L, TimeUnit.MILLISECONDS);
  private static final TimeUnitQuantity THOUSAND_ONE_MILLISECONDS = TimeUnitQuantity.of(1001L, TimeUnit.MILLISECONDS);
  private static final TimeUnitQuantity MAX_VALUE_MILLISECONDS = TimeUnitQuantity.of(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
  private static final Quantity<Time> FORTY_TWO_MINUTES_TIME_QUANTITY = Quantities.getQuantity(42L, Units.MINUTE);

  /**
   * Verifies that the factory method {@code of} with a long value and a time unit has the value wired correctly.
   */
  @Test
  public void valueWiredCorrectlyInFactoryMethodOfWithLongValueAndTimeUnit() {
    final Long testValue = 42L;
    TimeUnitQuantity quantity = TimeUnitQuantity.of(testValue, TimeUnit.MINUTES);
    assertEquals(testValue, quantity.getValue());
  }

  /**
   * Verifies that the factory method {@code of} with a long value and a time unit has the time unit wired correctly.
   */
  @Test
  public void timeUnitWiredCorrectlyInFactoryMethodOfWithLongValueAndTimeUnit() {
    final TimeUnit testUnit = TimeUnit.MINUTES;
    TimeUnitQuantity quantity = TimeUnitQuantity.of(42L, testUnit);
    assertEquals(testUnit, quantity.getTimeUnit());
  }

  /**
   * Verifies that the factory method {@code of} with an integer value and a time unit has the value wired correctly.
   */
  @Test
  public void valueWiredCorrectlyInFactoryMethodOfWithIntegerValueAndTimeUnit() {
    final Integer testValue = 42;
    TimeUnitQuantity quantity = TimeUnitQuantity.of(testValue, TimeUnit.MINUTES);
    assertEquals(Long.valueOf(testValue), quantity.getValue());
  }

  /**
   * Verifies that the factory method {@code of} with an integer value and a time unit has the time unit wired correctly.
   */
  @Test
  public void timeUnitWiredCorrectlyInFactoryMethodOfWithIntegerValueAndTimeUnit() {
    final TimeUnit testUnit = TimeUnit.MINUTES;
    TimeUnitQuantity quantity = TimeUnitQuantity.of(42, testUnit);
    assertEquals(testUnit, quantity.getTimeUnit());
  }

  /**
   * Verifies that the factory method {@code of} with a time unit and an integer value has the value wired correctly.
   */
  @Test
  public void valueWiredCorrectlyInFactoryMethodOfWithTimeUnitAndIntegerValue() {
    final Integer testValue = 42;
    TimeUnitQuantity quantity = TimeUnitQuantity.of(TimeUnit.MINUTES, testValue);
    assertEquals(Long.valueOf(testValue), quantity.getValue());
  }

  /**
   * Verifies that the factory method {@code of} with a time unit and an integer value has the time unit wired correctly.
   */
  @Test
  public void timeUnitWiredCorrectlyInFactoryMethodOfWithTimeUnitAndIntegerValue() {
    final TimeUnit testUnit = TimeUnit.MINUTES;
    TimeUnitQuantity quantity = TimeUnitQuantity.of(testUnit, 42);
    assertEquals(testUnit, quantity.getTimeUnit());
  }

  /**
   * Verifies that when a time quantity is provided to the factory method {@code of}, the value is calculated correctly in terms of seconds.
   */
  @Test
  public void valueIsCalculatedCorrectlyFromTimeQuantity() {
    TimeUnitQuantity quantity = TimeUnitQuantity.of(FORTY_TWO_MINUTES_TIME_QUANTITY);
    final Long expected = 42L * 60L;
    assertEquals(expected, quantity.getValue());
  }

  /**
   * Verifies that when a time quantity is provided to the factory method {@code of}, the time unit is seconds.
   */
  @Test
  public void temporalUnitIsSetToSecondsForFactoryMethodWithTimeQuantity() {
    TimeUnitQuantity quantity = TimeUnitQuantity.of(FORTY_TWO_MINUTES_TIME_QUANTITY);
    assertEquals(TimeUnit.SECONDS, quantity.getTimeUnit());
  }

  /**
   * Verifies that when a time quantity is provided to the factory method {@code of}, the unit is seconds.
   */
  @Test
  public void unitIsSetToSecondsForFactoryMethodWithTimeQuantity() {
    TimeUnitQuantity quantity = TimeUnitQuantity.of(FORTY_TWO_MINUTES_TIME_QUANTITY);
    assertEquals(Units.SECOND, quantity.getUnit());
  }

  /**
   * Verifies that the {@code toUnit} method returns the corresponding unit.
   */
  @Test
  public void toUnitReturnsTheCorrespondingUnit() {
    assertEquals(Units.MINUTE, FORTY_TWO_MINUTES.toUnit());
  }

  /**
   * Verifies that the {@code toQuantity} method converts to a quantity correctly.
   */
  @Test
  public void toQuantityConvertsCorrectly() {
    assertEquals(FORTY_TWO_MINUTES_TIME_QUANTITY, FORTY_TWO_MINUTES.toQuantity());
  }

  /**
   * Verifies that a time unit quantity is equal to itself.
   */
  @Test
  public void timeUnitQuantityIsEqualToItself() {
    assertTrue(FORTY_TWO_MINUTES.equals(FORTY_TWO_MINUTES));
  }

  /**
   * Verifies that a time unit quantity is not equal to null.
   */
  @Test
  public void timeUnitQuantityIsNotEqualToNull() {
    assertFalse(FORTY_TWO_MINUTES.equals(null));
  }

  /**
   * Verifies that a time unit quantity is equal to another time unit quantity instantiated with the same value and time unit.
   */
  @Test
  public void timeUnitQuantityIsEqualToTimeUnitQuantityInstantiatedFromSameValueAndTimeUnit() {
    assertTrue(FORTY_TWO_MINUTES.equals(TimeUnitQuantity.of(42L, TimeUnit.MINUTES)));
  }

  /**
   * Verifies that a time unit quantity is not equal to another time unit quantity with a different value.
   */
  @Test
  public void timeUnitQuantityIsNotEqualToTimeUnitQuantityWithAnotherValue() {
    assertFalse(FORTY_TWO_MINUTES.equals(TimeUnitQuantity.of(43L, TimeUnit.MINUTES)));
  }

  /**
   * Verifies that a time unit quantity is not equal to another time unit quantity with a different time unit.
   */
  @Test
  public void timeUnitQuantityIsNotEqualToTimeUnitQuantityWithAnotherTimeUnit() {
    assertFalse(FORTY_TWO_MINUTES.equals(TimeUnitQuantity.of(42L, TimeUnit.HOURS)));
  }

  /**
   * Verifies that a time unit quantity is not equal to an object of another class.
   */
  @Test
  public void timeUnitQuantityIsNotEqualToObjectOfAnotherClass() {
    assertFalse(FORTY_TWO_MINUTES.equals(Units.HOUR));
  }

  /**
   * Verifies that a time unit quantity has the same hashCode as another time unit quantity instantiated with the same value and time unit.
   */
  @Test
  public void timeUnitQuantityHasSameHashCodeAsTimeUnitQuantityInstantiatedWithSameValueAndTimeUnit() {
    assertEquals(FORTY_TWO_MINUTES.hashCode(), TimeUnitQuantity.of(42L, TimeUnit.MINUTES).hashCode());
  }

  /**
   * Verifies that a time unit quantity doesn't have the same hashCode as another time unit quantity with a different value. Note that this isn't a
   * requirement for the hashCode method, but generally a good property to have.
   */
  @Test
  public void timeUnitQuantityHasDifferentHashCodeThanTimeUnitQuantityWithADifferentValue() {
    assertFalse(FORTY_TWO_MINUTES.hashCode() == TimeUnitQuantity.of(43L, TimeUnit.MINUTES).hashCode());
  }

  /**
   * Verifies that a time unit quantity doesn't have the same hashCode as another time unit quantity with a different unit. Note that this isn't a
   * requirement for the hashCode method, but generally a good property to have.
   */
  @Test
  public void timeUnitQuantityHasDifferentHashCodeThanTimeUnitQuantityWithADifferentTimeUnit() {
    assertFalse(FORTY_TWO_MINUTES.hashCode() == TimeUnitQuantity.of(42L, TimeUnit.HOURS).hashCode());
  }

  /**
   * Verifies that toString produces the correct result.
   */
  @Test
  public void toStringProducesTheCorrectResult() {
    assertEquals("Time unit:MINUTES value: 42", FORTY_TWO_MINUTES.toString());
  }

  /**
   * Verifies that a TimeUnitQuantity isn't big.
   */
  @Test
  public void timeUnitQuantityIsNotBig() {
    assertFalse(FORTY_TWO_MINUTES.isBig());
  }

  /**
   * Verifies that the value is returned without conversion if decimalValue is called with the quantity's unit.
   */
  @Test
  public void decimalValueReturnsValueForSameUnit() {
    assertEquals(BigDecimal.valueOf(42), FORTY_TWO_MINUTES.decimalValue(Units.MINUTE));
  }

  /**
   * Verifies that the value is correctly converted if decimalValue is called with another unit than the quantity's unit.
   */
  @Test
  public void decimalValueReturnsConvertedValueForOtherUnit() {
    assertEquals(BigDecimal.valueOf(0.7), FORTY_TWO_MINUTES.decimalValue(Units.HOUR));
  }

  /**
   * Verifies that the value is returned without conversion if doubleValue is called with the quantity's unit.
   */
  @Test
  public void doubleValueReturnsValueForSameUnit() {
    assertEquals(42, FORTY_TWO_MINUTES.doubleValue(Units.MINUTE));
  }

  /**
   * Verifies that the value is correctly converted if doubleValue is called with another unit than the quantity's unit.
   */
  @Test
  public void doubleValueReturnsConvertedValueForOtherUnit() {
    assertEquals(0.7, FORTY_TWO_MINUTES.doubleValue(Units.HOUR));
  }

  /**
   * Verifies that addition with a quantity with the same time unit preserves the time unit.
   */
  @Test
  public void additionWithSameTimeUnitPreservesTimeUnit() {
    ComparableQuantity<Time> actual = FORTY_TWO_MINUTES.add(FORTY_TWO_MINUTES);
    ComparableQuantity<Time> expected = TimeUnitQuantity.of(84L, TimeUnit.MINUTES);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the addition of two quantities with the same time unit resulting in an overflow throws an exception.
   */
  @Test
  public void additionWithSameTimeUnitResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      ONE_MILLISECOND.add(MAX_VALUE_MILLISECONDS);
    });
  }

  /**
   * Verifies that addition with a quantity up to the max value doesn't cause an overflow.
   */
  @Test
  public void additionUpToMaxValueDoesNotCauseOverflow() {
    ComparableQuantity<Time> actual = ONE_MILLISECOND.add(TimeUnitQuantity.of(Long.MAX_VALUE - 1L, TimeUnit.MILLISECONDS));
    assertEquals(MAX_VALUE_MILLISECONDS, actual);
  }

  /**
   * Verifies that addition with a quantity down to the min value doesn't cause an overflow.
   */
  @Test
  public void subtractionDownToMinValueDoesNotCauseOverflow() {
    ComparableQuantity<Time> actual = TimeUnitQuantity.of(Long.MIN_VALUE + 1L, TimeUnit.MILLISECONDS).subtract(ONE_MILLISECOND);
    ComparableQuantity<Time> expected = TimeUnitQuantity.of(Long.MIN_VALUE, TimeUnit.MILLISECONDS);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that adding a quantity with a larger time unit keeps the result to the smaller time unit.
   */
  @Test
  public void additionWithLargerTimeUnitKeepsSmallerTimeUnit() {
    ComparableQuantity<Time> actual = ONE_MILLISECOND.add(ONE_SECOND);
    assertEquals(THOUSAND_ONE_MILLISECONDS, actual);
  }

  /**
   * Verifies that adding a quantity with a smaller time unit casts the result to the smaller time unit.
   */
  @Test
  public void additionWithSmallerTimeUnitCastsToSmallerTimeUnitIfNeeded() {
    ComparableQuantity<Time> actual = ONE_SECOND.add(ONE_MILLISECOND);
    assertEquals(THOUSAND_ONE_MILLISECONDS, actual);
  }

  /**
   * Verifies that adding a quantity with a larger time unit resulting in an overflowing sum casts the result to the larger time unit.
   */
  @Test
  public void additionWithLargerTimeUnitAndOverflowingResultCastsToLargerTimeUnit() {
    ComparableQuantity<Time> actual = MAX_VALUE_MILLISECONDS.add(ONE_SECOND);
    ComparableQuantity<Time> expected = TimeUnitQuantity.of(1L + Long.MAX_VALUE / 1000L, TimeUnit.SECONDS);
    assertEquals(expected, actual);
  }

  /**
   * Subtraction subtracts correctly.
   */
  @Test
  public void subtractionSubtractsCorrectly() {
    ComparableQuantity<Time> actual = FORTY_TWO_MINUTES.subtract(TimeUnitQuantity.of(1L, TimeUnit.MINUTES));
    ComparableQuantity<Time> expected = TimeUnitQuantity.of(41L, TimeUnit.MINUTES);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the multiplication of two quantities multiplies correctly.
   */
  @Test
  public void quantityMultiplicationMultipliesCorrectly() {
    Quantity<?> actual = FORTY_TWO_MINUTES.multiply(FORTY_TWO_MINUTES);
    Quantity<?> expected = Quantities.getQuantity(1764L, SQUARE_MINUTE);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the multiplication of two quantities resulting in an overflow throws an exception.
   */
  @Test
  public void quantityMultiplicationResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      TimeUnitQuantity halfMaxValuePlusOne = TimeUnitQuantity.of(1L + Long.MAX_VALUE / 2L, TimeUnit.SECONDS);
      halfMaxValuePlusOne.multiply(TimeUnitQuantity.of(2L, TimeUnit.SECONDS));
    });
  }

  /**
   * Verifies that the division of two quantities divides correctly.
   */
  @Test
  public void quantityDivisionDividesCorrectly() {
    Quantity<?> actual = FORTY_TWO_MINUTES.divide(FORTY_TWO_MINUTES);
    Quantity<Dimensionless> expected = Quantities.getQuantity(1L, AbstractUnit.ONE);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the multiplication with a number multiplies correctly.
   */
  @Test
  public void numberMultiplicationMultipliesCorrectly() {
    Quantity<?> actual = FORTY_TWO_MINUTES.multiply(2L);
    TimeUnitQuantity expected = TimeUnitQuantity.of(84L, TimeUnit.MINUTES);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the multiplication with a number resulting in an overflow throws an exception.
   */
  @Test
  public void numberMultiplicationResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      TimeUnitQuantity halfMaxValuePlusOne = TimeUnitQuantity.of(1L + Long.MAX_VALUE / 2L, TimeUnit.SECONDS);
      halfMaxValuePlusOne.multiply(2L);
    });
  }

  /**
   * Verifies that the division with a number divides correctly.
   */
  @Test
  public void numberDivisionDividesCorrectly() {
    Quantity<?> actual = FORTY_TWO_MINUTES.divide(2L);
    TimeUnitQuantity expected = TimeUnitQuantity.of(21L, TimeUnit.MINUTES);
    assertEquals(expected, actual);
  }

}
