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

import static org.junit.jupiter.api.Assertions.*;

import javax.measure.Unit;

import org.junit.jupiter.api.Test;

public class ProductUnitTest {

  private static final ProductUnit<?> KILOGRAM_METRE = (ProductUnit<?>) ProductUnit.ofProduct(Units.KILOGRAM, Units.METRE);
  private static final ProductUnit<?> SECOND_CANDELA = (ProductUnit<?>) ProductUnit.ofProduct(Units.SECOND, Units.CANDELA);
  private static final ProductUnit<?> KILOGRAM_PER_METRE = (ProductUnit<?>) ProductUnit.ofQuotient(Units.KILOGRAM, Units.METRE);
  private static final ProductUnit<?> KILOGRAM_METRE_SECOND_CANDELA = (ProductUnit<?>) ProductUnit.ofProduct(KILOGRAM_METRE, SECOND_CANDELA);

  /**
   * Verifies that the empty constructor creates an empty product unit.
   */
  @SuppressWarnings("rawtypes")
  @Test
  public void emptyConstructorCreatesEmptyProductUnit() {
    ProductUnit<?> product = new ProductUnit();
    assertEquals(0, product.getUnitCount());
  }

  /**
   * Verifies that the multiplication of two times the same system unit results in a unit count of one.
   */
  @Test
  public void productOfSameSystemUnitsProducesAProductUnitOfSizeOne() {
    ProductUnit<?> product = (ProductUnit<?>) ProductUnit.ofProduct(Units.KILOGRAM, Units.KILOGRAM);
    assertEquals(1, product.getUnitCount());
  }

  /**
   * Verifies that the multiplication of two system units results in a unit count of two.
   */
  @Test
  public void productOfTwoSystemUnitsProducesAProductUnitOfSizeTwo() {
    assertEquals(2, KILOGRAM_METRE.getUnitCount());
  }

  /**
   * Verifies that the division of two system units results in a unit count of two.
   */
  @Test
  public void divisionOfTwoSystemUnitsProducesAProductUnitOfSizeTwo() {
    assertEquals(2, KILOGRAM_PER_METRE.getUnitCount());
  }

  /**
   * Verifies that the multiplication of two product units of each two different system units results in a unit count of four.
   */
  @Test
  public void productOfTwoProductUnitsWithEachTwoDifferentSystemUnitsProducesAProductUnitOfSizeFour() {
    assertEquals(4, KILOGRAM_METRE_SECOND_CANDELA.getUnitCount());
  }

  /**
   * Verifies that if the product leads to a base unit with power one, the base unit is returned.
   */
  @Test
  public void divisionLeadingToBaseUnitResultsInBaseUnit() {
    Unit<?> product = ProductUnit.ofQuotient(KILOGRAM_METRE, Units.METRE);
    assertEquals(Units.KILOGRAM, product);
  }

  /**
   * Verifies that the elimination of a unit results in a reduced unit count.
   */
  @Test
  public void divisionLeadingToEliminationOfAUnitResultsInLowerUnitCount() {
    ProductUnit<?> product = (ProductUnit<?>) ProductUnit.ofQuotient(KILOGRAM_METRE_SECOND_CANDELA, Units.METRE);
    assertEquals(3, product.getUnitCount());
  }

  /**
   * Verifies that the square of a system unit leads to one unit count.
   */
  @Test
  public void squareOfSystemUnitProducesUnitCountOfOne() {
    ProductUnit<?> square = (ProductUnit<?>) ProductUnit.ofPow(Units.METRE, 2);
    assertEquals(1, square.getUnitCount());
  }

  /**
   * Verifies that the root of a system unit leads to one unit count.
   */
  @Test
  public void rootOfSystemUnitProducesUnitCountOfOne() {
    ProductUnit<?> root = (ProductUnit<?>) ProductUnit.ofRoot(Units.METRE, 2);
    assertEquals(1, root.getUnitCount());
  }

  /**
   * Verifies that the power of 1 of a base unit returns the base unit again.
   */
  @Test
  public void powerOfOneOfABaseUnitReturnsTheBaseUnit() {
    Unit<?> unit = ProductUnit.ofPow(Units.METRE, 1);
    assertEquals(Units.METRE, unit);
  }

  /**
   * Verifies that the root of 1 of a base unit returns the base unit again.
   */
  @Test
  public void rootOfOneOfABaseUnitReturnsTheBaseUnit() {
    Unit<?> unit = ProductUnit.ofRoot(Units.METRE, 1);
    assertEquals(Units.METRE, unit);
  }

  /**
   * Verifies that the power of a unit returns the same unit count.
   */
  @Test
  public void powerOfProductUnitReturnsTheSameUnitCount() {
    ProductUnit<?> square = (ProductUnit<?>) KILOGRAM_METRE.pow(2);
    assertEquals(2, square.getUnitCount());
  }

