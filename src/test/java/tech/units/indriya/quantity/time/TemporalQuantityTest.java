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
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.AbstractUnit;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.NumberQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/**
 * Unit tests on the {@code TemporalQuantity} class.
 */
public class TemporalQuantityTest {

  private static final Unit<?> SQUARE_MINUTE = Units.MINUTE.multiply(Units.MINUTE);

  private static final TemporalQuantity ONE_CHRONO_MILLISECOND = TemporalQuantity.of(1, ChronoUnit.MILLIS);
  private static final TemporalQuantity ONE_CHRONO_SECOND = TemporalQuantity.of(1, ChronoUnit.SECONDS);
  private static final TemporalQuantity THOUSAND_ONE_CHRONO_MILLISECONDS = TemporalQuantity.of(1001, ChronoUnit.MILLIS);
  private static final TemporalQuantity MAX_VALUE_CHRONO_MILLISECONDS = TemporalQuantity.of(Long.MAX_VALUE, ChronoUnit.MILLIS);
  private static final TemporalQuantity FORTY_TWO_CHRONO_MINUTES = TemporalQuantity.of(42, ChronoUnit.MINUTES);
  private static final TemporalQuantity FORTY_TWO_MINUTES = TemporalQuantity.of(Quantities.getQuantity(42L, Units.MINUTE));

  /**
   * Verifies that the factory method {@code of} with a long value and a temporal unit has the value wired correctly.
   */
  @Test
  public void valueWiredCorrectlyInFactoryMethodOfWithLongValueAndTemporalUnit() {
    final Long testValue = 42L;
    TemporalQuantity quantity = TemporalQuantity.of(testValue, ChronoUnit.MINUTES);
    assertEquals(testValue, quantity.getValue());
  }

  /**
   * Verifies that the factory method {@code of} with a long value and a temporal unit has the temporal unit wired correctly.
   */
  @Test
  public void temporalUnitWiredCorrectlyInFactoryMethodOfWithLongValueAndTemporalUnit() {
    final TemporalUnit testUnit = ChronoUnit.MINUTES;
    TemporalQuantity quantity = TemporalQuantity.of(42L, testUnit);
    assertEquals(testUnit, quantity.getTemporalUnit());
  }

  /**
   * Verifies that the temporal unit is converted to a unit by the factory method {@code of} with a long value and a temporal unit.
   */
  @Test
  public void temporalUnitIsConvertedToAUnitInFactoryMethodWithLongValueAndTemporalUnit() {
    TemporalQuantity quantity = TemporalQuantity.of(42L, ChronoUnit.MINUTES);
    assertEquals(Units.MINUTE, quantity.getUnit());
  }

  /**
   * Verifies that the temporal amount is calculated correctly by the factory method {@code of} with a long value and a temporal unit.
   */
  @Test
  public void temporalAmountIsCalculatedCorrectlyByFactoryMethodWithLongValueAndTemporalUnit() {
    TemporalQuantity quantity = TemporalQuantity.of(42L, ChronoUnit.MINUTES);
    assertEquals(Duration.ofMinutes(42), quantity.getTemporalAmount());
  }

  /**
   * Verifies that the factory method {@code of} with an integer value and a temporal unit has the value wired correctly.
   */
  @Test
  public void valueWiredCorrectlyInFactoryMethodOfWithIntegerValueAndTemporalUnit() {
    final Integer testValue = 42;
    TemporalQuantity quantity = TemporalQuantity.of(testValue, ChronoUnit.MINUTES);
    assertEquals(Long.valueOf(testValue), quantity.getValue());
  }

  /**
   * Verifies that the factory method {@code of} with an integer value and a temporal unit has the temporal unit wired correctly.
   */
  @Test
  public void temporalUnitWiredCorrectlyInFactoryMethodOfWithIntegerValueAndTemporalUnit() {
    final TemporalUnit testUnit = ChronoUnit.MINUTES;
    TemporalQuantity quantity = TemporalQuantity.of(42, testUnit);
    assertEquals(testUnit, quantity.getTemporalUnit());
  }

