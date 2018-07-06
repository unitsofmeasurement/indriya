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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static tech.units.indriya.unit.Units.METRE;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.spi.QuantityFactory;

import org.junit.jupiter.api.Test;

import tech.units.indriya.unit.Units;

/**
 * @author Werner Keil
 */
public class DefaultQuantityFactoryTest {

  private QuantityFactory<Length> lengthFactory = DefaultQuantityFactory.getInstance(Length.class);
  private QuantityFactory<QuantityInterface> testFactory = DefaultQuantityFactory.getInstance(QuantityInterface.class);

  /**
   * Local quantity interface to test the instance methods on.
   */
  interface QuantityInterface extends Quantity<QuantityInterface> {
  }

  /**
   * Verifies that the factory throws an exception if an instance is requested for null.
   */
  @Test
  public void getInstanceThrowsExceptionForNull() {
    assertThrows(Exception.class, () -> {
      DefaultQuantityFactory.getInstance(null);
    });
  }

  /**
   * Local quantity interface that hasn't been registered in the factory yet. This interface should only be used by the unit test method verifying
   * that the factory creates an instance for an unregistered quantity interface.
   */
  interface OneTimeUnregisteredQuantityInterface extends Quantity<OneTimeUnregisteredQuantityInterface> {
  }

  /**
   * Verifies that the factory returns a new instance for an unregistered quantity interface.
   */
  @Test
  public void getInstanceCreatesAFactoryForAnUnregisteredQuantityInterface() {
    assertNotNull(DefaultQuantityFactory.getInstance(OneTimeUnregisteredQuantityInterface.class));
  }

  /**
   * Local quantity interface that hasn't been registered in the factory yet. This interface should only be used by the unit test method verifying
   * that the factory doesn't create multiple instances for the same quantity interface.
   */
  interface TwoTimesUnregisteredQuantityInterface extends Quantity<TwoTimesUnregisteredQuantityInterface> {
  }

  /**
   * Verifies that the factory doesn't create a new instance the second time a previously unregistered quantity interface is requested.
   */
  @Test
  public void getInstanceDoesNotCreateTwoFactoriesForAnUnregisteredQuantityInterface() {
    QuantityFactory<TwoTimesUnregisteredQuantityInterface> instance1 = DefaultQuantityFactory
        .getInstance(TwoTimesUnregisteredQuantityInterface.class);
    QuantityFactory<TwoTimesUnregisteredQuantityInterface> instance2 = DefaultQuantityFactory
        .getInstance(TwoTimesUnregisteredQuantityInterface.class);
    assertTrue(instance1 == instance2);
  }

  /**
   * Local quantity class that hasn't been registered in the factory yet. This class should only be used by the unit test method verifying that the
   * factory creates an instance for an unregistered quantity class.
   */
  abstract class OneTimeUnregisteredQuantityClass implements Quantity<OneTimeUnregisteredQuantityClass> {
  }

  /**
   * Verifies that the factory returns a new instance for an unregistered quantity class.
   */
  @Test
  public void getInstanceCreatesAFactoryForAnUnregisteredQuantityClass() {
    assertNotNull(DefaultQuantityFactory.getInstance(OneTimeUnregisteredQuantityClass.class));
  }

  /**
   * Local quantity class that hasn't been registered in the factory yet. This class should only be used by the unit test method verifying that the
   * factory doesn't create multiple instances for the same quantity class.
   */
  abstract class TwoTimesUnregisteredQuantityClass implements Quantity<TwoTimesUnregisteredQuantityClass> {
  }

  /**
   * Verifies that the factory doesn't create a new instance the second time a previously unregistered quantity class is requested.
   */
  @Test
  public void getInstanceDoesNotCreateTwoFactoriesForAnUnregisteredQuantityClass() {
    QuantityFactory<TwoTimesUnregisteredQuantityClass> instance1 = DefaultQuantityFactory.getInstance(TwoTimesUnregisteredQuantityClass.class);
    QuantityFactory<TwoTimesUnregisteredQuantityClass> instance2 = DefaultQuantityFactory.getInstance(TwoTimesUnregisteredQuantityClass.class);
    assertTrue(instance1 == instance2);
  }

  /**
   * Local quantity class that hasn't been registered in the factory yet and that implements a non-quantity interface too. This class should only be
   * used by the unit test method verifying that the factory creates an instance for an unregistered quantity class implementing an extra interface.
   */
  abstract class OneTimeComparableUnregisteredQuantityClass
      implements Comparable<OneTimeComparableUnregisteredQuantityClass>, Quantity<OneTimeComparableUnregisteredQuantityClass> {
  }

  /**
   * Verifies that the factory returns a new instance for an unregistered quantity class implementing an extra interface.
   */
  @Test
  public void getInstanceCreatesAFactoryForAnUnregisteredQuantityClassWithExtraInterface() {
    assertNotNull(DefaultQuantityFactory.getInstance(OneTimeComparableUnregisteredQuantityClass.class));
  }

  /**
   * Verifies that a quantity created via DefaultQuantityFactory is equal to one created by the Quantities facade.
   */
  @Test
  public void testQuantityIsEqualToNumberQuantity() {
    final Quantity<Length> actual = lengthFactory.create(10, METRE);
    final Quantity<Length> expected = Quantities.getQuantity(10, METRE);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that a quantity factory isn't equal to null.
   */
  @Test
  public void defaultQuantityFactoryIsNotEqualToNull() {
    assertFalse(testFactory.equals(null));
  }

  /**
   * Verifies that a quantity factory is equal to itself.
   */
  @Test
  public void defaultQuantityFactoryIsEqualToItself() {
    assertTrue(testFactory.equals(testFactory));
  }

  /**
   * Verifies that a quantity factory is not equal to a quantity factory for a different type.
   */
  @Test
  public void testQuantityIsNotEqualToQuantityWithDifferentValue() {
    assertFalse(testFactory.equals(lengthFactory));
  }

  /**
   * Verifies that the hash codes of two factories that aren't equal aren't equal. Notice that this isn't a strict requirement on the hashCode method,
   * and that hash collisions may occur, but in general, objects that aren't equal shouldn't have an equal hash code.
   */
  @Test
  public void hashCodeShouldBeDifferentForDifferentFactories() {
    assertFalse(testFactory.hashCode() == lengthFactory.hashCode());
  }

  /**
   * Verifies that a quantity factory is not equal to an object of a different class.
   */
  @Test
  public void testQuantityIsNotEqualToObjectOfDifferentClass() {
    assertFalse(testFactory.equals(Length.class));
  }

  /**
   * Verifies that the toString method produces the correct text.
   */
  @Test
  public void toStringMustProduceCorrectText() {
    assertEquals("tech.units.indriya.quantity.DefaultQuantityFactory <tech.units.indriya.quantity.DefaultQuantityFactoryTest$QuantityInterface>",
        testFactory.toString());
  }

  /**
   * Verifies that the length factory returns the meter as the system unit.
   */
  @Test
  public void lengthFactoryMustReturnMeterAsSystemUnit() {
    assertEquals(METRE, lengthFactory.getSystemUnit());
  }
}
