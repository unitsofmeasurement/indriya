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

import static javax.measure.MetricPrefix.MILLI;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import javax.measure.Dimension;
import javax.measure.IncommensurableException;
import javax.measure.Prefix;
import javax.measure.Quantity;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.Length;

import org.junit.jupiter.api.Test;

class AlternateUnitTest {

  private static final String SYMBOL = "S";
  private static final String OTHER_SYMBOL = "O";
  private static final Unit<ElectricCurrent> PARENT_UNIT = Units.AMPERE;
  private static final AlternateUnit<ElectricCurrent> ALTERNATE_UNIT = new AlternateUnit<ElectricCurrent>(PARENT_UNIT, SYMBOL);

  /**
   * Verifies that the parent unit is wired correctly in the constructor.
   */
  @Test
  public void parentUnitIsWiredCorrectlyInConstructor() {
    assertEquals(PARENT_UNIT, ALTERNATE_UNIT.getParentUnit());
  }

  /**
   * Verifies that if the parent unit is an alternate unit, its parent is used as the parent unit.
   */
  @Test
  public void parentUnitOfAlternateUnitIsUsedAsParentUnitByConstructor() {
    AlternateUnit<ElectricCurrent> secondAlternateUnit = new AlternateUnit<ElectricCurrent>(ALTERNATE_UNIT, SYMBOL);
    assertEquals(PARENT_UNIT, secondAlternateUnit.getParentUnit());
  }
  
  /**
   * Verifies that the symbol is wired correctly in the constructor.
   */
  @Test
  public void symbolIsWiredCorrectlyInConstructor() {
    assertEquals(SYMBOL, ALTERNATE_UNIT.getSymbol());
  }

  /**
   * Verifies that if the parent unit isn't an AbstractUnit, an exception is thrown.
   */
  @Test
  public void constructorThrowsIllegalArgumentExceptionIfParentUnitIsNotAnAbstractUnit() {
    Unit<ElectricCurrent> nonAbstractUnit = new Unit<ElectricCurrent>() {

      @Override
      public Unit<ElectricCurrent> alternate(String arg0) {
        return null;
      }

      @Override
      public <T extends Quantity<T>> Unit<T> asType(Class<T> arg0) throws ClassCastException {
        return null;
      }

      @Override
      public Unit<ElectricCurrent> divide(double arg0) {
        return null;
      }

      @Override
      public Unit<?> divide(Unit<?> arg0) {
        return null;
      }

      @Override
      public Map<? extends Unit<?>, Integer> getBaseUnits() {
        return null;
      }

      @Override
      public UnitConverter getConverterTo(Unit<ElectricCurrent> arg0) throws UnconvertibleException {
        return null;
      }

      @Override
      public UnitConverter getConverterToAny(Unit<?> arg0) throws IncommensurableException, UnconvertibleException {
        return null;
      }

      @Override
      public Dimension getDimension() {
        return null;
      }

      @Override
      public String getName() {
        return null;
      }

      @Override
      public String getSymbol() {
        return null;
      }

      @Override
      public Unit<ElectricCurrent> getSystemUnit() {
        return null;
      }

      @Override
      public Unit<?> inverse() {
        return null;
      }

      @Override
      public boolean isCompatible(Unit<?> arg0) {
        return false;
      }

      @Override
      public Unit<ElectricCurrent> multiply(double arg0) {
        return null;
      }

      @Override
      public Unit<?> multiply(Unit<?> arg0) {
        return null;
      }

      @Override
      public Unit<?> pow(int arg0) {
        return null;
      }

      @Override
      public Unit<ElectricCurrent> prefix(Prefix arg0) {
        return null;
      }

      @Override
      public Unit<?> root(int arg0) {
        return null;
      }

      @Override
      public Unit<ElectricCurrent> shift(double arg0) {
        return null;
      }

      @Override
      public Unit<ElectricCurrent> transform(UnitConverter arg0) {
        return null;
      }

    @Override
    public Unit<ElectricCurrent> mix(Unit<ElectricCurrent> that) {
        return null;
    }
    };
    assertThrows(IllegalArgumentException.class, () -> {
      new AlternateUnit<ElectricCurrent>(nonAbstractUnit, SYMBOL);
    });
  }

  /**
   * Verifies that if the parent unit isn't a system unit, an exception is thrown.
   */
  @Test
  public void constructorThrowsIllegalArgumentExceptionIfParentUnitIsNotASystemUnit() {
    assertThrows(IllegalArgumentException.class, () -> {
      new AlternateUnit<ElectricCurrent>(MILLI(PARENT_UNIT), SYMBOL);
    });
  }

  /**
   * Verifies that getDimension returns the parent unit's dimension.
   */
  @Test
  public void getDimensionReturnsTheParentUnitsDimension() {
    assertEquals(PARENT_UNIT.getDimension(), ALTERNATE_UNIT.getDimension());
  }

