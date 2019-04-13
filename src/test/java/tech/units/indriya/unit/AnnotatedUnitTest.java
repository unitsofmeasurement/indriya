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

import javax.measure.quantity.ElectricCurrent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static javax.measure.MetricPrefix.MEGA;
import static javax.measure.MetricPrefix.MILLI;

import tech.units.indriya.AbstractUnit;

public class AnnotatedUnitTest {

  private static final String ANNOTATION = "Annotation";
  private static final AnnotatedUnit<ElectricCurrent> ANNOTATED_AMPERE = new AnnotatedUnit<ElectricCurrent>(
      (AbstractUnit<ElectricCurrent>) Units.AMPERE, ANNOTATION);

  /**
   * Verifies that the actual unit is wired correctly in the constructor.
   */
  @Test
  public void actualUnitIsWiredCorrectlyInConstructor() {
    assertEquals(Units.AMPERE, ANNOTATED_AMPERE.getActualUnit());
  }

  /**
   * Verifies that the annotation is wired correctly in the constructor.
   */
  @Test
  public void annotationIsWiredCorrectlyInConstructor() {
    assertEquals(ANNOTATION, ANNOTATED_AMPERE.getAnnotation());
  }

  /**
   * Verifies that when an annotation unit is provided to the constructor, its actual unit is used as the actual unit for the new annotated unit.
   */
  @Test
  public void annotatedUnitsActualUnitIsUsedAsActualUnitByInConstructor() {
    AnnotatedUnit<ElectricCurrent> unit = new AnnotatedUnit<ElectricCurrent>(ANNOTATED_AMPERE, ANNOTATION);
    assertEquals(Units.AMPERE, unit.getActualUnit());
  }

  /**
   * Verifies that getSymbol returns the actual unit's symbol.
   */
  @Test
  public void getSymbolReturnsTheActualUnitsSymbol() {
    assertEquals(Units.AMPERE.getSymbol(), ANNOTATED_AMPERE.getSymbol());
  }

  /**
   * Verifies that getBaseUnits returns the actual unit's base units.
   */
  @Test
  public void getBaseUnitsReturnsTheActualUnitsBaseUnits() {
    assertEquals(Units.AMPERE.getBaseUnits(), ANNOTATED_AMPERE.getBaseUnits());
  }

  /**
   * Verifies that toSystemUnit returns the actual unit's system unit.
   */
  @Test
  public void toSystemUnitReturnsTheActualUnitsSystemUnit() {
    assertEquals(Units.AMPERE.getSystemUnit(), ANNOTATED_AMPERE.toSystemUnit());
  }

  /**
   * Verifies that getDimension returns the actual unit's dimension.
   */
  @Test
  public void getDimensionReturnsTheActualUnitsDimension() {
    assertEquals(Units.AMPERE.getDimension(), ANNOTATED_AMPERE.getDimension());
  }

  /**
   * Verifies that getSystemConverter returns the actual unit's system converter.
   */
  @Test
  public void getSystemConverterReturnsTheActualUnitsSystemConverter() {
    assertEquals(Units.AMPERE.getConverterTo(Units.AMPERE.getSystemUnit()), ANNOTATED_AMPERE.getSystemConverter());
  }

  /**
   * Verifies that an annotated unit is equal to itself.
   */
  @Test
  public void annotatedUnitIsEqualToItself() {
    assertEquals(ANNOTATED_AMPERE, ANNOTATED_AMPERE);
  }

  /**
   * Verifies that an annotated unit is equal to another annotated unit with the same actual unit and annotationn.
   */
  @Test
  public void annotatedUnitIsEqualToAnotherAnnotatedUnitWithTheSameActualUnitAndAnnotation() {
    AnnotatedUnit<ElectricCurrent> otherUnit = new AnnotatedUnit<ElectricCurrent>((AbstractUnit<ElectricCurrent>) Units.AMPERE, ANNOTATION);
    assertEquals(ANNOTATED_AMPERE, otherUnit);
  }

  /**
   * Verifies that an annotated unit is not equal to another annotated unit with another actual unit.
   */
  @Test
  public void annotatedUnitIsNotEqualToAnotherAnnotatedUnitWithAnotherActualUnit() {
    AnnotatedUnit<ElectricCurrent> otherUnit = new AnnotatedUnit<ElectricCurrent>((AbstractUnit<ElectricCurrent>) MILLI(Units.AMPERE), ANNOTATION);
    assertNotEquals(ANNOTATED_AMPERE, otherUnit);
  }

