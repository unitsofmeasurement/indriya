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
package tech.units.indriya.function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import tech.units.indriya.function.ExpConverter;
import tech.units.indriya.function.LogConverter;

public class ExpConverterTest {

  private ExpConverter expConverterBase10;

  @Before
  public void setUp() throws Exception {
    expConverterBase10 = new ExpConverter(10.);
  }

  @Test
  public void testBaseUnmodified() {
    assertEquals(10., expConverterBase10.getBase(), 0.);
  }

  @Test
  public void testEqualityOfTwoLogConverter() {
    ExpConverter expConverter = new ExpConverter(10.);
    assertTrue(expConverter.equals(expConverterBase10));
    assertTrue(!expConverter.equals(null));
  }

  @Test
  public void testGetValueLogConverter() {
    ExpConverter expConverter = new ExpConverter(Math.E);
    assertEquals("Exp(10.0)", expConverterBase10.getValue());
    assertEquals("e", expConverter.getValue());
  }

  @Test
  public void isLinearOfLogConverterTest() {
    assertTrue(!expConverterBase10.isLinear());
  }

  @Test
  public void convertLogTest() {
    LogConverter logConverter = new LogConverter(10.);
    assertEquals(1.0, logConverter.convert(expConverterBase10.convert(1.0)), 0.);
    assertEquals(-10, logConverter.convert(expConverterBase10.convert(-10)), 0.);
  }

  @Test
  public void inverseLogTest() {
    ExpConverter expConverter = new ExpConverter(Math.E);
    assertEquals(new LogConverter(10.), expConverterBase10.inverse());
    assertEquals(new LogConverter(Math.E), expConverter.inverse());
  }
}