  /**
   * Verifies that the temporal unit is converted to a unit by the factory method {@code of} with an integer value and a temporal unit.
   */
  @Test
  public void temporalUnitIsConvertedToAUnitInFactoryMethodWithIntegerValueAndTemporalUnit() {
    TemporalQuantity quantity = FORTY_TWO_CHRONO_MINUTES;
    assertEquals(Units.MINUTE, quantity.getUnit());
  }

  /**
   * Verifies that the temporal amount is calculated correctly by the factory method {@code of} with an integer value and a temporal unit.
   */
  @Test
  public void temporalAmountIsCalculatedCorrectlyByFactoryMethodWithIntegerValueAndTemporalUnit() {
    TemporalQuantity quantity = FORTY_TWO_CHRONO_MINUTES;
    assertEquals(Duration.ofMinutes(42), quantity.getTemporalAmount());
  }

  /**
   * Verifies that when a time quantity is provided, the value is calculated correctly in terms of seconds.
   */
  @Test
  public void valueIsCalculatedCorrectlyFromTimeQuantity() {
    TemporalQuantity quantity = FORTY_TWO_MINUTES;
    final Long expected = 42L * 60L;
    assertEquals(expected, quantity.getValue());
  }

  /**
   * Verifies that when a time quantity is provided, the temporal unit is seconds.
   */
  @Test
  public void temporalUnitIsSetToSecondsForFactoryMethodWithTimeQuantity() {
    assertEquals(ChronoUnit.SECONDS, FORTY_TWO_MINUTES.getTemporalUnit());
  }

  /**
   * Verifies that when a time quantity is provided, the unit is seconds.
   */
  @Test
  public void unitIsSetToSecondsForFactoryMethodWithTimeQuantity() {
    assertEquals(Units.SECOND, FORTY_TWO_MINUTES.getUnit());
  }

  /**
   * Verifies that the temporal amount is calculated correctly by the factory method {@code of} with a time quantity.
   */
  @Test
  public void temporalAmountIsCalculatedCorrectlyByFactoryMethodWithTimeQuantity() {
    assertEquals(Duration.ofMinutes(42), FORTY_TWO_MINUTES.getTemporalAmount());
  }

  /**
   * Verifies that an IllegalArgumentException is thrown if a temporal quantity with HALF_DAYS is passed to the factory method.
   */
  @Test
  public void factoryMethodThrowsIllegalArgumentExceptionWhenUsingIllegalChronoUnit() {
    assertThrows(IllegalArgumentException.class, () -> {
      TemporalQuantity.of(42, ChronoUnit.HALF_DAYS);
    });
  }

  /**
   * Verifies that an IllegalArgumentException is thrown if another temporal unit than ChronoUnit is passed to the factory method.
   */
  @Test
  public void factoryMethodThrowsIllegalArgumentExceptionWhenUsingIllegalTemporalUnit() {
    assertThrows(IllegalArgumentException.class, () -> {
      TemporalQuantity.of(42, new TemporalUnit() {

        @Override
        public <R extends Temporal> R addTo(R arg0, long arg1) {
          return null;
        }

        @Override
        public long between(Temporal arg0, Temporal arg1) {
          return 0;
        }

        @Override
        public Duration getDuration() {
          return null;
        }

        @Override
        public boolean isDateBased() {
          return false;
        }

        @Override
        public boolean isDurationEstimated() {
          return false;
        }

        @Override
        public boolean isTimeBased() {
          return false;
        }
      });
    });
  }

  /**
   * Verifies that toString produces the correct result.
   */
  @Test
  public void toStringProducesTheCorrectResult() {
    assertEquals("Temporal unit:Seconds value: 2520", FORTY_TWO_MINUTES.toString());
  }

  /**
   * Verifies that a temporal quantity is equal to itself.
   */
  @Test
  public void temporalQuantityIsEqualToItself() {
    assertTrue(FORTY_TWO_MINUTES.equals(FORTY_TWO_MINUTES));
  }

  /**
   * Verifies that a temporal quantity is not equal to null.
   */
  @Test
  public void temporalQuantityIsNotEqualToNull() {
    assertFalse(FORTY_TWO_MINUTES.equals(null));
  }

