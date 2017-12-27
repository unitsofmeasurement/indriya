/*
 * Next Generation Units of Measurement Implementation
 * Copyright (c) 2005-2017, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
package tec.units.indriya.format;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.format.ParserException;

import tec.units.indriya.AbstractUnit;
import tec.units.indriya.function.AddConverter;
import tec.units.indriya.function.MultiplyConverter;
import tec.units.indriya.function.RationalConverter;
import tec.units.indriya.internal.format.LocalUnitFormatParser;
import tec.units.indriya.internal.format.TokenException;
import tec.units.indriya.internal.format.TokenMgrError;
import tec.units.indriya.unit.AlternateUnit;
import tec.units.indriya.unit.AnnotatedUnit;
import tec.units.indriya.unit.BaseUnit;
import tec.units.indriya.unit.MetricPrefix;
import tec.units.indriya.unit.TransformedUnit;

import static tec.units.indriya.unit.Units.CUBIC_METRE;
import static tec.units.indriya.unit.Units.GRAM;
import static tec.units.indriya.unit.Units.KILOGRAM;
import static tec.units.indriya.unit.Units.LITRE;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * <p>
 * This class represents the local sensitive format.
 * </p>
 *
 * <h3>Here is the grammar for CommonUnits in Extended Backus-Naur Form (EBNF)</h3>
 * <p>
 * Note that the grammar has been left-factored to be suitable for use by a top-down parser generator such as <a
 * href="https://javacc.dev.java.net/">JavaCC</a>
 * </p>
 * <table width="90%" * align="center">
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
 * <tr * valign="top">
 * <td>&lt;number&gt;</td>
 * <td>:=</td>
 * <td>(&lt;sign&gt;)? (&lt;digit&gt;)* (".")? (&lt;digit&gt;)+ (("e" | "E") (&lt;sign&gt;)? (&lt;digit&gt;)+)?</td>
 * </tr>
 * <tr valign="top">
 * <td>&lt;exponent&gt;</td>
 * <td>:=</td>
 * <td>( "^" ( &lt;sign&gt; )? &lt;integer&gt; ) <br>
 * | ( "^(" (&lt;sign&gt;)? &lt;integer&gt; ( "/" (&lt;sign&gt;)? &lt;integer&gt; )? ")" ) <br>
 * | ( &lt;superscript_digit&gt; )+</td>
 * </tr>
 * <tr valign="top">
 * <td>&lt;initial_char&gt;</td>
 * <td>:=</td>
 * <td>? Any Unicode character excluding the following: ASCII control & whitespace (&#92;u0000 - &#92;u0020), decimal digits '0'-'9', '('
 * (&#92;u0028), ')' (&#92;u0029), '*' (&#92;u002A), '+' (&#92;u002B), '-' (&#92;u002D), '.' (&#92;u002E), '/' (&#92;u005C), ':' (&#92;u003A), '^'
 * (&#92;u005E), '²' (&#92;u00B2), '³' (&#92;u00B3), '·' (&#92;u00B7), '¹' (&#92;u00B9), '⁰' (&#92;u2070), '⁴' (&#92;u2074), '⁵' (&#92;u2075), '⁶'
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
 * <tr * valign="top">
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
 * <td>( &lt;number&gt; &lt;sign&gt; )? &lt;mul_expr&gt; ( &lt;sign&gt; &lt;number&gt; )?</td>
 * </tr>
 * <tr valign="top">
 * <td>&lt;mul_expr&gt;</td>
 * <td>:=</td>
 * <td>&lt;exponent_expr&gt; ( ( ( "*" | "·" ) &lt;exponent_expr&gt; ) | ( "/" &lt;exponent_expr&gt; ) )*</td>
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
 * @version 1.0.2, April 30, 2017
 * @since 1.0
 */
public class LocalUnitFormat extends AbstractUnitFormat {

