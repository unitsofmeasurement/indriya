/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2019, Units of Measurement project.
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

import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Time;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

/**
*
* @author Werner Keil
*/
public class MixedUnitTest {

  @SuppressWarnings("unchecked")
  private static final Unit<Time>[] TIMES = new Unit[] {Units.DAY, Units.HOUR, Units.MINUTE};
  private static final MixedUnit<Time> MIXED_TIME = MixedUnit.of(TIMES);
  private static final String STRING = "day;h;min";
  
  /**
   * Verifies that the lead unit is wired correctly in the constructor.
   */
  @Test
  public void leadUnitIsWiredCorrectlyInConstructor() {
    assertEquals(Units.DAY, MIXED_TIME.getLeadUnit());
  }

  /**
   * Verifies that the units are wired correctly in the constructor.
   */
  @Test
  public void unitsAreWiredCorrectlyInConstructor() {
    //assertThat(MIXED_TIME.getUnits(), Matchers.arrayContaining(expected));
    assertEquals(Arrays.asList(TIMES), MIXED_TIME.getUnits());
  }

  /**
   * Verifies that getSymbol returns the lead unit's symbol.
   */
  @Test
  public void getSymbolReturnsLeadUnitsSymbol() {
    assertEquals(Units.DAY.getSymbol(), MIXED_TIME.getSymbol());
  }

  /**
   * Verifies that getBaseUnits returns the actual unit's base units.
   */
  @Test
  public void getBaseUnitsReturnsTheActualUnitsBaseUnits() {
    assertEquals(Units.AMPERE.getBaseUnits(), MIXED_TIME.getBaseUnits());
  }

  /**
   * Verifies that toSystemUnit returns the actual unit's system unit.
   */
  @Test
  public void toSystemUnitReturnsTheActualUnitsSystemUnit() {
    assertEquals(Units.DAY.getSystemUnit(), MIXED_TIME.toSystemUnit());
  }

  /**
   * Verifies that getDimension returns the actual unit's dimension.
   */
  @Test
  public void getDimensionReturnsTheActualUnitsDimension() {
    assertEquals(Units.DAY.getDimension(), MIXED_TIME.getDimension());
  }

  /**
   * Verifies that getSystemConverter returns the actual unit's system converter.
   */
  @Test
  public void getSystemConverterReturnsTheActualUnitsSystemConverter() {
    assertEquals(Units.DAY.getConverterTo(Units.DAY.getSystemUnit()), MIXED_TIME.getSystemConverter());
  }

  /**
   * Verifies that an mixed unit is equal to itself.
   */
  @Test
  public void mixedUnitIsEqualToItself() {
    assertEquals(MIXED_TIME, MIXED_TIME);
  }

  /**
   * Verifies that an mixed unit is equal to another mixed unit with the same units.
   */
  @Test
  public void mixedUnitIsEqualToAnotherMixedUnitWithTheSameActualUnits() {
    MixedUnit<Time> otherUnit = MixedUnit.of(TIMES);
    assertEquals(MIXED_TIME, otherUnit);
  }

  /**
   * Verifies that an mixed unit is not equal to another mixed unit with other units.
   */
  @Test
  public void mixedUnitIsNotEqualToAnotherMixedUnit() {
    MixedUnit<Time> otherUnit = MixedUnit.of(Units.HOUR, Units.MINUTE, Units.SECOND);
    assertNotEquals(MIXED_TIME, otherUnit);
  }
  
  /**
   * Verifies that an mixed unit is not equal to a single unit.
   */
  @Test
  public void mixedUnitIsNotEqualToAnotherSingleUnit() {
    assertNotEquals(MIXED_TIME, Units.DAY);
  }

  /**
   * Verifies that an mixed unit is not equal to an object of a different type.
   */
  @Test
  public void mixedUnitIsNotEqualToAString() {
    assertNotEquals(MIXED_TIME, STRING);
  }

  /**
   * Verifies that an mixed unit is not equal to null.
   */
  @Test
  public void mixedUnitIsNotNull() {
    assertNotNull(MIXED_TIME);
  }

  /**
   * Verifies that an mixed unit has the same hash code as another mixed unit with the same actual unit and annotation.
   */
  @Test
  public void mixedUnitHasTheSameHashCodeAsAnotherMixedUnitWithTheSameUnits() {
    MixedUnit<Time> otherUnit = MixedUnit.of(Units.DAY, Units.HOUR, Units.MINUTE);
    assertEquals(MIXED_TIME.hashCode(), otherUnit.hashCode());
  }

  /**
   * Verifies that an mixed unit has a different hash code if the actual unit is different. Note that this isn't a requirement for the hashCode
   * method, but generally a good property to have.
   */
  @Test
  public void mixedUnitHasDifferentHashCodeForMixedUnitWithDifferentUnits() {
    MixedUnit<Time> otherUnit = MixedUnit.of(Units.HOUR, Units.MINUTE, Units.SECOND);
    assertNotEquals(MIXED_TIME.hashCode(), otherUnit.hashCode());
  }
  
  /**
   * Verifies that an mixed unit has an identity converter to its lead unit.
   */
  @Test
  public void mixedUnitHasIdentityConverter() {
    assertTrue(MIXED_TIME.getConverterTo(Units.DAY).isIdentity());
  }
  
  /**
   * Verifies the string representation of a mixed unit.
   */
  @Test
  public void testStringRepresentation() {
    assertEquals(STRING, MIXED_TIME.toString());
  }

  /**
   * Verifies the Unit.mix() creation of a mixed unit.
   */
  @Test
  public void testMixTime() {
    final Unit<Time> mixUnit =  Units.DAY.mix(Units.HOUR).mix(Units.MINUTE);
    assertEquals(MIXED_TIME, mixUnit);
  }
  
  /**
   * Test method for {@link javax.measure.Unit#getConverterTo}.
   */
  @Test
  public void testConverterToDay() {
    final Unit<Time> mixTime =  Units.HOUR.
              mix(Units.MINUTE).mix(Units.SECOND);
    assertTrue(mixTime instanceof MixedUnit);
    final MixedUnit<Time> actualMixTime = (MixedUnit<Time>)mixTime;
    assertEquals(Units.HOUR, actualMixTime.getLeadUnit());
    UnitConverter converter = mixTime.getConverterTo(Units.DAY);
    UnitConverter converter2 = Units.HOUR.getConverterTo(Units.DAY);
    Double result = converter.convert(1d);
    Double result2 = converter2.convert(1d);
    UnitConverter converter3 = Units.DAY.mix(Units.HOUR).getConverterTo(Units.DAY);
    assertEquals(result, result2);
    assertTrue(Units.DAY.getConverterTo(Units.DAY).isIdentity());
    assertFalse(converter.isIdentity());
    assertFalse(converter2.isIdentity());
    assertTrue(converter3.isIdentity());
  }
}
