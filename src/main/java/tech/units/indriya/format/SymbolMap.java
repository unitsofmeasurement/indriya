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
package tech.units.indriya.format;

import javax.measure.Prefix;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.function.AbstractConverter;
import tech.units.indriya.function.MultiplyConverter;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * <p>
 * This class provides a set of mappings between {@link AbstractUnit units} and symbols (both ways), between {@link MetricPrefix prefixes} and symbols
 * (both ways), and from {@link AbstractConverter unit converters} to {@link MetricPrefix prefixes} (one way). No attempt is made to verify the
 * uniqueness of the mappings.
 * </p>
 *
 * <p>
 * Mappings are read from a <code>ResourceBundle</code>, the keys of which should consist of a fully-qualified class name, followed by a dot ('.'),
 * and then the name of a static field belonging to that class, followed optionally by another dot and a number. If the trailing dot and number are
 * not present, the value associated with the key is treated as a {@link SymbolMap#label(AbstractUnit, String) label}, otherwise if the trailing dot
 * and number are present, the value is treated as an {@link SymbolMap#alias(AbstractUnit,String) alias}. Aliases map from String to Unit only,
 * whereas labels map in both directions. A given unit may have any number of aliases, but may have only one label.
 * </p>
 *
 * @author <a href="mailto:eric-r@northwestern.edu">Eric Russell</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.7, February 25, 2017
 */
@SuppressWarnings("rawtypes")
public final class SymbolMap {
  private static final Logger logger = Logger.getLogger(SymbolMap.class.getName());

  private final Map<String, Unit<?>> symbolToUnit;
  private final Map<Unit<?>, String> unitToSymbol;
  private final Map<String, Object> symbolToPrefix;
  private final Map<Object, String> prefixToSymbol;
  private final Map<UnitConverter, Prefix> converterToPrefix;

  /**
   * Creates an empty mapping.
   */
  private SymbolMap() {
    symbolToUnit = new TreeMap<>();
    unitToSymbol = new HashMap<>();
    symbolToPrefix = new TreeMap<>();
    prefixToSymbol = new HashMap<>();
    converterToPrefix = new HashMap<>();
  }

  /**
   * Creates a symbol map from the specified resource bundle,
   *
   * @param rb
   *          the resource bundle.
   */
  private SymbolMap(ResourceBundle rb) {
    this();
    for (Enumeration<String> i = rb.getKeys(); i.hasMoreElements();) {
      String fqn = i.nextElement();
      String symbol = rb.getString(fqn);
      boolean isAlias = false;
      int lastDot = fqn.lastIndexOf('.');
      String className = fqn.substring(0, lastDot);
      String fieldName = fqn.substring(lastDot + 1, fqn.length());
      if (Character.isDigit(fieldName.charAt(0))) {
        isAlias = true;
        fqn = className;
        lastDot = fqn.lastIndexOf('.');
        className = fqn.substring(0, lastDot);
        fieldName = fqn.substring(lastDot + 1, fqn.length());
      }
      try {
        Class<?> c = Class.forName(className);
        Field field = c.getField(fieldName);
        Object value = field.get(null);
        if (value instanceof Unit<?>) {
          if (isAlias) {
            alias((Unit) value, symbol);
          } else {
            label((AbstractUnit<?>) value, symbol);
          }
        } else if (value instanceof Prefix) {
          label((Prefix) value, symbol);
        } else {
          throw new ClassCastException("unable to cast " + value + " to Unit or Prefix");
        }
      } catch (Exception error) {
        logger.log(Level.SEVERE, "Error", error);
      }
    }
  }

  /**
   * Creates a symbol map from the specified resource bundle,
   *
   * @param rb
   *          the resource bundle.
   */
  public static SymbolMap of(ResourceBundle rb) {
    return new SymbolMap(rb);
  }

  /**
   * Attaches a label to the specified unit. For example:<br>
   * <code> symbolMap.label(DAY.multiply(365), "year"); symbolMap.label(US.FOOT, "ft");
   * </code>
   *
   * @param unit
   *          the unit to label.
   * @param symbol
   *          the new symbol for the unit.
   */
  public void label(Unit<?> unit, String symbol) {
    symbolToUnit.put(symbol, unit);
    unitToSymbol.put(unit, symbol);
  }

  /**
   * Attaches an alias to the specified unit. Multiple aliases may be attached to the same unit. Aliases are used during parsing to recognize
   * different variants of the same unit.<code> symbolMap.alias(US.FOOT, "foot"); symbolMap.alias(US.FOOT, "feet");
   * symbolMap.alias(Units.METER, "meter"); symbolMap.alias(Units.METER, "metre"); </code>
   *
   * @param unit
   *          the unit to label.
   * @param symbol
   *          the new symbol for the unit.
   */
  public void alias(Unit<?> unit, String symbol) {
    symbolToUnit.put(symbol, unit);
  }

  /**
   * Attaches a label to the specified prefix. For example:<br>
   * <code> symbolMap.label(MetricPrefix.GIGA, "G"); symbolMap.label(MetricPrefix.MICRO, "µ");
   * </code>
   * 
   * TODO should be able to do this with a generic Prefix
   */
  public void label(Prefix prefix, String symbol) {
    symbolToPrefix.put(symbol, prefix);
    prefixToSymbol.put(prefix, symbol);
    converterToPrefix.put(MultiplyConverter.ofPrefix(prefix), prefix);
  }

  /**
   * Returns the unit for the specified symbol.
   *
   * @param symbol
   *          the symbol.
   * @return the corresponding unit or <code>null</code> if none.
   */
  public Unit<?> getUnit(String symbol) {
    return symbolToUnit.get(symbol);
  }

  /**
   * Returns the symbol (label) for the specified unit.
   *
   * @param unit
   *          the corresponding symbol.
   * @return the corresponding symbol or <code>null</code> if none.
   */
  public String getSymbol(Unit<?> unit) {
    return unitToSymbol.get(unit);
  }

  /**
   * Returns the prefix (if any) for the specified symbol.
   *
   * @param symbol
   *          the unit symbol.
   * @return the corresponding prefix or <code>null</code> if none.
   */
  public Prefix getPrefix(String symbol) {
	final List<String> list = symbolToPrefix.keySet().stream().collect(Collectors.toList());
	final Comparator<String> comparator = Comparator.comparing(String::length);
	Collections.sort(list, comparator.reversed());

	for (String key : list) {
	    if (symbol.startsWith(key)) {
		return (Prefix) symbolToPrefix.get(key);
	    }
	}
	return null;
    }

  /**
   * Returns the prefix for the specified converter.
   *
   * @param converter
   *          the unit converter.
   * @return the corresponding prefix or <code>null</code> if none.
   */
  public Prefix getPrefix(UnitConverter converter) {
    return converterToPrefix.get(converter);
  }

  /**
   * Returns the symbol for the specified prefix.
   *
   * @param prefix
   *          the prefix.
   * @return the corresponding symbol or <code>null</code> if none.
   */
  public String getSymbol(Prefix prefix) {
    return prefixToSymbol.get(prefix);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("tech.units.indriya.format.SymbolMap: [");
    sb.append("symbolToUnit: ").append(symbolToUnit).append(',');
    sb.append("unitToSymbol: ").append(unitToSymbol).append(',');
    sb.append("symbolToPrefix: ").append(symbolToPrefix).append(',');
    sb.append("prefixToSymbol: ").append(prefixToSymbol).append(',');
    sb.append("converterToPrefix: ").append(converterToPrefix).append(',');
    sb.append("converterToPrefix: ").append(converterToPrefix);
    sb.append(" ]");
    return sb.toString();
  }

}
