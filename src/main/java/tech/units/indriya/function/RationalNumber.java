/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2025, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
import java.util.Objects;

/**
 * Represents a rational number {@code dividend/divisor} with {@code dividend}
 * and {@code divisor} being integer numbers.
 * <p>
 * @implSpec
 * This implementation uses {@link BigInteger} to represent 'dividend' and
 * 'divisor'.
 * 
 * @author Andi Huber
 * @author Werner Keil
 * @version 1.2, June 21, 2019
 * @since 2.0
 */
public final class RationalNumber extends Number {

	private static final long serialVersionUID = 1L;
	private final Object $lock1 = new Object[0]; // serializable lock for 'divisionResult'
	private final Object $lock2 = new Object[0]; // serializable lock for 'longValue'

	private final int signum;
	private final BigInteger absDividend;
	private final BigInteger absDivisor;
	private final int hashCode;
	private final boolean isInteger;

	private transient BigDecimal divisionResult;
	private transient Long longValue;

	/**
	 * The default {@code DIVISION_CHARACTER} is ÷ which (on Windows) can by typed
	 * using Alt+ 246.
	 * <p>
	 * Note: Number parsing will fail if this is a white-space character.
	 */
	public static char DIVISION_CHARACTER = '÷'; // Alt+ 246

	public final static RationalNumber ZERO = ofInteger(BigInteger.ZERO);
	public final static RationalNumber ONE = ofInteger(BigInteger.ONE);

	/**
	 * Returns a {@code RationalNumber} with divisor <i>ONE</i>. In other words,
	 * returns a {@code RationalNumber} that represents given integer
	 * {@code number}.
	 * 
	 * @param number
	 * @return number/1
	 * @throws NullPointerException - if number is {@code null}
	 */
	public static RationalNumber ofInteger(long number) {
		return ofInteger(BigInteger.valueOf(number));
	}

	/**
	 * Returns a {@code RationalNumber} with divisor <i>ONE</i>. In other words,
	 * returns a {@code RationalNumber} that represents given integer
	 * {@code number}.
	 * 
	 * @param number
	 * @return number/1
	 * @throws NullPointerException - if number is {@code null}
	 */
	public static RationalNumber ofInteger(BigInteger number) {
		Objects.requireNonNull(number);
		return new RationalNumber(number.signum(), number.abs(), BigInteger.ONE);
	}

	/**
	 * Returns a {@code RationalNumber} that represents the division
	 * {@code dividend/divisor}.
	 * 
	 * @param dividend
	 * @param divisor
	 * @return dividend/divisor
	 * @throws IllegalArgumentException if <code>divisor = 0</code>
	 */
	public static RationalNumber of(long dividend, long divisor) {
		return of(BigInteger.valueOf(dividend), BigInteger.valueOf(divisor));
	}
	
	/**
     * Returns a {@code RationalNumber} that represents the given double precision 
     * {@code number}, with an accuracy equivalent to {@link BigDecimal#valueOf(double)}.
     * 
	 * @param number
	 */
	public static RationalNumber of(double number) {
	    final BigDecimal decimalValue = BigDecimal.valueOf(number);
	    return of(decimalValue);
	}

	/**
	 * Returns a {@code RationalNumber} that represents the given BigDecimal decimalValue.
	 * 
	 * @param decimalValue
	 */
	public static RationalNumber of(BigDecimal decimalValue) {
	    Objects.requireNonNull(decimalValue);
	    
	    final int scale = decimalValue.scale();
        
        if(scale<=0) {
            return ofInteger(decimalValue.toBigIntegerExact()); 
        }
        
        final BigInteger dividend = decimalValue.unscaledValue();
        final BigInteger divisor = BigInteger.TEN.pow(scale);
        
        return of(dividend, divisor);
	}

