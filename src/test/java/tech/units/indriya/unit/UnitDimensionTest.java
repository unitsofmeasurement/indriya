/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2024, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import javax.measure.Dimension;
import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Volume;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * UnitDimension tests.
 */
public class UnitDimensionTest {

  /**
   * Verifies that the factory method returns null for null.
   */
  @Test
  public void ofReturnsNullForNull() {
    assertNull(UnitDimension.of(null));
  }

  /**
   * Verifies that the factory method returns a length dimension for the length quantity class.
   */
  @Test
  public void ofReturnsLengthDimensionForLengthQuantityClass() {
    assertEquals(UnitDimension.LENGTH, UnitDimension.of(Length.class));
  }

  /**
   * Verifies that the parse method returns the length dimension for L.
   */
  @Test
  public void parseReturnsLengthForL() {
    assertEquals(UnitDimension.LENGTH, UnitDimension.parse('L'));
  }

  /**
   * Verifies that the multiplication is done correctly. Relies on the toString method to verify the result.
   */
  @Test
  public void multiplicationIsDoneCorrectly() {
    Dimension result = UnitDimension.LENGTH.multiply(UnitDimension.MASS);
    assertEquals("[L]·[M]", result.toString());
  }

  /**
   * Verifies that the division is done correctly. Relies on the toString method to verify the result.
   */
  @Test
  public void divisionIsDoneCorrectly() {
    Dimension result = UnitDimension.LENGTH.divide(UnitDimension.MASS);
    assertEquals("[L]/[M]", result.toString());
  }

  /**
   * Verifies that the division is done correctly by the division overloaded method that takes a UnitDimension. Relies on the toString method to
   * verify the result.
   */
  @Test
  public void divisionIsDoneCorrectlyInOverloadedUnitDimensionMethod() {
    Dimension result = ((UnitDimension) UnitDimension.LENGTH).divide((UnitDimension) UnitDimension.MASS);
    assertEquals("[L]/[M]", result.toString());
  }

  /**
   * Verifies that the division is done correctly on powered units. Relies on the toString method to
   * verify the result.
   */
  @Test
  public void divisionIsDoneCorrectlyOnPoweredBaseUnits() {
    Dimension m2 = UnitDimension.MASS.pow(2);
    Dimension result = UnitDimension.LENGTH.divide(m2);
    assertEquals("[L]/[M]²", result.toString());
  }

  /**
   * Verifies that the division is done correctly on powered units. Relies on the toString method to
   * verify the result.
   */
  @Test
  public void divisionIsDoneCorrectlyOnPoweredProductUnits() {
    Dimension ml = UnitDimension.MASS.multiply(UnitDimension.LENGTH);
    Dimension result = UnitDimension.LENGTH.divide(ml);
    assertEquals("1/[M]", result.toString());
  }

