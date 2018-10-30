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

import javax.measure.MetricPrefix;
import javax.measure.Prefix;
import javax.measure.Unit;
import javax.measure.format.UnitFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.units.indriya.unit.Units.*;

/**
 * package private utility class to consolidate format testing idioms 
 *   
 * @author Andi Huber
 */
class FormatTestingUtil {

  /**
   * Complete set of built-in non-prefixed Units.
   */
  static enum NonPrefixedUnits {

    Ampere(AMPERE),
    Candela(CANDELA),
    Kelvin(KELVIN),
    Metre(METRE),
    Mole(MOLE),
    Second(SECOND),
    Gram(GRAM),
    Radian(RADIAN),
    Steradian(STERADIAN),
    Hertz(HERTZ),
    Newton(NEWTON),
    Pascal(PASCAL),
    Joule(JOULE),
    Watt(WATT),
    Coulomb(COULOMB),
    Volt(VOLT),
    Farad(FARAD),
    Ohm(OHM),
    Siemens(SIEMENS),
    Weber(WEBER),
    Tesla(TESLA),
    Henry(HENRY),
    Celsius(CELSIUS),
    Lumen(LUMEN),
    Lux(LUX),
    Becquerel(BECQUEREL),
    Gray(GRAY),
    Sievert(SIEVERT),
    Katal(KATAL),
    //SQUARE_METRE
    //CUBIC_METRE
    //PERCENT
    //MINUTE
    //HOUR
    //DAY
    //WEEK
    //YEAR
    Litre(LITRE)

    ;

    final Unit<?> unit;
    final String unitLiteral;
    final String onFailureMsg = String.format("testing %s", this.name());

    private NonPrefixedUnits(Unit<?> unit) {
      this.unit = unit;
      this.unitLiteral = unit.getSymbol();
    }

    void roundtrip(final UnitFormat format) {

      test(format);

      for(Prefix prefix : MetricPrefix.values()) {
        test(format, prefix);
      }

    }

    private void test(final UnitFormat format) {

      // parsing
      assertEquals(unit, format.parse(unitLiteral), onFailureMsg);

      // formatting
      assertEquals(unitLiteral, format.format(unit), onFailureMsg);
    }

    private void test(final UnitFormat format, Prefix prefix) {

      final Unit<?> prefixedUnit = unit.prefix(prefix);
      final String prefixedUnitLiteral = prefix.getSymbol()+unitLiteral;

      // parsing
      assertEquals(prefixedUnit, format.parse(prefixedUnitLiteral), onFailureMsg);

      // formatting
      assertEquals(prefixedUnitLiteral, format.format(prefixedUnit), onFailureMsg);

    }

  }


}
