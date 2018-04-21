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
import java.util.Objects;

import javax.measure.UnitConverter;

import tech.units.indriya.AbstractConverter;
import tech.uom.lib.common.function.DoubleFactorSupplier;
import tech.uom.lib.common.function.ValueSupplier;

/**
 * <p>
 * This class represents a converter multiplying numeric values by a constant
 * scaling factor (<code>double</code> based).
 * </p>
 * 
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.1, Apr 18, 2018
 * @since 1.0
 */
public final class MultiplyConverter extends AbstractConverter implements ValueSupplier<Double>, DoubleFactorSupplier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6588759878444545649L;

	/**
	 * Holds the scale factor.
	 */
	private final double factor;

	/**
	 * Creates a multiply converter with the specified scale factor.
	 * 
	 * @param factor
	 *            the scaling factor.
	 */
	public MultiplyConverter(double factor) {
		this.factor = factor;
	}

	/**
	 * Creates a multiply converter with the specified scale factor.
	 * 
	 * @param factor
	 *            the scaling factor.
	 */
	public static MultiplyConverter of(double factor) {
		return new MultiplyConverter(factor);
	}

	/**
	 * Returns the scale factor of this converter.
	 * 
	 * @return the scale factor.
	 */
	public double getFactor() {
		return factor;
	}

	@Override
	public boolean isIdentity() {
		return factor == 1.0;
	}

	@Override
	protected boolean isSimpleCompositionWith(AbstractConverter that) {
		return that.isLinear();
	}

	@Override
	protected AbstractConverter simpleCompose(AbstractConverter that) {
		if (that instanceof MultiplyConverter) {
			return new MultiplyConverter(factor * ((MultiplyConverter) that).factor);
		}
		throw new IllegalStateException(String.format(
				"%s.simpleCompose() not handled for linear converter %s", 
				this, that));
	}

	@Override
	public MultiplyConverter inverse() {
		return new MultiplyConverter(1.0 / factor);
	}

	@Override
	public double convert(double value) {
		return value * factor;
	}

	@Override
	public BigDecimal convert(BigDecimal value, MathContext ctx) throws ArithmeticException {
		return value.multiply(BigDecimal.valueOf(factor), ctx);
	}

	@Override
	public final String toString() {
		return MultiplyConverter.class.getSimpleName() + "(" + factor + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof MultiplyConverter) {
			MultiplyConverter that = (MultiplyConverter) obj;
			return Objects.equals(factor, that.factor);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(factor);
	}

	@Override
	public boolean isLinear() {
		return true;
	}

	@Override
	public Double getValue() {
		return factor;
	}

	@Override
	public int compareTo(UnitConverter o) {
		if (this == o) {
			return 0;
		}
		if (o instanceof MultiplyConverter) {
			return getValue().compareTo(((MultiplyConverter) o).getValue());
		}
		return -1;
	}
}