  /**
   * Verifies that raising to a power is done correctly by checking that multiplication with itself is the same as raising to the power of two.
   */
  @Test
  public void powerIsDoneCorrectly() {
    Dimension actual = UnitDimension.LENGTH.pow(2);
    Dimension expected = UnitDimension.LENGTH.multiply(UnitDimension.LENGTH);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the root is taken correctly by checking that the root of the square is neutral.
   */
  @Test
  public void rootIsDoneCorrectly() {
    Dimension result = UnitDimension.LENGTH.pow(2).root(2);
    assertEquals(UnitDimension.LENGTH, result);
  }

  /**
   * Verifies that the method getBaseDimensions returns null for a base dimension.
   */
  @Test
  public void getBaseDimensionsIsNullForBaseDimension() {
    assertNull(UnitDimension.LENGTH.getBaseDimensions());
  }

  /**
   * Verifies that the method getBaseDimensions returns the correct map for a square length times a mass.
   */
  @Test
  public void getBaseDimensionsReturnsCorrectMap() {
    Dimension dimension = UnitDimension.LENGTH.pow(2).multiply(UnitDimension.MASS);
    Map<? extends Dimension, Integer> baseDimensions = dimension.getBaseDimensions();
    assertEquals(2, baseDimensions.size());
    assertEquals(1, baseDimensions.get(UnitDimension.MASS).intValue());
    assertEquals(2, baseDimensions.get(UnitDimension.LENGTH).intValue());
  }

  /**
   * Verifies that a quantity dimension isn't equal to null.
   */
  @Test
  public void UnitDimensionIsNotEqualToNull() {
    assertNotNull(UnitDimension.LENGTH);
  }

  /**
   * Verifies that a quantity dimension is equal to itself.
   */
  @Test
  public void integerQuantityIsEqualToItself() {
    assertTrue(UnitDimension.LENGTH.equals(UnitDimension.LENGTH));
  }

  /**
   * Verifies that a quantity dimension is not equal to another quantity dimension.
   */
  @Test
  public void UnitDimensionIsNotEqualToAnotherUnitDimension() {
    assertFalse(UnitDimension.LENGTH.equals(UnitDimension.MASS));
  }

  /**
   * Verifies that a quantity dimension is not equal to an object of a different class.
   */
  @SuppressWarnings("unlikely-arg-type")
  @Test
  public void UnitDimensionIsNotEqualToObjectOfDifferentClass() {
    assertFalse(UnitDimension.LENGTH.equals("A String"));
  }

  /**
   * Verifies that the length and the mass quantity dimensions have different hash codes. Notice that this isn't a strict requirement on the hashCode
   * method, and that hash collisions may occur, but in general, objects that aren't equal shouldn't have an equal hash code.
   */
  @Test
  public void hashCodesAreDifferentForMassAndLengthUnitDimension() {
    assertFalse(UnitDimension.LENGTH.hashCode() == UnitDimension.MASS.hashCode());
  }
  
  private static class USCustomary {
	    public static final Unit<Length> MILE = Units.METRE.multiply(1609.344).asType(Length.class);
	    public static final Unit<Volume> GALLON_LIQUID = Units.LITRE.multiply(3.785411784).asType(Volume.class);
	  }

	  interface FuelConsumption extends Quantity<FuelConsumption> {
	    static final Unit<FuelConsumption> LITRE_PER_100KM  = 
	        Units.LITRE.divide(Units.METRE).divide(100_000).asType(FuelConsumption.class);
	  }

	  interface FuelEconomy extends Quantity<FuelEconomy> {
	    static final Unit<FuelEconomy> MILES_PER_GALLON = 
	        USCustomary.MILE.divide(USCustomary.GALLON_LIQUID).asType(FuelEconomy.class);
	  }
	  
	  @Test
	  @DisplayName("dimensions should be equal when same (shared) BaseUnit")
	  public void dimensionsShouldBeEqual() {
	    
	    // multiplication of these two results in a dimensionless entity  
	    Dimension consumptionDim = FuelConsumption.LITRE_PER_100KM.getDimension();
	    Dimension economyDim = FuelEconomy.MILES_PER_GALLON.getDimension();
	    
	    assertTrue(consumptionDim.equals(economyDim.pow(-1)));
	    assertTrue(economyDim.equals(consumptionDim.pow(-1)));
	    
	    assertTrue(economyDim.equals(consumptionDim.pow(-1)));
	    assertTrue(consumptionDim.equals(economyDim.pow(-1)));
	    
	  }

	  @Test
	  @DisplayName("units should be compatible when 'same' dimensions")
	  public void unitsShouldBeCompatible() 
	      throws UnconvertibleException, IncommensurableException {
	    
	    // given: a and b, having compatible dimensions	    
	    Unit<FuelConsumption> a = FuelConsumption.LITRE_PER_100KM;
	    Unit<?> b = FuelEconomy.MILES_PER_GALLON.pow(-1);
	    
	    // when: even though a and b are different	    
	    assertFalse(a.getConverterToAny(b).isIdentity());
	    
	    // then: a and b should be compatible	    
	    assertTrue(a.isCompatible(b));	    
	  }
}
