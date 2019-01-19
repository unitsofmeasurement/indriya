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
import java.math.BigDecimal;
import java.text.ParsePosition;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.MeasurementParseException;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.AbstractUnit;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.NumberQuantity;
import tech.units.indriya.quantity.Quantities;

/**
 * A simple implementation of QuantityFormat
 * @version 0.9.2
 * @since 2.0
 */
@SuppressWarnings("rawtypes")
public class SimpleQuantityFormat extends AbstractQuantityFormat {
	/**
	 * Holds the default format instance.
	 */
	private static final SimpleQuantityFormat DEFAULT = new SimpleQuantityFormat();

	/**
	 * The pattern string of this formatter. This is always a non-localized pattern.
	 * May not be null. See class documentation for details.
	 * 
	 * @serial
	 */
	private String pattern;

	/**
	*
	*/
	private static final long serialVersionUID = 2758248665095734058L;

	/**
	 * Constructs a <code>SimpleQuantityFormat</code> using the given pattern.
	 * <p>
	 * 
	 * @param pattern
	 *            the pattern describing the quantity and unit format
	 * @exception NullPointerException
	 *                if the given pattern is null
	 * @exception IllegalArgumentException
	 *                if the given pattern is invalid
	 */
	public SimpleQuantityFormat(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * Constructs a <code>SimpleQuantityFormat</code> using the default pattern. For
	 * full coverage, use the factory methods.
	 */
	public SimpleQuantityFormat() {
		this("");
	}

	@Override
	public Appendable format(Quantity quantity, Appendable dest) throws IOException {
		Unit unit = quantity.getUnit();

		dest.append(quantity.getValue().toString());
		if (quantity.getUnit().equals(AbstractUnit.ONE))
			return dest;
		dest.append(' ');
		return SimpleUnitFormat.getInstance().format(unit, dest);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ComparableQuantity<?> parse(CharSequence csq, ParsePosition cursor) throws MeasurementParseException {
		int startDecimal = cursor.getIndex();
		while ((startDecimal < csq.length()) && Character.isWhitespace(csq.charAt(startDecimal))) {
			startDecimal++;
		}
		int endDecimal = startDecimal + 1;
		while ((endDecimal < csq.length()) && !Character.isWhitespace(csq.charAt(endDecimal))) {
			endDecimal++;
		}
		BigDecimal decimal = new BigDecimal(csq.subSequence(startDecimal, endDecimal).toString());
		cursor.setIndex(endDecimal + 1);
		Unit unit = SimpleUnitFormat.getInstance().parse(csq, cursor);
		return Quantities.getQuantity(decimal, unit);
	}

	@SuppressWarnings("unchecked")
	@Override
	AbstractQuantity<?> parse(CharSequence csq, int index) throws MeasurementParseException {
		int startDecimal = index; // cursor.getIndex();
		while ((startDecimal < csq.length()) && Character.isWhitespace(csq.charAt(startDecimal))) {
			startDecimal++;
		}
		int endDecimal = startDecimal + 1;
		while ((endDecimal < csq.length()) && !Character.isWhitespace(csq.charAt(endDecimal))) {
			endDecimal++;
		}
		Double decimal = Double.valueOf(csq.subSequence(startDecimal, endDecimal).toString());
		Unit unit = SimpleUnitFormat.getInstance().parse(csq, index);
		return NumberQuantity.of(decimal, unit);
	}

	@Override
	public ComparableQuantity<?> parse(CharSequence csq) throws MeasurementParseException {
		return parse(csq, new ParsePosition(0));
	}

	/**
	 * Returns the quantity format for the default locale. The default format
	 * assumes the quantity is composed of a decimal number and a {@link Unit}
	 * separated by whitespace(s).
	 *
	 * @return a default <code>SimpleQuantityFormat</code> instance.
	 */
	public static SimpleQuantityFormat getInstance() {
		return DEFAULT;
	}
	
	/**
	 * Returns a <code>SimpleQuantityFormat</code> using the given pattern.
	 * <p>
	 * 
	 * @param pattern
	 *            the pattern describing the quantity and unit format
	 *
	 * @return <code>MeasureFormat.getInstance(NumberFormat.getInstance(), UnitFormat.getInstance())</code>
	 */
	public static SimpleQuantityFormat getInstance(String pattern) {
		return new SimpleQuantityFormat(pattern);
	}

	public String getPattern() {
		return pattern;
	}
}