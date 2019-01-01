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

import javax.measure.Unit;
import javax.measure.UnitConverter;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.unit.AnnotatedUnit;
import tech.units.indriya.unit.BaseUnit;
import tech.units.indriya.unit.TransformedUnit;

import static tech.units.indriya.format.ConverterFormatter.formatConverter;
import static tech.units.indriya.unit.Units.*;

import java.io.IOException;
import java.util.Map;

/**
 * Helper class that handles internals of formatting in {@link EBNFUnitFormat}
 * 
 * @author otaviojava
 * @author keilw
 */
class EBNFHelper {

  /** Operator precedence for the addition and subtraction operations */
  static final int ADDITION_PRECEDENCE = 0;

  /** Operator precedence for the multiplication and division operations */
  static final int PRODUCT_PRECEDENCE = ADDITION_PRECEDENCE + 2;

  /** Operator precedence for the exponentiation and logarithm operations */
  static final int EXPONENT_PRECEDENCE = PRODUCT_PRECEDENCE + 2;

  static final char MIDDLE_DOT = '\u00b7'; // $NON-NLS-1$

  /** Exponent 1 character */
  private static final char EXPONENT_1 = '\u00b9'; // $NON-NLS-1$

  /** Exponent 2 character */
  private static final char EXPONENT_2 = '\u00b2'; // $NON-NLS-1$

  /**
   * Operator precedence for a unit identifier containing no mathematical operations (i.e., consisting exclusively of an identifier and possibly a
   * prefix). Defined to be <code>Integer.MAX_VALUE</code> so that no operator can have a higher precedence.
   */
  static final int NOOP_PRECEDENCE = Integer.MAX_VALUE;

  /**
   * Format the given unit to the given Appendable, then return the operator precedence of the outermost operator in the unit expression that was
   * formatted. See {@link ConverterFormatter} for the constants that define the various precedence values.
   *
   * @param unit
   *          the unit to be formatted
   * @param buffer
   *          the <code>StringBuffer</code> to be written to
   * @return the operator precedence of the outermost operator in the unit expression that was output
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
static int formatInternal(Unit<?> unit, Appendable buffer, SymbolMap symbolMap) throws IOException {
    if (unit instanceof AnnotatedUnit<?>) {
      unit = ((AnnotatedUnit<?>) unit).getActualUnit();
    }
    final String symbol = symbolMap.getSymbol(unit);
    if (symbol != null) {
      return noopPrecedenceInternal(buffer, symbol);
    } else if (unit.getBaseUnits() != null) {
      return productPrecedenceInternal(unit, buffer, symbolMap);
    } else if (unit instanceof BaseUnit<?>) {
      return noopPrecedenceInternal(buffer, ((BaseUnit<?>) unit).getSymbol());
    } else if (unit.getSymbol() != null) { // Alternate unit.
      return noopPrecedenceInternal(buffer, unit.getSymbol());
    } else { // A transformed unit or new unit type!
      UnitConverter converter = null;
      boolean printSeparator = false;
      StringBuilder temp = new StringBuilder();
      int unitPrecedence = NOOP_PRECEDENCE;
      Unit<?> parentUnit = unit.getSystemUnit();
      converter = ((AbstractUnit<?>) unit).getSystemConverter();
      if (KILOGRAM.equals(parentUnit)) {
        // More special-case hackery to work around gram/kilogram
        // inconsistency
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
          parentUnit = transUnit.getSystemUnit();
        converter = transUnit.getConverter();
      }

      unitPrecedence = formatInternal(parentUnit, temp, symbolMap);
      printSeparator = !parentUnit.equals(AbstractUnit.ONE);
      int result = formatConverter(converter, printSeparator, unitPrecedence, temp, symbolMap);
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
  private static void formatExponent(Unit<?> unit, int pow, int root, boolean continued, Appendable buffer, SymbolMap symbolMap) throws IOException {

    if (continued) {
      buffer.append(MIDDLE_DOT);
    }
    final StringBuilder temp = new StringBuilder();
    int unitPrecedence = EBNFHelper.formatInternal(unit, temp, symbolMap);

    if (unitPrecedence < EBNFHelper.PRODUCT_PRECEDENCE) {
      temp.insert(0, '('); // $NON-NLS-1$
      temp.append(')'); // $NON-NLS-1$
    }
    buffer.append(temp);
    if (root == 1 && pow == 1) {
      // do nothing
    } else if (root == 1 && pow > 1) {
      String powStr = Integer.toString(pow);
      for (int i = 0; i < powStr.length(); i += 1) {
        char c = powStr.charAt(i);
        switch (c) {
          case '0':
            buffer.append('\u2070'); // $NON-NLS-1$
            break;
          case '1':
            buffer.append(EXPONENT_1); // $NON-NLS-1$
            break;
          case '2':
            buffer.append(EXPONENT_2);
            break;
          case '3':
            buffer.append('\u00b3'); // $NON-NLS-1$
            break;
          case '4':
            buffer.append('\u2074'); // $NON-NLS-1$
            break;
          case '5':
            buffer.append('\u2075'); // $NON-NLS-1$
            break;
          case '6':
            buffer.append('\u2076'); // $NON-NLS-1$
            break;
          case '7':
            buffer.append('\u2077'); // $NON-NLS-1$
            break;
          case '8':
            buffer.append('\u2078'); // $NON-NLS-1$
            break;
          case '9':
            buffer.append('\u2079'); // $NON-NLS-1$
            break;
        }
      }
    } else if (root == 1) {
      buffer.append('^'); // $NON-NLS-1$
      buffer.append(String.valueOf(pow));
    } else {
      buffer.append("^("); //$NON-NLS-1$
      buffer.append(String.valueOf(pow));
      buffer.append('/'); // $NON-NLS-1$
      buffer.append(String.valueOf(root));
      buffer.append(')'); // $NON-NLS-1$
    }
  }

  private static int noopPrecedenceInternal(Appendable buffer, String symbol) throws IOException {
    buffer.append(symbol);
    return NOOP_PRECEDENCE;
  }

  @SuppressWarnings("unchecked")
  private static int productPrecedenceInternal(Unit<?> unit, Appendable buffer, SymbolMap symbolMap) throws IOException {
    Map<Unit<?>, Integer> productUnits = (Map<Unit<?>, Integer>) unit.getBaseUnits();
    int negativeExponentCount = 0;
    // Write positive exponents first...
    boolean start = true;
    for (Map.Entry<Unit<?>, Integer> e : productUnits.entrySet()) {
      int pow = e.getValue();
      if (pow >= 0) {
        formatExponent(e.getKey(), pow, 1, !start, buffer, symbolMap);
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
          formatExponent(e.getKey(), -pow, 1, !start, buffer, symbolMap);
          start = false;
        }
      }
      if (negativeExponentCount > 1) {
        buffer.append(')');
      }
    }
    return PRODUCT_PRECEDENCE;
  }
}
