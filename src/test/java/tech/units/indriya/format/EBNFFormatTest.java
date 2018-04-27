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
package tech.units.indriya.format;

import static org.junit.jupiter.api.Assertions.*;
import static tech.units.indriya.unit.MetricPrefix.*;
import static tech.units.indriya.unit.Units.*;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.measure.Unit;
import javax.measure.format.ParserException;
import javax.measure.format.UnitFormat;
import javax.measure.quantity.Length;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.units.indriya.format.EBNFUnitFormat;
import tech.units.indriya.function.RationalConverter;
import tech.units.indriya.unit.TransformedUnit;
import tech.units.indriya.unit.Units;

/**
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 *
 */
public class EBNFFormatTest {
  private static final Logger logger = Logger.getLogger(EBNFFormatTest.class.getName());

  private UnitFormat format;

  @BeforeEach
  public void init() {
    format = EBNFUnitFormat.getInstance();
  }

  @Test
  public void testParseKm() {
    Unit<?> u = format.parse("km");
    assertEquals(KILO(METRE), u);
    assertEquals("km", u.toString());
  }

  @Test
  public void testParseInverseM() {
    Unit<?> u = format.parse("1/m");
    assertEquals("1/m", u.toString());
  }

  @Test
  public void testParseInverseKg() {
    Unit<?> u = format.parse("1/kg");
    assertEquals("1/kg", u.toString());
  }

  @Test
  public void testParseInverseL() {
    Unit<?> u = format.parse("1/l");
    assertEquals("1/l", u.toString());
  }

  @Test
  public void testParseInverses() {
    for (Unit<?> u : Units.getInstance().getUnits()) {
      try {
        Unit<?> v = format.parse("1/" + u.toString());
        assertNotNull(v);
        logger.log(Level.FINER, v.toString());
      } catch (ParserException pex) {
        logger.log(Level.WARNING, String.format(" %s parsing %s", pex, u));
      }
    }
  }

  @Test
  // TODO address https://github.com/unitsofmeasurement/uom-se/issues/145
  public void testFormatKm() {
    String s = format.format(KILO(METRE));
    assertEquals("km", s);
  }

  @Test
  // TODO address https://github.com/unitsofmeasurement/uom-se/issues/145
  public void testFormatmm() {
    String s = format.format(MILLI(METRE));
    assertEquals("mm", s);
  }

  @Test
  public void testParseIrregularStringEBNF() {
	    assertThrows(ParserException.class, () -> {
	    	   @SuppressWarnings("unused")
			Unit<?> u = format.parse("bl//^--1a");
        });
  }

  @Test
  public void testTransformed() {
    final String ANGSTROEM_SYM = "\u212B";
    final Unit<Length> ANGSTROEM = new TransformedUnit<Length>(ANGSTROEM_SYM, METRE, METRE, new RationalConverter(BigInteger.ONE,
        BigInteger.TEN.pow(10)));
    final String s = format.format(ANGSTROEM);
    assertEquals(ANGSTROEM_SYM, s);
  }
}