	/**
	 * Returns a {@code RationalNumber} that represents the division
	 * {@code dividend/divisor}.
	 * 
	 * <dl>
     * <dt><span class="strong">Implementation Note:</span></dt><dd>this implementation stores dividend and divisor after canceling
	 *           down from given parameters</dd>
     * </dl>
	 * 
	 * @param dividend the dividend
	 * @param divisor the divisor
	 * @return dividend/divisor
	 * @throws IllegalArgumentException if <code>divisor = 0</code>
	 * @throws NullPointerException     - if dividend is {@code null} or divisor is
	 *                                  {@code null}
	 */
	public static RationalNumber of(BigInteger dividend, BigInteger divisor) {
		Objects.requireNonNull(dividend);
		Objects.requireNonNull(divisor);

		if (BigInteger.ONE.equals(divisor)) {
			return ofInteger(dividend);
		}

		if (BigInteger.ZERO.equals(divisor)) {
			throw new IllegalArgumentException("cannot initalize a rational number with divisor equal to ZERO");
		}

		final int signumDividend = dividend.signum();
		final int signumDivisor = divisor.signum();
		final int signum = signumDividend * signumDivisor;

		if (signum == 0) {
			return ZERO;
		}

		final BigInteger absDividend = dividend.abs();
		final BigInteger absDivisor = divisor.abs();

		// cancel down
		final BigInteger gcd = absDividend.gcd(absDivisor);
		return new RationalNumber(signum, absDividend.divide(gcd), absDivisor.divide(gcd));
	}

	// hidden constructor, that expects non-negative dividend and positive divisor,
	// these already canceled down
	private RationalNumber(int signum, BigInteger absDividend, BigInteger absDivisor) {
		this.signum = signum;
		this.absDividend = absDividend;
		this.absDivisor = absDivisor;
		this.hashCode = Objects.hash(signum, absDividend, absDivisor);
		this.isInteger = BigInteger.ONE.equals(absDivisor);
	}

	/**
	 * For a non-negative rational number, returns a non-negative dividend.
	 * Otherwise returns a negative <i>dividend</i>. In other words, by convention,
	 * the integer returned includes the sign of this {@code RationalNumber},
	 * whereas @link {@link #getDivisor()} does not and is always non-negative.
	 * 
	 * @return sign(a/b) * abs(a), (given rational number a/b)
	 */
	public BigInteger getDividend() {
		return signum < 0 ? absDividend.negate() : absDividend;
	}

	/**
	 * By convention, returns a non-negative <i>divisor</i>.
	 * 
	 * @return abs(b), (given rational number a/b)
	 */
	public BigInteger getDivisor() {
		return absDivisor;
	}

	/**
	 * @return whether this {@code RationalNumber} represents an integer number
	 */
	public boolean isInteger() {
		return isInteger;
	}

	/**
	 * 
	 * @return the sign of this {@code RationalNumber}: -1, 0 or +1
	 */
	public int signum() {
		return signum;
	}

	/**
	 * The {@link BigDecimal} representation of this {@code RationalNumber}.
	 * <dl>
     * <dt><span class="strong">Implementation Note:</span></dt><dd>the conversion calculation is done lazily and thread-safe</dd>           
     * </dl>
     * @return this {@code RationalNumber} converted to {@link BigDecimal}
	 *         representation 
	 */
	public BigDecimal bigDecimalValue() {
		synchronized ($lock1) {
			if (divisionResult == null) {
				divisionResult = new BigDecimal(absDividend).divide(new BigDecimal(absDivisor), Calculus.MATH_CONTEXT);
				if (signum < 0) {
					divisionResult = divisionResult.negate();
				}
			}
		}
		return divisionResult;
	}

	/**
	 * Returns a new instance of {@code RationalNumber} representing the addition
	 * {@code this + that}.
	 * 
	 * @param that
	 * @return this + that
	 */
	public RationalNumber add(RationalNumber that) {

		// a/b + c/d = (ad + bc) / bd
		BigInteger a = this.absDividend;
		BigInteger b = this.absDivisor;
		BigInteger c = that.absDividend;
		BigInteger d = that.absDivisor;

		if (this.signum < 0) {
			a = a.negate();
		}
		if (that.signum < 0) {
			c = c.negate();
		}

		return of(a.multiply(d).add(b.multiply(c)), // (ad + bc)
				b.multiply(d) // bd
		);
	}

