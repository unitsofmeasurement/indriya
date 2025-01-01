/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2025, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import static java.lang.StrictMath.E;
import static org.apiguardian.api.API.Status.INTERNAL;
import static tech.units.indriya.format.FormatConstants.ADDITION_PRECEDENCE;
import static tech.units.indriya.format.FormatConstants.EXPONENT_PRECEDENCE;
import static tech.units.indriya.format.FormatConstants.MIDDLE_DOT;
import static tech.units.indriya.format.FormatConstants.NOOP_PRECEDENCE;
import static tech.units.indriya.format.FormatConstants.PRODUCT_PRECEDENCE;

import java.util.Formattable;
import java.util.Formatter;

import javax.measure.Prefix;
import javax.measure.UnitConverter;

import org.apiguardian.api.API;

import tech.units.indriya.function.AbstractConverter;
import tech.units.indriya.function.AddConverter;
import tech.units.indriya.function.Calculus;
import tech.units.indriya.function.ExpConverter;
import tech.units.indriya.function.LogConverter;
import tech.units.indriya.function.MultiplyConverter;
import tech.units.indriya.function.PowerOfIntConverter;
import tech.units.indriya.function.RationalNumber;
import tech.units.indriya.spi.NumberSystem;

/**
 * Helper class that handles internals of formatting {@link UnitConverter} instances.

 * @author otaviojava
 * @author keilw
 */
@API(status=INTERNAL)
class ConverterFormatter {
    private static final String LOCAL_FORMAT_PATTERN = "%s"; //$NON-NLS-1$

    /**
     * Formats the given converter to the given StringBuilder and returns the operator precedence of the converter's mathematical operation. This is
     * the default implementation, which supports all built-in UnitConverter implementations. Note that it recursively calls itself in the case of a
     * {@link AbstractConverter.Pair pair} converter.
     *
     * @param converter
     *            the converter to be formatted
     * @param continued
     *            <code>true</code> if the converter expression should begin with an operator, otherwise <code>false</code>.
     * @param unitPrecedence
     *            the operator precedence of the operation expressed by the unit being modified by the given converter.
     * @param buffer
     *            the <code>StringBuffer</code> to append to.
     * @return the operator precedence of the given UnitConverter
     */
    static int formatConverter(UnitConverter converter, boolean continued, int unitPrecedence, StringBuilder buffer, final SymbolMap symbolMap) {
        if (converter instanceof AddConverter) {
            return additionPrecedence((AddConverter) converter, continued, unitPrecedence, buffer);
        } else if (converter instanceof LogConverter) {
            return exponentPrecedenceLogConveter((LogConverter) converter, buffer);
        } else if (converter instanceof ExpConverter) {
            return exponentPrecedenceExpConveter((ExpConverter) converter, unitPrecedence, buffer);
        } else if ((converter instanceof MultiplyConverter) && 
                !(converter instanceof PowerOfIntConverter)) {            
        	final Prefix prefix = symbolMap.getPrefix(converter);
        	if ((prefix != null) && (unitPrecedence == NOOP_PRECEDENCE))
                return noopPrecedence(buffer, symbolMap, prefix);
        	return productPrecedence((MultiplyConverter) converter, continued, unitPrecedence, buffer);            
        } else if (converter instanceof PowerOfIntConverter) {
            final Prefix prefix = symbolMap.getPrefix(converter);
            if ((prefix != null) && (unitPrecedence == NOOP_PRECEDENCE))
                return noopPrecedence(buffer, symbolMap, prefix);
            return productPrecedence((PowerOfIntConverter) converter, continued, unitPrecedence, buffer);
        } else if (converter instanceof AbstractConverter.Pair) {
            final AbstractConverter.Pair pair = (AbstractConverter.Pair) converter;
            if (pair.getLeft() == AbstractConverter.IDENTITY)
                return formatConverter(pair.getRight(), true, unitPrecedence, buffer, symbolMap);
            if (pair.getLeft() instanceof Formattable) {
                return formatFormattable((Formattable) pair.getLeft(), unitPrecedence, buffer);
            } else if (pair.getRight() instanceof Formattable) {
                return formatFormattable((Formattable) pair.getRight(), unitPrecedence, buffer);
            } else {
                return formatConverter(pair.getLeft(), true, unitPrecedence, buffer, symbolMap);
                // FIXME use getRight() here, too
            }
            // return formatConverter(pair.getRight(), true,
            // unitPrecedence, buffer);

        } else {
            if (converter != null) {
                // throw new IllegalArgumentException(
                // "Unable to format the given UnitConverter: " +
                // converter.getClass()); //$NON-NLS-1$
                buffer.replace(0, 1, converter.toString());
                return NOOP_PRECEDENCE;
            }
            throw new IllegalArgumentException("Unable to format, no UnitConverter given"); //$NON-NLS-1$
        }
    }

    private static int productPrecedence(PowerOfIntConverter converter, boolean continued, int unitPrecedence, StringBuilder buffer) {
        if (unitPrecedence < PRODUCT_PRECEDENCE) {
            buffer.insert(0, '(');
            buffer.append(')');
        }
        PowerOfIntConverter powerConverter = converter;

        if (!powerConverter.isIdentity()) {
            if (continued) {
                buffer.append(MIDDLE_DOT);
            }
            buffer.append(powerConverter.getBase()).append("^").append(powerConverter.getExponent());
        }
        return PRODUCT_PRECEDENCE;
    }

//TODO[220] remove comment    
//    private static int productPrecedence(RationalConverter converter, boolean continued, int unitPrecedence, StringBuilder buffer) {
//        if (unitPrecedence < PRODUCT_PRECEDENCE) {
//            buffer.insert(0, '(');
//            buffer.append(')');
//        }
//        RationalConverter rationalConverter = converter;
//        if (rationalConverter.getDividend() != BigInteger.ONE) {
//            if (continued) {
//                buffer.append(MIDDLE_DOT);
//            }
//            buffer.append(rationalConverter.getDividend());
//        }
//        if (rationalConverter.getDivisor() != BigInteger.ONE) {
//            buffer.append('/');
//            buffer.append(rationalConverter.getDivisor());
//        }
//        return PRODUCT_PRECEDENCE;
//    }

