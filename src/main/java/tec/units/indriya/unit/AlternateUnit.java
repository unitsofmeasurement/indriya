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
 * This class represents units used in expressions to distinguish between quantities of a different nature but of the same dimensions.
 * </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.3, July 20, 2017
 */
public final class AlternateUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> {

  /**
   * 
   */
  private static final long serialVersionUID = 4696690756456282705L;

  /**
   * Holds the parent unit (a system unit).
   */
  private final Unit<?> parentUnit;

  /**
   * Holds the symbol for this unit.
   */
  private final String symbol;

  /**
   * Creates an alternate unit for the specified system unit identified by the specified name and symbol.
   *
   * @param parent
   *          the system unit from which this alternate unit is derived.
   * @param symbol
   *          the symbol for this alternate unit.
   * @throws IllegalArgumentException
   *           if the specified parent unit is not an {@link AbstractUnit#isSystemUnit() system unit}
   */
  public AlternateUnit(Unit<?> parentUnit, String symbol) {
    if (!((AbstractUnit) parentUnit).isSystemUnit())
      throw new IllegalArgumentException("The parent unit: " + parentUnit + " is not an unscaled SI unit");
    this.parentUnit = (parentUnit instanceof AlternateUnit) ? ((AlternateUnit) parentUnit).getParentUnit() : parentUnit;
    this.symbol = symbol;
  }

  /**
   * Returns the parent unit of this alternate unit, always a system unit and never an alternate unit.
   *
   * @return the parent unit.
   */
  public Unit<?> getParentUnit() {
    return parentUnit;
  }

  @Override
  public String getSymbol() {
    return symbol;
  }

  @Override
  public Dimension getDimension() {
    return parentUnit.getDimension();
  }

  @Override
  public UnitConverter getSystemConverter() {
    return ((AbstractUnit) parentUnit).getSystemConverter();
  }

  @Override
  public Unit<Q> toSystemUnit() {
    return this; // Alternate units are SI units.
  }

  @Override
  public Map<? extends Unit<?>, Integer> getBaseUnits() {
    return parentUnit.getBaseUnits();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(symbol);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof AlternateUnit) {
      AlternateUnit that = (AlternateUnit) obj;
      return Objects.equals(parentUnit, that.parentUnit) && Objects.equals(symbol, that.symbol);
    }
    return false;
  }
}
