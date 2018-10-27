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
import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import javax.measure.UnitConverter;

import tech.units.indriya.AbstractConverter;
import tech.uom.lib.common.function.ValueSupplier;

/**
 * <p>
 * This class represents a converter multiplying numeric values by an exact scaling factor (represented as the quotient of two <code>BigInteger</code>
 * numbers).
 * </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.0, Oct 11, 2016
 * @since 1.0
 */
public final class RationalConverter extends AbstractConverter implements ValueSupplier<Double>, Supplier<Double>, DoubleSupplier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3563384008357680074L;

	/**
	 * Holds the converter dividend.
	 */
	private final BigInteger dividend;

	/**
	 * Holds the converter divisor (always positive).
	 */
	private final BigInteger divisor;

	/**
	 * Creates a rational converter with the specified dividend and divisor.
	 *
	 * @param dividend
	 *          the dividend.
	 * @param divisor
	 *          the positive divisor.
	 * @throws IllegalArgumentException
	 *           if <code>divisor &lt;= 0</code>
	 */
	public RationalConverter(BigInteger dividend, BigInteger divisor) {
		if (divisor.compareTo(BigInteger.ZERO) <= 0)
			throw new IllegalArgumentException("Negative or zero divisor");
		this.dividend = dividend; // Exact conversion.
		this.divisor = divisor; // Exact conversion.
	}

	/**
	 * Convenience method equivalent to <code>new RationalConverter(BigInteger.valueOf(dividend), BigInteger.valueOf(divisor))</code>
	 *
	 * @param dividend
	 *          the dividend.
	 * @param divisor
	 *          the positive divisor.
	 * @throws IllegalArgumentException
	 *           if <code>divisor &lt;= 0</code>
	 * @throws IllegalArgumentException
	 *           if <code>dividend == divisor</code>
	 */
	public RationalConverter(long dividend, long divisor) {
		this(BigInteger.valueOf(dividend), BigInteger.valueOf(divisor));
	}

	/**
	 * Convenience method equivalent to <code>new RationalConverter(dividend, divisor)</code>
	 *
	 * @param dividend
	 *          the dividend.
	 * @param divisor
	 *          the positive divisor.
	 * @throws IllegalArgumentException
	 *           if <code>divisor &lt;= 0</code>
	 * @throws IllegalArgumentException
	 *           if <code>dividend == divisor</code>
	 */
	public static RationalConverter of(BigInteger dividend, BigInteger divisor) {
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
	 *           if <code>divisor &lt;= 0</code>
	 * @throws IllegalArgumentException
	 *           if <code>dividend == divisor</code>
	 */
	public static RationalConverter of(long dividend, long divisor) {
		return new RationalConverter(dividend, divisor);
	}

	/**
	 * Convenience method equivalent to <code>new RationalConverter(BigDecimal.valueOf(dividend).toBigInteger(), 
	 *    BigDecimal.valueOf(divisor).toBigInteger())</code>
	 *
	 * @param dividend
	 *          the dividend.
	 * @param divisor
	 *          the positive divisor.
	 * @throws IllegalArgumentException
	 *           if <code>divisor &lt;= 0</code>
	 * @throws IllegalArgumentException
	 *           if <code>dividend == divisor</code>
	 */
	public static RationalConverter of(double dividend, double divisor) {
		return new RationalConverter(BigDecimal.valueOf(dividend).toBigInteger(), BigDecimal.valueOf(divisor).toBigInteger());
	}

	/**
	 * Returns the integer dividend for this rational converter.
	 *
	 * @return this converter dividend.
	 */
	public BigInteger getDividend() {
		return dividend;
	}

	/**
	 * Returns the integer (positive) divisor for this rational converter.
	 *
	 * @return this converter divisor.
	 */
	public BigInteger getDivisor() {
		return divisor;
	}

	@Override
	public double convertWhenNotIdentity(double value) {
		return value * toDouble(dividend) / toDouble(divisor);
	}

	// Optimization of BigInteger.doubleValue() (implementation too
	// inneficient).
	private static double toDouble(BigInteger integer) {
		return (integer.bitLength() < 64) ? integer.longValue() : integer.doubleValue();
	}

	@Override
	protected Number convertWhenNotIdentity(BigInteger value, MathContext ctx) {
		BigInteger newDividend = dividend.multiply(value);

		//[ahuber] we try to return an exact BigInteger if possible
		final BigInteger[] divideAndRemainder = newDividend.divideAndRemainder(divisor);
		final BigInteger divisionResult = divideAndRemainder[0]; 
		final BigInteger divisionRemainder = divideAndRemainder[1];

		if(BigInteger.ZERO.compareTo(divisionRemainder) == 0) {
			return divisionResult;
		}
		//[ahuber] fallback to BigDecimal, thats where we are loosing 'exactness'	
		return convertWhenNotIdentity(new BigDecimal(value), ctx);
	}

	@Override
	public BigDecimal convertWhenNotIdentity(BigDecimal value, MathContext ctx) throws ArithmeticException {
		BigDecimal decimalDividend = new BigDecimal(dividend, 0);
		BigDecimal decimalDivisor = new BigDecimal(divisor, 0);
		return value.multiply(decimalDividend, ctx).divide(decimalDivisor, ctx);
	}

	@Override
	public boolean isIdentity() {
		return dividend.equals(divisor);
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
			return (AbstractConverter) composeSameType((RationalConverter) that); 
		}
		if (that instanceof PowerOfIntConverter) {
			return (AbstractConverter) composeSameType(((PowerOfIntConverter) that).toRationalConverter()); 
		}
		throw new IllegalStateException(String.format(
				"%s.simpleCompose() not handled for converter %s", 
				this, that));
	}


	@Override
	public RationalConverter inverseWhenNotIdentity() {
		return dividend.signum() == -1 ? new RationalConverter(getDivisor().negate(), getDividend().negate()) : new RationalConverter(getDivisor(),
				getDividend());
	}

	@Override
	public final String transformationLiteral() {
		return String.format("x -> x * (%s)/(%s)", dividend, divisor);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof RationalConverter) {

			RationalConverter that = (RationalConverter) obj;
			return Objects.equals(dividend, that.dividend) && Objects.equals(divisor, that.divisor);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dividend, divisor);
	}

	@Override
	public boolean isLinear() {
		return true;
	}

	@Override
	public Double getValue() {
		return getAsDouble();
	}

	@Override
	public double getAsDouble() {
		return toDouble(dividend) / toDouble(divisor);
	}

	@Override
	public Double get() {
		return getValue();
	}

	@Override
	public int compareTo(UnitConverter o) {
		if (this == o) {
			return 0;
		}
		if (o instanceof RationalConverter) {
			return getValue().compareTo(((RationalConverter) o).getValue());
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