  // ////////////////////////////////////////////////////
  // Class variables //
  // ////////////////////////////////////////////////////
  /**
   * DefaultQuantityFactory locale instance. If the default locale is changed after the class is initialized, this instance will no longer be used.
   */
  private static final LocalUnitFormat DEFAULT_INSTANCE = new LocalUnitFormat(SymbolMap.of(ResourceBundle.getBundle(LocalUnitFormat.class
      .getPackage().getName() + ".messages")));
  /**
   * Multiplicand character
   */
  private static final char MIDDLE_DOT = '\u00b7';
  /**
   * Operator precedence for the addition and subtraction operations
   */
  private static final int ADDITION_PRECEDENCE = 0;
  /**
   * Operator precedence for the multiplication and division operations
   */
  private static final int PRODUCT_PRECEDENCE = ADDITION_PRECEDENCE + 2;
  /**
   * Operator precedence for the exponentiation and logarithm operations
   */
  private static final int EXPONENT_PRECEDENCE = PRODUCT_PRECEDENCE + 2;
  /**
   * Operator precedence for a unit identifier containing no mathematical operations (i.e., consisting exclusively of an identifier and possibly a
   * prefix). Defined to be <code>Integer.MAX_VALUE</code> so that no operator can have a higher precedence.
   */
  private static final int NOOP_PRECEDENCE = Integer.MAX_VALUE;

  // /////////////////
  // Class methods //
  // /////////////////
  /**
   * Returns the instance for the current default locale (non-ascii characters are allowed)
   */
  public static LocalUnitFormat getInstance() {
    return DEFAULT_INSTANCE;
  }

  /**
   * Returns an instance for the given locale.
   * 
   * @param locale
   */
  public static LocalUnitFormat getInstance(Locale locale) {
    return new LocalUnitFormat(SymbolMap.of(ResourceBundle.getBundle(LocalUnitFormat.class.getPackage().getName() + ".messages", locale)));
  }

  /** Returns an instance for the given symbol map. */
  public static LocalUnitFormat getInstance(SymbolMap symbols) {
    return new LocalUnitFormat(symbols);
  }

  // //////////////////////
  // Instance variables //
  // //////////////////////
  /**
   * The symbol map used by this instance to map between {@link Unit Unit}s and <code>String</code>s, etc...
   */
  private final transient SymbolMap symbolMap;

  // ////////////////
  // Constructors //
  // ////////////////
  /**
   * Base constructor.
   *
   * @param symbols
   *          the symbol mapping.
   */
  private LocalUnitFormat(SymbolMap symbols) {
    symbolMap = symbols;
  }

  // //////////////////////
  // Instance methods //
  // //////////////////////
  /**
   * Get the symbol map used by this instance to map between {@link AbstractUnit Unit}s and <code>String</code>s, etc...
   * 
   * @return SymbolMap the current symbol map
   */
  @Override
  protected SymbolMap getSymbols() {
    return symbolMap;
  }

  // //////////////
  // Formatting //
  // //////////////
  @Override
  public Appendable format(Unit<?> unit, Appendable appendable) throws IOException {
    if (!(unit instanceof AbstractUnit)) {
      return appendable.append(unit.toString()); // Unknown unit (use
      // intrinsic toString()
      // method)
    }
    formatInternal(unit, appendable);
    return appendable;
  }

  public boolean isLocaleSensitive() {
    return true;
  }

  protected Unit<?> parse(CharSequence csq, int index) throws ParserException {
    return parse(csq, new ParsePosition(index));
  }

  public Unit<?> parse(CharSequence csq, ParsePosition cursor) throws ParserException {
    // Parsing reads the whole character sequence from the parse position.
    int start = cursor.getIndex();
    int end = csq.length();
    if (end <= start) {
      return AbstractUnit.ONE;
    }
    String source = csq.subSequence(start, end).toString().trim();
    if (source.length() == 0) {
      return AbstractUnit.ONE;
    }
    try {
      LocalUnitFormatParser parser = new LocalUnitFormatParser(symbolMap, new StringReader(source));
      Unit<?> result = parser.parseUnit();
      cursor.setIndex(end);
      return result;
    } catch (TokenException e) {
      if (e.currentToken != null) {
        cursor.setErrorIndex(start + e.currentToken.endColumn);
      } else {
        cursor.setErrorIndex(start);
      }
      throw new IllegalArgumentException(e); // TODO should we throw
      // ParserException here,
      // too?
    } catch (TokenMgrError e) {
      cursor.setErrorIndex(start);
      throw new ParserException(e);
    }
  }

