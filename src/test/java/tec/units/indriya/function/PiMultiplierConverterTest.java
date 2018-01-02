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
package tec.units.indriya.function;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.MathContext;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tec.units.indriya.function.PiMultiplierConverter;
import tec.units.indriya.quantity.Quantities;
import tec.units.indriya.unit.Units;

public class PiMultiplierConverterTest {

  private PiMultiplierConverter piMultiplierConverter;

  @Before
  public void setUp() throws Exception {
    piMultiplierConverter = new PiMultiplierConverter();
  }

  @Test
  public void testConvertMethod() {
    Assert.assertEquals(314.15, piMultiplierConverter.convert(100), 0.1);
    Assert.assertEquals(0, piMultiplierConverter.convert(0), 0.0);
    Assert.assertEquals(-314.15, piMultiplierConverter.convert(-100), 0.1);
  }

  @Test
  public void testConvertBigDecimalMethod() {
    Assert.assertEquals(314.15, piMultiplierConverter.convert(new BigDecimal("100"), MathContext.DECIMAL32).doubleValue(), 0.1);
    Assert.assertEquals(0, piMultiplierConverter.convert(BigDecimal.ZERO, MathContext.DECIMAL32).doubleValue(), 0.0);
    Assert.assertEquals(-314.15, piMultiplierConverter.convert(new BigDecimal("-100"), MathContext.DECIMAL32).doubleValue(), 0.1);
  }

  @Test
  public void testEqualityOfTwoLogConverter() {
    assertTrue(!piMultiplierConverter.equals(null));
  }

  @Test
  public void testGetValuePiDivisorConverter() {
    assertEquals("(π)", piMultiplierConverter.getValue());
  }

  @Test
  public void isLinearOfLogConverterTest() {
    assertTrue(piMultiplierConverter.isLinear());
  }

  @Test
  public void testAngleConverter() {
    Quantity<Angle> sut = Quantities.getQuantity(BigDecimal.ONE, Units.DEGREE_ANGLE).to(Units.RADIAN);
    assertNotNull(sut);
    assertEquals(Units.RADIAN, sut.getUnit());
    assertEquals(new BigDecimal("0.01745329251994329576923690768488613"), sut.getValue());
  }

  @Test
  public void testAngleConverterOpposite() {
    Quantity<Angle> sut = Quantities.getQuantity(BigDecimal.ONE, Units.RADIAN).to(Units.DEGREE_ANGLE);
    assertNotNull(sut);
    assertEquals(Units.DEGREE_ANGLE, sut.getUnit());
    assertEquals(new BigDecimal("57.29577951308232087679815481410517"), sut.getValue());
  }
}
