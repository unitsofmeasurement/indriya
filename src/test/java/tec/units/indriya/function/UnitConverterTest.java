/*
 * Next Generation Units of Measurement Implementation
 * Copyright (c) 2005-2017, Jean-Marie Dautelle, Werner Keil, V2COM.
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
import static tec.units.indriya.unit.MetricPrefix.*;
import static tec.units.indriya.unit.Units.*;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.quantity.Temperature;

import org.junit.Test;

import tec.units.indriya.quantity.Quantities;
import tec.units.indriya.unit.Units;

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
    assertEquals(400, result1, 0);
    assertEquals(600, result2, 0);
  }

  @Test
  public void testQuantity() {
    Quantity<Length> quantLength1 = Quantities.getQuantity(4.0, sourceUnit);
    // Quantity<Length> quantLength2 = Quantities.getQuantity(6.0, targetUnit);
    Quantity<Length> quantResult1 = quantLength1.to(targetUnit);
    assertNotNull(quantResult1);
    assertEquals(400.0, quantResult1.getValue());
    assertEquals(targetUnit, quantResult1.getUnit());
  }

  @Test
  public void testKelvinToCelsius() {
    Quantity<Temperature> sut = Quantities.getQuantity(273.15d, Units.KELVIN).to(Units.CELSIUS);
    assertNotNull(sut);
    assertEquals(Units.CELSIUS, sut.getUnit());
    assertEquals(0d, sut.getValue());
  }
}
