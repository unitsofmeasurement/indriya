/*
 * Next Generation Units of Measurement Implementation
 * Copyright (c) 2005-2017, Jean-Marie Dautelle, Werner Keil, V2COM.
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
 * 3. Neither the name of JSR-363, Indriya nor the names of their contributors may be used to endorse or promote products
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
package tec.units.indriya.unit;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import tec.units.indriya.AbstractUnit;

import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * This class represents an annotated unit.
 * </p>
 * 
 * <p>
 * Instances of this class are created through the {@link AbstractUnit#annotate(String)} method.
 * </p>
 *
 * @param <Q>
 *          The type of the quantity measured by this unit.
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 0.3, June 09, 2014
 */
public final class AnnotatedUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> {

  /**
	 * 
	 */
  private static final long serialVersionUID = -5676462045106887728L;

  /**
   * Holds the actual unit.
   */
  private final AbstractUnit<Q> actualUnit;

  /**
   * Holds the annotation.
   */
  private final String annotation;

  /**
   * Creates an annotated unit equivalent to the specified unit.
   *
   * @param actualUnit
   *          the unit to be annotated.
   * @param annotation
   *          the annotation.
   * @return the annotated unit.
   */
  public AnnotatedUnit(AbstractUnit<Q> actualUnit, String annotation) {
    this.actualUnit = (actualUnit instanceof AnnotatedUnit) ? ((AnnotatedUnit<Q>) actualUnit).actualUnit : actualUnit;
    this.annotation = annotation;
  }

  /**
   * Returns the actual unit of this annotated unit (never an annotated unit itself).
   *
   * @return the actual unit.
   */
  public AbstractUnit<Q> getActualUnit() {
    return actualUnit;
  }

  /**
   * Returns the annotqtion of this annotated unit.
   *
   * @return the annotation.
   */
  public String getAnnotation() {
    return annotation;
  }

  @Override
  public String getSymbol() {
    return actualUnit.getSymbol();
  }

  @Override
  public Map<? extends Unit<?>, Integer> getBaseUnits() {
    return actualUnit.getBaseUnits();
  }

  @Override
  public Unit<Q> toSystemUnit() {
    return actualUnit.getSystemUnit();
  }

  @Override
  public Dimension getDimension() {
    return actualUnit.getDimension();
  }

  @Override
  public UnitConverter getSystemConverter() {
    return actualUnit.getSystemConverter();
  }

  @Override
  public int hashCode() {
    return Objects.hash(actualUnit, annotation);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof AnnotatedUnit<?>) {
      AnnotatedUnit<?> other = (AnnotatedUnit<?>) obj;
      return Objects.equals(actualUnit, other.actualUnit) && Objects.equals(annotation, other.annotation);
    }
    return false;
  }
}
