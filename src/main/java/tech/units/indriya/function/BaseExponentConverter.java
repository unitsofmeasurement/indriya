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

import javax.measure.UnitConverter;
import javax.measure.spi.Prefix;

import tech.units.indriya.AbstractConverter.Pair;

public class BaseExponentConverter implements UnitConverter {

	private final int base;
	private final int exponent;

	public static UnitConverter of(Prefix prefix) {
		return new BaseExponentConverter(prefix.getBase(), prefix.getExponent());
	}

	protected BaseExponentConverter(int base, int exponent) {
		if(base == 0 && exponent == 0) {
			throw new IllegalArgumentException("base and exponent can not be both zero at the same time (0^0 is undefined)");
		}
		this.base = base;
		this.exponent = exponent;
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
	public UnitConverter inverse() {
		return isIdentity() ? this : new BaseExponentConverter(base, -exponent);
	}

	@Override
	public Number convert(Number value) {

		if(isIdentity()) {
			return value;
		}

		//[ahuber] at this point we know exponent is not zero

		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}

		if (value instanceof BigDecimal) {
			//[ahuber] exact number representation of factor 
			final BigDecimal bdecFactor = new BigDecimal(BigInteger.valueOf(base).pow(Math.abs(exponent)));
			final BigDecimal bdecValue = (BigDecimal) value;

			//[ahuber] thats where we are loosing 'exactness'
			return exponent>0 
					? bdecValue.multiply(bdecFactor, MathContext.DECIMAL128)
							: bdecValue.divide(bdecFactor, MathContext.DECIMAL128);
		}
		if (value instanceof BigInteger) {
			//[ahuber] exact number representation of factor 
			final BigInteger bintFactor = BigInteger.valueOf(base).pow(Math.abs(exponent));

			if(exponent>0) {
				return bintFactor.multiply((BigInteger) value);
			}

			//[ahuber] we try to return an exact BigInteger if possible
			final BigInteger[] divideAndRemainder = bintFactor.divideAndRemainder(bintFactor);
			final BigInteger divisionResult = divideAndRemainder[0]; 
			final BigInteger divisionRemainder = divideAndRemainder[1];

			if(BigInteger.ZERO.compareTo(divisionRemainder) == 0) {
				return divisionResult;
			}

			//[ahuber] fallback to BigDecimal, thats where we are loosing 'exactness'
			final BigDecimal bdecFactor = new BigDecimal(bintFactor);
			final BigDecimal bdecValue = new BigDecimal((BigInteger) value);

			return bdecValue.divide(bdecFactor, MathContext.DECIMAL128);
		}

		return convert(value.doubleValue());
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
		return new Pair(this, converter);
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
		if (obj instanceof BaseExponentConverter) {
			BaseExponentConverter other = (BaseExponentConverter) obj;
			return this.base == other.base && this.exponent == other.exponent;
		}
		return false;
	}

}
