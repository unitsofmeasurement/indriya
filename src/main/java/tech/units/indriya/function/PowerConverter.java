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
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.measure.Prefix;
import javax.measure.UnitConverter;

import tech.units.indriya.AbstractConverter;

/**
 * 
 * UnitConverter for numbers in base^exponent representation. 
 *
 */
public class PowerConverter extends AbstractConverter {
	private static final long serialVersionUID = 3546932001671571300L;
	
	private final int base;
	private final int exponent;
	private final int hashCode;

	public static UnitConverter of(Prefix prefix) {
		return new PowerConverter(prefix.getBase(), prefix.getExponent());
	}

	protected PowerConverter(int base, int exponent) {
		if(base == 0 && exponent == 0) {
			throw new IllegalArgumentException("base and exponent can not be both zero at the same time (0^0 is undefined)");
		}
		this.base = base;
		this.exponent = exponent;
		this.hashCode = Objects.hash(base, exponent);
	}

	public int getBase() {
		return base;
	}
	
	public int getExponent() {
		return exponent;
	}
	
	@Override
	public boolean isIdentity() {
		if( base == 1 ) {
			return true; // 1^x = 1
		}
		return exponent == 0; // x^0 = 1, for any x!=0
		// [ahuber] 0^0 is undefined, but we guard against this case in the constructor
	}

	@Override
	public boolean isLinear() {
		return true;
	}

	@Override
	public AbstractConverter inverse() {
		return isIdentity() ? this : new PowerConverter(base, -exponent);
	}

	@Override
	protected Number convert(BigInteger value, MathContext ctx) {
		//[ahuber] exact number representation of factor 
		final BigInteger bintFactor = BigInteger.valueOf(base).pow(Math.abs(exponent));

		if(exponent>0) {
			return bintFactor.multiply(value);
		}

		//[ahuber] we try to return an exact BigInteger if possible
		final BigInteger[] divideAndRemainder = value.divideAndRemainder(bintFactor);
		final BigInteger divisionResult = divideAndRemainder[0]; 
		final BigInteger divisionRemainder = divideAndRemainder[1];

		if(BigInteger.ZERO.compareTo(divisionRemainder) == 0) {
			return divisionResult;
		}

		//[ahuber] fallback to BigDecimal, thats where we are loosing 'exactness'
		final BigDecimal bdecFactor = new BigDecimal(bintFactor);
		final BigDecimal bdecValue = new BigDecimal(value);

		return bdecValue.divide(bdecFactor, MathContext.DECIMAL128);
	}
	
	@Override
	public BigDecimal convert(BigDecimal value, MathContext ctx) throws ArithmeticException {
		//[ahuber] at this point we know exponent is not zero
		
		//[ahuber] exact number representation of factor 
		final BigDecimal bdecFactor = new BigDecimal(BigInteger.valueOf(base).pow(Math.abs(exponent)));
		final BigDecimal bdecValue = (BigDecimal) value;

		//[ahuber] thats where we are loosing 'exactness'
		return exponent>0 
				? bdecValue.multiply(bdecFactor, ctx)
				: bdecValue.divide(bdecFactor, ctx);
	}
	
	@Override
	public Number convert(Number value) {

		//TODO [ahuber] this should be handled in super class
		if(isIdentity()) {
			return value;
		}
		
		return super.convert(value);
	}

	@Override
	public double convert(double value) {
		if(isIdentity()) {
			return value;
		}
		//[ahuber] multiplication is probably non-critical regarding preservation of precision
		return value * Math.pow(base, exponent);
	}

	@Override
	public UnitConverter concatenate(UnitConverter converter) {
		Objects.requireNonNull(converter);
		if(isIdentity()) {
			return converter;
		}
		if(converter.isIdentity()) {
			return this;
		}
		if(converter instanceof PowerConverter) {
			PowerConverter other = (PowerConverter) converter;
			if(this.base == other.base) {
				return composeSameBaseNonIdentity(other);
			}
		}
		if(converter instanceof RationalConverter) {
			return toRationalConverter().concatenate((RationalConverter) converter);
		}
		return super.concatenate(converter);
	}

	@Override
	public List<? extends UnitConverter> getConversionSteps() {
		return Collections.singletonList(this);
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
		if (obj instanceof PowerConverter) {
			PowerConverter other = (PowerConverter) obj;
			return this.base == other.base && this.exponent == other.exponent;
		}
		return false;
	}

	@Override
	public final String toString() {
		return "PowerConverter(" + base + "^" + exponent + ")";
	}
	
	@Override
	public int compareTo(UnitConverter o) {
		if (this == o) {
			return 0;
		}
		if(this.isIdentity() && o.isIdentity()) {
			return 0;
		}
		if (o instanceof PowerConverter) {
			PowerConverter other = (PowerConverter) o;
			int c = Integer.compare(base, other.base);
			if(c!=0) {
				return c;
			}
			return Integer.compare(exponent, other.exponent);
		}
		return this.getClass().getName().compareTo(o.getClass().getName());
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
	
	// -- HELPER
	
	private PowerConverter composeSameBaseNonIdentity(PowerConverter other) {
		// no check for identity required
		return new PowerConverter(this.base, this.exponent + other.exponent);
	}
	
	public RationalConverter toRationalConverter() {
		if(isIdentity()) {
			throw new IllegalArgumentException("can not convert identity operator to RationalConverter");
		}
		return exponent>0
				? new RationalConverter(BigInteger.valueOf(base).pow(exponent), BigInteger.ONE)
				: new RationalConverter(BigInteger.ONE, BigInteger.valueOf(base).pow(-exponent));
	}

}
