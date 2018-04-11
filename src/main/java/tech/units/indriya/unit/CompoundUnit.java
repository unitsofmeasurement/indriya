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
package tech.units.indriya.unit;

import javax.measure.UnitConverter;

import tech.units.indriya.AbstractUnit;

import java.util.Map;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * <p>
 * This class represents multi-radix units (such as "hour:min:sec" or "ft, in"). Instances of this class are created using the {@link Unit#compound
 * Unit.compound} method.
 * </p>
 * 
 * <p>
 * Examples of compound units:<code> Unit<Time> HOUR_MINUTE_SECOND = HOUR.compound(MINUTE).compound(SECOND); <br>Unit<Length> FOOT_INCH =
 * FOOT.compound(INCH); </code>
 * </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.8, April 11, 2018
 * @since 1.1
 */
public final class CompoundUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> {

  /**
   * 
   */
  private static final long serialVersionUID = -6588505921476784171L;

  /**
   * Holds the upper unit.
   */
  private final Unit<Q> upper;

  /**
   * Holds the lower unit.
   */
  private final Unit<Q> lower;

  /**
   * Creates a compound unit from the specified units.
   *
   * @param up
   *          the upper unit.
   * @param low
   *          the lower unit(s)
   * @throws IllegalArgumentException
   *           if both units do not the same system unit.
   */
  public CompoundUnit(Unit<Q> up, Unit<Q> low) {
    if (!up.getSystemUnit().equals(low.getSystemUnit()))
      throw new IllegalArgumentException("Both units do not have the same system unit");
    upper = up;
    lower = low;
  }

  /**
   * Returns the lower unit of this compound unit.
   *
   * @return the lower unit.
   */
  public Unit<Q> getLower() {
    return lower;
  }

  /**
   * Returns the upper unit of this compound unit.
   *
   * @return the upper unit.
   */
  public Unit<Q> getUpper() {
    return upper;
  }

  /**
   * Indicates if this compound unit is considered equals to the specified object (both are compound units with same composing units in the same
   * order).
   *
   * @param obj
   *          the object to compare for equality.
   * @return <code>true</code> if <code>this</code> and <code>obj</code> are considered equal; <code>false</code>otherwise.
   */
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof CompoundUnit) {
      CompoundUnit<?> thatUnit = (CompoundUnit<?>) obj;
      return this.upper.equals(thatUnit.upper) && this.lower.equals(thatUnit.lower);
    }
    if (obj instanceof AbstractUnit) {
      return AbstractUnit.Equalizer.areEqual(this, (AbstractUnit<?>) obj);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return upper.hashCode() ^ lower.hashCode();
  }

  @Override
  public UnitConverter getSystemConverter() {
    return ((AbstractUnit<?>) lower).getSystemConverter();
  }

  @Override
  protected Unit<Q> toSystemUnit() {
    return lower.getSystemUnit();
  }

  @Override
  public Map<? extends Unit<?>, Integer> getBaseUnits() {
    return lower.getBaseUnits();
  }

  @Override
  public Dimension getDimension() {
    return lower.getDimension();
  }
}
