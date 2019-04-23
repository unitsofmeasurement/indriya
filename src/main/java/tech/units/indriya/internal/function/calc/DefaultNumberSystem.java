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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import tech.units.indriya.function.Calculus;
import tech.units.indriya.function.NumberSystem;
import tech.units.indriya.function.RationalNumber;

/**
 * {@link NumberSystem} implementation to support Java's built-in {@link Number}s and the
 * {@link RationalNumber} type.   
 * 
 * @author Andi Huber
 * @since 2.0
 */
public class DefaultNumberSystem implements NumberSystem {
    
    /**
     *  In order of increasing number type 'widening'.
     */
    private enum NumberType {
        
        // integer types
        BYTE_BOXED(true, Byte.class, (byte)1, (byte)0),
        SHORT_BOXED(true, Short.class, (short)1, (short)0),
        INTEGER_BOXED(true, Integer.class, 1, 0),
        INTEGER_ATOMIC(true, AtomicInteger.class, 1, 0),
        LONG_BOXED(true, Long.class, 1L, 0L),
        LONG_ATOMIC(true, AtomicLong.class, 1L, 0),
        BIG_INTEGER(true, BigInteger.class, BigInteger.ONE, BigInteger.ZERO),
        
        // rational types
        RATIONAL(false, RationalNumber.class, RationalNumber.ONE, RationalNumber.ZERO),
        
        // fractional types
        FLOAT_BOXED(false, Float.class, 1.f, 0.f),
        DOUBLE_BOXED(false, Double.class, 1.d, 0.d),
        BIG_DECIMAL(false, BigDecimal.class, BigDecimal.ONE, BigDecimal.ZERO),
        
        ;
        private final boolean integerOnly;
        private final Class<? extends Number> type;
        private final Number one;
        private final Number zero;
        
        private NumberType(boolean integerOnly, Class<? extends Number> type, 
                Number one, Number zero) {
            
            this.integerOnly = integerOnly;
            this.type = type;
            this.one = one;
            this.zero = zero;
        }

        public boolean isIntegerOnly() {
            return integerOnly;
        }
        
        public Class<? extends Number> getType() {
            return type;
        }

        // 'hardcoded' for performance reasons
        static NumberType valueOf(Number number) {
            if(number instanceof Long) {
                return LONG_BOXED; 
            }
            if(number instanceof AtomicLong) {
                return LONG_ATOMIC; 
            }
            if(number instanceof Integer) {
                return INTEGER_BOXED;
            }
            if(number instanceof AtomicInteger) {
                return INTEGER_ATOMIC;
            }
            if(number instanceof Double) {
                return DOUBLE_BOXED;
            }
            if(number instanceof Short) {
                return SHORT_BOXED;
            }
            if(number instanceof Byte) {
                return BYTE_BOXED;
            }
            if(number instanceof Float) {
                return FLOAT_BOXED;
            }
            if(number instanceof BigDecimal) {
                return BIG_DECIMAL;
            }
            if(number instanceof BigInteger) {
                return BIG_INTEGER;
            }
            if(number instanceof RationalNumber) {
                return RATIONAL;
            }
            final String msg = String.format("Unsupported number type '%s'",
                    number.getClass().getName());
            throw new IllegalArgumentException(msg);
        }
        
    }

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
        
        // TODO[220] certainly this can be further optimized
        
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
        
        // TODO[220] certainly this can be further optimized
        
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
    
    @Override
    public int compare(Number x, Number y) {
        
        final NumberType type_x = NumberType.valueOf(x);
        final NumberType type_y = NumberType.valueOf(y);
        
        final boolean reorder = type_y.ordinal()>type_x.ordinal();
        
        return reorder
                ? -compareWideVsNarrow(type_y, y, type_x, x)
                        : compareWideVsNarrow(type_x, x, type_y, y);
    }
    
    @Override
    public boolean isZero(Number number) {
        NumberType numberType = NumberType.valueOf(number);
        return compare(numberType.zero, number) == 0;
    }

    @Override
    public boolean isOne(Number number) {
        NumberType numberType = NumberType.valueOf(number);
        return compare(numberType.one, number) == 0;
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
                number instanceof AtomicLong ||
                number instanceof Integer || 
                number instanceof AtomicInteger ||
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

    private int compareWideVsNarrow(
            NumberType wideType, Number wide, 
            NumberType narrowType, Number narrow) {
        
        
        if(wideType.isIntegerOnly()) {
            // at this point we know, that narrow must also be an integer-only type
            if(wide instanceof BigInteger) {
                return ((BigInteger) wide).compareTo(integerToBigInteger(narrow));
            }
            
            // at this point we know, that 'wide' and 'narrow' are one of {Long, Integer, Short, Byte}
            return Long.compare(wide.longValue(), narrow.longValue());
        }
        
        if(wide instanceof RationalNumber) {
            
            // at this point we know, that narrow must either be rational or an integer-only type
            if(narrow instanceof RationalNumber) {
                return ((RationalNumber) wide).compareTo((RationalNumber) narrow);
            }
            
            return ((RationalNumber) wide).compareTo(
                    RationalNumber.ofWholeNumber(integerToBigInteger(narrow)));
        }
        
        // at this point we know, that wide is fractional
        
        if(wide instanceof BigDecimal) {
            
            if(narrow instanceof BigDecimal) {
                return ((BigDecimal) wide).compareTo((BigDecimal) narrow);
            }
            
            if(narrow instanceof Double || narrow instanceof Float) {
                return ((BigDecimal) wide).compareTo(BigDecimal.valueOf(narrow.doubleValue()));
            }
            
            if(narrow instanceof RationalNumber) {
                return ((BigDecimal) wide).compareTo(((RationalNumber) narrow).bigDecimalValue());
            }
            
            // at this point we know, that 'narrow' is one of {(Atomic)Long, (Atomic)Integer, Short, Byte}
            return ((BigDecimal) wide).compareTo(BigDecimal.valueOf(narrow.longValue()));
            
        }
        
        // at this point we know, that wide is one of {Double, Float}
        
        if(narrow instanceof Double || narrow instanceof Float) {
            return Double.compare(wide.doubleValue(), narrow.doubleValue());
        }
        
        if(narrow instanceof RationalNumber) {
            return BigDecimal.valueOf(wide.doubleValue())
                    .compareTo(((RationalNumber) narrow).bigDecimalValue());
        }
        
        if(narrow instanceof BigInteger) {
            return BigDecimal.valueOf(wide.doubleValue())
                    .compareTo(new BigDecimal((BigInteger) narrow));
        }
        
        // at this point we know, that 'narrow' is one of {(Atomic)Long, (Atomic)Integer, Short, Byte}
        return BigDecimal.valueOf(wide.doubleValue())
                .compareTo(BigDecimal.valueOf(narrow.longValue()));
        
    }


    

}
