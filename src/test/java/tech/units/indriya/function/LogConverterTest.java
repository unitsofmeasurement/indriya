/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2019, Units of Measurement project.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.units.indriya.function.ExpConverter;
import tech.units.indriya.function.LogConverter;

public class LogConverterTest {

  private LogConverter logConverterBase10;

  @BeforeEach
  public void setUp() throws Exception {
    logConverterBase10 = new LogConverter(10.);
  }

  @Test
  public void testBaseUnmodified() {
    assertEquals(10., logConverterBase10.getBase());
  }

  @Test
  public void testEqualityOfTwoLogConverter() {
    LogConverter logConverter = new LogConverter(10.);
    assertNotNull(logConverter);
    assertEquals(logConverter, logConverterBase10);
  }

  @Test
  public void testGetValueLogConverter() {
    LogConverter logConverter = new LogConverter(Math.E);
    assertEquals("Log(x -> log(base=10.0, x))", logConverterBase10.getValue());
    assertEquals("Log(x -> ln(x))", logConverter.getValue());
  }

  @Test
  public void isLinearOfLogConverterTest() {
    assertTrue(!logConverterBase10.isLinear());
  }

  @Test
  public void convertLogTest() {
    assertEquals(1, logConverterBase10.convert(10));
    assertEquals(Double.NaN, logConverterBase10.convert(-10));
    assertTrue(Double.isInfinite(logConverterBase10.convert(0)));
  }

  @Test
  public void inverseLogTest() {
    LogConverter logConverter = new LogConverter(Math.E);
    assertEquals(new ExpConverter(10.), logConverterBase10.inverse());
    assertEquals(new ExpConverter(Math.E), logConverter.inverse());
  }
}