  @Override
  public Unit<? extends Quantity<?>> parse(CharSequence csq) throws ParserException {
    return parse(csq, new ParsePosition(0));
  }

  /**
   * Format the given unit to the given StringBuilder, then return the operator precedence of the outermost operator in the unit expression that was
   * formatted. See {@link ConverterFormat} for the constants that define the various precedence values.
   * 
   * @param unit
   *          the unit to be formatted
   * @param buffer
   *          the <code>StringBuilder</code> to be written to
   * @return the operator precedence of the outermost operator in the unit expression that was output
   */
  /*
   * private int formatInternal(Unit<?> unit, Appendable buffer) throws
   * IOException { if (unit instanceof AnnotatedUnit) { unit =
   * ((AnnotatedUnit) unit).getActualUnit(); } String symbol =
   * symbolMap.getSymbol((AbstractUnit<?>) unit); if (symbol != null) {
   * buffer.append(symbol); return NOOP_PRECEDENCE; } else if
   * (unit.getBaseUnits() != null) { Map<? extends Unit, Integer> productUnits
   * = unit.getBaseUnits(); int negativeExponentCount = 0; // Write positive
   * exponents first... boolean start = true; for (Unit u :
   * productUnits.keySet()) { int pow = productUnits.get(u); if (pow >= 0) {
   * formatExponent(u, pow, 1, !start, buffer); start = false; } else {
   * negativeExponentCount += 1; } } // ..then write negative exponents. if
   * (negativeExponentCount > 0) { if (start) { buffer.append('1'); }
   * buffer.append('/'); if (negativeExponentCount > 1) { buffer.append('(');
   * } start = true; for (Unit u : productUnits.keySet()) { int pow =
   * productUnits.get(u); if (pow < 0) { formatExponent(u, -pow, 1, !start,
   * buffer); start = false; } } if (negativeExponentCount > 1) {
   * buffer.append(')'); } } return PRODUCT_PRECEDENCE; } else if
   * ((!((AbstractUnit)unit).isSystemUnit()) || unit.equals(Units.KILOGRAM)) {
   * UnitConverter converter = null; boolean printSeparator = false;
   * StringBuffer temp = new StringBuffer(); int unitPrecedence =
   * NOOP_PRECEDENCE; if (unit.equals(Units.KILOGRAM)) { // A special case
   * because KILOGRAM is a BaseUnit instead of // a transformed unit, even
   * though it has a prefix. converter = MetricPrefix.KILO.getConverter();
   * unitPrecedence = formatInternal(Units.GRAM, temp); printSeparator = true;
   * } else { Unit parentUnit = unit.getSystemUnit(); converter =
   * unit.getConverterTo(parentUnit); if (parentUnit.equals(Units.KILOGRAM)) {
   * // More special-case hackery to work around gram/kilogram // incosistency
   * parentUnit = Units.GRAM; converter =
   * converter.concatenate(MetricPrefix.KILO.getConverter()); } unitPrecedence
   * = formatInternal(parentUnit, temp); printSeparator =
   * !parentUnit.equals(Units.ONE); } int result = formatConverter(converter,
   * printSeparator, unitPrecedence, temp); buffer.append(temp); return
   * result; } else if (unit.getSymbol() != null) {
   * buffer.append(unit.getSymbol()); return NOOP_PRECEDENCE; } else { throw
   * new IllegalArgumentException(
   * "Cannot format the given Object as a Unit (unsupported unit type " +
   * unit.getClass().getName() + ")"); } }
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private int formatInternal(Unit<?> unit, Appendable buffer) throws IOException {
    if (unit instanceof AnnotatedUnit<?>) {
      unit = ((AnnotatedUnit<?>) unit).getActualUnit();
      // } else if (unit instanceof ProductUnit<?>) {
      // ProductUnit<?> p = (ProductUnit<?>)unit;
    }
    // TODO is the behavior similar to EBNFUnitFormat for AnnotatedUnit?
    String symbol = symbolMap.getSymbol((AbstractUnit<?>) unit);
    if (symbol != null) {
      buffer.append(symbol);
      return NOOP_PRECEDENCE;
    } else if (unit.getBaseUnits() != null) {
      Map<Unit<?>, Integer> productUnits = (Map<Unit<?>, Integer>) unit.getBaseUnits();
      int negativeExponentCount = 0;
      // Write positive exponents first...
      boolean start = true;
      for (Map.Entry<Unit<?>, Integer> e : productUnits.entrySet()) {
        int pow = e.getValue();
        if (pow >= 0) {
          formatExponent(e.getKey(), pow, 1, !start, buffer);
          start = false;
        } else {
          negativeExponentCount += 1;
        }
      }
      // ..then write negative exponents.
      if (negativeExponentCount > 0) {
        if (start) {
          buffer.append('1');
        }
        buffer.append('/');
        if (negativeExponentCount > 1) {
          buffer.append('(');
        }
        start = true;
        for (Map.Entry<Unit<?>, Integer> e : productUnits.entrySet()) {
          int pow = e.getValue();
          if (pow < 0) {
            formatExponent(e.getKey(), -pow, 1, !start, buffer);
            start = false;
          }
        }
        if (negativeExponentCount > 1) {
          buffer.append(')');
        }
      }
      return PRODUCT_PRECEDENCE;
    } else if (unit instanceof BaseUnit<?>) {
      buffer.append(((BaseUnit<?>) unit).getSymbol());
      return NOOP_PRECEDENCE;
    } else if (unit instanceof AlternateUnit<?>) { // unit.getSymbol() !=
      // null) { // Alternate
      // unit.
      buffer.append(unit.getSymbol());
      return NOOP_PRECEDENCE;
    } else { // A transformed unit or new unit type!
      UnitConverter converter = null;
      boolean printSeparator = false;
      StringBuilder temp = new StringBuilder();
      int unitPrecedence = NOOP_PRECEDENCE;
      Unit<?> parentUnit = unit.getSystemUnit();
      converter = ((AbstractUnit<?>) unit).getSystemConverter();
      if (KILOGRAM.equals(parentUnit)) {
        // More special-case hackery to work around gram/kilogram
        // incosistency
        if (unit.equals(GRAM)) {
          buffer.append(symbolMap.getSymbol(GRAM));
          return NOOP_PRECEDENCE;
        }
        parentUnit = GRAM;
        if (unit instanceof TransformedUnit<?>) {
          converter = ((TransformedUnit<?>) unit).getConverter();
        } else {
          converter = unit.getConverterTo((Unit) GRAM);
        }
      } else if (CUBIC_METRE.equals(parentUnit)) {
        if (converter != null) {
          parentUnit = LITRE;
        }
      }

      if (unit instanceof TransformedUnit) {
        TransformedUnit<?> transUnit = (TransformedUnit<?>) unit;
        if (parentUnit == null)
          parentUnit = transUnit.getParentUnit();
        // String x = parentUnit.toString();
        converter = transUnit.getConverter();
      }

      unitPrecedence = formatInternal(parentUnit, temp);
      printSeparator = !parentUnit.equals(AbstractUnit.ONE);
      int result = formatConverter(converter, printSeparator, unitPrecedence, temp);
      buffer.append(temp);
      return result;
    }
  }

  /**
   * Format the given unit raised to the given fractional power to the given <code>StringBuffer</code>.
   * 
   * @param unit
   *          Unit the unit to be formatted
   * @param pow
   *          int the numerator of the fractional power
   * @param root
   *          int the denominator of the fractional power
   * @param continued
   *          boolean <code>true</code> if the converter expression should begin with an operator, otherwise <code>false</code>. This will always be
   *          true unless the unit being modified is equal to Unit.ONE.
   * @param buffer
   *          StringBuffer the buffer to append to. No assumptions should be made about its content.
   */
  private void formatExponent(Unit<?> unit, int pow, int root, boolean continued, Appendable buffer) throws IOException {
    if (continued) {
      buffer.append(MIDDLE_DOT);
    }
    StringBuffer temp = new StringBuffer();
    int unitPrecedence = formatInternal(unit, temp);
    if (unitPrecedence < PRODUCT_PRECEDENCE) {
      temp.insert(0, '(');
      temp.append(')');
    }
    buffer.append(temp);
    if ((root == 1) && (pow == 1)) {
      // do nothing
    } else if ((root == 1) && (pow > 1)) {
      String powStr = Integer.toString(pow);
      for (int i = 0; i < powStr.length(); i += 1) {
        char c = powStr.charAt(i);
        switch (c) {
          case '0':
            buffer.append('\u2070');
            break;
          case '1':
            buffer.append('\u00b9');
            break;
          case '2':
            buffer.append('\u00b2');
            break;
          case '3':
            buffer.append('\u00b3');
            break;
          case '4':
            buffer.append('\u2074');
            break;
          case '5':
            buffer.append('\u2075');
            break;
          case '6':
            buffer.append('\u2076');
            break;
          case '7':
            buffer.append('\u2077');
            break;
          case '8':
            buffer.append('\u2078');
            break;
          case '9':
            buffer.append('\u2079');
            break;
        }
      }
    } else if (root == 1) {
      buffer.append("^");
      buffer.append(String.valueOf(pow));
    } else {
      buffer.append("^(");
      buffer.append(String.valueOf(pow));
      buffer.append('/');
      buffer.append(String.valueOf(root));
      buffer.append(')');
    }
  }

