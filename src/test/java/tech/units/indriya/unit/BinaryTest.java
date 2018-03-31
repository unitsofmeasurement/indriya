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
package tech.units.indriya.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.units.indriya.unit.BinaryPrefix.*;
import static tech.units.indriya.unit.MetricPrefix.*;
import static tech.units.indriya.unit.Units.*;

import javax.measure.UnitConverter;

import org.junit.jupiter.api.Test;

import tech.units.indriya.function.RationalConverter;

public class BinaryTest {
  @Test
  public void testKibi() {
    final UnitConverter expected = new RationalConverter(128, 125);
    final UnitConverter actual = KIBI(METRE).getConverterTo(KILO(METRE));
    assertEquals(expected, actual);
  }

  @Test
  public void testMebi() {
    final UnitConverter expected = new RationalConverter(8, 15625);
    final UnitConverter actual = MEBI(METRE).getConverterTo(MEGA(METRE));
    assertEquals(expected, actual);
  }

  @Test
  public void testGibi() {
    final UnitConverter expected = new RationalConverter(2, 5859375);
    final UnitConverter actual = GIBI(METRE).getConverterTo(GIGA(METRE));
    assertEquals(expected, actual);
  }
  
  @Test
  public void testTebi() {
    final UnitConverter expected = new RationalConverter(1, 3906250000l);
    final UnitConverter actual = TEBI(LITRE).getConverterTo(TERA(LITRE));
    assertEquals(expected, actual);
  }
  
  @Test
  public void testPebi() {
    final UnitConverter expected = new RationalConverter(1, 4882812500000l);
    final UnitConverter actual = PEBI(LITRE).getConverterTo(PETA(LITRE));
    assertEquals(expected, actual);
  }
}
