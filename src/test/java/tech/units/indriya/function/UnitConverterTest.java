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

import static javax.measure.MetricPrefix.CENTI;
import static javax.measure.MetricPrefix.KILO;
import static javax.measure.MetricPrefix.MICRO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tech.units.indriya.NumberAssertions.assertNumberEquals;
import static tech.units.indriya.unit.Units.CELSIUS;
import static tech.units.indriya.unit.Units.GRAM;
import static tech.units.indriya.unit.Units.KELVIN;
import static tech.units.indriya.unit.Units.KILOGRAM;
import static tech.units.indriya.unit.Units.METRE;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.quantity.Temperature;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;


public class UnitConverterTest {
  private final Unit<Length> sourceUnit = METRE;
  private final Unit<Length> targetUnit = CENTI(METRE);

  @Test
  public void testDouble() {
    UnitConverter converter = sourceUnit.getConverterTo(targetUnit);
    double length1 = 4.0;
    double length2 = 6.0;
    double result1 = converter.convert(length1);
    double result2 = converter.convert(length2);
    assertNumberEquals(400, result1, 1E-12);
    assertNumberEquals(600, result2, 1E-12);
  }

  @Test
  public void testQuantity() {
    Quantity<Length> quantLength1 = Quantities.getQuantity(4.0, sourceUnit);
    // Quantity<Length> quantLength2 = Quantities.getQuantity(6.0,
    // targetUnit);
    Quantity<Length> quantResult1 = quantLength1.to(targetUnit);
    assertNotNull(quantResult1);
    assertNumberEquals(400, quantResult1.getValue(), 1E-12);
    assertEquals(targetUnit, quantResult1.getUnit());
  }

  @Test
  public void testQuantityDouble() {
    Quantity<Length> quantLength1 = Quantities.getQuantity(1.56, sourceUnit);
    Quantity<Length> quantResult1 = quantLength1.to(targetUnit);
    assertNotNull(quantResult1);
    assertNumberEquals(156, quantResult1.getValue(), 1E-12);
    assertEquals(targetUnit, quantResult1.getUnit());
  }

  @Test
  public void testQuantityFloat() {
    Quantity<Length> quantLength1 = Quantities.getQuantity(1.56f, sourceUnit);
    Quantity<Length> quantResult1 = quantLength1.to(targetUnit);
    assertNotNull(quantResult1);
    assertNumberEquals(156, quantResult1.getValue(), 1E-12);
    assertEquals(targetUnit, quantResult1.getUnit());
  }

  @Test
  public void testKelvinToCelsius() {
    Quantity<Temperature> sut = Quantities.getQuantity(273.15d, KELVIN).to(CELSIUS);
    assertNotNull(sut);
    assertEquals(CELSIUS, sut.getUnit());
    assertNumberEquals(0, sut.getValue(), 1E-12);
  }

  @Test
  public void testKelvinToCelsiusFloat() {
    Quantity<Temperature> sut = Quantities.getQuantity(273.15f, KELVIN).to(CELSIUS);
    assertNotNull(sut);
    assertEquals(CELSIUS, sut.getUnit());
    assertNumberEquals(0, sut.getValue(), 1E-12);
  }

  @Test
  public void testConverterTo() {
    assertEquals(KILOGRAM.getConverterTo(KILO(GRAM)), KILO(GRAM).getConverterTo(KILOGRAM));
  }

  @Test
  public void testChainedOps() {
    assertEquals(MICRO(GRAM).getConverterTo(GRAM.divide(1000).divide(1000)), GRAM.divide(1000).divide(1000).getConverterTo(MICRO(GRAM)));
  }
}
