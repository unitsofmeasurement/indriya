/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2021, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tech.units.indriya.NumberAssertions.assertNumberEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.units.indriya.NumberAssertions;

public class AddConverterTest {

  private AddConverter converter;

  @BeforeEach
  public void setUp() throws Exception {
    converter = new AddConverter(10);
  }

  @Test
  public void testEqualityOfTwoConverter() {
    AddConverter addConverter = new AddConverter(10);
    assertEquals(addConverter, converter);
    assertNotNull(addConverter);
  }

  @Test
  public void inverseTest() {
    assertEquals(new AddConverter(-10), converter.inverse());
  }

  @Test
  public void linearTest() {
    assertFalse(converter.isLinear());
  }

  @Test
  public void offsetTest() {
    assertNumberEquals(10, converter.getOffset(), 1E-12);
  }

  @Test
  public void valueTest() {
    assertNumberEquals(10, converter.getValue(), 1E-12);
  }

  @Test
  public void toStringTest() {
    assertEquals("Add(x -> x + 10)", converter.toString());
    assertEquals("Add(x -> x - 10)", converter.inverse().toString());
  }

  @Test
  public void identityTest() {
    assertFalse(converter.isIdentity());
  }

  @Test
  public void conversionStepsTest() {
    assertNotNull(converter.getConversionSteps());
  }
}
