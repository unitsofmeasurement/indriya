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
package tech.units.indriya.internal.function.calc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import tech.units.indriya.function.Calculus;

/**
 * Represents a rational number {@code dividend/divisor} with {@code dividend} and 
 * {@code divisor} being integer numbers.
 * <p>
 * This implementation uses {@link BigInteger} to represent dividend and divisor.
 *  
 * @author Andi Huber
 * @since 2.0
 */
public class RationalNumber extends Number {
    
    private static final long serialVersionUID = 1L;
    private final Object $lock = new Object[0]; // serializable lock
    
    private final int signum;
    private final BigInteger absDividend;
    private final BigInteger absDivisor;
    private transient BigDecimal divisionResult;

    public final static RationalNumber ZERO = ofWholeNumber(BigInteger.ZERO);
    public final static RationalNumber ONE = ofWholeNumber(BigInteger.ONE);
    
    public static RationalNumber ofWholeNumber(BigInteger number) {
        Objects.requireNonNull(number);
        return new RationalNumber(number.signum(), number.abs(), BigInteger.ONE);
    }
    
    public static RationalNumber of(BigInteger dividend, BigInteger divisor) {
        Objects.requireNonNull(dividend);
        Objects.requireNonNull(divisor);
        
        if(BigInteger.ONE.equals(divisor)) {
            return ofWholeNumber(dividend);
        }
        
        if(BigInteger.ZERO.equals(divisor)) {
            throw new IllegalArgumentException(
                    "cannot initalize a rational number with divisor equal to ZERO");
        }
        
        final int signumDividend = dividend.signum();
        final int signumDivisor = divisor.signum();
        final int signum = signumDividend * signumDivisor;
        
        if(signum==0) {
            return ZERO;
        }
        
        final BigInteger absDividend = dividend.abs();
        final BigInteger absDivisor = divisor.abs();

        // cancel down
        final BigInteger gcd = absDividend.gcd(absDivisor);
        return new RationalNumber(signum, absDividend.divide(gcd), absDivisor.divide(gcd));
    }
    
    private RationalNumber(int signum, BigInteger absDividend, BigInteger absDivisor) {
        this.signum = signum;
        this.absDividend = absDividend;
        this.absDivisor = absDivisor;
    }

    public int signum() {
        return signum; 
    }
    
    public BigDecimal bigDecimalValue() {
        synchronized ($lock) {
            if(divisionResult==null) {
                divisionResult = Calculus.toBigDecimal(absDividend)
                        .divide(Calculus.toBigDecimal(absDivisor));
                
                if(signum<0) {
                    divisionResult = divisionResult.negate();
                }
            }    
        }
        return divisionResult;
    }
    
    public RationalNumber add(RationalNumber that) {
        
        // a/b + c/d = (ad + bc) / bd
        BigInteger a = this.absDividend;
        BigInteger b = this.absDivisor;
        BigInteger c = that.absDividend;
        BigInteger d = that.absDivisor;
        
        if(this.signum<0) {
            a = a.negate();
        }
        if(that.signum<0) {
            c = c.negate();
        }
        
        return of(
                a.multiply(d).add(b.multiply(c)), // (ad + bc)
                b.multiply(d) // bd
                );
    }
    
    public Number multiply(RationalNumber that) {
        
        final int productSignum = this.signum * that.signum;
        if(productSignum==0) {
            return ZERO;
        }
        
        // a/b * c/d = ac / bd
        final BigInteger a = this.absDividend;
        final BigInteger b = this.absDivisor;
        final BigInteger c = that.absDividend;
        final BigInteger d = that.absDivisor;
        
        return new RationalNumber(productSignum, 
                a.multiply(c), // bd 
                b.multiply(d) // bd
                );
    }
    
    public RationalNumber negate() {
        return new RationalNumber(-signum, absDividend, absDivisor);
    }
    
    public RationalNumber reciprocal() {
        return new RationalNumber(signum, absDivisor, absDividend);
    }
    
    // -- NUMBER IMPLEMENTATION
    
    @Override
    public int intValue() {
        return (int) longValue();
    }

    @Override
    public long longValue() {
        return bigDecimalValue().longValue();
    }

    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    @Override
    public double doubleValue() {
        return bigDecimalValue().doubleValue();
    }


}
