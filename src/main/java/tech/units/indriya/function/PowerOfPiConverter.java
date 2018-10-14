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
import tech.uom.lib.common.function.IntExponentSupplier;

/**
 * This class represents a converter multiplying numeric values by a factor of
 * Pi to the power of an integer exponent (π^exponent).
 * @author Andi Huber
 * @author Werner Keil
 * @version 1.2, October 14, 2018
 * @since 2.0
 */
public final class PowerOfPiConverter extends AbstractConverter 
 implements IntExponentSupplier {
	private static final long serialVersionUID = 5000593326722785126L;
	
	private final int exponent;
	private final int hashCode;
	private final double doubleFactor; // for double calculus only

	/**
     * A converter by Pi to the power of 1.
     *
     * @since  2.0
     */
    public static final PowerOfPiConverter ONE = of(1);
	
	/**
	 * Creates a converter with the specified exponent.
	 * 
	 * @param exponent
	 *            the exponent for the factor π^exponent.
	 */
	public static PowerOfPiConverter of(int exponent) {
		return new PowerOfPiConverter(exponent);
	}

	protected PowerOfPiConverter(int exponent) {
		this.exponent = exponent;
		this.doubleFactor =  Math.pow(Math.PI, exponent);
		this.hashCode = Objects.hash(exponent);
	}

	public int getExponent() {
		return exponent;
	}

	@Override
	public boolean isIdentity() {
		return exponent == 0; // x^0 = 1, for any x!=0
	}

	@Override
	public boolean isLinear() {
		return true;
	}

	@Override
	public AbstractConverter inverseWhenNotIdentity() {
		return new PowerOfPiConverter(-exponent);
	}

	@Override
	public BigDecimal convertWhenNotIdentity(BigDecimal value, MathContext ctx) throws ArithmeticException {
		int nbrDigits = ctx.getPrecision();
		if (nbrDigits == 0) {
			throw new ArithmeticException("Pi multiplication with unlimited precision");
		}
		BigDecimal pi = Constants.Pi.ofNumDigits(nbrDigits);
		return pi.pow(exponent, ctx).multiply(value);
	}

	@Override
	public double convertWhenNotIdentity(double value) {
		//[ahuber] multiplication is probably non-critical regarding preservation of precision
		return value * doubleFactor;
	}

	@Override
	protected boolean isSimpleCompositionWith(AbstractConverter that) {
		return that instanceof PowerOfPiConverter;
	}

	@Override
	protected AbstractConverter simpleCompose(AbstractConverter that) {
		return new PowerOfPiConverter(this.exponent + ((PowerOfPiConverter)that).exponent);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof UnitConverter) {
			UnitConverter other = (UnitConverter) obj;
			if(this.isIdentity() && other.isIdentity()) {
				return true;
			}
		}
		if (obj instanceof PowerOfPiConverter) {
			PowerOfPiConverter other = (PowerOfPiConverter) obj;
			return this.exponent == other.exponent;
		}
		return false;
	}

	@Override
	public final String transformationLiteral() {
		return String.format("x -> x * π^%s", exponent);
	}

	@Override
	public int compareTo(UnitConverter o) {
		if (this == o) {
			return 0;
		}
		if(this.isIdentity() && o.isIdentity()) {
			return 0;
		}
		if (o instanceof PowerOfPiConverter) {
			PowerOfPiConverter other = (PowerOfPiConverter) o;
			return Integer.compare(exponent, other.exponent);
		}
		return this.getClass().getName().compareTo(o.getClass().getName());
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