  /**
   * Verifies that getSystemConverter returns the parent unit's system converter.
   */
  @Test
  public void getSystemConverterReturnsTheParentUnitsSystemConverter() {
    assertEquals(PARENT_UNIT.getConverterTo(PARENT_UNIT.getSystemUnit()), ALTERNATE_UNIT.getSystemConverter());
  }

  /**
   * Verifies that toSystemUnit returns the alternate unit.
   */
  @Test
  public void toSystemUnitReturnsTheAlternateUnit() {
    assertEquals(ALTERNATE_UNIT, ALTERNATE_UNIT.toSystemUnit());
  }

  /**
   * Verifies that getBaseUnits returns the parent unit's base units.
   */
  @Test
  public void getBaseUnitsReturnsTheParentUnitsBaseUnits() {
    assertEquals(PARENT_UNIT.getBaseUnits(), ALTERNATE_UNIT.getBaseUnits());
  }

  /**
   * Verifies that an alternate unit is equal to itself.
   */
  @Test
  public void alternateUnitIsEqualToItself() {
    assertEquals(ALTERNATE_UNIT, ALTERNATE_UNIT);
  }

  /**
   * Verifies that an alternate unit is equal to another alternate unit with the same parent unit and symbol.
   */
  @Test
  public void alternateUnitIsEqualToAnotherAlternateUnitWithTheSameParentUnitAndSymbol() {
    AlternateUnit<ElectricCurrent> otherUnit = new AlternateUnit<ElectricCurrent>(PARENT_UNIT, SYMBOL);
    assertEquals(ALTERNATE_UNIT, otherUnit);
  }

  /**
   * Verifies that an alternate unit is not equal to another alternate unit with another parent unit.
   */
  @Test
  public void alternateUnitIsNotEqualToAnotherAlternateUnitWithAnotherParentUnit() {
    AlternateUnit<Length> otherUnit = new AlternateUnit<Length>(Units.METRE, SYMBOL);
    assertNotEquals(ALTERNATE_UNIT, otherUnit);
  }

  /**
   * Verifies that an alternate unit is not equal to another alternate unit with another symbol.
   */
  @Test
  public void alternateUnitIsNotEqualToAnotherAlternateUnitWithAnotherSymbol() {
    AlternateUnit<ElectricCurrent> otherUnit = new AlternateUnit<ElectricCurrent>(PARENT_UNIT, OTHER_SYMBOL);
    assertNotEquals(ALTERNATE_UNIT, otherUnit);
  }

  /**
   * Verifies that an alternate unit is not equal to an object of a different type.
   */
  @Test
  public void alternateUnitIsNotEqualToAString() {
    assertNotEquals(ALTERNATE_UNIT, PARENT_UNIT);
  }

  /**
   * Verifies that an alternate unit is not equal to null.
   */
  @Test
  public void alternateUnitIsNotEqualToNull() {
    assertNotEquals(ALTERNATE_UNIT, null);
  }

  /**
   * Verifies that an alternate unit has the same hash code as another alternate unit with the same parent unit and symbol.
   */
  @Test
  public void alternateUnitHasTheSameHashCodeAsAnotherAlternateUnitWithTheSameParentUnitAndSymbol() {
    AlternateUnit<ElectricCurrent> otherUnit = new AlternateUnit<ElectricCurrent>(PARENT_UNIT, SYMBOL);
    assertEquals(ALTERNATE_UNIT.hashCode(), otherUnit.hashCode());
  }

  /**
   * Verifies that an alternate unit has a different hash code if the parent unit is different. Note that this isn't a requirement for the hashCode
   * method, but generally a good property to have.
   */
  @Test
  public void alternateUnitHasDifferentHashCodeForAlternateUnitWithDifferentParentUnit() {
    AlternateUnit<Length> otherUnit = new AlternateUnit<Length>(Units.METRE, SYMBOL);
    assertNotEquals(ALTERNATE_UNIT.hashCode(), otherUnit.hashCode());
  }

  /**
   * Verifies that an alternate unit has a different hash code if the symbol is different. Note that this isn't a requirement for the hashCode method,
   * but generally a good property to have.
   */
  @Test
  public void alternateUnitHasDifferentHashCodeForAlternateUnitWithDifferentSymbol() {
    AlternateUnit<ElectricCurrent> otherUnit = new AlternateUnit<ElectricCurrent>(PARENT_UNIT, OTHER_SYMBOL);
    assertNotEquals(ALTERNATE_UNIT.hashCode(), otherUnit.hashCode());
  }
  
  /**
   * Verifies the construction of an alternate unit via the of() method.
   */
  @Test
  public void alternateUnitOf() {
    AlternateUnit<Length> otherUnit = AlternateUnit.of(PARENT_UNIT, SYMBOL);
    assertEquals(ALTERNATE_UNIT, otherUnit);
  }
}