  /**
   * Verifies that an annotated unit is not equal to another annotated unit with another annotation.
   */
  @Test
  public void annotatedUnitIsNotEqualToAnotherAnnotatedUnitWithAnotherAnnotation() {
    AnnotatedUnit<ElectricCurrent> otherUnit = new AnnotatedUnit<ElectricCurrent>((AbstractUnit<ElectricCurrent>) Units.AMPERE, "Other annotation");
    assertNotEquals(ANNOTATED_AMPERE, otherUnit);
  }

  /**
   * Verifies that an annotated unit is not equal to an object of a different type.
   */
  @Test
  public void annotatedUnitIsNotEqualToAString() {
    assertNotEquals(ANNOTATED_AMPERE, ANNOTATION);
  }

  /**
   * Verifies that an annotated unit is not equal to null.
   */
  @Test
  public void annotatedUnitIsNotNull() {
      assertNotNull(ANNOTATED_AMPERE);
  }

  /**
   * Verifies that an annotated unit has the same hash code as another annotated unit with the same actual unit and annotation.
   */
  @Test
  public void annotatedUnitHasTheSameHashCodeAsAnotherAnnotatedUnitWithTheSameActualUnitAndAnnotation() {
    AnnotatedUnit<ElectricCurrent> otherUnit = new AnnotatedUnit<ElectricCurrent>((AbstractUnit<ElectricCurrent>) Units.AMPERE, ANNOTATION);
    assertEquals(ANNOTATED_AMPERE.hashCode(), otherUnit.hashCode());
  }

  /**
   * Verifies that an annotated unit has a different hash code if the actual unit is different. Note that this isn't a requirement for the hashCode
   * method, but generally a good property to have.
   */
  @Test
  public void annotatedUnitHasDifferentHashCodeForAnnotatedUnitWithDifferentActualUnit() {
    AnnotatedUnit<ElectricCurrent> otherUnit = new AnnotatedUnit<ElectricCurrent>((AbstractUnit<ElectricCurrent>) MILLI(Units.AMPERE), ANNOTATION);
    assertNotEquals(ANNOTATED_AMPERE.hashCode(), otherUnit.hashCode());
  }

  /**
   * Verifies that an annotated unit has a different hash code if the annotation is different. Note that this isn't a requirement for the hashCode
   * method, but generally a good property to have.
   */
  @Test
  public void annotatedUnitHasDifferentHashCodeForAnnotatedUnitWithDifferentAnnotation() {
    AnnotatedUnit<ElectricCurrent> otherUnit = new AnnotatedUnit<ElectricCurrent>((AbstractUnit<ElectricCurrent>) Units.AMPERE, "Other annotation");
    assertNotEquals(ANNOTATED_AMPERE.hashCode(), otherUnit.hashCode());
  }
  
  /**
   * Verifies that an annotated unit has an identity converter to its actual unit.
   */
  @Test
  public void annotatedUnitHasIdentityConverter() {
    assertTrue(ANNOTATED_AMPERE.getConverterTo(Units.AMPERE).isIdentity());
  }
  
  /**
   * Verifies the construction of an annotated unit via the of() method.
   */
  @Test
  public void testUnitOf() {
    AnnotatedUnit<ElectricCurrent> otherUnit = AnnotatedUnit.of(Units.AMPERE, ANNOTATION);
    assertEquals(ANNOTATED_AMPERE, otherUnit);
  }
  
  /**
   * Verifies the string representation of an annotated unit.
   */
  @Test
  public void testStringRepresentation() {
    assertEquals("A{Annotation}", ANNOTATED_AMPERE.toString());
  }
  
  /**
   * Verifies that the actual unit is wired correctly in the constructor.
   */
  @Test
  public void actualUnitIsNotEqualToShift() {
    assertNotEquals(Units.AMPERE.shift(10), ANNOTATED_AMPERE.shift(10));
  }
  
  /**
   * Verifies that the lead unit is wired correctly in the constructor.
   */
  @Test
  public void actualUnitIsNotEqualToMega() {
    assertNotEquals(MEGA(Units.AMPERE), MEGA(ANNOTATED_AMPERE).toString());
  }
}
