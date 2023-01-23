/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2023, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import java.math.BigInteger;
import java.util.Objects;
import java.util.function.DoubleSupplier;

import javax.measure.UnitConverter;

import tech.units.indriya.internal.function.Calculator;

/**
 * <p>
 * This class represents a converter multiplying numeric values by an exact scaling factor (represented as the quotient of two <code>BigInteger</code>
 * numbers).
 * </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:werner@units.tech">Werner Keil</a>
 * @author Andi Huber
 * @version 2.0, October 8, 2020
 * @since 1.0
 */
public final class RationalConverter extends AbstractConverter 
implements MultiplyConverter {

	/**
     * 
     */
    private static final long serialVersionUID = -9192231963353351648L;

	/**
     * Holds the scale factor.
     */
	private final RationalNumber factor;

	/**
     * Creates a rational converter with the specified scale factor.
     *
     * @param factor
     *          the scale factor.
     * @throws NullPointerException
     *           if factor is {@code null}
     */
    RationalConverter(RationalNumber factor) {
        Objects.requireNonNull(factor);
        this.factor = factor;
    }
    
	/**
	 * Creates a rational converter with the specified dividend and divisor.
	 *
	 * @param dividend
	 *          the dividend.
	 * @param divisor
	 *          the non-zero divisor.
	 * @throws IllegalArgumentException
	 *           if <code>divisor = 0</code>
	 * @throws NullPointerException
	 *           if dividend is {@code null} or divisor is {@code null}
	 */
	RationalConverter(BigInteger dividend, BigInteger divisor) {
	    factor = RationalNumber.of(dividend, divisor);
	}

	/**
	 * Convenience method equivalent to <code>new RationalConverter(BigInteger.valueOf(dividend), BigInteger.valueOf(divisor))</code>
	 *
	 * @param dividend
	 *          the dividend.
	 * @param divisor
	 *          the positive divisor.
	 * @throws IllegalArgumentException
	 *           if <code>divisor = 0</code>
	 */
	RationalConverter(long dividend, long divisor) {
	    factor = RationalNumber.of(dividend, divisor);
	}

	/**
     * Creates a rational converter with the specified scale factor.
     *
     * @param factor
     *          the scale factor.
     * @throws NullPointerException
     *           if factor is {@code null}
     */
    static RationalConverter of(RationalNumber factor) {
        return new RationalConverter(factor);
    }
	
	/**
	 * Convenience method equivalent to <code>new RationalConverter(dividend, divisor)</code>
	 *
	 * @param dividend
	 *          the dividend.
	 * @param divisor
	 *          the positive divisor.
	 * @throws IllegalArgumentException
	 *           if <code>divisor = 0</code>
     * @throws NullPointerException
     *           if dividend is {@code null} or divisor is {@code null}
	 */
	static RationalConverter of(BigInteger dividend, BigInteger divisor) {
		return new RationalConverter(dividend, divisor);
	}

	/**
	 * Convenience method equivalent to <code>new RationalConverter(dividend, divisor)</code>
	 *
	 * @param dividend
	 *          the dividend.
	 * @param divisor
	 *          the positive divisor.
	 * @throws IllegalArgumentException
	 *           if <code>divisor = 0</code>
	 */
	static RationalConverter of(long dividend, long divisor) {
		return new RationalConverter(dividend, divisor);
	}

	/**
	 * Returns the integer dividend for this rational converter.
	 *
	 * @return this converter dividend.
	 */
	public BigInteger getDividend() {
		return factor.getDividend();
	}

	/**
	 * Returns the integer (positive) divisor for this rational converter.
	 *
	 * @return this converter divisor.
	 */
	public BigInteger getDivisor() {
		return factor.getDivisor();
	}

    @Override
    protected Number convertWhenNotIdentity(Number value) {
        return Calculator.of(factor)
              .multiply(value)
              .peek();
    }
	
	@Override
	public boolean isIdentity() {
		return factor.compareTo(RationalNumber.ONE)==0;
	}

	@Override
	protected boolean canReduceWith(AbstractConverter that) {
		if (that instanceof RationalConverter) {
			return true; 
		}
		return that instanceof PowerOfIntConverter;
	}

	@Override
	protected AbstractConverter reduce(AbstractConverter that) {
		if (that instanceof RationalConverter) {
			return composeSameType((RationalConverter) that); 
		}
		if (that instanceof PowerOfIntConverter) {
			return composeSameType(((PowerOfIntConverter) that).toRationalConverter()); 
		}
		throw new IllegalStateException(String.format(
				"%s.simpleCompose() not handled for converter %s", 
				this, that));
	}


	@Override
	protected RationalConverter inverseWhenNotIdentity() {
		return RationalConverter.of(factor.reciprocal());
	}

	@Override
	protected final String transformationLiteral() {
		return String.format("x -> x * %s", factor);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof RationalConverter) {
			RationalConverter that = (RationalConverter) obj;
			return Objects.equals(this.factor, that.factor);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return factor.hashCode();
	}

	@Override
	public Number getValue() {
		return factor;
	}
	
	@Override
	public double getAsDouble() {
		return factor.doubleValue();
	}

	@Override
	public int compareTo(UnitConverter o) {
		if (this == o) {
			return 0;
		}
		if (o instanceof RationalConverter) {
		    RationalConverter that = (RationalConverter) o;
			return this.factor.compareTo(that.factor);
		}
		return this.getClass().getName().compareTo(o.getClass().getName());
	}

	// -- HELPER

	private AbstractConverter composeSameType(RationalConverter that) {
		BigInteger newDividend = this.getDividend().multiply(that.getDividend());
		BigInteger newDivisor = this.getDivisor().multiply(that.getDivisor());
		BigInteger gcd = newDividend.gcd(newDivisor);
		newDividend = newDividend.divide(gcd);
		newDivisor = newDivisor.divide(gcd);
		return (newDividend.equals(BigInteger.ONE) && newDivisor.equals(BigInteger.ONE)) 
				? IDENTITY 
						: new RationalConverter(newDividend, newDivisor);
	}
}