  /**
   * Verifies that a temporal quantity is equal to another temporal quantity instantiated from an equal quantity.
   */
  @Test
  public void temporalQuantityIsEqualToTemporalQuantityInstantiatedFromAnEqualQuantity() {
    assertTrue(FORTY_TWO_MINUTES.equals(TemporalQuantity.of(Quantities.getQuantity(42L, Units.MINUTE))));
  }

  /**
   * Verifies that a temporal quantity is not equal to another temporal quantity with a different value.
   */
  @Test
  public void temporalQuantityIsNotEqualToTemporalQuantityWithAnotherValue() {
    assertFalse(FORTY_TWO_MINUTES.equals(TemporalQuantity.of(Quantities.getQuantity(43L, Units.MINUTE))));
  }

  /**
   * Verifies that a temporal quantity is not equal to another temporal quantity with a different unit.
   */
  @Test
  public void temporalQuantityIsNotEqualToTemporalQuantityWithAnotherUnit() {
    assertFalse(FORTY_TWO_MINUTES.equals(TemporalQuantity.of(Quantities.getQuantity(42L, Units.HOUR))));
  }

  /**
   * Verifies that a temporal quantity is not equal to an object of another class.
   */
  @Test
  public void temporalQuantityIsNotEqualToObjectOfAnotherClass() {
    assertFalse(FORTY_TWO_MINUTES.equals(Units.HOUR));
  }

  /**
   * Verifies that a temporal quantity has the same hashCode as another temporal quantity instantiated from an equal quantity.
   */
  @Test
  public void temporalQuantityHasSameHashCodeAsTemporalQuantityInstantiatedFromAnEqualQuantity() {
    assertEquals(FORTY_TWO_MINUTES.hashCode(), TemporalQuantity.of(Quantities.getQuantity(42L, Units.MINUTE)).hashCode());
  }

  /**
   * Verifies that a temporal quantity doesn't have the same hashCode as another temporal quantity with a different value. Note that this isn't a
   * requirement for the hashCode method, but generally a good property to have.
   */
  @Test
  public void temporalQuantityHasDifferentHashCodeThanTemporalQuantityWithADifferentValue() {
    assertFalse(FORTY_TWO_MINUTES.hashCode() == TemporalQuantity.of(Quantities.getQuantity(43L, Units.MINUTE)).hashCode());
  }

  /**
   * Verifies that a temporal quantity doesn't have the same hashCode as another temporal quantity with a different unit. Note that this isn't a
   * requirement for the hashCode method, but generally a good property to have.
   */
  @Test
  public void temporalQuantityHasDifferentHashCodeThanTemporalQuantityWithADifferentUnit() {
    assertFalse(FORTY_TWO_MINUTES.hashCode() == TemporalQuantity.of(Quantities.getQuantity(42L, Units.HOUR)).hashCode());
  }

