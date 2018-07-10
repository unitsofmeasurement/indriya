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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import javax.measure.Dimension;
import javax.measure.quantity.Length;

import org.junit.jupiter.api.Test;

public class QuantityDimensionTest {

  /**
   * Verifies that the factory method returns null for null.
   */
  @Test
  public void ofReturnsNullForNull() {
    assertNull(QuantityDimension.of(null));
  }

  /**
   * Verifies that the factory method returns a length dimension for the length quantity class.
   */
  @Test
  public void ofReturnsLengthDimensionForLengthQuantityClass() {
    assertEquals(QuantityDimension.LENGTH, QuantityDimension.of(Length.class));
  }

  /**
   * Verifies that the parse method returns the length dimension for L.
   */
  @Test
  public void parseReturnsLengthForL() {
    assertEquals(QuantityDimension.LENGTH, QuantityDimension.parse('L'));
  }

  /**
   * Verifies that the multiplication is done correctly. Relies on the toString method to verify the result.
   */
  @Test
  public void multiplicationIsDoneCorrectly() {
    Dimension result = QuantityDimension.LENGTH.multiply(QuantityDimension.MASS);
    assertEquals("[L]·[M]", result.toString());
  }

  /**
   * Verifies that the division is done correctly. Relies on the toString method to verify the result.
   */
  @Test
  public void divisionIsDoneCorrectly() {
    Dimension result = QuantityDimension.LENGTH.divide(QuantityDimension.MASS);
    assertEquals("[L]/[M]", result.toString());
  }

  /**
   * Verifies that the division is done correctly by the division overloaded method that takes a QuantityDimension. Relies on the toString method to
   * verify the result.
   */
  @Test
  public void divisionIsDoneCorrectlyInOverloadedQuantityDimensionMethod() {
    Dimension result = ((QuantityDimension) QuantityDimension.LENGTH).divide((QuantityDimension) QuantityDimension.MASS);
    assertEquals("[L]/[M]", result.toString());
  }

  /**
   * Verifies that raising to a power is done correctly by checking that multiplication with itself is the same as raising to the power of two.
   */
  @Test
  public void powerIsDoneCorrectly() {
    Dimension actual = QuantityDimension.LENGTH.pow(2);
    Dimension expected = QuantityDimension.LENGTH.multiply(QuantityDimension.LENGTH);
    assertEquals(expected, actual);
  }

  /**
   * Verifies that the root is taken correctly by checking that the root of the square is neutral.
   */
  @Test
  public void rootIsDoneCorrectly() {
    Dimension result = QuantityDimension.LENGTH.pow(2).root(2);
    assertEquals(QuantityDimension.LENGTH, result);
  }

  /**
   * Verifies that the method getBaseDimensions returns null for a base dimension.
   */
  @Test
  public void getBaseDimensionsIsNullForBaseDimension() {
    assertNull(QuantityDimension.LENGTH.getBaseDimensions());
  }

  /**
   * Verifies that the method getBaseDimensions returns the correct map for a square length times a mass.
   */
  @Test
  public void getBaseDimensionsReturnsCorrectMap() {
    Dimension dimension = QuantityDimension.LENGTH.pow(2).multiply(QuantityDimension.MASS);
    Map baseDimensions = dimension.getBaseDimensions();
    assertEquals(2, baseDimensions.size());
    assertEquals(1, baseDimensions.get(QuantityDimension.MASS));
    assertEquals(2, baseDimensions.get(QuantityDimension.LENGTH));
  }

  /**
   * Verifies that a quantity dimension isn't equal to null.
   */
  @Test
  public void quantityDimensionIsNotEqualToNull() {
    assertFalse(QuantityDimension.LENGTH.equals(null));
  }

  /**
   * Verifies that a quantity dimension is equal to itself.
   */
  @Test
  public void integerQuantityIsEqualToItself() {
    assertTrue(QuantityDimension.LENGTH.equals(QuantityDimension.LENGTH));
  }

  /**
   * Verifies that a quantity dimension is not equal to another quantity dimension.
   */
  @Test
  public void quantityDimensionIsNotEqualToAnotherQuantityDimension() {
    assertFalse(QuantityDimension.LENGTH.equals(QuantityDimension.MASS));
  }

  /**
   * Verifies that a quantity dimension is not equal to an object of a different class.
   */
  @Test
  public void quantityDimensionIsNotEqualToObjectOfDifferentClass() {
    assertFalse(QuantityDimension.LENGTH.equals("A String"));
  }

  /**
   * Verifies that the length and the mass quantity dimensions have different hash codes. Notice that this isn't a strict requirement on the hashCode
   * method, and that hash collisions may occur, but in general, objects that aren't equal shouldn't have an equal hash code.
   */
  @Test
  public void hashCodesAreDifferentForMassAndLengthQuantityDimension() {
    assertFalse(QuantityDimension.LENGTH.hashCode() == QuantityDimension.MASS.hashCode());
  }
}
