/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2020, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
package tech.units.indriya;

import static tech.units.indriya.format.UnitStyle.LABEL;
import static tech.units.indriya.format.UnitStyle.NAME;
import static tech.units.indriya.format.UnitStyle.NAME_AND_SYMBOL;
import static tech.units.indriya.format.UnitStyle.SYMBOL;
import static tech.units.indriya.format.UnitStyle.SYMBOL_AND_LABEL;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.spi.SystemOfUnits;

import tech.units.indriya.format.SimpleUnitFormat;
import tech.units.indriya.format.UnitStyle;
import tech.uom.lib.common.function.Nameable;

/**
 * <p>
 * An abstract base class for unit systems.
 * </p>
 *
 * @author <a href="mailto:werner@units.tech">Werner Keil</a>
 * @version 2.0, Feb 18, 2020
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see SystemOfUnits#getUnit()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <Q extends Quantity<Q>> Unit<Q> getUnit(Class<Q> quantityType) {
		return quantityToUnit.get(quantityType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SystemOfUnits#getUnit()
	 */
	@Override
	public Unit<?> getUnit(String string) {
		Objects.requireNonNull(string);
		return this.getUnits().stream().filter((u) -> string.equals(u.toString())).findAny().orElse(null);
	}

	/**
	 * <p>
	 * Returns a unit with the given {@linkplain String string} representation in a
	 * particular {@linkplain UnitStyle style} or {@code null} if none is found in
	 * this unit system and requested style.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> Use {@code ignoreCase} carefully, as it will find the
	 * <b>FIRST</b> unit for a particular string, e.g. the symbol of {@code SECOND}
	 * and {@code SIEMENS} would be the same without case, but the UPPERCASE letter
	 * sorted first.
	 * </p>
	 * 
	 * @param string     the string representation of a unit, not {@code null}.
	 * @param style      the style of unit representation.
	 * @param ignoreCase ignore the case or not?
	 * @return the unit with the given string representation.
	 * @since 2.0
	 */
	public Unit<?> getUnit(String string, UnitStyle style, boolean ignoreCase) {
		Objects.requireNonNull(string);
		switch (style) {
		case NAME:
			if (ignoreCase) {
				return this.getUnits().stream().filter((u) -> string.equalsIgnoreCase(u.getName())).findFirst()
						.orElse(null);
			} else {
				return this.getUnits().stream().filter((u) -> string.equals(u.getName())).findFirst().orElse(null);
			}
		case SYMBOL:
			if (ignoreCase) {
				return this.getUnits().stream().filter((u) -> string.equalsIgnoreCase(u.getSymbol())).findFirst()
						.orElse(null);
			} else {
				return this.getUnits().stream().filter((u) -> string.equals(u.getSymbol())).findFirst().orElse(null);
			}
		default:
			return getUnit(string);
		}
	}

	/**
	 * Returns a unit with the given {@linkplain String string} representation in a
	 * particular {@linkplain UnitStyle style} or {@code null} if none is found in
	 * this unit system and requested style.
	 *
	 * @param string the string representation of a unit, not {@code null}.
	 * @param style  the style of unit representation.
	 * @return the unit with the given string representation.
	 * @since 2.0
	 */
	public Unit<?> getUnit(String string, UnitStyle style) {
		return getUnit(string, style, false);
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
		 * @param unit the unit being added.
		 * @param name the name of the unit.
		 * @return <code>unit</code>.
		 * @since 1.0
		 */
		public static <U extends Unit<?>> U addUnit(Set<Unit<?>> units, U unit, String name) {
			return addUnit(units, unit, name, NAME);
		}

		/**
		 * Adds a new named unit to the collection.
		 * 
		 * @param unit the unit being added.
		 * @param name the name of the unit.
		 * @param name the symbol of the unit.
		 * @return <code>unit</code>.
		 * @since 1.0
		 */
		public static <U extends Unit<?>> U addUnit(Set<Unit<?>> units, U unit, String name, String symbol) {
			return addUnit(units, unit, name, symbol, NAME_AND_SYMBOL);
		}

		/**
		 * Adds a new named unit to the collection.
		 * 
		 * @param unit  the unit being added.
		 * @param name  the name of the unit.
		 * @param name  the symbol of the unit.
		 * @param style style of the unit.
		 * @return <code>unit</code>.
		 * @since 1.0.1
		 */
		@SuppressWarnings("unchecked")
		public static <U extends Unit<?>> U addUnit(Set<Unit<?>> units, U unit, final String name, final String symbol,
				UnitStyle style) {
			switch (style) {
			case NAME:
				if (name != null && unit instanceof AbstractUnit) {
					AbstractUnit<?> aUnit = (AbstractUnit<?>) unit;
					aUnit.setName(name);
					units.add(aUnit);
					return (U) aUnit;
				}
				break;
			case NAME_AND_SYMBOL:
			case SYMBOL:
				if (unit instanceof AbstractUnit) {
					AbstractUnit<?> aUnit = (AbstractUnit<?>) unit;
					if (name != null && NAME_AND_SYMBOL.equals(style)) {
						aUnit.setName(name);
					}
					if (name != null && (SYMBOL.equals(style) || NAME_AND_SYMBOL.equals(style))) {
						aUnit.setSymbol(symbol);
					}
					units.add(aUnit);
					return (U) aUnit;
				}
				break;
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
				break;
			default:
				if (logger.isLoggable(Level.FINEST)) {
					logger.log(Level.FINEST,
							"Unknown style " + style + "; unit " + unit + " can't be rendered with '" + symbol + "'.");
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
		 * @param units the set to add to.
		 * 
		 * @param unit  the unit being added.
		 * @param text  the text for the unit.
		 * @param style style of the unit.
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
				}
				// label in any case, returning below
				SimpleUnitFormat.getInstance().label(unit, text);
				break;
			case LABEL:
				SimpleUnitFormat.getInstance().label(unit, text);
				break;
			default:
				logger.log(Level.FINEST,
						"Unknown style " + style + "; unit " + unit + " can't be rendered with '" + text + "'.");
				break;
			}
			units.add(unit);
			return unit;
		}
	}
}
