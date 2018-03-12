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
package tech.units.indriya.format;

import static org.junit.Assert.assertEquals;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.measure.Unit;

import org.junit.Test;

import tech.units.indriya.format.EBNFUnitFormat;
import tech.units.indriya.format.SymbolMap;
import tech.units.indriya.unit.MetricPrefix;
import tech.units.indriya.unit.Units;

public class SymbolMapTest {

  private static final String BUNDLE_NAME = "tech.units.indriya.format.messages";

  /**
   * Test for all prefixes.
   */
  @Test
  public void testSymbolMap() {
    SymbolMap symbols = SymbolMap.of(ResourceBundle.getBundle(BUNDLE_NAME, Locale.ROOT));
    assertEquals(MetricPrefix.ATTO, symbols.getPrefix("ag"));
    assertEquals(MetricPrefix.CENTI, symbols.getPrefix("cg"));
    assertEquals(MetricPrefix.DECI, symbols.getPrefix("dg"));
    assertEquals(MetricPrefix.EXA, symbols.getPrefix("Eg"));
    assertEquals(MetricPrefix.FEMTO, symbols.getPrefix("fg"));
    assertEquals(MetricPrefix.GIGA, symbols.getPrefix("Gg"));
    assertEquals(MetricPrefix.HECTO, symbols.getPrefix("hg"));
    assertEquals(MetricPrefix.KILO, symbols.getPrefix("kg"));
    assertEquals(MetricPrefix.MEGA, symbols.getPrefix("Mg"));
    assertEquals(MetricPrefix.MICRO, symbols.getPrefix("µg"));
    assertEquals(MetricPrefix.MILLI, symbols.getPrefix("mg"));
    assertEquals(MetricPrefix.NANO, symbols.getPrefix("ng"));
    assertEquals(MetricPrefix.PETA, symbols.getPrefix("Pg"));
    assertEquals(MetricPrefix.PICO, symbols.getPrefix("pg"));
    assertEquals(MetricPrefix.TERA, symbols.getPrefix("Tg"));
    assertEquals(MetricPrefix.YOCTO, symbols.getPrefix("yg"));
    assertEquals(MetricPrefix.YOTTA, symbols.getPrefix("Yg"));
    assertEquals(MetricPrefix.ZEPTO, symbols.getPrefix("zg"));
    assertEquals(MetricPrefix.ZETTA, symbols.getPrefix("Zg"));
    assertEquals(MetricPrefix.DEKA, symbols.getPrefix("dag"));
  }

  /**
   * Test if parsing 'dag' equals DEKA(GRAM)
   */
  @Test
  public void parseWithEBNFUnitFormat() {
    Unit u2 = EBNFUnitFormat.getInstance().parse("dag");

    assertEquals(MetricPrefix.DEKA(Units.GRAM), u2);
  }
}