    private static int productPrecedence(MultiplyConverter converter, boolean continued, int unitPrecedence, StringBuilder buffer) {
        if (unitPrecedence < PRODUCT_PRECEDENCE) {
            buffer.insert(0, '(');
            buffer.append(')');
        }
        Number factor = converter.getFactor();
        if(factor instanceof RationalNumber) {
            RationalNumber rational = (RationalNumber)factor;
            if (continued) {

                if(rational.isInteger()) {
                    buffer.append(MIDDLE_DOT);
                    buffer.append(rational.toString()); // renders as integer
                } else {
                    
                    RationalNumber reciprocal = rational.reciprocal();
                    
                    if(reciprocal.isInteger()) {
                        buffer.append('/');
                        buffer.append(reciprocal.toString()); // renders as integer
                    } else {
                        buffer.append(MIDDLE_DOT);
                        buffer.append(rational.toRationalString('/'));
                    }
                    
                }
                
            } else {
                buffer.append(rational.toRationalString('/'));    
            }
        } else {
            if (continued) {
                buffer.append(MIDDLE_DOT);
            }
            buffer.append(String.valueOf(factor));    
        }
        return PRODUCT_PRECEDENCE;
    }

    private static int exponentPrecedenceExpConveter(ExpConverter converter, int unitPrecedence, StringBuilder buffer) {
        if (unitPrecedence < EXPONENT_PRECEDENCE) {
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
        return EXPONENT_PRECEDENCE;
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
        return EXPONENT_PRECEDENCE;
    }

    private static int additionPrecedence(AddConverter converter, boolean continued, int unitPrecedence, StringBuilder buffer) {
        if (unitPrecedence < ADDITION_PRECEDENCE) {
            buffer.insert(0, '(');
            buffer.append(')');
        }
        NumberSystem ns = Calculus.currentNumberSystem();
        Number offset = converter.getOffset();
        if (ns.compare(offset, 0)<0) {
            buffer.append("-"); //$NON-NLS-1$
            offset = ns.negate(offset);
        } else if (continued) {
            buffer.append("+"); //$NON-NLS-1$
        }
        buffer.append(offset);
//TODO[220] remove comment        
//        long lOffset = (long) offset;
//        if (lOffset == offset) {
//            buffer.append(lOffset);
//        } else {
//            buffer.append(offset);
//        }
        return ADDITION_PRECEDENCE;
    }

    private static int noopPrecedence(StringBuilder buffer, SymbolMap symbolMap, Prefix prefix) {
        buffer.insert(0, symbolMap.getSymbol(prefix));
        return NOOP_PRECEDENCE;
    }

    /**
     * Formats the given <code>Formattable</code> to the given StringBuffer and returns the given precedence of the converter's mathematical
     * operation.
     *
     * @param f
     *            the formattable to be formatted
     * @param unitPrecedence
     *            the operator precedence of the operation expressed by the unit being modified by the given converter.
     * @param buffer
     *            the <code>StringBuffer</code> to append to.
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

    /**
     * Formats the given converter to the given StringBuffer and returns the operator precedence of the converter's mathematical operation. This is
     * the default implementation, which supports all built-in UnitConverter implementations. Note that it recursively calls itself in the case of a
     * {@link AbstractConverter.Pair pair} converter.
     * 
     * @param converter
     *            the converter to be formatted
     * @param continued
     *            <code>true</code> if the converter expression should begin with an operator, otherwise <code>false</code>.
     * @param unitPrecedence
     *            the operator precedence of the operation expressed by the unit being modified by the given converter.
     * @param buffer
     *            the <code>StringBuffer</code> to append to.
     * @return the operator precedence of the given UnitConverter
     */
    static int formatConverterLocal(UnitConverter converter, boolean continued, int unitPrecedence, StringBuilder buffer, final SymbolMap symbolMap) {
        Prefix prefix = symbolMap.getPrefix(converter);
        if ((prefix != null) && (unitPrecedence == NOOP_PRECEDENCE)) {
            buffer.insert(0, symbolMap.getSymbol(prefix));
            return NOOP_PRECEDENCE;
        } else if (converter instanceof AddConverter) {
            if (unitPrecedence < ADDITION_PRECEDENCE) {
                buffer.insert(0, '(');
                buffer.append(')');
            }
            NumberSystem ns = Calculus.currentNumberSystem();
            Number offset = ((AddConverter) converter).getOffset();
            if (ns.compare(offset, 0)<0) {
                buffer.append("-");
                offset = ns.negate(offset);
            } else if (continued) {
                buffer.append("+");
            }
            buffer.append(offset);
            return ADDITION_PRECEDENCE;
        } else if (converter instanceof MultiplyConverter) {
            if (unitPrecedence < PRODUCT_PRECEDENCE) {
                buffer.insert(0, '(');
                buffer.append(')');
            }
            if (continued) {
                buffer.append(MIDDLE_DOT);
            }
            Number factor = ((MultiplyConverter) converter).getFactor();
            if(factor instanceof RationalNumber) {
                RationalNumber rational = (RationalNumber)factor;
                buffer.append(rational.toRationalString('/'));
            } else {
                buffer.append(factor);    
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
