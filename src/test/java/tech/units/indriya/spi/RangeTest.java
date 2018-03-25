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
package tech.units.indriya.spi;

import static org.junit.jupiter.api.Assertions.*;
import static tech.units.indriya.unit.Units.KILOGRAM;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.quantity.QuantityRange;
import tech.units.indriya.spi.Range;

public class RangeTest {
  private Quantity<Mass> min;
  private Quantity<Mass> max;
  private Quantity<Mass> res;
  @SuppressWarnings("rawtypes")
  private Range range;

  @BeforeEach
  public void init() {
    min = Quantities.getQuantity(1d, KILOGRAM);
    max = Quantities.getQuantity(10d, KILOGRAM);
    res = Quantities.getQuantity(2d, KILOGRAM);

    range = QuantityRange.of(min, max, res);
  }

  @Test
  public void testGetMinimum() {
    assertEquals(min, range.getMinimum());
  }

  @Test
  public void testGetMaximum() {
    assertEquals(max, range.getMaximum());
  }

  @Test
  public void testGetResolution() {
    assertTrue(range.getClass().equals(QuantityRange.class));
    @SuppressWarnings("unchecked")
    QuantityRange<Mass> qr = (QuantityRange<Mass>) range;
    assertEquals(res, qr.getResolution());
  }

  @Test
  public void testToString() {
    assertEquals("min= 1.0 kg, max= 10.0 kg, res= 2.0 kg", range.toString());
  }
}
