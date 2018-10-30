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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static tech.units.indriya.unit.Units.AMPERE;
import static tech.units.indriya.unit.Units.BECQUEREL;
import static tech.units.indriya.unit.Units.CANDELA;
import static tech.units.indriya.unit.Units.CELSIUS;
import static tech.units.indriya.unit.Units.COULOMB;
import static tech.units.indriya.unit.Units.FARAD;
import static tech.units.indriya.unit.Units.GRAM;
import static tech.units.indriya.unit.Units.GRAY;
import static tech.units.indriya.unit.Units.HENRY;
import static tech.units.indriya.unit.Units.HERTZ;
import static tech.units.indriya.unit.Units.JOULE;
import static tech.units.indriya.unit.Units.KATAL;
import static tech.units.indriya.unit.Units.KELVIN;
import static tech.units.indriya.unit.Units.LITRE;
import static tech.units.indriya.unit.Units.LUMEN;
import static tech.units.indriya.unit.Units.LUX;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.MOLE;
import static tech.units.indriya.unit.Units.NEWTON;
import static tech.units.indriya.unit.Units.OHM;
import static tech.units.indriya.unit.Units.PASCAL;
import static tech.units.indriya.unit.Units.RADIAN;
import static tech.units.indriya.unit.Units.SECOND;
import static tech.units.indriya.unit.Units.SIEMENS;
import static tech.units.indriya.unit.Units.SIEVERT;
import static tech.units.indriya.unit.Units.STERADIAN;
import static tech.units.indriya.unit.Units.TESLA;
import static tech.units.indriya.unit.Units.VOLT;
import static tech.units.indriya.unit.Units.WATT;
import static tech.units.indriya.unit.Units.WEBER;

import java.util.Iterator;
import java.util.Map;

import javax.measure.IncommensurableException;
import javax.measure.MetricPrefix;
import javax.measure.Prefix;
import javax.measure.Quantity;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;
import javax.measure.format.UnitFormat;

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

    private NonPrefixedUnits(Unit<?> unit) {
      this.unit = unit;
    }

    /**
     * 
     * @param format
     */
    void roundtrip(final UnitFormat format) {

      test(format);

      for(Prefix prefix : MetricPrefix.values()) {
        test(format, prefix);
      }

    }
    
    // -- HELPER

    /** whether the UnitFormat {@code format} can correctly handle the non-prefixed unit */
    private void test(final UnitFormat format) {

      // formatting
      final String unitLiteral = format.format(unit);
      assertNotNull(unitLiteral);
      assertTrue(unitLiteral.length()>0);
      
      // parsing
      Unit<?> parsedUnit;
      try {
        parsedUnit = format.parse(unitLiteral);
      } catch (Exception e) {
        fail(
            String.format("testing '%s', parsing literal '%s' threw an exception", 
                this.name(), unitLiteral), 
            e);
        return;
      }
      
      assertEquals(unit, parsedUnit, 
          ()->String.format("testing '%s'", this.name()));
      
    }

    /** whether the UnitFormat {@code format} can correctly handle the {@code prefix}-ed unit */
    private void test(final UnitFormat format, Prefix prefix) {

      // formatting
      final Unit<?> prefixedUnit = unit.prefix(prefix);
      final String prefixedUnitLiteral = format.format(prefixedUnit);
      assertNotNull(prefixedUnitLiteral);
      assertTrue(prefixedUnitLiteral.length()>0);

      // parsing
      Unit<?> parsedPrefixedUnit;
      try {
        parsedPrefixedUnit = format.parse(prefixedUnitLiteral);
      } catch (Exception e) {
        fail(
            String.format("testing '%s' with prefix '%s', parsing literal '%s' threw an exception", 
                this.name(), prefix.getName(), prefixedUnitLiteral), 
            e);
        return;
      }
      
      assertTrue(areEquivalent(prefixedUnit.getBaseUnits(), parsedPrefixedUnit.getBaseUnits()), 
          ()->String.format("testing '%s' with prefix '%s', base unit mismatch", this.name(), prefix.getName()));
      
      assertTrue(areEquivalent(prefixedUnit, parsedPrefixedUnit), 
          ()->String.format("testing '%s' with prefix '%s'", this.name(), prefix.getName()));

    }
    
    /** unit equivalence test */
    private <Q1 extends Quantity<Q1>, Q2 extends Quantity<Q2>> boolean areEquivalent(Unit<Q1> a, Unit<Q2> b) {
      try {
        return a.getConverterToAny(b).isIdentity();
      } catch (UnconvertibleException | IncommensurableException e) {
        return false;
      }
    }
    
    /** base unit (map) equivalence test */
    private <U1 extends Unit<?>, U2 extends Unit<?>> boolean areEquivalent(
        Map<U1, Integer> a, 
        Map<U2, Integer> b) {
      
      if(a==null) {
        return b==null;
      }
      
      if(a.size()!=b.size()) {
        return false;
      }
      
      Iterator<Map.Entry<U1, Integer>> it1 = a.entrySet().iterator();
      Iterator<Map.Entry<U2, Integer>> it2 = b.entrySet().iterator();
      
      while(it1.hasNext()) {
        Map.Entry<U1, Integer> b1 = it1.next();
        Map.Entry<U2, Integer> b2 = it2.next();

        if(Integer.compare(b1.getValue(), b2.getValue())!=0) {
          return false;
        }
        
        Unit<?> u1 = (Unit<?>) b1.getKey();
        Unit<?> u2 = (Unit<?>) b2.getKey();
        
        if(!u1.equals(u2)) {
          return false;
        }
        
      }
      
      return true;
    }

  }


}
