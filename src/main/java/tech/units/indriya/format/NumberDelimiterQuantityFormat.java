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

import static tech.units.indriya.format.FormatBehavior.LOCALE_NEUTRAL;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.MeasurementParseException;
import javax.measure.format.UnitFormat;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

/**
 * An implementation of {@link javax.measure.format.QuantityFormat
 * QuantityFormat} combining {@linkplain NumberFormat} and {@link UnitFormat} separated by a delimiter.
 * 
 * @version 1.3, $Date: 2018-11-11 $
 * @since 2.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class NumberDelimiterQuantityFormat extends AbstractQuantityFormat {
	/**
	 * The default delimiter.
	 */
	private static final String DEFAULT_DELIMITER = " ";

	/**
	 * Holds the default format instance.
	 */
	private static final NumberDelimiterQuantityFormat DEFAULT = new NumberDelimiterQuantityFormat(NumberFormat.getInstance(),
			EBNFUnitFormat.getInstance());

	/**
	 * Holds the localized format instance.
	 */
	private static final NumberDelimiterQuantityFormat LOCAL = new NumberDelimiterQuantityFormat(NumberFormat.getInstance(),
			LocalUnitFormat.getInstance());

	/**
	 * 
	 */
	private static final long serialVersionUID = 3546952599885869402L;

	private final NumberFormat numberFormat;

	private final UnitFormat unitFormat;

	NumberDelimiterQuantityFormat(NumberFormat numberFormat, UnitFormat unitFormat) {
		this.numberFormat = numberFormat;
		this.unitFormat = unitFormat;
	}

	static int getFractionDigitsCount(double d) {
		if (d >= 1) { // we only need the fraction digits
			d = d - (long) d;
		}
		if (d == 0) { // nothing to count
			return 0;
		}
		d *= 10; // shifts 1 digit to left
		int count = 1;
		while (d - (long) d != 0) { // keeps shifting until there are no more
			// fractions
			d *= 10;
			count++;
		}
		return count;
	}

	@Override
	public Appendable format(Quantity<?> quantity, Appendable dest) throws IOException {
		int fract = 0;
		if (quantity != null && quantity.getValue() != null) {
			fract = getFractionDigitsCount(quantity.getValue().doubleValue());
		}
		if (fract > 1) {
			numberFormat.setMaximumFractionDigits(fract + 1);
		}
		dest.append(numberFormat.format(quantity.getValue()));
		if (quantity.getUnit().equals(AbstractUnit.ONE))
			return dest;
		dest.append(DEFAULT_DELIMITER);
		return unitFormat.format(quantity.getUnit(), dest);
	}

	@Override
	public ComparableQuantity<?> parse(CharSequence csq, ParsePosition cursor)
			throws IllegalArgumentException, MeasurementParseException {
		final String str = csq.toString();
		final Number number = numberFormat.parse(str, cursor);
		if (number == null)
			throw new IllegalArgumentException("Number cannot be parsed");
		final String[] parts = str.split(DEFAULT_DELIMITER);
		if (parts.length < 2) {
			throw new IllegalArgumentException("No Unit found");
		}
		final Unit unit = unitFormat.parse(parts[1]);
		return Quantities.getQuantity(number, unit);
	}

	@Override
	ComparableQuantity<?> parse(CharSequence csq, int index)
			throws IllegalArgumentException, MeasurementParseException {
		return parse(csq, new ParsePosition(index));
	}

	@Override
	public ComparableQuantity<?> parse(CharSequence csq) throws IllegalArgumentException, MeasurementParseException {
		return parse(csq, 0);
	}

	/**
	 * Returns the culture invariant format based upon {@link BigDecimal} canonical
	 * format and the {@link UnitFormat#getStandardInstance() standard} unit format.
	 * This format <b>is not</b> locale-sensitive and can be used for unambiguous
	 * electronic communication of quantities together with their units without loss
	 * of information. For example: <code>"1.23456789 kg.m/s2"</code> returns
	 * <code>Quantities.getQuantity(new BigDecimal("1.23456789"), AbstractUnit.parse("kg.m/s2")));</code>
	 *
	 * @param style
	 *            the format style to apply.
	 * @return the desired format.
	 */
	public static NumberDelimiterQuantityFormat getInstance(FormatBehavior style) {
		switch (style) {
		case LOCALE_NEUTRAL:
			return DEFAULT;
		case LOCALE_SENSITIVE:
			return LOCAL;
		default:
			return DEFAULT;
		}
	}

	/**
	 * Returns the default format.
	 * 
	 * @return the desired format.
	 */
	public static NumberDelimiterQuantityFormat getInstance() {
		return getInstance(LOCALE_NEUTRAL);
	}

	/**
	 * Returns the quantity format using the specified number format and unit format
	 * (the number and unit are separated by one space).
	 *
	 * @param numberFormat
	 *            the number format.
	 * @param unitFormat
	 *            the unit format.
	 * @return the corresponding format.
	 */
	public static NumberDelimiterQuantityFormat getInstance(NumberFormat numberFormat, UnitFormat unitFormat) {
		return new NumberDelimiterQuantityFormat(numberFormat, unitFormat);
	}
}