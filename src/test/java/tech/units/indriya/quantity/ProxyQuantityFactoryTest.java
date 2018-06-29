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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tech.units.indriya.unit.Units.KILOGRAM;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.MINUTE;
import static tech.units.indriya.unit.Units.SECOND;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;
import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.ProxyQuantityFactory;

/**
 * @author Werner Keil
 */
public class ProxyQuantityFactoryTest {

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
    assertEquals(instance1, instance2);
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
    ProxyQuantityFactory<TwoTimesUnregisteredQuantityClass> instance1 = ProxyQuantityFactory.getInstance(TwoTimesUnregisteredQuantityClass.class);
    ProxyQuantityFactory<TwoTimesUnregisteredQuantityClass> instance2 = ProxyQuantityFactory.getInstance(TwoTimesUnregisteredQuantityClass.class);
    assertEquals(instance1, instance2);
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

  @Test
  public void testLength() {
    Quantity<Length> l = ProxyQuantityFactory.getInstance(Length.class).create(23.5, METRE); // 23.5 m
    assertEquals(23.5d, l.getValue());
    assertEquals(METRE, l.getUnit());
    assertEquals("m", l.getUnit().getSymbol());
  }

  @Test
  public void testMass() {
    Quantity<Mass> m = ProxyQuantityFactory.getInstance(Mass.class).create(10, KILOGRAM); // 10 kg
    assertEquals(10, m.getValue());
    assertEquals(KILOGRAM, m.getUnit());
    assertEquals("kg", m.getUnit().getSymbol());
    assertEquals("10 kg", m.toString());
  }

  @Test
  public void testTime() {
    Quantity<Time> t = ProxyQuantityFactory.getInstance(Time.class).create(30, SECOND); // 30 sec
    assertEquals(30, t.getValue());
    assertEquals(SECOND, t.getUnit());
    assertEquals("s", t.getUnit().getSymbol());
    assertEquals("30 s", t.toString());
  }

  @Test
  public void testTimeDerived() {
    Quantity<Time> t = ProxyQuantityFactory.getInstance(Time.class).create(40, MINUTE); // 40 min
    assertEquals(40, t.getValue());
    assertEquals(MINUTE, t.getUnit());
    assertEquals("min", t.getUnit().getSymbol()); // TODO see
    // https://github.com/unitsofmeasurement/uom-se/issues/54
    assertEquals("40 min", t.toString());
  }
}
