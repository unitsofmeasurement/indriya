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
package tec.units.indriya.format;

import static org.junit.Assert.*;
import static tec.units.indriya.unit.MetricPrefix.KILO;
import static tec.units.indriya.unit.MetricPrefix.MEGA;
import static tec.units.indriya.unit.Units.*;

import java.math.BigDecimal;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.ParserException;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;

import org.junit.Before;
import org.junit.Test;

import tec.units.indriya.format.QuantityFormat;
import tec.units.indriya.quantity.Quantities;
import tec.units.indriya.unit.Units;

/**
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 *
 */
public class QuantityFormatTest {
  private Quantity<Length> sut;
  private QuantityFormat format;

  @Before
  public void init() {
    // sut =
    // DefaultQuantityFactoryService.getQuantityFactory(Length.class).create(10,
    // METRE);
    sut = Quantities.getQuantity(10, METRE);
    format = QuantityFormat.getInstance();
  }

  @Test
  public void testFormat() {
    Unit<Frequency> hz = HERTZ;
    assertEquals("Hz", hz.toString());
  }

  @Test
  public void testFormat2() {
    Unit<Frequency> mhz = MEGA(HERTZ);
    assertEquals("MHz", mhz.toString());
  }

  @Test
  public void testFormat3() {
    Unit<Frequency> khz = KILO(HERTZ);
    assertEquals("kHz", khz.toString());
  }

  @Test
  public void testParseSimple1() {
    Quantity<?> parsed1 = QuantityFormat.getInstance().parse("10 min");
    assertNotNull(parsed1);
    assertEquals(BigDecimal.valueOf(10), parsed1.getValue());
    assertEquals(Units.MINUTE, parsed1.getUnit());
  }

  @Test
  public void testParse2() {
    Quantity<?> parsed1 = QuantityFormat.getInstance().parse("60 m");
    assertNotNull(parsed1);
    assertEquals(BigDecimal.valueOf(60), parsed1.getValue());
    assertEquals(Units.METRE, parsed1.getUnit());
  }

  @Test
  public void testParseSimple3() {
    try {
      Quantity<?> parsed1 = format.parse("5 kg");
      assertNotNull(parsed1);
      assertEquals(BigDecimal.valueOf(5), parsed1.getValue());
      assertNotNull(parsed1.getUnit());
      assertEquals("kg", parsed1.getUnit().getSymbol());
      assertEquals(KILOGRAM, parsed1.getUnit());
    } catch (ParserException e) {
      fail(e.getMessage());
    }
  }
}
