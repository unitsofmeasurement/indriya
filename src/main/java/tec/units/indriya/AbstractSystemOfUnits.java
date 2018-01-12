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
package tec.units.indriya;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.spi.SystemOfUnits;

import tec.units.indriya.format.SimpleUnitFormat;
import tec.units.indriya.format.UnitStyle;
import tec.uom.lib.common.function.Nameable;

import static tec.units.indriya.format.UnitStyle.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * <p>
 * An abstract base class for unit systems.
 * </p>
 *
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.0.2, November 3, 2016
 * @since 1.0
 */
public abstract class AbstractSystemOfUnits implements SystemOfUnits, Nameable {
  /**
   * Holds the units.
   */
  protected final Set<Unit<?>> units = new HashSet<>();

  /**
   * Holds the mapping quantity to unit.
   */
  @SuppressWarnings("rawtypes")
  protected final Map<Class<? extends Quantity>, Unit> quantityToUnit = new HashMap<>();

  protected static final Logger logger = Logger.getLogger(AbstractSystemOfUnits.class.getName());

  /**
   * Adds a new named unit to the collection.
   * 
   * @param unit
   *          the unit being added.
   * @param name
   *          the name of the unit.
   * @return <code>unit</code>.
   */
  /*
   * @SuppressWarnings("unchecked") private <U extends Unit<?>> U addUnit(U
   * unit, String name) { if (name != null && unit instanceof AbstractUnit) {
   * AbstractUnit<?> aUnit = (AbstractUnit<?>) unit; aUnit.setName(name);
   * units.add(aUnit); return (U) aUnit; } units.add(unit); return unit; }
   */
  /**
   * The natural logarithm.
   **/
  protected static final double E = 2.71828182845904523536028747135266;

  /*
   * (non-Javadoc)
   * 
   * @see SystemOfUnits#getName()
   */
  public abstract String getName();

  // ///////////////////
  // Collection View //
  // ///////////////////
  @Override
  public Set<Unit<?>> getUnits() {
    return Collections.unmodifiableSet(units);
  }

  @Override
    public Set<? extends Unit<?>> getUnits(Dimension dimension) {
	return this.getUnits().stream().filter(unit -> dimension.equals(unit.getDimension()))
		.collect(Collectors.toSet());
    }

  @SuppressWarnings("unchecked")
  @Override
  public <Q extends Quantity<Q>> Unit<Q> getUnit(Class<Q> quantityType) {
    return quantityToUnit.get(quantityType);
  }

  protected static class Helper {
    static Set<Unit<?>> getUnitsOfDimension(final Set<Unit<?>> units, Dimension dimension) {
	    if (dimension != null) {
		return units.stream().filter(u -> dimension.equals(u.getDimension())).collect(Collectors.toSet());

	    }
	    return null;
	}

    /**
     * Adds a new named unit to the collection.
     * 
     * @param unit
     *          the unit being added.
     * @param name
     *          the name of the unit.
     * @return <code>unit</code>.
     * @since 1.0
     */
    public static <U extends Unit<?>> U addUnit(Set<Unit<?>> units, U unit, String name) {
      return addUnit(units, unit, name, NAME);
    }

    /**
     * Adds a new named unit to the collection.
     * 
     * @param unit
     *          the unit being added.
     * @param name
     *          the name of the unit.
     * @param name
     *          the symbol of the unit.
     * @return <code>unit</code>.
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public static <U extends Unit<?>> U addUnit(Set<Unit<?>> units, U unit, String name, String symbol) {
      if (name != null && symbol != null && unit instanceof AbstractUnit) {
        AbstractUnit<?> aUnit = (AbstractUnit<?>) unit;
        aUnit.setName(name);
        aUnit.setSymbol(symbol);
        units.add(aUnit);
        return (U) aUnit;
      }
      if (name != null && unit instanceof AbstractUnit) {
        AbstractUnit<?> aUnit = (AbstractUnit<?>) unit;
        aUnit.setName(name);
        units.add(aUnit);
        return (U) aUnit;
      }
      units.add(unit);
      return unit;
    }

    /**
     * Adds a new named unit to the collection.
     * 
     * @param unit
     *          the unit being added.
     * @param name
     *          the name of the unit.
     * @param name
     *          the symbol of the unit.
     * @param style
     *          style of the unit.
     * @return <code>unit</code>.
     * @since 1.0.1
     */
    @SuppressWarnings("unchecked")
    public static <U extends Unit<?>> U addUnit(Set<Unit<?>> units, U unit, final String name, final String symbol, UnitStyle style) {
      switch (style) {
        case NAME:
        case SYMBOL:
        case SYMBOL_AND_LABEL:
          if (name != null && symbol != null && unit instanceof AbstractUnit) {
            AbstractUnit<?> aUnit = (AbstractUnit<?>) unit;
            aUnit.setName(name);
            if (SYMBOL.equals(style) || SYMBOL_AND_LABEL.equals(style)) {
              aUnit.setSymbol(symbol);
            }
            if (LABEL.equals(style) || SYMBOL_AND_LABEL.equals(style)) {
              SimpleUnitFormat.getInstance().label(unit, symbol);
            }
            units.add(aUnit);
            return (U) aUnit;
          }
          if (name != null && unit instanceof AbstractUnit) {
            AbstractUnit<?> aUnit = (AbstractUnit<?>) unit;
            aUnit.setName(name);
            units.add(aUnit);
            return (U) aUnit;
          }
          break;
        default:
          if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Unknown style " + style + "; unit " + unit + " can't be rendered with '" + symbol + "'.");
          }
          break;
      }
      if (LABEL.equals(style) || SYMBOL_AND_LABEL.equals(style)) {
        SimpleUnitFormat.getInstance().label(unit, symbol);
      }
      units.add(unit);
      return unit;
    }

    /**
     * Adds a new labeled unit to the set.
     * 
     * @param units
     *          the set to add to.
     * 
     * @param unit
     *          the unit being added.
     * @param text
     *          the text for the unit.
     * @param style
     *          style of the unit.
     * @return <code>unit</code>.
     * @since 1.0.1
     */
    @SuppressWarnings("unchecked")
    public static <U extends Unit<?>> U addUnit(Set<Unit<?>> units, U unit, String text, UnitStyle style) {
      switch (style) {
        case NAME:
          if (text != null && unit instanceof AbstractUnit) {
            AbstractUnit<?> aUnit = (AbstractUnit<?>) unit;
            aUnit.setName(text);
            units.add(aUnit);
            return (U) aUnit;
          }
          break;
        case SYMBOL:
          if (text != null && unit instanceof AbstractUnit) {
            AbstractUnit<?> aUnit = (AbstractUnit<?>) unit;
            aUnit.setSymbol(text);
            units.add(aUnit);
            return (U) aUnit;
          }
          break;
        case SYMBOL_AND_LABEL:
          if (text != null && unit instanceof AbstractUnit) {
            AbstractUnit<?> aUnit = (AbstractUnit<?>) unit;
            aUnit.setSymbol(text);
            units.add(aUnit);
            SimpleUnitFormat.getInstance().label(aUnit, text);
            return (U) aUnit;
          } else { // label in any case, returning below
            SimpleUnitFormat.getInstance().label(unit, text);
          }
          break;
        case LABEL:
          SimpleUnitFormat.getInstance().label(unit, text);
          break;
        default:
          logger.log(Level.FINEST, "Unknown style " + style + "; unit " + unit + " can't be rendered with '" + text + "'.");
          break;
      }
      units.add(unit);
      return unit;
    }
  }
}
