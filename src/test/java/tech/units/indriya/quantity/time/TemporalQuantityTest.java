package tech.units.indriya.quantity.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/**
 * Unit tests on the {@code TemporalQuantity} class.
 */
public class TemporalQuantityTest {

  private static final TemporalQuantity FORTY_TWO_MINUTES = TemporalQuantity.of(Quantities.getQuantity(42, Units.MINUTE));

  /**
   * Verifies that the factory method {@code of} with a value and a temporal unit has the value wired correctly.
   */
  @Test
  public void valueWiredCorrectlyInFactoryMethodOfWithValueAndTemporalUnit() {
    final Integer testValue = 42;
    TemporalQuantity quantity = TemporalQuantity.of(testValue, ChronoUnit.MINUTES);
    assertEquals(testValue, quantity.getValue());
  }

  /**
   * Verifies that the factory method {@code of} with a value and a temporal unit has the temporal unit wired correctly.
   */
  @Test
  public void temporalUnitWiredCorrectlyInFactoryMethodOfWithValueAndTemporalUnit() {
    final TemporalUnit testUnit = ChronoUnit.MINUTES;
    TemporalQuantity quantity = TemporalQuantity.of(42, testUnit);
    assertEquals(testUnit, quantity.getTemporalUnit());
  }

  /**
   * Verifies that the temporal unit is converted to a unit by the factory method {@code of} with a value and a temporal unit.
   */
  @Test
  public void temporalUnitIsConvertedToAUnitInFactoryMethodWithValueAndTemporalUnit() {
    TemporalQuantity quantity = TemporalQuantity.of(42, ChronoUnit.MINUTES);
    assertEquals(Units.MINUTE, quantity.getUnit());
  }

  /**
   * Verifies that the temporal amount is calculated correctly by the factory method {@code of} with a value and a temporal unit.
   */
  @Test
  public void temporalAmountIsCalculatedCorrectlyByFactoryMethodWithValueAndTemporalUnit() {
    TemporalQuantity quantity = TemporalQuantity.of(42, ChronoUnit.MINUTES);
    assertEquals(Duration.ofMinutes(42), quantity.getTemporalAmount());
  }

  /**
   * Verifies that when a time quantity is provided, the value is calculated correctly in terms of seconds.
   */
  @Test
  public void valueIsCalculatedCorrectlyFromTimeQuantity() {
    TemporalQuantity quantity = FORTY_TWO_MINUTES;
    final Integer expected = 42 * 60;
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
    assertTrue(FORTY_TWO_MINUTES.equals(TemporalQuantity.of(Quantities.getQuantity(42, Units.MINUTE))));
  }

}
