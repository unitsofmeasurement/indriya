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

import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.spi.QuantityFactory;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.ProxyQuantityFactory;
import tech.units.indriya.unit.BaseUnit;
import tech.units.indriya.unit.Units;

/**
 * @author Werner Keil
 */
public class ProxyQuantityFactoryTest {
  private final Quantity<QuantityInterface> testQuantity = createTestQuantity(testQuantityValue, testQuantityUnit);
  private static final int testQuantityValue = 1;
  private static final String testQuantitySymbol = "Q";
  private static final Unit<QuantityInterface> testQuantityUnit = new BaseUnit<QuantityInterface>(testQuantitySymbol, QuantityDimension.NONE);

  /**
   * Local quantity interface to test the instance methods on.
   */
  interface QuantityInterface extends Quantity<QuantityInterface> {
  }

  private Quantity<QuantityInterface> createTestQuantity(int value, Unit<QuantityInterface> unit) {
    ProxyQuantityFactory<QuantityInterface> pqf = ProxyQuantityFactory.getInstance(QuantityInterface.class);
    return pqf.create(value, unit);
  }

  /**
   * Verifies that the factory throws an exception if an instance is requested for null.
   */
  @Test
  public void getInstanceThrowsExceptionForNull() {
    assertThrows(Exception.class, () -> {
      ProxyQuantityFactory.getInstance(null);
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
    assertNotNull(ProxyQuantityFactory.getInstance(OneTimeUnregisteredQuantityInterface.class));
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
    ProxyQuantityFactory<TwoTimesUnregisteredQuantityInterface> instance1 = ProxyQuantityFactory
        .getInstance(TwoTimesUnregisteredQuantityInterface.class);
    ProxyQuantityFactory<TwoTimesUnregisteredQuantityInterface> instance2 = ProxyQuantityFactory
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
    assertNotNull(ProxyQuantityFactory.getInstance(OneTimeUnregisteredQuantityClass.class));
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
    QuantityFactory<TwoTimesUnregisteredQuantityClass> instance1 = ProxyQuantityFactory.getInstance(TwoTimesUnregisteredQuantityClass.class);
    QuantityFactory<TwoTimesUnregisteredQuantityClass> instance2 = ProxyQuantityFactory.getInstance(TwoTimesUnregisteredQuantityClass.class);
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
    assertNotNull(ProxyQuantityFactory.getInstance(OneTimeComparableUnregisteredQuantityClass.class));
  }

  /**
   * Verifies that getUnit returns the unit used to create the test quantity.
   */
  @Test
  public void getUnitReturnsTheUnit() {
    assertEquals(testQuantityUnit, testQuantity.getUnit());
  }

  /**
   * Verifies that getValue returns the value used to create the test quantity.
   */
  @Test
  public void getValueReturnsTheValue() {
    assertEquals(testQuantityValue, testQuantity.getValue());
  }

  /**
   * Verifies that toString returns a correct String representation of the test quantity.
   */
  @Test
  public void toStringReturnsCorrectStringRepresentation() {
    assertEquals("1 Q", testQuantity.toString());
  }

  /**
   * Verifies that a quantity isn't equal to null.
   */
  @Test
  public void testQuantityIsNotEqualToNull() {
    assertFalse(testQuantity.equals(null));
  }

  /**
   * Verifies that a quantity is equal to itself.
   */
  @Test
  public void testQuantityIsEqualToItself() {
    assertTrue(testQuantity.equals(testQuantity));
  }

  /**
   * Verifies that a quantity is equal to another instance with the same value and unit.
   */
  @Test
  public void testQuantityIsEqualToIdenticalInstance() {
    assertTrue(testQuantity.equals(createTestQuantity(testQuantityValue, testQuantityUnit)));
  }

  /**
   * Verifies that a quantity is not equal to a quantity with a different value.
   */
  @Test
  public void testQuantityIsNotEqualToQuantityWithDifferentValue() {
    assertFalse(testQuantity.equals(createTestQuantity(testQuantityValue + 1, testQuantityUnit)));
  }

  /**
   * Verifies that a quantity is not equal to a quantity with a different unit.
   */
  @Test
  public void testQuantityIsNotEqualToQuantityWithDifferentUnit() {
    assertFalse(testQuantity.equals(createTestQuantity(testQuantityValue, MetricPrefix.DEKA(testQuantityUnit))));
  }

  /**
   * Verifies that a quantity is not equal to an object of a different class.
   */
  @Test
  public void testQuantityIsNotEqualToObjectOfDifferentClass() {
    assertFalse(testQuantity.equals(testQuantityUnit));
  }

  /**
   * Verifies that a quantity has the same hash code as another instance with the same value and unit.
   */
  @Test
  public void testQuantityHasSameHashCodeAsIdenticalInstance() {
    assertEquals(testQuantity.hashCode(), createTestQuantity(testQuantityValue, testQuantityUnit).hashCode());
  }

  /**
   * Verifies that methods defined by Quantity but not supported by the default implementation throw an UnsupportedOperationException.
   */
  @Test
  public void inverseThrowsAnUnsupportedOperationException() {
    assertThrows(UnsupportedOperationException.class, () -> {
      testQuantity.inverse();
    });
  }

  /**
   * Verifies that a quantity created via ProxyQuantityFactory is equal to one created by the Quantities facade.
   */
  @Test
  public void testQuantityIsEqualToNumberQuantity() {
    final QuantityFactory<Length> lenFactory = ProxyQuantityFactory.getInstance(Length.class);
    final Quantity<Length> len1 = lenFactory.create(10, Units.METRE);
    final Quantity<Length> len2 = Quantities.getQuantity(10, Units.METRE);
    assertEquals(len1, len2);
  }

}
