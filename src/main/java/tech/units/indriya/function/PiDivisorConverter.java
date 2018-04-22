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

import javax.measure.UnitConverter;

import tech.units.indriya.AbstractConverter;
import tech.uom.lib.common.function.ValueSupplier;

/**
 * <p>
 * This class represents a converter dividing numeric values by π (Pi).
 * </p>
 *
 * <p>
 * This class is package private, instances are created using the {@link PiMultiplierConverter#inverse()} method.
 * </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.1, April 22, 2018
 * @since 1.0
 */
final class PiDivisorConverter extends AbstractConverter implements ValueSupplier<String> {

  /**
     * 
     */
  private static final long serialVersionUID = 5052794216568914141L;

  /**
   * Creates a Pi divisor converter.
   */
  public PiDivisorConverter() {
  }

  @Override
  public double convert(double value) {
    return value / PI;
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
	if(that instanceof PiMultiplierConverter) {
		return AbstractConverter.IDENTITY;	
	}
	throw new IllegalStateException(String.format(
			"%s.simpleCompose() not handled for linear converter %s", 
			this, that));
  }
  
  @Override
  public BigDecimal convert(BigDecimal value, MathContext ctx) throws ArithmeticException {
    int nbrDigits = ctx.getPrecision();
    if (nbrDigits == 0)
      throw new ArithmeticException("Pi multiplication with unlimited precision");
    BigDecimal pi = PiMultiplierConverter.Pi.pi(nbrDigits);
    return value.divide(pi, ctx);
  }

  @Override
  public AbstractConverter inverse() {
    return new PiMultiplierConverter();
  }

  @Override
  public final String toString() {
    return "(1/π)";
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof PiDivisorConverter);
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean isLinear() {
    return true;
  }

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
