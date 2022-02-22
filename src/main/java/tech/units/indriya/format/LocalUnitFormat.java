/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2022, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import static tech.units.indriya.format.ConverterFormatter.formatConverterLocal;
import static tech.units.indriya.format.FormatConstants.*;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.format.MeasurementParseException;
import tech.units.indriya.AbstractUnit;
import tech.units.indriya.internal.format.UnitFormatParser;
import tech.units.indriya.unit.AlternateUnit;
import tech.units.indriya.unit.AnnotatedUnit;
import tech.units.indriya.unit.BaseUnit;
import tech.units.indriya.unit.ProductUnit;
import tech.units.indriya.unit.TransformedUnit;

import static tech.units.indriya.unit.Units.CUBIC_METRE;
import static tech.units.indriya.unit.Units.GRAM;
import static tech.units.indriya.unit.Units.KILOGRAM;
import static tech.units.indriya.unit.Units.LITRE;

import java.io.IOException;
import java.io.StringReader;
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
 * <td>&lt;mix_expr&gt;</td>
 * </tr>
 * <tr valign="top">
 * <td>&lt;mix_expr&gt;</td>
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
 * @author <a href="mailto:werner@units.tech">Werner Keil</a>
 * @version 1.4, March 3, 2020
 * @since 1.0
 */
public class LocalUnitFormat extends AbstractUnitFormat {

  //////////////////////////////////////////////////////
  // Class variables                                  //
  //////////////////////////////////////////////////////
  /**
   * DefaultQuantityFactory locale instance. If the default locale is changed after the class is initialized, this instance will no longer be used.
   */
  private static final LocalUnitFormat DEFAULT_INSTANCE = new LocalUnitFormat(SymbolMap.of(ResourceBundle.getBundle(LocalUnitFormat.class
      .getPackage().getName() + ".messages")));

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
   * @param locale the locale to use
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

  ////////////////////////
  // Instance methods //
  ////////////////////////
  /**
   * Get the symbol map used by this instance to map between {@link AbstractUnit Unit}s and <code>String</code>s, etc...
   * 
   * @return SymbolMap the current symbol map
   */
  protected SymbolMap getSymbols() {
    return symbolMap;
  }
  
  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  ////////////////
  // Formatting //
  ////////////////
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

  protected Unit<?> parse(CharSequence csq, int index) throws MeasurementParseException {
    return parse(csq, new ParsePosition(index));
  }

  public Unit<?> parse(CharSequence csq, ParsePosition cursor) throws MeasurementParseException {
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
      UnitFormatParser parser = new UnitFormatParser(symbolMap, new StringReader(source));
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
      throw new MeasurementParseException(e);
    }
  }

  @Override
  public Unit<? extends Quantity<?>> parse(CharSequence csq) throws MeasurementParseException {
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
    } else if (unit instanceof ProductUnit<?> && unit.getBaseUnits() != null) {
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
   // TODO add case for MixedUnit
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
      int result = formatConverterLocal(converter, printSeparator, unitPrecedence, temp, symbolMap);
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
}