  /**
   * Verifies that the result of calling <code>pow</code> and <code>ofPow</code> are considered equal.
   */
  @Test
  public void powAndOfPowShouldProduceResultsThatAreConsideredEqual() {
    ProductUnit<?> square1 = (ProductUnit<?>) KILOGRAM_METRE.pow(2);
    ProductUnit<?> square2 = (ProductUnit<?>) ProductUnit.ofPow(KILOGRAM_METRE, 2);
    assertEquals(square1, square2);
  }

  /**
   * Verifies that a product unit is equal to itself.
   */
  @Test
  public void productUnitIsEqualToItself() {
    assertEquals(KILOGRAM_METRE, KILOGRAM_METRE);
  }

  /**
   * Verifies that a product unit is equal to another product unit composed in the same manner.
   */
  @Test
  public void productUnitIsEqualToAnotherProductUnitComposedOfTheSameUnits() {
    ProductUnit<?> otherKilogramMetre = (ProductUnit<?>) ProductUnit.ofProduct(Units.KILOGRAM, Units.METRE);
    assertEquals(KILOGRAM_METRE, otherKilogramMetre);
  }

  /**
   * Verifies that a product unit is equal to another product unit composed with the same units but in a different order.
   */
  @Test
  public void productUnitIsEqualToAnotherProductUnitComposedOfTheSameUnitsInADifferentOrder() {
    ProductUnit<?> otherKilogramMetre = (ProductUnit<?>) ProductUnit.ofProduct(Units.METRE, Units.KILOGRAM);
    assertEquals(KILOGRAM_METRE, otherKilogramMetre);
  }

  /**
   * Verifies that a product unit is not equal to another product unit with a different number of units.
   */
  @Test
  public void productUnitIsNotEqualToAnotherProductUnitWithADifferentNumberOfUnits() {
    assertNotEquals(KILOGRAM_METRE, KILOGRAM_METRE_SECOND_CANDELA);
  }

  /**
   * Verifies that a product unit is not equal to another product unit with a different units.
   */
  @Test
  public void productUnitIsNotEqualToAnotherProductUnitWithDifferentUnits() {
    assertNotEquals(KILOGRAM_METRE, SECOND_CANDELA);
  }

  /**
   * Verifies that a product unit is not equal to another product unit with the same units but a different power.
   */
  @Test
  public void productUnitIsNotEqualToAnotherProductUnitWithADifferentPowerForAUnit() {
    assertNotEquals(KILOGRAM_METRE, KILOGRAM_PER_METRE);
  }

  /**
   * Verifies that a product unit is not equal to an object of another class, like a string.
   */
  @Test
  public void productUnitIsNotEqualToAString() {
    assertNotEquals(KILOGRAM_METRE, "a string");
  }

  /**
   * Verifies that a product unit is not equal to null.
   */
  @Test
  public void productUnitIsNotEqualNull() {
    assertNotEquals(KILOGRAM_METRE, null);
  }

  /**
   * Verifies that a product unit has the same hash code as another product unit composed in the same manner.
   */
  @Test
  public void productUnitHasTheSameHashCodeAsAnotherProductUnitComposedOfTheSameUnits() {
    ProductUnit<?> otherKilogramMetre = (ProductUnit<?>) ProductUnit.ofProduct(Units.KILOGRAM, Units.METRE);
    assertEquals(KILOGRAM_METRE.hashCode(), otherKilogramMetre.hashCode());
  }

  /**
   * Verifies that a product unit has the same hash code as another product unit composed with the same units but in a different order.
   */
  @Test
  public void productUnitHasTheSameHashCodeAsAnotherProductUnitComposedOfTheSameUnitsInADifferentOrder() {
    ProductUnit<?> otherKilogramMetre = (ProductUnit<?>) ProductUnit.ofProduct(Units.METRE, Units.KILOGRAM);
    assertEquals(KILOGRAM_METRE.hashCode(), otherKilogramMetre.hashCode());
  }

  /**
   * Verifies that a product unit doesn't have the same hash code as another product unit with a different number of units. Note that this isn't a
   * requirement for the hashCode method, but generally a good property to have.
   */
  @Test
  public void productUnitDoesNotHaveTheSameHashCodeAsAnotherProductUnitWithADifferentNumberOfUnits() {
    assertNotEquals(KILOGRAM_METRE.hashCode(), KILOGRAM_METRE_SECOND_CANDELA.hashCode());
  }

  /**
   * Verifies that a product unit doesn't have the same hash code as another product unit with a different units. Note that this isn't a requirement
   * for the hashCode method, but generally a good property to have.
   */
  @Test
  public void productUnitDoesNotHaveTheSameHashCodeAsAnotherProductUnitWithDifferentUnits() {
    assertNotEquals(KILOGRAM_METRE.hashCode(), SECOND_CANDELA.hashCode());
  }

  /**
   * Verifies that a product unit doesn't have the same hash code as another product unit with the same units but a different power. Note that this
   * isn't a requirement for the hashCode method, but generally a good property to have.
   */
  @Test
  public void productUnitDoesNotHaveTheSameHashCodeAsAnotherProductUnitWithADifferentPowerForAUnit() {
    assertNotEquals(KILOGRAM_METRE.hashCode(), KILOGRAM_PER_METRE.hashCode());
  }
}
