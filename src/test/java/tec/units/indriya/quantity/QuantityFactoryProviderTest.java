/*
 * Next Generation Units of Measurement Implementation
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
 * 3. Neither the name of JSR-363, Indriya nor the names of their contributors may be used to endorse or promote products
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
package tec.units.indriya.quantity;

import static org.junit.Assert.assertEquals;
import static tec.units.indriya.unit.Units.KILOGRAM;
import static tec.units.indriya.unit.Units.METRE;
import static tec.units.indriya.unit.Units.MINUTE;
import static tec.units.indriya.unit.Units.SECOND;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;
import javax.measure.spi.ServiceProvider;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Werner Keil
 */
public class QuantityFactoryProviderTest {
  static ServiceProvider service;

  @BeforeClass
  public static void init() {
    service = ServiceProvider.current();
  }

  @Test
  public void testLength() {
    Quantity<Length> l = service.getQuantityFactory(Length.class).create(23.5, METRE); // 23.0 km
    assertEquals(23.5d, l.getValue());
    assertEquals(METRE, l.getUnit());
    assertEquals("m", l.getUnit().getSymbol());
  }

  @Test
  public void testMass() {
    Quantity<Mass> m = service.getQuantityFactory(Mass.class).create(10, KILOGRAM); // 10 kg
    assertEquals(10, m.getValue());
    assertEquals(KILOGRAM, m.getUnit());
    assertEquals("kg", m.getUnit().getSymbol());
    assertEquals("10 kg", m.toString());
  }

  @Test
  public void testTime() {
    Quantity<Time> t = service.getQuantityFactory(Time.class).create(30, SECOND); // 30 sec
    assertEquals(30, t.getValue());
    assertEquals(SECOND, t.getUnit());
    assertEquals("s", t.getUnit().getSymbol());
    assertEquals("30 s", t.toString());
  }

  @Test
  public void testTimeDerived() {
    Quantity<Time> t = service.getQuantityFactory(Time.class).create(40, MINUTE); // 40 min
    assertEquals(40, t.getValue());
    assertEquals(MINUTE, t.getUnit());
    assertEquals("min", t.getUnit().getSymbol()); // TODO see
    // https://github.com/unitsofmeasurement/uom-se/issues/54
    assertEquals("40 min", t.toString());
  }
}
