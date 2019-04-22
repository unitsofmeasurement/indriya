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

import tech.units.indriya.function.Calculus;

/**
 * {@link NumberSystem} implementation to support Java's built-in {@link Number}s and the
 * {@link RationalNumber} type.   
 * 
 * @author Andi Huber
 * @since 2.0
 */
public class DefaultNumberSystem implements NumberSystem {

    @Override
    public Number add(Number x, Number y) {
        
        if(isInteger(x) && isInteger(y)) {
            
            // +1 carry, not including sign
            int total_bits_required = Math.max(bitLengthOfInteger(x), bitLengthOfInteger(y)) + 1; 
            
            if(total_bits_required<63) {
                return longValueOfIntegerExact(x) + longValueOfIntegerExact(y);
            }
            
            return integerToBigInteger(x).add(integerToBigInteger(y));
            
        }
        
        if((x instanceof RationalNumber) && (y instanceof RationalNumber)) {
            return ((RationalNumber) x).add((RationalNumber) y);
        }
        
        // TODO[220] certainly this can be optimized
        
        return toBigDecimal(x).add(toBigDecimal(y));
    }

    @Override
    public Number subtract(Number x, Number y) {
        return add(x, negate(y));
    }

    @Override
    public Number multiply(Number x, Number y) {
        
        if(isInteger(x) && isInteger(y)) {
            
            int total_bits_required = bitLengthOfInteger(x) + bitLengthOfInteger(y); // not including sign
            
            if(total_bits_required<63) {
                return longValueOfIntegerExact(x) * longValueOfIntegerExact(y);
            }
            
            return integerToBigInteger(x).multiply(integerToBigInteger(y));
            
        }
        
        if((x instanceof RationalNumber) && (y instanceof RationalNumber)) {
            return ((RationalNumber) x).multiply((RationalNumber) y);
        }
        
        // TODO[220] certainly this can be optimized
        
        return toBigDecimal(x).multiply(toBigDecimal(y));
    }

    @Override
    public Number divide(Number x, Number y) {
        return multiply(x, reciprocal(y));
    }

    @Override
    public Number reciprocal(Number number) {
        if(isInteger(number)) {
            return RationalNumber.of(BigInteger.ONE, integerToBigInteger(number));
        }
        if(number instanceof BigDecimal) {
            return BigDecimal.ONE.divide(((BigDecimal) number), Calculus.MATH_CONTEXT);
        }
        if(number instanceof RationalNumber) {
            return ((RationalNumber) number).reciprocal();
        }
        if(number instanceof Double) {
            return BigDecimal.ONE.divide(BigDecimal.valueOf((double)number), Calculus.MATH_CONTEXT);
        }
        if(number instanceof Float) {
            return BigDecimal.ONE.divide(BigDecimal.valueOf(number.doubleValue()), Calculus.MATH_CONTEXT);
        }
        throw unsupportedNumberType(number);
    }

    @Override
    public Number negate(Number number) {
        if(number instanceof BigInteger) {
            return ((BigInteger) number).negate();
        }
        if(number instanceof BigDecimal) {
            return ((BigDecimal) number).negate();
        }
        if(number instanceof RationalNumber) {
            return ((RationalNumber) number).negate();
        }
        if(number instanceof Double) {
            return -((double)number);
        }
        if(number instanceof Float) {
            return -((float)number);
        }
        if(number instanceof Long) {
            return -((long)number);
        }
        if(number instanceof Integer) {
            return -((int)number);
        }
        if(number instanceof Short) {
            return -((short)number);
        }
        if(number instanceof Byte) {
            return -((byte)number);
        }
        throw unsupportedNumberType(number);
    }
    
    @Override
    public Number narrow(Number number) {
        // TODO[220]
        return number;
    }
    
    // -- HELPER
    
    private IllegalArgumentException unsupportedNumberType(Number number) {
        final String msg = String.format("Unsupported number type '%s' in number system '%s'",
                number.getClass().getName(),
                this.getClass().getName());
        
        return new IllegalArgumentException(msg);
    }
    
    private boolean isInteger(Number number) {
        if(number instanceof Long || 
                number instanceof Integer || 
                number instanceof BigInteger || 
                number instanceof Short || 
                number instanceof Byte) {
            return true;
        }
        return false;
    }
    
    private int bitLengthOfInteger(Number number) {
        if(number instanceof BigInteger) {
            return ((BigInteger) number).bitLength();
        }
        long long_value = number.longValue(); 
        
        if(long_value == Long.MIN_VALUE) {
            return 63;
        } else {
            return Long.bitCount(Math.abs(long_value));
        }
    }
    
    /**
     * Converts this integer number to a long, checking for lost information. If the value 
     * of this number is out of the range of the long type, then an ArithmeticException is thrown.
     * @param number
     */
    private long longValueOfIntegerExact(Number number) {
        if(number instanceof BigInteger) {
            return ((BigInteger) number).longValueExact();
        }
        return number.longValue();
    }
    
    private BigInteger integerToBigInteger(Number number) {
        if(number instanceof BigInteger) {
            return (BigInteger) number;
        }
        return BigInteger.valueOf(number.longValue());
    }
    
    private BigDecimal toBigDecimal(Number number) {
        if(number instanceof BigDecimal) {
            return (BigDecimal) number;
        }
        if(number instanceof BigInteger) {
            return new BigDecimal((BigInteger) number);
        }
        if(number instanceof Long || 
                number instanceof Integer || 
                number instanceof Short || 
                number instanceof Byte) {
            return BigDecimal.valueOf(number.longValue());
        }
        if(number instanceof Double || number instanceof Float) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        if(number instanceof RationalNumber) {
            return ((RationalNumber) number).bigDecimalValue();
        }
        throw unsupportedNumberType(number);
    }
    

}
