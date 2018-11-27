package tech.units.indriya.quantity.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/**
 * Unit tests on the {@code TimeUnitQuantity} class.
 */
public class TimeUnitQuantityTest {

  private static final TimeUnitQuantity FORTY_TWO_MINUTES = TimeUnitQuantity.of(42L, TimeUnit.MINUTES);
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

}
