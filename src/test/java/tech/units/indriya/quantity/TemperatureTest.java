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
package tech.units.indriya.quantity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static tech.units.indriya.unit.Units.CELSIUS;
import static tech.units.indriya.unit.Units.KELVIN;

import javax.measure.Quantity;
import javax.measure.quantity.Temperature;

import org.junit.jupiter.api.Test;

public class TemperatureTest {

  @Test
  public void testInstantiate() {
    Quantity<Temperature> t = Quantities.getQuantity(23.0d, CELSIUS); // 23.0 °C
    assertEquals("23.0 ℃", t.toString());
  }

  @Test
  public void testTemperatureQuantityDoubleTemperatureUnit() {
    Quantity<Temperature> t = Quantities.getQuantity(Double.valueOf(20d), CELSIUS);
    assertEquals(Double.valueOf(20d), t.getValue());
  }

  @Test
  public void testTo() {
    Quantity<Temperature> t = Quantities.getQuantity(Double.valueOf(30d), CELSIUS);
    Quantity<Temperature> t2 = t.to(KELVIN);
    assertEquals(Double.valueOf(303.15d), t2.getValue());
  }

  @Test
  public void testTo2() {
    Quantity<Temperature> t = Quantities.getQuantity(Double.valueOf(2d), KELVIN);
    Quantity<Temperature> t2 = t.to(CELSIUS);
    assertEquals(Double.valueOf(-271.15d), t2.getValue());
  }

}
