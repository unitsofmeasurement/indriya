/*
 * Next Generation Units of Measurement Implementation
 * Copyright (c) 2005-2017, Jean-Marie Dautelle, Werner Keil, V2COM.
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

import javax.measure.UnitConverter;

import tec.units.indriya.AbstractConverter;
import tec.units.indriya.function.*;
import tec.units.indriya.unit.MetricPrefix;

import java.math.BigInteger;
import java.util.Formattable;
import java.util.Formatter;

import static java.lang.StrictMath.E;

/**
 * @author otaviojava
 * @author keilw
 */
class ConverterFormatter {

  private static final String LOCAL_FORMAT_PATTERN = "%s";

  /**
   * Formats the given converter to the given StringBuilder and returns the operator precedence of the converter's mathematical operation. This is the
   * default implementation, which supports all built-in UnitConverter implementations. Note that it recursively calls itself in the case of a
   * {@link AbstractConverter.converter.UnitConverter.Compound Compound} converter.
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
  static int formatConverter(UnitConverter converter, boolean continued, int unitPrecedence, StringBuilder buffer, SymbolMap symbolMap) {
    final MetricPrefix prefix = symbolMap.getPrefix((AbstractConverter) converter);
    if ((prefix != null) && (unitPrecedence == EBNFHelper.NOOP_PRECEDENCE)) {
      return noopPrecedence(buffer, symbolMap, prefix);
    } else if (converter instanceof AddConverter) {
      return additionPrecedence((AddConverter) converter, continued, unitPrecedence, buffer);
    } else if (converter instanceof LogConverter) {
      return exponentPrecedenceLogConveter((LogConverter) converter, buffer);
    } else if (converter instanceof ExpConverter) {
      return exponentPrecedenceExpConveter((ExpConverter) converter, unitPrecedence, buffer);
    } else if (converter instanceof MultiplyConverter) {
      return productPrecedence((MultiplyConverter) converter, continued, unitPrecedence, buffer);
    } else if (converter instanceof RationalConverter) {
      return productPrecedence((RationalConverter) converter, continued, unitPrecedence, buffer);
    } else if (converter instanceof AbstractConverter.Pair) {
      AbstractConverter.Pair compound = (AbstractConverter.Pair) converter;
      if (compound.getLeft() == AbstractConverter.IDENTITY) {
        return formatConverter(compound.getRight(), true, unitPrecedence, buffer, symbolMap);
      } else {
        if (compound.getLeft() instanceof Formattable) {
          return formatFormattable((Formattable) compound.getLeft(), unitPrecedence, buffer);
        } else if (compound.getRight() instanceof Formattable) {
          return formatFormattable((Formattable) compound.getRight(), unitPrecedence, buffer);
        } else {
          return formatConverter(compound.getLeft(), true, unitPrecedence, buffer, symbolMap);
          // FIXME use getRight() here, too
        }
      }
      // return formatConverter(compound.getRight(), true,
      // unitPrecedence, buffer);

    } else {
      if (converter != null) {
        // throw new IllegalArgumentException(
        // "Unable to format the given UnitConverter: " +
        // converter.getClass()); //$NON-NLS-1$
        buffer.replace(0, 1, converter.toString());
        return EBNFHelper.NOOP_PRECEDENCE;
      } else
        throw new IllegalArgumentException("Unable to format, no UnitConverter given"); //$NON-NLS-1$
    }
  }

  private static int productPrecedence(RationalConverter converter, boolean continued, int unitPrecedence, StringBuilder buffer) {
    if (unitPrecedence < EBNFHelper.PRODUCT_PRECEDENCE) {
      buffer.insert(0, '(');
      buffer.append(')');
    }
    RationalConverter rationalConverter = converter;
    if (rationalConverter.getDividend() != BigInteger.ONE) {
      if (continued) {
        buffer.append(EBNFHelper.MIDDLE_DOT);
      }
      buffer.append(rationalConverter.getDividend());
    }
    if (rationalConverter.getDivisor() != BigInteger.ONE) {
      buffer.append('/');
      buffer.append(rationalConverter.getDivisor());
    }
    return EBNFHelper.PRODUCT_PRECEDENCE;
  }

  private static int productPrecedence(MultiplyConverter converter, boolean continued, int unitPrecedence, StringBuilder buffer) {
    if (unitPrecedence < EBNFHelper.PRODUCT_PRECEDENCE) {
      buffer.insert(0, '(');
      buffer.append(')');
    }
    if (continued) {
      buffer.append(EBNFHelper.MIDDLE_DOT);
    }
    double factor = converter.getFactor();
    long lFactor = (long) factor;
    if (lFactor == factor) {
      buffer.append(lFactor);
    } else {
      buffer.append(factor);
    }
    return EBNFHelper.PRODUCT_PRECEDENCE;
  }

  private static int exponentPrecedenceExpConveter(ExpConverter converter, int unitPrecedence, StringBuilder buffer) {
    if (unitPrecedence < EBNFHelper.EXPONENT_PRECEDENCE) {
      buffer.insert(0, '(');
      buffer.append(')');
    }
    StringBuilder expr = new StringBuilder();
    double base = converter.getBase();
    if (base == E) {
      expr.append('e');
    } else {
      expr.append((int) base);
    }
    expr.append('^');
    buffer.insert(0, expr);
    return EBNFHelper.EXPONENT_PRECEDENCE;
  }

  private static int exponentPrecedenceLogConveter(LogConverter converter, StringBuilder buffer) {
    double base = converter.getBase();
    StringBuilder expr = new StringBuilder();
    if (base == E) {
      expr.append("ln"); //$NON-NLS-1$
    } else {
      expr.append("log"); //$NON-NLS-1$
      if (base != 10) {
        expr.append((int) base);
      }
    }
    expr.append("("); //$NON-NLS-1$
    buffer.insert(0, expr);
    buffer.append(")"); //$NON-NLS-1$
    return EBNFHelper.EXPONENT_PRECEDENCE;
  }

  private static int additionPrecedence(AddConverter converter, boolean continued, int unitPrecedence, StringBuilder buffer) {
    if (unitPrecedence < EBNFHelper.ADDITION_PRECEDENCE) {
      buffer.insert(0, '(');
      buffer.append(')');
    }
    double offset = converter.getOffset();
    if (offset < 0) {
      buffer.append("-"); //$NON-NLS-1$
      offset = -offset;
    } else if (continued) {
      buffer.append("+"); //$NON-NLS-1$
    }
    long lOffset = (long) offset;
    if (lOffset == offset) {
      buffer.append(lOffset);
    } else {
      buffer.append(offset);
    }
    return EBNFHelper.ADDITION_PRECEDENCE;
  }

  private static int noopPrecedence(StringBuilder buffer, SymbolMap symbolMap, MetricPrefix prefix) {
    buffer.insert(0, symbolMap.getSymbol(prefix));
    return EBNFHelper.NOOP_PRECEDENCE;
  }

  /**
   * Formats the given <code>Formattable</code> to the given StringBuffer and returns the given precedence of the converter's mathematical operation.
   *
   * @param f
   *          the formattable to be formatted
   * @param unitPrecedence
   *          the operator precedence of the operation expressed by the unit being modified by the given converter.
   * @param buffer
   *          the <code>StringBuffer</code> to append to.
   * @return the given operator precedence
   */
  private static int formatFormattable(Formattable f, int unitPrecedence, StringBuilder buffer) {
    Formatter fmt = new Formatter();
    fmt.format(LOCAL_FORMAT_PATTERN, f);
    buffer.replace(0, 1, fmt.toString());
    fmt.close(); // XXX try Java 7 with res, but for now let's leave J6
    // compliant
    return unitPrecedence;
  }
}