  /**
   * Formats the given converter to the given StringBuffer and returns the operator precedence of the converter's mathematical operation. This is the
   * default implementation, which supports all built-in UnitConverter implementations. Note that it recursively calls itself in the case of a
   * {@link javax.measure.converter.UnitConverter.Compound Compound} converter.
   * 
   * @param converter
   *          the converter to be formatted
   * @param continued
   *          <code>true</code> if the converter expression should begin with an operator, otherwise <code>false</code>.
   * @param unitPrecedence
   *          the operator precedence of the operation expressed by the unit being modified by the given converter.
   * @param buffer
   *          the <code>StringBuffer</code> to append to.
   * @return the operator precedence of the given UnitConverter
   */
  private int formatConverter(UnitConverter converter, boolean continued, int unitPrecedence, StringBuilder buffer) {
    MetricPrefix prefix = symbolMap.getPrefix(converter);
    if ((prefix != null) && (unitPrecedence == NOOP_PRECEDENCE)) {
      buffer.insert(0, symbolMap.getSymbol(prefix));
      return NOOP_PRECEDENCE;
    } else if (converter instanceof AddConverter) {
      if (unitPrecedence < ADDITION_PRECEDENCE) {
        buffer.insert(0, '(');
        buffer.append(')');
      }
      double offset = ((AddConverter) converter).getOffset();
      if (offset < 0) {
        buffer.append("-");
        offset = -offset;
      } else if (continued) {
        buffer.append("+");
      }
      long lOffset = (long) offset;
      if (lOffset == offset) {
        buffer.append(lOffset);
      } else {
        buffer.append(offset);
      }
      return ADDITION_PRECEDENCE;
    } else if (converter instanceof MultiplyConverter) {
      if (unitPrecedence < PRODUCT_PRECEDENCE) {
        buffer.insert(0, '(');
        buffer.append(')');
      }
      if (continued) {
        buffer.append(MIDDLE_DOT);
      }
      double factor = ((MultiplyConverter) converter).getFactor();
      long lFactor = (long) factor;
      if (lFactor == factor) {
        buffer.append(lFactor);
      } else {
        buffer.append(factor);
      }
      return PRODUCT_PRECEDENCE;
    } else if (converter instanceof RationalConverter) {
      if (unitPrecedence < PRODUCT_PRECEDENCE) {
        buffer.insert(0, '(');
        buffer.append(')');
      }
      RationalConverter rationalConverter = (RationalConverter) converter;
      if (!rationalConverter.getDividend().equals(BigInteger.ONE)) {
        if (continued) {
          buffer.append(MIDDLE_DOT);
        }
        buffer.append(rationalConverter.getDividend());
      }
      if (!rationalConverter.getDivisor().equals(BigInteger.ONE)) {
        buffer.append('/');
        buffer.append(rationalConverter.getDivisor());
      }
      return PRODUCT_PRECEDENCE;
    } else { // All other converter type (e.g. exponential) we use the
      // string representation.
      buffer.insert(0, converter.toString() + "(");
      buffer.append(")");
      return EXPONENT_PRECEDENCE;
    }
  }
}