	/**
	 * Returns a new instance of {@code RationalNumber} representing the subtraction
	 * {@code this - that}.
	 * 
	 * @param that
	 * @return this - that
	 */
	public RationalNumber subtract(RationalNumber that) {
		return add(that.negate());
	}

	/**
	 * Returns a new instance of {@code RationalNumber} representing the
	 * multiplication {@code this * that}.
	 * 
	 * @param that
	 * @return this * that
	 */
	public RationalNumber multiply(RationalNumber that) {

		final int productSignum = this.signum * that.signum;
		if (productSignum == 0) {
			return ZERO;
		}

		// a/b * c/d = ac / bd
		final BigInteger a = this.absDividend;
		final BigInteger b = this.absDivisor;
		final BigInteger c = that.absDividend;
		final BigInteger d = that.absDivisor;

		final BigInteger ac = a.multiply(c);
		final BigInteger bd = b.multiply(d);

		// cancel down
		final BigInteger gcd = ac.gcd(bd);

		return new RationalNumber(productSignum, ac.divide(gcd), bd.divide(gcd));
	}

	/**
	 * Returns a new instance of {@code RationalNumber} representing the division
	 * {@code this / that}.
	 * 
	 * @param that
	 * @return this / that
	 */
	public RationalNumber divide(RationalNumber that) {
		return multiply(that.reciprocal());
	}

	/**
	 * Returns a new instance of {@code RationalNumber} representing the negation of
	 * {@code this}.
	 * 
	 * @return -this
	 */
	public RationalNumber negate() {
		return new RationalNumber(-signum, absDividend, absDivisor);
	}

	/**
	 * Returns a new instance of {@code RationalNumber} representing the reciprocal
	 * of {@code this}.
	 * 
	 * @return 1/this
	 */
	public RationalNumber reciprocal() {
		return new RationalNumber(signum, absDivisor, absDividend);
	}

	/**
	 * Returns a new instance of {@code RationalNumber} representing the reciprocal
	 * of {@code this}.
	 * 
	 * @param exponent
	 * @return this^exponent
	 */
	public RationalNumber pow(int exponent) {
		if (exponent == 0) {
			if (signum == 0) {
				throw new ArithmeticException("0^0 is not defined");
			}
			return ONE; // x^0 == 1, for any x!=0
		}
		if (signum == 0) {
			return ZERO;
		}

		final boolean isExponentEven = (exponent & 1) == 0;
		final int newSignum;
		if (signum < 0) {
			newSignum = isExponentEven ? 1 : -1;
		} else {
			newSignum = 1;
		}

		if (exponent > 0) {
			return new RationalNumber(newSignum, absDividend.pow(exponent), absDivisor.pow(exponent));
		} else {
			return new RationalNumber(newSignum, absDivisor.pow(exponent), absDividend.pow(exponent));
		}

	}

	/**
	 * Returns a {@code RationalNumber} whose value is the absolute value of this
	 * {@code RationalNumber}.
	 *
	 * @return {@code abs(this)}
	 */
	public RationalNumber abs() {
		return signum < 0 ? new RationalNumber(1, absDividend, absDivisor) : this;
	}

