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
package tech.units.indriya.format;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.ParserException;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.internal.format.TokenException;
import tech.units.indriya.internal.format.TokenMgrError;
import tech.units.indriya.internal.format.UnitFormatParser;
import tech.units.indriya.unit.AnnotatedUnit;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <p>
 * This class represents the local neutral format.
 * </p>
 * 
 * <h3>Here is the grammar for Units in Extended Backus-Naur Form (EBNF)</h3>
 * <p>
 * Note that the grammar has been left-factored to be suitable for use by a
 * top-down parser generator such as
 * <a href="https://javacc.dev.java.net/">JavaCC</a>
 * </p>
 * <table width="90%" align="center">
 * <tr>
 * <th colspan="3" align="left">Lexical Entities:</th>
 * </tr>
 * <tr valign="top">
 * <td>&lt;sign&gt;</td>
 * <td>:=</td>
 * <td>"+" | "-"</td>
 * </tr>
 * <tr valign="top">
 * <td>&lt;digit&gt;</td>
 * <td>:=</td>
 * <td>"0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"</td>
 * </tr>
 * <tr valign="top">
 * <td>&lt;superscript_digit&gt;</td>
 * <td>:=</td>
 * <td>"⁰" | "¹" | "²" | "³" | "⁴" | "⁵" | "⁶" | "⁷" | "⁸" | "⁹"</td>
 * </tr>
 * <tr valign="top">
 * <td>&lt;integer&gt;</td>
 * <td>:=</td>
 * <td>(&lt;digit&gt;)+</td>
 * </tr>
 * <tr valign="top">
 * <td>&lt;number&gt;</td>
 * <td>:=</td>
 * <td>(&lt;sign&gt;)? (&lt;digit&gt;)* (".")? (&lt;digit&gt;)+ (("e" | "E")
 * (&lt;sign&gt;)? (&lt;digit&gt;)+)?</td>
 * </tr>
 * <tr valign="top">
 * <td>&lt;exponent&gt;</td>
 * <td>:=</td>
 * <td>( "^" ( &lt;sign&gt; )? &lt;integer&gt; ) <br>
 * | ( "^(" (&lt;sign&gt;)? &lt;integer&gt; ( "/" (&lt;sign&gt;)?
 * &lt;integer&gt; )? ")" ) <br>
 * | ( &lt;superscript_digit&gt; )+</td>
 * </tr>
 * <tr valign="top">
 * <td>&lt;initial_char&gt;</td>
 * <td>:=</td>
 * <td>? Any Unicode character excluding the following: ASCII control &
 * whitespace (&#92;u0000 - &#92;u0020), decimal digits '0'-'9', '('
 * (&#92;u0028), ')' (&#92;u0029), '*' (&#92;u002A), '+' (&#92;u002B), '-'
 * (&#92;u002D), '.' (&#92;u002E), '/' (&#92;u005C), ':' (&#92;u003A), '^'
 * (&#92;u005E), '²' (&#92;u00B2), '³' (&#92;u00B3), '·' (&#92;u00B7), '¹'
 * (&#92;u00B9), '⁰' (&#92;u2070), '⁴' (&#92;u2074), '⁵' (&#92;u2075), '⁶'
 * (&#92;u2076), '⁷' (&#92;u2077), '⁸' (&#92;u2078), '⁹' (&#92;u2079) ?</td>
 * </tr>
 * <tr valign="top">
 * <td>&lt;unit_identifier&gt;</td>
 * <td>:=</td>
 * <td>&lt;initial_char&gt; ( &lt;initial_char&gt; | &lt;digit&gt; )*</td>
 * </tr>
 * <tr>
 * <th colspan="3" align="left">Non-Terminals:</th>
 * </tr>
 * <tr valign="top">
 * <td>&lt;unit_expr&gt;</td>
 * <td>:=</td>
 * <td>&lt;compound_expr&gt;</td>
 * </tr>
 * <tr valign="top">
 * <td>&lt;compound_expr&gt;</td>
 * <td>:=</td>
 * <td>&lt;add_expr&gt; ( ":" &lt;add_expr&gt; )*</td>
 * </tr>
 * <tr valign="top">
 * <td>&lt;add_expr&gt;</td>
 * <td>:=</td>
 * <td>( &lt;number&gt; &lt;sign&gt; )? &lt;mul_expr&gt; ( &lt;sign&gt;
 * &lt;number&gt; )?</td>
 * </tr>
 * <tr valign="top">
 * <td>&lt;mul_expr&gt;</td>
 * <td>:=</td>
 * <td>&lt;exponent_expr&gt; ( ( ( "*" | "·" ) &lt;exponent_expr&gt; ) | ( "/"
 * &lt;exponent_expr&gt; ) )*</td>
 * </tr>
 * <tr valign="top">
 * <td>&lt;exponent_expr&gt;</td>
 * <td>:=</td>
 * <td>( &lt;atomic_expr&gt; ( &lt;exponent&gt; )? ) <br>
 * | (&lt;integer&gt; "^" &lt;atomic_expr&gt;) <br>
 * | ( ( "log" ( &lt;integer&gt; )? ) | "ln" ) "(" &lt;add_expr&gt; ")" )</td>
 * </tr>
 * <tr valign="top">
 * <td>&lt;atomic_expr&gt;</td>
 * <td>:=</td>
 * <td>&lt;number&gt; <br>
 * | &lt;unit_identifier&gt; <br>
 * | ( "(" &lt;add_expr&gt; ")" )</td>
 * </tr>
 * </table>
 * 
 * @author <a href="mailto:eric-r@northwestern.edu">Eric Russell</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.1, $Date: 2018-04-05 $
 * @since 1.0
 */
public class EBNFUnitFormat extends AbstractUnitFormat {

	// ////////////////////////////////////////////////////
	// Class variables //
	// ////////////////////////////////////////////////////

	/**
	* 
	*/
	// private static final long serialVersionUID = 8968559300292910840L;

	/**
	 * Name of the resource bundle
	 */
	private static final String BUNDLE_NAME = "tech.units.indriya.format.messages"; //$NON-NLS-1$

	/**
	 * Default locale instance. If the default locale is changed after the class is
	 * initialized, this instance will no longer be used.
	 */
	private static final EBNFUnitFormat DEFAULT_INSTANCE = new EBNFUnitFormat();

	/**
	 * Returns the instance for the current default locale (non-ascii characters are
	 * allowed)
	 */
	public static EBNFUnitFormat getInstance() {
		return DEFAULT_INSTANCE;
	}

	/** Returns an instance for the given symbol map. */
	public static EBNFUnitFormat getInstance(SymbolMap symbols) {
		return new EBNFUnitFormat(symbols);
	}

	// //////////////////////
	// Instance variables //
	// //////////////////////
	/**
	 * The symbol map used by this instance to map between
	 * {@link org.unitsofmeasure.Unit Unit}s and <code>String</code>s, etc...
	 */
	private final transient SymbolMap symbolMap;

	// ////////////////
	// Constructors //
	// ////////////////
	/**
	 * Base constructor.
	 * 
	 */
	EBNFUnitFormat() {
		this(SymbolMap.of(ResourceBundle.getBundle(BUNDLE_NAME, Locale.ROOT)));
	}

	/**
	 * Private constructor.
	 * 
	 * @param symbols
	 *            the symbol mapping.
	 */
	private EBNFUnitFormat(SymbolMap symbols) {
		symbolMap = symbols;
	}

	// //////////////////////
	// Instance methods //
	// //////////////////////
	/**
	 * Get the symbol map used by this instance to map between
	 * {@link org.unitsofmeasure.Unit Unit}s and <code>String</code>s, etc...
	 * 
	 * @return SymbolMap the current symbol map
	 */
	protected SymbolMap getSymbols() {
		return symbolMap;
	}

	// //////////////
	// Formatting //
	// //////////////
	public Appendable format(Unit<?> unit, Appendable appendable) throws IOException {

		EBNFHelper.formatInternal(unit, appendable, symbolMap);
		if (unit instanceof AnnotatedUnit<?>) {
			AnnotatedUnit<?> annotatedUnit = (AnnotatedUnit<?>) unit;
			if (annotatedUnit.getAnnotation() != null) {
				appendable.append('{');
				appendable.append(annotatedUnit.getAnnotation());
				appendable.append('}');
			}
		}
		// TODO add support for CompoundUnit similar to AnnotatedUnit
		return appendable;
	}

	@Override
	protected Unit<? extends Quantity<?>> parse(CharSequence csq, ParsePosition cursor) throws ParserException {
		// Parsing reads the whole character sequence from the parse position.
		int start = cursor != null ? cursor.getIndex() : 0;
		int end = csq.length();
		if (end <= start) {
			return AbstractUnit.ONE;
		}
		String source = csq.subSequence(start, end).toString().trim();
		if (source.length() == 0) {
			return AbstractUnit.ONE;
		}
		try {
			UnitFormatParser parser = new UnitFormatParser(symbolMap, new StringReader(source));
			Unit<?> result = parser.parseUnit();
			if (cursor != null)
				cursor.setIndex(end);
			return result;
		} catch (TokenException e) {
			if (e.currentToken != null) {
				cursor.setErrorIndex(start + e.currentToken.endColumn);
			} else {
				cursor.setErrorIndex(start);
			}
			throw new ParserException(e);
		} catch (TokenMgrError e) {
			cursor.setErrorIndex(start);
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	protected Unit<?> parse(CharSequence csq, int index) throws IllegalArgumentException {
		return parse(csq, new ParsePosition(index));
	}

	public Unit<?> parse(CharSequence csq) throws ParserException {
		return parse(csq, 0);
	}

	@Override
	public boolean isLocaleSensitive() {
		return false;
	}
}
