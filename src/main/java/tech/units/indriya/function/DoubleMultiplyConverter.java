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
package tech.units.indriya.function;

import java.util.Objects;

import javax.measure.UnitConverter;

import tech.units.indriya.internal.function.calc.Calculator;

/**
 * <p>
 * This class represents a converter multiplying numeric values by a constant
 * scaling factor (<code>double</code> based).
 * </p>
 * 
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @author Andi Huber
 * @version 1.4, Jun 23, 2019
 * @since 1.0
 */
final class DoubleMultiplyConverter 
extends AbstractConverter 
implements MultiplyConverter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6588759878444545649L;

	/**
	 * Holds the scale factor.
	 */
	private final double doubleFactor;

	/**
	 * Creates a multiply converter with the specified scale factor.
	 * 
	 * @param factor
	 *            the scaling factor.
	 */
	private DoubleMultiplyConverter(double factor) {
		this.doubleFactor = factor;
	}

	/**
	 * Creates a multiply converter with the specified scale factor.
	 * 
	 * @param factor
	 *            the scaling factor.
	 */
	static DoubleMultiplyConverter of(double factor) {
		return new DoubleMultiplyConverter(factor);
	}

	@Override
	public boolean isIdentity() {
		return doubleFactor == 1.0;
	}

	@Override
	protected boolean canReduceWith(AbstractConverter that) {
		return that instanceof DoubleMultiplyConverter;
	}

	@Override
	protected AbstractConverter reduce(AbstractConverter that) {
		return new DoubleMultiplyConverter(doubleFactor * ((DoubleMultiplyConverter) that).doubleFactor);
	}

	@Override
	public DoubleMultiplyConverter inverseWhenNotIdentity() {
		return new DoubleMultiplyConverter(1.0 / doubleFactor);
	}

    @Override
    protected Number convertWhenNotIdentity(Number value) {
        return Calculator.of(doubleFactor)
              .multiply(value)
              .peek();
    }
	
	@Override
	public final String transformationLiteral() {
		return String.format("x -> x * %s", doubleFactor);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof DoubleMultiplyConverter) {
		    DoubleMultiplyConverter that = (DoubleMultiplyConverter) obj;
			return Objects.equals(doubleFactor, that.doubleFactor);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(doubleFactor);
	}

	@Override
	public Double getValue() {
		return doubleFactor;
	}

	@Override
	public int compareTo(UnitConverter o) {
		if (this == o) {
			return 0;
		}
		if (o instanceof DoubleMultiplyConverter) {
			return getValue().compareTo(((DoubleMultiplyConverter) o).getValue());
		}
		return -1;
	}
}
