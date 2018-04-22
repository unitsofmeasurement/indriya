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
package tech.units.indriya.function;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.measure.UnitConverter;

import tech.units.indriya.AbstractConverter;
import tech.uom.lib.common.function.ValueSupplier;

/**
 * <p>
 * This class represents a converter multiplying numeric values by π (Pi).
 * </p>
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Pi"> Wikipedia: Pi</a>
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.1, August 8, 2017
 * @since 1.0
 */
public final class PiMultiplierConverter extends AbstractConverter implements ValueSupplier<String> {

  /**
     * 
     */
  private static final long serialVersionUID = -5763262154104962367L;

  private static final Logger logger = Logger.getLogger(PiMultiplierConverter.class.getName());

  /**
   * Creates a Pi multiplier converter.
   */
  public PiMultiplierConverter() {
  }

  @Override
  public double convert(double value) {
    return value * PI;
  }

  @Override
  public BigDecimal convert(BigDecimal value, MathContext ctx) throws ArithmeticException {
    int nbrDigits = ctx.getPrecision();
    if (nbrDigits == 0)
      throw new ArithmeticException("Pi multiplication with unlimited precision");
    BigDecimal pi = Pi.pi(nbrDigits);
    return value.multiply(pi, ctx);
  }

  @Override
  public boolean isIdentity() {
    return false;
  }

  @Override
  protected boolean isSimpleCompositionWith(AbstractConverter that) {
	return that.isLinear();
  }

  @Override
  protected AbstractConverter simpleCompose(AbstractConverter that) {
	if(that instanceof PiDivisorConverter) {
		return AbstractConverter.IDENTITY;	
	}
	throw new IllegalStateException(String.format(
			"%s.simpleCompose() not handled for linear converter %s", 
			this, that));
  }

  
  @Override
  public AbstractConverter inverse() {
    return new PiDivisorConverter();
  }

  @Override
  public final String toString() {
    return "(π)";
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof PiMultiplierConverter);
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean isLinear() {
    return true;
  }

  /**
   * Pi calculation with Machin's formula.
   * 
   * @see <a href= "http://en.literateprograms.org/Pi_with_Machin's_formula_(Java)" >Pi with Machin's formula</a>
   * 
   */
  static final class Pi {

    private Pi() {
    }

    public static BigDecimal pi(int numDigits) {
      int calcDigits = numDigits + 10;
      return FOUR.multiply((FOUR.multiply(arccot(FIVE, calcDigits))).subtract(arccot(TWO_THIRTY_NINE, calcDigits))).setScale(numDigits,
          RoundingMode.DOWN);
    }

    /*
     * private static BigDecimal compute(int numDigits, boolean verbose) {
     * int calcDigits = numDigits + 10;
     * 
     * return FOUR .multiply((FOUR.multiply(arccot(FIVE,
     * calcDigits))).subtract(arccot(TWO_THIRTY_NINE, calcDigits)))
     * .setScale(numDigits, RoundingMode.DOWN); }
     */
    /** Compute arccot via the Taylor series expansion. */
    private static BigDecimal arccot(BigDecimal x, int numDigits) {
      BigDecimal unity = BigDecimal.ONE.setScale(numDigits, RoundingMode.DOWN);
      BigDecimal sum = unity.divide(x, RoundingMode.DOWN);
      BigDecimal xpower = new BigDecimal(sum.toString());
      BigDecimal term = null;
      int nTerms = 0;

      BigDecimal nearZero = BigDecimal.ONE.scaleByPowerOfTen(-numDigits);
      logger.log(Level.FINER, "arccot: ARGUMENT=" + x + " (nearZero=" + nearZero + ")");
      boolean add = false;
      // Add one term of Taylor series each time thru loop. Stop looping
      // when _term_
      // gets very close to zero.
      for (BigDecimal n = THREE; term == null || !term.equals(BigDecimal.ZERO); n = n.add(TWO)) {
        if (term != null && term.compareTo(nearZero) < 0)
          break;
        xpower = xpower.divide(x.pow(2), RoundingMode.DOWN);
        term = xpower.divide(n, RoundingMode.DOWN);
        sum = add ? sum.add(term) : sum.subtract(term);
        add = !add;
        // System.out.println("arccot: xpower=" + xpower + ", term=" +
        // term);
        logger.log(Level.FINEST, "arccot: term=" + term);
        nTerms++;
      }
      logger.log(Level.FINER, "arccot: done. nTerms=" + nTerms);
      return sum;
    }
  }

  private static final BigDecimal TWO = new BigDecimal("2");

  private static final BigDecimal THREE = new BigDecimal("3");

  private static final BigDecimal FOUR = new BigDecimal("4");

  private static final BigDecimal FIVE = new BigDecimal("5");

  private static final BigDecimal TWO_THIRTY_NINE = new BigDecimal("239");

  @Override
  public String getValue() {
    return toString();
  }

  @Override
  public int compareTo(UnitConverter o) {
    if (this == o) {
      return 0;
    }
    if (o instanceof ValueSupplier) {
      return getValue().compareTo(String.valueOf(((ValueSupplier) o).getValue()));
    }
    return -1;
  }
}
