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

import static tech.units.indriya.format.CommonFormatter.parseMixed;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;

import javax.measure.MeasurementException;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.MeasurementParseException;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.AbstractUnit;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.CompositeQuantity;
import tech.units.indriya.quantity.NumberQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.MixedUnit;

/**
 * A simple implementation of QuantityFormat
 * 
 * <p>
 * The following pattern letters are defined:
 * <blockquote>
 * <table class="striped">
 * <caption style="display:none">Chart shows pattern letters, date/time component, presentation, and examples.</caption>
 * <thead>
 *     <tr>
 *         <th style="text-align:left">Letter
 *         <th style="text-align:left">Quantity Component
 *         <th style="text-align:left">Presentation
 *         <th style="text-align:left">Examples
 * </thead>
 * <tbody>
 *     <tr>
 *         <td><code>n</code>
 *         <td>Numeric value
 *         <td><a href="#number">Number</a>
 *         <td><code>27</code>
 *     <tr>
 *         <td><code>u</code>
 *         <td>Unit
 *         <td><a href="#text">Text</a>
 *         <td><code>m</code>
 *    <tr>
 *         <td><code>~</code>
 *         <td>Mix
 *         <td><a href="#text">Text</a>
 *         <td><code>m</code>; <code>cm</code>
 * </tbody>
 * </table>
 * </blockquote>
 * Pattern letters are usually repeated, as their number determines the
 * exact presentation:
 * <ul>
 * <li><strong><a id="text">Text:</a></strong>
 *     For formatting, if the number of pattern letters is 4 or more,
 *     the full form is used; otherwise a short or abbreviated form
 *     is used if available.
 *     For parsing, both forms are accepted, independent of the number
 *     of pattern letters.<br><br></li>
 * <li><strong><a id="number">Number:</a></strong>
 *     For formatting, the number of pattern letters is the minimum
 *     number of digits, and shorter numbers are zero-padded to this amount.
 *     For parsing, the number of pattern letters is ignored unless
 *     it's needed to separate two adjacent fields.<br><br></li>
 * </ul>
 * </p>
 * @version 1.3, $Date: 2019-03-31 $
 * @since 2.0
 */
@SuppressWarnings("rawtypes")
public class SimpleQuantityFormat extends AbstractQuantityFormat {
	/**
	 * Holds the default format instance.
	 */
	private static final SimpleQuantityFormat DEFAULT = new SimpleQuantityFormat();

	private static final String NUM_PART = "n";
	private static final String UNIT_PART = "u";
	private static final String MIXER = "~";
	
	/**
	 * The pattern string of this formatter. This is always a non-localized pattern.
	 * May not be null. See class documentation for details.
	 * 
	 * @serial
	 */
	private final String pattern;
	
	private String delimiter;
	
	private String mixDelimiter;

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
		if (pattern != null && !pattern.isEmpty()) {
		   if (pattern.contains(MIXER)) {
		       final String singlePattern = pattern.substring(0, pattern.indexOf(MIXER));
		       mixDelimiter = pattern.substring(pattern.indexOf(MIXER) + 1);
		       delimiter = singlePattern.substring(pattern.indexOf(NUM_PART)+1, pattern.indexOf(UNIT_PART));
		   } else {
		       delimiter = pattern.substring(pattern.indexOf(NUM_PART)+1, pattern.indexOf(UNIT_PART));
		   }
		}
	}

	/**
	 * Constructs a <code>SimpleQuantityFormat</code> using the default pattern. For
	 * full coverage, use the factory methods.
	 */
	protected SimpleQuantityFormat() {
		this("n u");
	}

	@Override
	public Appendable format(Quantity<?> quantity, Appendable dest) throws IOException {
		final Unit unit = quantity.getUnit();
        /*
		if (unit instanceof MixedUnit) {
            if (quantity instanceof MixedQuantity) {
                final MixedQuantity<?> compQuant = (MixedQuantity<?>) quantity;
                final MixedUnit<?> compUnit = (MixedUnit<?>) unit;
                final Number[] values = compQuant.getValues();
                if (values.length == compUnit.getUnits().size()) {
                    final StringBuffer sb = new StringBuffer(); // we use StringBuffer here because of java.text.Format compatibility
                    for (int i = 0; i < values.length; i++) {
                       sb.append(SimpleQuantityFormat.getInstance().format(
                               Quantities.getQuantity(values[i], compUnit.getUnits().get(i), compQuant.getScale())));
                       if (i < values.length-1) {
                           sb.append(delimiter);
                       }
                    }
                    return sb;
                } else {
                    throw new IllegalArgumentException(String.format("%s values don't match %s in mixed unit", values.length, compUnit.getUnits().size()));
                }
            } else {
                throw new MeasurementException("The quantity is not a mixed quantity");
            }
        } else { */
    		dest.append(quantity.getValue().toString());
    		if (quantity.getUnit().equals(AbstractUnit.ONE))
    			return dest;
    		dest.append(delimiter);
    		return SimpleUnitFormat.getInstance().format(unit, dest);
        //}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ComparableQuantity<?> parse(CharSequence csq, ParsePosition cursor) throws MeasurementParseException {
        if (mixDelimiter != null && !mixDelimiter.equals(delimiter)) {
            return parseMixed(csq.toString(), NumberFormat.getInstance(), SimpleUnitFormat.getInstance(), delimiter, mixDelimiter, cursor.getIndex());
        } else if (mixDelimiter != null && mixDelimiter.equals(delimiter)) {
            return parseMixed(csq.toString(), NumberFormat.getInstance(), SimpleUnitFormat.getInstance(), delimiter, cursor.getIndex());
        }
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
	 * @return <code>SimpleQuantityFormat.getInstance(a pattern)</code>
	 */
	public static SimpleQuantityFormat getInstance(String pattern) {
		return new SimpleQuantityFormat(pattern);
	}

	@Override
	public String toString() {
	    return getClass().getSimpleName();
	}
	  
	public String getPattern() {
		return pattern;
	}
	
    @Override
    protected StringBuffer formatComposite(CompositeQuantity<?> comp, StringBuffer dest) {
        final StringBuffer sb = new StringBuffer();
        int i = 0;
        for (Quantity<?> q : comp.getQuantities()) {
            sb.append(format(q));
            if (i < comp.getQuantities().size() - 1 ) {
                sb.append((mixDelimiter != null ? mixDelimiter : DEFAULT_DELIMITER)); // we need null for parsing but not
            }
            i++;
        }
        return sb;
    }
}