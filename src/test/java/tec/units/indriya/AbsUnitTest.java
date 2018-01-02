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
package tec.units.indriya;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static tec.units.indriya.unit.MetricPrefix.MICRO;
import static tec.units.indriya.unit.Units.GRAM;
import static tec.units.indriya.unit.Units.KILOGRAM;
import static tec.units.indriya.unit.Units.HOUR;
import static tec.units.indriya.unit.Units.MINUTE;
import static tec.units.indriya.unit.Units.SECOND;

import javax.measure.quantity.Length;

import org.junit.BeforeClass;
import org.junit.Test;

import tec.units.indriya.AbstractUnit;
import tec.units.indriya.unit.BaseUnit;
import tec.units.indriya.unit.MetricPrefix;
import tec.units.indriya.unit.Units;

public class AbsUnitTest {
  private static final AbstractUnit<Length> sut = new BaseUnit<>("m");

  @BeforeClass
  public static void init() {
    sut.setName("Test");
  }

  @Test
  public void testName() {
    assertEquals("Test", sut.getName());
  }

  @Test
  public void testReturnedClass() {
    // assertEquals("Q", String.valueOf(sut.getActualType())); // TODO we
    // hope to get better type information like <Length> in future Java
    // versions
    assertEquals("java.lang.reflect.TypeVariable<D>", String.valueOf(sut.getActualType()));
  }

  @Test
  public void testParse() {
    assertEquals(MetricPrefix.KILO(Units.WATT), AbstractUnit.parse("kW"));
  }

  @Test
  public void testParse2() {
    assertEquals(MetricPrefix.MILLI(Units.CELSIUS), AbstractUnit.parse("m°C"));
  }

  @Test
  public void testCompareTo() {
    assertEquals(0, ((AbstractUnit) Units.KILOGRAM).compareTo(Units.KILOGRAM));
  }

  @Test
  public void testCompareToOther() {
    assertEquals(-1, ((AbstractUnit) Units.KILOGRAM).compareTo(MetricPrefix.KILO(Units.GRAM)));
  }

  @Test
  public void testEquivalent() {
    assertTrue((((AbstractUnit) MICRO(GRAM))).isEquivalentOf(GRAM.divide(1000).divide(1000)));
  }

  @Test
  public void testAnnotate() {
    assertEquals("g{Gr}", (((AbstractUnit) GRAM).annotate("Gr")).toString());
  }

  @Test
  public void testAnnotateClass() {
    assertEquals("tec.units.indriya.unit.AnnotatedUnit", (((AbstractUnit) GRAM).annotate("Gr")).getClass().getName());
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void testCompound() {
    assertEquals("kg:g", (((AbstractUnit) KILOGRAM).compound(GRAM)).toString());
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void testCompoundClass() {
    assertEquals("tec.units.indriya.unit.CompoundUnit", (((AbstractUnit) KILOGRAM).compound(GRAM)).getClass().getName());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCompound2() {
    @SuppressWarnings("rawtypes")
    final AbstractUnit hourMin = (AbstractUnit) ((AbstractUnit) HOUR).compound(MINUTE);
    assertEquals("h:min:s", hourMin.compound(SECOND).toString());
  }
}
