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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.units.indriya.function.PiDivisorConverter;

public class PiDivisorConverterTest {

  private PiDivisorConverter converter;

  @BeforeEach
  public void setUp() throws Exception {
    converter = new PiDivisorConverter();
  }

  @Test
  public void testConvertMethod() {
    assertEquals(1000, converter.convert(3141), 0.2);
    assertEquals(0, converter.convert(0));
    assertEquals(-1000, converter.convert(-3141), 0.2);
  }

  @Test
  public void testConvertBigDecimalMethod() {
    assertEquals(1000, converter.convert(new BigDecimal("3141"), MathContext.DECIMAL32).doubleValue(), 0.2);
    assertEquals(0, converter.convert(BigDecimal.ZERO, MathContext.DECIMAL32).doubleValue());
    assertEquals(-1000, converter.convert(new BigDecimal("-3141"), MathContext.DECIMAL32).doubleValue(), 0.2);
  }

  @Test
  public void testEqualityOfTwoLogConverter() {
    assertTrue(!converter.equals(null));
  }

  @Test
  public void testGetValuePiDivisorConverter() {
    assertEquals("(1/π)", converter.getValue());
  }

  @Test
  public void isLinearOfLogConverterTest() {
    assertTrue(converter.isLinear());
  }
}
