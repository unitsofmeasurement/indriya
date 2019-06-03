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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Represents a rational number {@code dividend/divisor} with {@code dividend}
 * and {@code divisor} being integer numbers.
 * <p>
 * This implementation uses {@link BigInteger} to represent 'dividend' and
 * 'divisor'.
 * 
 * @author Andi Huber
 * @author Werner Keil
 * @since 2.0
 */
public class RationalNumber extends Number {

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

    private final static String DIVIDE_CHARACTER = " ÷ ";

    public final static RationalNumber ZERO = ofInteger(BigInteger.ZERO);
    public final static RationalNumber ONE = ofInteger(BigInteger.ONE);

    /**
     * Returns a {@code RationalNumber} with divisor <i>ONE</i>. In other words, returns
     * a {@code RationalNumber} that represents given integer {@code number}. 
     * @param number
     * @return number/1
     * @throws NullPointerException - if number is {@code null}
     */
    public static RationalNumber ofInteger(long number) {
        return ofInteger(BigInteger.valueOf(number));
    }

    /**
     * Returns a {@code RationalNumber} with divisor <i>ONE</i>. In other words, returns
     * a {@code RationalNumber} that represents given integer {@code number}. 
     * @param number
     * @return number/1
     * @throws NullPointerException - if number is {@code null}
     */
    public static RationalNumber ofInteger(BigInteger number) {
        Objects.requireNonNull(number);
        return new RationalNumber(number.signum(), number.abs(), BigInteger.ONE);
    }

    /**
     * Returns a {@code RationalNumber} that represents the division {@code dividend/divisor}.
     * @param dividend 
     * @param divisor
     * @return dividend/divisor
     * @throws IllegalArgumentException
     *           if <code>divisor = 0</code>
     */
    public static RationalNumber of(long dividend, long divisor) {
        return of(BigInteger.valueOf(dividend), BigInteger.valueOf(divisor));
    }

    /**
     * Returns a {@code RationalNumber} that represents the division {@code dividend/divisor}.
     * @param dividend 
     * @param divisor
     * @return dividend/divisor
     * @throws IllegalArgumentException
     *           if <code>divisor = 0</code>
     * @throws NullPointerException - if dividend is {@code null} or divisor is {@code null}
     * 
     * @implNote this implementation stores dividend and divisor after canceling down from given parameters
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

    // hidden constructor, that expects non-negative dividend and positive divisor, these already canceled down
    private RationalNumber(int signum, BigInteger absDividend, BigInteger absDivisor) {
        this.signum = signum;
        this.absDividend = absDividend;
        this.absDivisor = absDivisor;
        this.hashCode = Objects.hash(signum, absDividend, absDivisor);
        this.isInteger = BigInteger.ONE.equals(absDivisor);
    }

    /**
     * For a non-negative rational number, returns a non-negative dividend. Otherwise 
     * returns a negative <i>dividend</i>. In other words, by convention, the integer returned 
     * includes the sign of this {@code RationalNumber}, whereas @link {@link #getDivisor()} 
     * does not and is always non-negative.
     * @return sign(a/b) * abs(a), (given rational number a/b) 
     */
    public BigInteger getDividend() {
        return signum < 0 ? absDividend.negate() : absDividend;
    }

    /**
     * By convention, returns a non-negative <i>divisor</i>.
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
     * @return this {@code RationalNumber} converted to {@link BigDecimal} representation
     * @implNote the conversion calculation is done lazily and thread-safe   
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
     * Returns a new instance of {@code RationalNumber} representing the addition {@code this + that}.
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
     * Returns a new instance of {@code RationalNumber} representing the subtraction {@code this - that}.
     * @param that
     * @return this - that
     */
    public RationalNumber subtract(RationalNumber that) {
        return add(that.negate());
    }

    /**
     * Returns a new instance of {@code RationalNumber} representing the multiplication {@code this * that}.
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

        return new RationalNumber(productSignum, 
                ac.divide(gcd),
                bd.divide(gcd)
                );
    }

    /**
     * Returns a new instance of {@code RationalNumber} representing the division {@code this / that}.
     * @param that
     * @return this / that
     */
    public RationalNumber divide(RationalNumber that) {
        return multiply(that.reciprocal());
    }

    /**
     * Returns a new instance of {@code RationalNumber} representing the negation of {@code this}.
     * @return -this
     */
    public RationalNumber negate() {
        return new RationalNumber(-signum, absDividend, absDivisor);
    }

    /**
     * Returns a new instance of {@code RationalNumber} representing the reciprocal of {@code this}.
     * @return 1/this
     */
    public RationalNumber reciprocal() {
        return new RationalNumber(signum, absDivisor, absDividend);
    }
    
    /**
     * Returns a new instance of {@code RationalNumber} representing the reciprocal of {@code this}.
     * @param exponent
     * @return this^exponent
     */
    public RationalNumber pow(int exponent) {
        if(exponent==0) {
            if(signum==0) {
                throw new ArithmeticException("0^0 is not defined");
            }
            return ONE; // x^0 == 1, for any x!=0
        }
        if(signum==0) {
            return ZERO; 
        }
        
        final boolean isExponentEven = (exponent & 1) == 0;
        final int newSignum;
        if(signum<0) {
            newSignum = isExponentEven ? 1 : -1;
        } else {
            newSignum = 1;
        }
        
        if(exponent>0) {
            return new RationalNumber(newSignum, absDividend.pow(exponent), absDivisor.pow(exponent));    
        } else {
            return new RationalNumber(newSignum, absDivisor.pow(exponent), absDividend.pow(exponent));
        }
        
    }

    /**
     * Compares two {@code RationalNumber} values numerically.
     *
     * @param that
     * @return the value {@code 0} if {@code this} equals (numerically) {@code that};
     *         a value less than {@code 0} if {@code this < that}; and
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

    /**
     * Default {@code Java Object} equality check. To check for numerical equality use 
     * {@link #compareTo(RationalNumber)} instead.  
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return hashCode;
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

    @Override
    public String toString() {
        if(signum==0) {
            return "0";
        }
        if(isInteger) {
            return getDividend().toString(); // already includes the sign
        }
        return "(" + getDividend() + DIVIDE_CHARACTER + absDivisor + ")";
    }


}
