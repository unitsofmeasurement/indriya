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
import tech.uom.lib.common.function.ValueSupplier;

/**
 * <p>
 * This class represents a exponential converter of limited precision. Such converter is used to create inverse of logarithmic unit.
 *
 * <p>
 * This class is package private, instances are created using the {@link LogConverter#inverse()} method.
 * </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @author Andi Huber
 * @version 1.5, Jun 21, 2019
 * @since 1.0
 */
public final class ExpConverter extends AbstractConverter implements ValueSupplier<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8851436813812059827L;

	/**
	 * Holds the logarithmic base.
	 */
	private final double base;

	/**
	 * Holds the natural logarithm of the base.
	 */
	private final double logOfBase;

	/**
	 * Creates a logarithmic converter having the specified base.
	 *
	 * @param base
	 *          the logarithmic base (e.g. <code>Math.E</code> for the Natural Logarithm).
	 */
	public ExpConverter(double base) {
		this.base = base;
		this.logOfBase = Math.log(base);
	}

	/**
	 * Creates a logarithmic converter having the specified base.
	 *
	 * @param base
	 *          the logarithmic base (e.g. <code>Math.E</code> for the Natural Logarithm).
	 */
	public static ExpConverter of(double base) {
		return new ExpConverter(base);
	}

	/**
	 * Returns the exponential base of this converter.
	 *
	 * @return the exponential base (e.g. <code>Math.E</code> for the Natural Exponential).
	 */
	public double getBase() {
		return base;
	}

	@Override
	public boolean isIdentity() {
		return false;
	}

	@Override
	protected boolean canReduceWith(AbstractConverter that) {
		if(that instanceof LogConverter) {
			return ((LogConverter)that).getBase() == base; // can compose with log to identity, provided it has same base
		}
		return false;
	}

	@Override
	protected AbstractConverter reduce(AbstractConverter that) {
		return AbstractConverter.IDENTITY;
	}

	@Override
	public AbstractConverter inverseWhenNotIdentity() {
		return new LogConverter(base);
	}

	@Override
	public final String transformationLiteral() {
		if (base == Math.E) return "x -> e^x";

		if (base<0) return String.format("x -> (%s)^x", base);

		return String.format("x -> %s^x", base);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ExpConverter) {
			ExpConverter that = (ExpConverter) obj;
			return Objects.equals(base, that.base);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(base);
	}

    @Override
    protected Number convertWhenNotIdentity(Number value) {
        return Calculator.of(logOfBase)
              .multiply(value)
              .exp()
              .peek();
    }

	@Override
	public boolean isLinear() {
		return false;
	}

	@Override
	public String getValue() {
		return toString();
	}

	@SuppressWarnings("rawtypes")
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
