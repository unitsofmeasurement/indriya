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

import javax.measure.UnitConverter;

import tec.units.indriya.AbstractUnit;

import java.util.Map;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * <p>
 * This class represents the multi-radix units (such as "hour:min:sec"). Instances of this class are created using the {@link Unit#compound
 * Unit.compound} method.
 * </p>
 * 
 * <p>
 * Examples of compound units:<code> Unit<Duration> HOUR_MINUTE_SECOND = HOUR.compound(MINUTE).compound(SECOND); Unit<Angle> DEGREE_MINUTE_ANGLE =
 * DEGREE_ANGLE.compound(MINUTE_ANGLE); </code>
 * </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.4.1, July 20, 2017
 */
public final class CompoundUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> {

  /**
   * 
   */
  private static final long serialVersionUID = -6588505921476784171L;

  /**
   * Holds the higher unit.
   */
  private final Unit<Q> higher;

  /**
   * Holds the lower unit.
   */
  private final Unit<Q> lower;

  /**
   * Creates a compound unit from the specified units.
   *
   * @param high
   *          the high unit.
   * @param low
   *          the lower unit(s)
   * @throws IllegalArgumentException
   *           if both units do not the same system unit.
   */
  CompoundUnit(Unit<Q> high, Unit<Q> low) {
    if (!high.getSystemUnit().equals(low.getSystemUnit()))
      throw new IllegalArgumentException("Both units do not have the same system unit");
    higher = high;
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
   * Returns the higher unit of this compound unit.
   *
   * @return the higher unit.
   */
  public Unit<Q> getHigher() {
    return higher;
  }

  /**
   * Indicates if this compound unit is considered equals to the specified object (both are compound units with same composing units in the same
   * order).
   *
   * @param that
   *          the object to compare for equality.
   * @return <code>true</code> if <code>this</code> and <code>that</code> are considered equals; <code>false</code>otherwise.
   */
  public boolean equals(Object that) {
    if (this == that)
      return true;
    if (!(that instanceof CompoundUnit))
      return false;
    CompoundUnit<?> thatUnit = (CompoundUnit<?>) that;
    return this.higher.equals(thatUnit.higher) && this.lower.equals(thatUnit.lower);
  }

  @Override
  public int hashCode() {
    return higher.hashCode() ^ lower.hashCode();
  }

  @Override
  public UnitConverter getSystemConverter() {
    return ((AbstractUnit) lower).getSystemConverter();
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