  /**
   * Verifies that a TemporalQuantity isn't big.
   */
  @Test
  public void temporalQuantityIsNotBig() {
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
   * Verifies that addition with a quantity with the same chrono unit preserves the chrono unit.
   */
  @Test
  public void additionWithSameChronoUnitPreservesChronoUnit() {
    ComparableQuantity<Time> actual = FORTY_TWO_CHRONO_MINUTES.add(FORTY_TWO_CHRONO_MINUTES);
    ComparableQuantity<Time> expected = TemporalQuantity.of(84, ChronoUnit.MINUTES);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the addition of two quantities with the same chrono unit resulting in an overflow throws an exception.
   */
  @Test
  public void additionWithSameChronoUnitResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      ONE_CHRONO_MILLISECOND.add(MAX_VALUE_CHRONO_MILLISECONDS);
    });
  }

  /**
   * Verifies that adding a quantity with a larger chrono unit keeps the result to the smaller chrono unit.
   */
  @Test
  public void additionWithLargerChronoUnitKeepsSmallerChronoUnit() {
    ComparableQuantity<Time> actual = ONE_CHRONO_MILLISECOND.add(ONE_CHRONO_SECOND);
    assertEquals(THOUSAND_ONE_CHRONO_MILLISECONDS, actual);
  }

  /**
   * Verifies that adding a quantity with a smaller chrono unit casts the result to the smaller chrono unit.
   */
  @Test
  public void additionWithSmallerChronoUnitCastsToSmallerChronoUnitIfNeeded() {
    ComparableQuantity<Time> actual = ONE_CHRONO_SECOND.add(ONE_CHRONO_MILLISECOND);
    assertEquals(THOUSAND_ONE_CHRONO_MILLISECONDS, actual);
  }

  /**
   * Verifies that adding a quantity with a larger chrono unit resulting in an overflowing sum casts the result to the larger chrono unit.
   */
  @Test
  public void additionWithLargerChronoUnitAndOverflowingResultCastsToLargerChronoUnit() {
    ComparableQuantity<Time> actual = MAX_VALUE_CHRONO_MILLISECONDS.add(ONE_CHRONO_SECOND);
    ComparableQuantity<Time> expected = TemporalQuantity.of(1 + Long.MAX_VALUE / 1000, ChronoUnit.SECONDS);
    assertEquals(expected, actual);
  }

  /**
   * Subtraction subtracts correctly.
   */
  @Test
  public void subtractionSubtractsCorrectly() {
    ComparableQuantity<Time> actual = FORTY_TWO_CHRONO_MINUTES.subtract(TemporalQuantity.of(1, ChronoUnit.MINUTES));
    ComparableQuantity<Time> expected = TemporalQuantity.of(41, ChronoUnit.MINUTES);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the multiplication of two quantities multiplies correctly.
   */
  @Test
  public void quantityMultiplicationMultipliesCorrectly() {
    Quantity<?> actual = FORTY_TWO_CHRONO_MINUTES.multiply(FORTY_TWO_CHRONO_MINUTES);
    AbstractQuantity<?> expected = NumberQuantity.of(1764L, SQUARE_MINUTE);
    assertEquals(expected, actual);
  }
  
  /**
   * Verifies that the multiplication of two quantities resulting in an overflow throws an exception.
   */
  @Test
  public void quantityMultiplicationResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      TemporalQuantity halfMaxValuePlusOne = TemporalQuantity.of(514L + Long.MAX_VALUE / 2L,  ChronoUnit.SECONDS);
      halfMaxValuePlusOne.multiply(TemporalQuantity.of(2L, ChronoUnit.SECONDS));
    });
  }
  
  /**
   * Verifies that the division of two quantities divides correctly.
   */
  @Test
  public void quantityDivisionDividesCorrectly() {
    Quantity<?> actual = FORTY_TWO_CHRONO_MINUTES.divide(FORTY_TWO_CHRONO_MINUTES);
    AbstractQuantity<Dimensionless> expected = NumberQuantity.of(1L, AbstractUnit.ONE);
    assertEquals(expected, actual);
  }
  
  /**
   * Verifies that the multiplication with a number multiplies correctly.
   */
  @Test
  public void numberMultiplicationMultipliesCorrectly() {
    Quantity<?> actual = FORTY_TWO_CHRONO_MINUTES.multiply(2L);
    TemporalQuantity expected = TemporalQuantity.of(84L, ChronoUnit.MINUTES);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the multiplication with a number resulting in an overflow throws an exception.
   */
  @Test
  public void numberMultiplicationResultingInOverflowThrowsException() {
    assertThrows(ArithmeticException.class, () -> {
      TemporalQuantity halfMaxValuePlusOne = TemporalQuantity.of(514L + Long.MAX_VALUE / 2L,  ChronoUnit.SECONDS);
      halfMaxValuePlusOne.multiply(2L);
    });
  }
  
  /**
   * Verifies that the division with a number divides correctly.
   */
  @Test
  public void numberDivisionDividesCorrectly() {
    Quantity<?> actual = FORTY_TWO_CHRONO_MINUTES.divide(2L);
    TemporalQuantity expected = TemporalQuantity.of(21, ChronoUnit.MINUTES);
    assertEquals(expected, actual);
  }

}