	/**
	 * Compares two {@code RationalNumber} values numerically.
	 *
	 * @param that
	 * @return the value {@code 0} if {@code this} equals (numerically)
	 *         {@code that}; a value less than {@code 0} if {@code this < that}; and
	 *         a value greater than {@code 0} if {@code this > that}
	 */
	public int compareTo(RationalNumber that) {

		final int comp = Integer.compare(this.signum, that.signum);
		if (comp != 0) {
			return comp;
		}
		if (comp == 0 && this.signum == 0) {
			return 0; // both are ZERO
		}

		// we have same signum

		// a/b > c/d <=> ad > bc

		final BigInteger a = this.absDividend;
		final BigInteger b = this.absDivisor;
		final BigInteger c = that.absDividend;
		final BigInteger d = that.absDivisor;

		final BigInteger ad = a.multiply(d);
		final BigInteger bc = b.multiply(c);

		final int absCompare = ad.compareTo(bc);

		return this.signum > 0 ? absCompare : -absCompare;
	}

	// -- NUMBER IMPLEMENTATION

	@Override
	public int intValue() {
		return (int) longValue();
	}

	@Override
	public long longValue() {
		// performance optimized version, rounding mode is FLOOR
		// equivalent to 'bigDecimalValue().longValue()';
		synchronized ($lock2) {
			if (longValue == null) {
				longValue = signum() < 0 ? absDividend.negate().divide(absDivisor).longValue()
						: absDividend.divide(absDivisor).longValue();
			}
		}
		return longValue;
	}

	@Override
	public float floatValue() {
		return (float) doubleValue();
	}

	@Override
	public double doubleValue() {
		return bigDecimalValue().doubleValue();
	}

	/**
	 * Lay out this {@code RationalNumber} into a {@code String}.
	 *
	 * @param useFractionalRepresentation {@code true} for fractional representation {@code 5÷3}; 
	 *         {@code false} for decimal {@code 1.66667}.
	 *         
	 * @return string with canonical string representation of this
	 *         {@code RationalNumber}
	 */
	private String layoutChars(boolean useFractionalRepresentation, char divisionCharacter) {
		if (signum == 0) {
			return "0";
		}
		if (isInteger) {
			return getDividend().toString(); // already includes the sign
		}
		if (useFractionalRepresentation) {
			return getDividend().toString() + divisionCharacter + absDivisor;
		} else {
			return String.valueOf(bigDecimalValue());
		}
	}

	@Override
	public String toString() {
		return layoutChars(false, DIVISION_CHARACTER);
	}

	/**
	 * Returns a string representation of this {@code RationalNumber}, using
	 * fractional notation, eg. {@code 5÷3} or {@code -5÷3}.
	 *
	 * @return string representation of this {@code RationalNumber}, using
	 *         fractional notation.
	 * @since 2.0
	 */
	public String toRationalString() {
		return layoutChars(true, DIVISION_CHARACTER);
	}
	
	/**
     * Returns a string representation of this {@code RationalNumber}, using
     * fractional notation, eg. {@code 5÷3} or {@code -5÷3}.
     * 
     * @param divisionCharacter the character to use instead of the default {@code ÷}
     * @return string representation of this {@code RationalNumber}, using
     *         fractional notation.
     * @since 2.0
     */
	public String toRationalString(char divisionCharacter) {
	    return layoutChars(true, divisionCharacter);
    }

	@Override
	public int hashCode() {
		return hashCode;
	}

	/**
     * Compares this RationalNumber with the specified Object for equality.
     *
     * @param  x Object to which this RationalNumber is to be compared.
     * @return {@code true} if and only if the specified Object is a
     *         RationalNumber whose value is numerically equal to this RationalNumber.
     */
	@Override
    public boolean equals(Object x) {
        // This test is just an optimization, which may or may not help
        if (x == this) {
            return true;
        }

        // no explicit null check required
        if (!(x instanceof RationalNumber)) { 
            return false; // will also return here if x is null 
        }

        final RationalNumber other = (RationalNumber) x;
        
//        // null checks not needed, since the constructor guards against dividend or divisor being null
//        boolean result = (
//                this.signum == other.signum &&
//                Objects.equals(this.absDividend, other.absDividend) && 
//                Objects.equals(this.absDivisor, other.absDivisor));
//        return result; This is still broken
        
        return Objects.equals(this.bigDecimalValue(), other.bigDecimalValue());
    }
	

}
