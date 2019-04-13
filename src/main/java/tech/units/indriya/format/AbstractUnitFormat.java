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

import java.io.IOException;
import java.text.ParsePosition;

import javax.measure.MeasurementException;
import javax.measure.Unit;
import javax.measure.format.UnitFormat;

import tech.units.indriya.AbstractUnit;

/**
 * <p>
 * This class provides the interface for formatting and parsing {@link Unit units}.
 * </p>
 *
 * <p>
 * For all metric units, the 20 <b>SI prefixes</b> used to form decimal multiples and sub-multiples of SI units are recognized.<br>
 * As well as the 8 <b>Binary prefixes</b>.<br>
 * For example:<code>
 *        AbstractUnit.parse("m°C").equals(MetricPrefix.MILLI(Units.CELSIUS))
 *        AbstractUnit.parse("kW").equals(MetricPrefix.KILO(Units.WATT))</code>
 * </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.2, $Date: 2019-02-23 $
 * @since 1.0
 * 
 */
public abstract class AbstractUnitFormat implements UnitFormat {

  /**
   * Returns the {@link SymbolMap} for this unit format.
   *
   * @return the symbol map used by this format.
   */
  protected abstract SymbolMap getSymbols();

  /**
   * Formats the specified unit.
   *
   * @param unit
   *          the unit to format.
   * @param appendable
   *          the appendable destination.
   * @return The appendable destination passed in as {@code appendable}, with formatted text appended.
   * @throws IOException
   *           if an error occurs.
   */
  public abstract Appendable format(Unit<?> unit, Appendable appendable) throws IOException;

  /**
   * Formats an object to produce a string. This is equivalent to <blockquote> {@link #format(Unit, StringBuilder) format}<code>(unit,
   *         new StringBuilder()).toString();</code> </blockquote>
   *
   * @param obj
   *          The object to format
   * @return Formatted string.
   * @exception IllegalArgumentException
   *              if the Format cannot format the given object
   */
  public final String format(Unit<?> unit) {
    if (unit instanceof AbstractUnit) return format((AbstractUnit<?>) unit, new StringBuilder()).toString();

    try {
      return (this.format(unit, new StringBuilder())).toString();
    } catch (IOException ex) {
      throw new MeasurementException(ex); // Should never happen.
    }
  }

  @Override
  public void label(Unit<?> unit, String label) {
    // do nothing, if subclasses want to use it, override there
  }

  /**
   * Parses a portion of the specified <code>CharSequence</code> from the specified position to produce a unit. If there is no unit to parse
   * {@link AbstractUnit#ONE} is returned.
   *
   * @param csq
   *          the <code>CharSequence</code> to parse.
   * @param cursor
   *          the cursor holding the current parsing index.
   * @return the unit parsed from the specified character sub-sequence.
   * @throws IllegalArgumentException
   *           if any problem occurs while parsing the specified character sequence (e.g. illegal syntax).
   */
  public abstract Unit<?> parse(CharSequence csq, ParsePosition cursor) throws IllegalArgumentException;

  /**
   * Parses a portion of the specified <code>CharSequence</code> from the specified position to produce a unit. If there is no unit to parse
   * {@link AbstractUnit#ONE} is returned.
   *
   * @param csq
   *          the <code>CharSequence</code> to parse.
   * @param index
   *          the current parsing index.
   * @return the unit parsed from the specified character sub-sequence.
   * @throws IllegalArgumentException
   *           if any problem occurs while parsing the specified character sequence (e.g. illegal syntax).
   */
  protected abstract Unit<?> parse(CharSequence csq, int index) throws IllegalArgumentException;

  /**
   * Convenience method equivalent to {@link #format(AbstractUnit, Appendable)} except it does not raise an IOException.
   *
   * @param unit
   *          the unit to format.
   * @param dest
   *          the appendable destination.
   * @return the specified <code>StringBuilder</code>.
   */
  private final StringBuilder format(AbstractUnit<?> unit, StringBuilder dest) {
    try {
      return (StringBuilder) this.format(unit, (Appendable) dest);
    } catch (IOException ex) {
      throw new MeasurementException(ex); // Can never happen.
    }
  }

  /**
   * serialVersionUID
   */
  // private static final long serialVersionUID = -2046025267890654321L;
}
