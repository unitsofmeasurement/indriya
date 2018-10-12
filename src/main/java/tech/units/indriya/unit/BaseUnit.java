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

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import tech.units.indriya.AbstractConverter;
import tech.units.indriya.AbstractUnit;
import tech.units.indriya.quantity.QuantityDimension;

import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * This class represents the building blocks on top of which all others physical units are created. Base units are always unscaled SI units.
 * </p>
 * 
 * <p>
 * When using the {@link tech.units.indriya.spi.StandardModel standard model}, all seven <b>SI</b> base units are dimensionally independent.
 * </p>
 *
 * @see <a href="http://en.wikipedia.org/wiki/SI_base_unit"> Wikipedia: SI base unit</a>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.3, October 12, 2018
 * @since 1.0
 */
public final class BaseUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> {

  /**
     * 
     */
  private static final long serialVersionUID = 1721629233768215930L;

  /**
   * Holds the base unit dimension.
   */
  private final Dimension dimension;

  private Q quantityType;

  protected Q getQuantityType() {
    return quantityType;
  }

  /**
   * Creates a base unit having the specified symbol and dimension.
   *
   * @param symbol
   *          the symbol of this base unit.
   */
  public BaseUnit(String symbol, Dimension dimension, Q quant) {
    super(symbol);
    this.dimension = dimension;
    quantityType = quant;
  }

  /**
   * Creates a base unit having the specified symbol and dimension.
   *
   * @param symbol
   *          the symbol of this base unit.
   */
  public BaseUnit(String symbol, Dimension dimension) {
    super(symbol);
    this.dimension = dimension;
  }

  /**
   * Creates a base unit having the specified symbol.
   *
   * @param symbol
   *          the symbol of this base unit.
   */
  public BaseUnit(String symbol) {
    super(symbol);
    this.dimension = QuantityDimension.NONE;
  }

  /**
   * Creates a base unit having the specified symbol and name.
   *
   * @param symbol
   *          the symbol of this base unit.
   * @param name
   *          the name of this base unit.
   * @throws IllegalArgumentException
   *           if the specified symbol is associated to a different unit.
   */
  public BaseUnit(String symbol, String name) {
    this(symbol);
    this.name = name;
  }

  @Override
  public Unit<Q> toSystemUnit() {
    return this;
  }

  @Override
  public UnitConverter getSystemConverter() throws UnsupportedOperationException {
    return AbstractConverter.IDENTITY;
  }

  @Override
  public Dimension getDimension() {
    return dimension;
  }

  @Override
  public final boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj instanceof BaseUnit) {
      BaseUnit<?> thatUnit = (BaseUnit<?>) obj;
      return Objects.equals(this.getSymbol(), thatUnit.getSymbol()) && this.dimension.equals(thatUnit.dimension);
    }
    if (obj instanceof AbstractUnit) {
      return AbstractUnit.Equalizer.areEqual(this, (AbstractUnit) obj);
    } else {
      return false;
    }
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(getSymbol());
  }

  @Override
  public Map<? extends AbstractUnit<Q>, Integer> getBaseUnits() {
    // TODO Shall we return null, empty list or what (e.g. Optional in SE
    // 8)?
    return null;
  }
}
