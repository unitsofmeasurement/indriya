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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static tech.units.indriya.unit.MetricPrefix.HECTO;
import static tech.units.indriya.unit.MetricPrefix.MILLI;
import static tech.units.indriya.unit.Units.PASCAL;

import java.math.BigDecimal;

import javax.measure.Quantity;
import javax.measure.quantity.Pressure;
import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.CompoundQuantity;
import tech.units.indriya.quantity.Quantities;

/**
 *
 * @author Werner Keil
 * @version 0.1
 */
public class CompoundQuantityTest {

  private static final Quantity<Pressure> ONE_HPA = Quantities.getQuantity(BigDecimal.ONE, HECTO(PASCAL));
  private static final Quantity<Pressure> TEN_PA = Quantities.getQuantity(BigDecimal.TEN, PASCAL);

  @Test
  public void ofTest() {
    CompoundQuantity<Pressure> pressures = CompoundQuantity.of(ONE_HPA, TEN_PA);
    assertEquals(2, pressures.getUnits().size());
  }

  @Test
  public void getTest() {
    CompoundQuantity<Pressure> pressures = CompoundQuantity.of(ONE_HPA, TEN_PA);
    assertEquals(ONE_HPA, pressures.get(HECTO(PASCAL)));
    assertEquals(TEN_PA, pressures.get(PASCAL));
    assertNull(pressures.get(MILLI(PASCAL)));
  }

  @Test
  public void toTest() {
    CompoundQuantity<Pressure> pressures = CompoundQuantity.of(ONE_HPA, TEN_PA);
    Quantity<Pressure> inMillis = pressures.to(MILLI(PASCAL));
    assertNotNull(inMillis);
    assertEquals(MILLI(PASCAL), inMillis.getUnit());
    assertEquals(BigDecimal.valueOf(110000d), inMillis.getValue());
  }

  @Test
  public void toStringTest() {
    CompoundQuantity<Pressure> pressures = new CompoundQuantity<>(ONE_HPA, TEN_PA);
    assertEquals("1 hPa: 10 Pa", pressures.toString());
  }

}