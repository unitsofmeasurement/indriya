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
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;

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
        
        @SuppressWarnings("unused")
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
        
        final NumberType type_x = NumberType.valueOf(x);
        final NumberType type_y = NumberType.valueOf(y);
        
        final boolean reorder_args = type_y.ordinal()>type_x.ordinal();
        
        return reorder_args
                ? addWideAndNarrow(type_y, y, type_x, x)
                        : addWideAndNarrow(type_x, x, type_y, y);
    }

    @Override
    public Number subtract(Number x, Number y) {
        return add(x, negate(y));
    }

    @Override
    public Number multiply(Number x, Number y) {
        
        final NumberType type_x = NumberType.valueOf(x);
        final NumberType type_y = NumberType.valueOf(y);
        
        final boolean reorder_args = type_y.ordinal()>type_x.ordinal();
        
        return reorder_args
                ? multiplyWideAndNarrow(type_y, y, type_x, x)
                        : multiplyWideAndNarrow(type_x, x, type_y, y);
    }

    @Override
    public Number divide(Number x, Number y) {
        return multiply(x, reciprocal(y));
    }
    
    @Override
    public Number[] divideAndRemainder(Number x, Number y, boolean roundRemainderTowardsZero) {
        
        final int sign_x = signum(x);
        final int sign_y = signum(y);
        
        final int sign = sign_x * sign_y;
        // handle corner cases when x or y are zero
        if(sign==0) {
            if(sign_y==0) {
                throw new ArithmeticException("division by zero");
            }
            if(sign_x==0) {
                return new Number[] {0, 0};
            }
        }
        
        final Number absX = abs(x);
        final Number absY = abs(y);
        
        final NumberType type_x = NumberType.valueOf(absX);
        final NumberType type_y = NumberType.valueOf(absY);
        
        // if x and y are both integer types than we can calculate integer results,
        // otherwise we resort to BigDecimal
        final boolean yieldIntegerResult = type_x.isIntegerOnly() && type_y.isIntegerOnly();
        
        if(yieldIntegerResult) {
                            
            final BigInteger integer_x = integerToBigInteger(absX);
            final BigInteger integer_y = integerToBigInteger(absY);
            
            final BigInteger[] divAndRemainder = integer_x.divideAndRemainder(integer_y);
            
            return applyToArray(divAndRemainder, number->copySignTo(sign, (BigInteger)number));
            
        } else {
            
            final MathContext mathContext = 
                    new MathContext(Calculus.MATH_CONTEXT.getPrecision(), RoundingMode.FLOOR);
            
            final BigDecimal decimal_x = (type_x==NumberType.RATIONAL)
                    ? ((RationalNumber) absX).bigDecimalValue()
                            : toBigDecimal(absX);
            final BigDecimal decimal_y = (type_y==NumberType.RATIONAL)
                    ? ((RationalNumber) absY).bigDecimalValue()
                            : toBigDecimal(absY);
            
            final BigDecimal[] divAndRemainder = decimal_x.divideAndRemainder(decimal_y, mathContext);
            
            if(roundRemainderTowardsZero) {
                return new Number[] {
                        copySignTo(sign, divAndRemainder[0]), 
                        copySignTo(sign, divAndRemainder[1].toBigInteger())};
                
            } else {
                return applyToArray(divAndRemainder, number->copySignTo(sign, (BigDecimal)number));
            }
            
        }

    }

    @Override
    public Number reciprocal(Number number) {
        if(isIntegerOnly(number)) {
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
    public int signum(Number number) {
        if(number instanceof BigInteger) {
            return ((BigInteger) number).signum();
        }
        if(number instanceof BigDecimal) {
            return ((BigDecimal) number).signum();
        }
        if(number instanceof RationalNumber) {
            return ((RationalNumber) number).signum();
        }
        if(number instanceof Double) {
            return (int)Math.signum((double)number);
        }
        if(number instanceof Float) {
            return (int)Math.signum((float)number);
        }
        if(number instanceof Long || number instanceof AtomicLong) {
            final long longValue = number.longValue();
            return Long.signum(longValue);
        }
        if(number instanceof Integer || number instanceof AtomicInteger ||
                number instanceof Short || number instanceof Byte) {
            final int intValue = number.intValue();
            return Integer.signum(intValue);
        }
        throw unsupportedNumberType(number);    
    }
    
    @Override
    public Number abs(Number number) {
        if(number instanceof BigInteger) {
            return ((BigInteger) number).abs();
        }
        if(number instanceof BigDecimal) {
            return ((BigDecimal) number).abs();
        }
        if(number instanceof RationalNumber) {
            return ((RationalNumber) number).abs();
        }
        if(number instanceof Double) {
            return Math.abs((double)number);
        }
        if(number instanceof Float) {
            return Math.abs((float)number);
        }
        if(number instanceof Long || number instanceof AtomicLong) {
            final long longValue = number.longValue();
            if(longValue == Long.MIN_VALUE) {
                return BigInteger.valueOf(longValue).abs(); // widen to BigInteger
            }
            return Math.abs(longValue);
        }
        if(number instanceof Integer || number instanceof AtomicInteger) {
            final int intValue = number.intValue();
            if(intValue == Integer.MIN_VALUE) {
                return Math.abs(number.longValue()); // widen to long
            }
            return Math.abs(intValue);
        }
        if(number instanceof Short || number instanceof Byte) {
            Math.abs(number.intValue()); // widen to int
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
        if(number instanceof Long || number instanceof AtomicLong) {
            final long longValue = number.longValue();
            if(longValue == Long.MIN_VALUE) {
                return BigInteger.valueOf(longValue).negate(); // widen to BigInteger
            }
            return -longValue;
        }
        if(number instanceof Integer || number instanceof AtomicInteger) {
            final int intValue = number.intValue();
            if(intValue == Integer.MIN_VALUE) {
                return -number.longValue(); // widen to long
            }
            return -intValue;
        }
        if(number instanceof Short) {
            final short shortValue = (short)number;
            if(shortValue == Short.MIN_VALUE) {
                return -number.intValue(); // widen to int
            }
            return -shortValue;
        }
        if(number instanceof Byte) {
            final short byteValue = (byte)number;
            if(byteValue == Byte.MIN_VALUE) {
                return -number.intValue(); // widen to int
            }
            return -byteValue;
        }
        throw unsupportedNumberType(number);
    }
    
    @Override
    public Number power(Number number, int exponent) {
        if(exponent==0) {
            if(isZero(number)) {
                throw new ArithmeticException("0^0 is not defined");
            }
            return 1; // x^0 == 1, for any x!=0
        }
        if(number instanceof BigInteger ||
                number instanceof Long || number instanceof AtomicLong ||
                number instanceof Integer || number instanceof AtomicInteger ||
                number instanceof Short || number instanceof Byte) {
            final BigInteger bigInt = integerToBigInteger(number);
            if(exponent>0) {
                return bigInt.pow(exponent);    
            }
            return RationalNumber.ofInteger(bigInt).pow(exponent);
            
        }
        if(number instanceof BigDecimal) {
            return ((BigDecimal) number).pow(exponent, Calculus.MATH_CONTEXT);
        }
        if(number instanceof RationalNumber) {
            ((RationalNumber) number).pow(exponent);
        }
        if(number instanceof Double || number instanceof Float) {
            return toBigDecimal(number).pow(exponent, Calculus.MATH_CONTEXT);
        }
        throw unsupportedNumberType(number);
    }
    
    @Override
    public Number exp(Number number) {
        //TODO[220] this is a poor implementation, certainly we can do better using BigDecimal 
        return Math.exp(number.doubleValue());
    }
    
    @Override
    public Number log(Number number) {
        //TODO[220] this is a poor implementation, certainly we can do better using BigDecimal
        return Math.log(number.doubleValue());
    }
    
    @Override
    public Number narrow(Number number) {
        
        //Implementation Note: for performance we stop narrowing down at 'double' or 'integer' level
        
        if(number instanceof Integer || number instanceof AtomicInteger ||
                number instanceof Short || number instanceof Byte) {
            return number;
        }
        
        if(number instanceof Double || number instanceof Float) {
            final double doubleValue = number.doubleValue();
            if(doubleValue % 1 == 0) {
                // double represents an integer
                return narrow(BigDecimal.valueOf(doubleValue));
            }
            return number;
        }
        
        if(isIntegerOnly(number)) {
            
            // number is one of {BigInteger, Long}
            
            final int total_bits_required = bitLengthOfInteger(number);
            
            // check whether we have enough bits to store the result into an int
            if(total_bits_required<31) { 
                return number.intValue();
            }
            
            // check whether we have enough bits to store the result into a long
            if(total_bits_required<63) { 
                return number.longValue();
            }
            
            return number; // cannot narrow down
            
        }

        if(number instanceof BigDecimal) {
            
            final BigDecimal decimal = ((BigDecimal) number);
            try {
                BigInteger integer = decimal.toBigIntegerExact(); 
                return narrow(integer);
            } catch (ArithmeticException e) {
                return number; // cannot narrow to integer
            }
        }
        
        if(number instanceof RationalNumber) {
            
            final RationalNumber rational = ((RationalNumber) number);
            
            return rational.isInteger() 
                    ? narrow(rational.getDividend()) // divisor is ONE
                            : number; // cannot narrow to integer;
        }

        // for any other number type just do nothing
        return number;
    }
    
    @Override
    public int compare(Number x, Number y) {
        
        final NumberType type_x = NumberType.valueOf(x);
        final NumberType type_y = NumberType.valueOf(y);
        
        final boolean reorder_args = type_y.ordinal()>type_x.ordinal();
        
        return reorder_args
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
    
    @Override
    public boolean isLessThanOne(Number number) {
        NumberType numberType = NumberType.valueOf(number);
        return compare(numberType.one, number) < 0;
    }
     
    @Override
    public boolean isInteger(Number number) {
        NumberType numberType = NumberType.valueOf(number);
        return isInteger(numberType, number);
    }
    
    
    // -- HELPER
    
    private IllegalArgumentException unsupportedNumberType(Number number) {
        final String msg = String.format("Unsupported number type '%s' in number system '%s'",
                number.getClass().getName(),
                this.getClass().getName());
        
        return new IllegalArgumentException(msg);
    }
    
    private IllegalStateException unexpectedCodeReach() {
        final String msg = String.format("Implementation Error: Code was reached that is expected unreachable");
        return new IllegalStateException(msg);
    }
    
    private boolean isIntegerOnly(Number number) {
        return NumberType.valueOf(number).isIntegerOnly();
    }
    
    private boolean isInteger(NumberType numberType, Number number) {
        if(numberType.isIntegerOnly()) {
            return true; // numberType only allows integer
        }
        if(number instanceof RationalNumber) {
            return ((RationalNumber)number).isInteger();
        }
        
        // remaining types to check: Double, Float, BigDecimal ...
        
        if(number instanceof BigDecimal) {
            final BigDecimal decimal = (BigDecimal)number; 
            // see https://stackoverflow.com/questions/1078953/check-if-bigdecimal-is-integer-value
            if(decimal.scale()<=0) {
                return true;
            }
            try {
                decimal.toBigIntegerExact();
                return true;
            } catch (ArithmeticException ex) {
                return false;
            }
        }
        if(number instanceof Double || number instanceof Float) {
            double doubleValue = number.doubleValue();
            // see https://stackoverflow.com/questions/15963895/how-to-check-if-a-double-value-has-no-decimal-part
            return doubleValue % 1 == 0; 
        }
        throw unsupportedNumberType(number);
    }
    
    private int bitLengthOfInteger(Number number) {
        if(number instanceof BigInteger) {
            return ((BigInteger) number).bitLength();
        }
        long long_value = number.longValue(); 
        
        if(long_value == Long.MIN_VALUE) {
            return 63;
        } else {
            int leadingZeros = Long.numberOfLeadingZeros(Math.abs(long_value));
            return 64-leadingZeros;
        }
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
            throw unexpectedCodeReach();
            //Note: don't do that (potential precision loss)
            //return ((RationalNumber) number).bigDecimalValue(); 
        }
        throw unsupportedNumberType(number);
    }

    private Number addWideAndNarrow(
            NumberType wideType, Number wide, 
            NumberType narrowType, Number narrow) {
        
        if(wideType.isIntegerOnly()) {
            // at this point we know, that narrow must also be an integer-only type
            if(wide instanceof BigInteger) {
                return ((BigInteger) wide).add(integerToBigInteger(narrow));
            }
            
            // at this point we know, that 'wide' and 'narrow' are one of {(Atomic)Long, (Atomic)Integer, Short, Byte}
            
            // +1 carry, not including sign
            int total_bits_required = Math.max(bitLengthOfInteger(wide), bitLengthOfInteger(narrow)) + 1; 
            
            // check whether we have enough bits to store the result into a long
            if(total_bits_required<63) { 
                return wide.longValue() + narrow.longValue();
            }
            
            return integerToBigInteger(wide).add(integerToBigInteger(narrow));
        }
        
        if(wide instanceof RationalNumber) {
            
            // at this point we know, that narrow must either be rational or an integer-only type
            if(narrow instanceof RationalNumber) {
                return ((RationalNumber) wide).add((RationalNumber) narrow);
            }
            
            return ((RationalNumber) wide).add(
                    RationalNumber.ofInteger(integerToBigInteger(narrow)));
        }
        
        // at this point we know, that wide is one of {BigDecimal, Double, Float}
        
        if(wide instanceof BigDecimal) {
            
            if(narrow instanceof BigDecimal) {
                return ((BigDecimal) wide).add((BigDecimal) narrow, Calculus.MATH_CONTEXT);
            }
            
            if(narrow instanceof Double || narrow instanceof Float) {
                return ((BigDecimal) wide).add(BigDecimal.valueOf(narrow.doubleValue()), Calculus.MATH_CONTEXT);
            }
            
            if(narrow instanceof RationalNumber) {
                //TODO[220] can we do better than that, eg. by converting BigDecimal to RationalNumber 
                return ((BigDecimal) wide).add(((RationalNumber) narrow).bigDecimalValue());
            }
            
            // at this point we know, that 'narrow' is one of {(Atomic)Long, (Atomic)Integer, Short, Byte}
            return ((BigDecimal) wide).add(BigDecimal.valueOf(narrow.longValue()));
            
        }
        
        // at this point we know, that wide is one of {Double, Float}
        
        if(narrow instanceof Double || narrow instanceof Float) {
            //converting to BigDecimal, because especially fractional addition is sensitive to precision loss
            return BigDecimal.valueOf(wide.doubleValue())
                .add(BigDecimal.valueOf(narrow.doubleValue()));
        }
        
        if(narrow instanceof RationalNumber) {
            //TODO[220] can we do better than that, eg. by converting BigDecimal to RationalNumber
            return BigDecimal.valueOf(wide.doubleValue())
                    .add(((RationalNumber) narrow).bigDecimalValue());
        }
        
        if(narrow instanceof BigInteger) {
            return BigDecimal.valueOf(wide.doubleValue())
                    .add(new BigDecimal((BigInteger) narrow));
        }
        
        // at this point we know, that 'narrow' is one of {(Atomic)Long, (Atomic)Integer, Short, Byte}
        return BigDecimal.valueOf(wide.doubleValue())
                .add(BigDecimal.valueOf(narrow.longValue()));
        
    }
    
    private Number multiplyWideAndNarrow(
            NumberType wideType, Number wide, 
            NumberType narrowType, Number narrow) {
        
        if(wideType.isIntegerOnly()) {
            // at this point we know, that narrow must also be an integer-only type
            if(wide instanceof BigInteger) {
                return ((BigInteger) wide).multiply(integerToBigInteger(narrow));
            }
            
            // at this point we know, that 'wide' and 'narrow' are one of {(Atomic)Long, (Atomic)Integer, Short, Byte}
            
            int total_bits_required = bitLengthOfInteger(wide) + bitLengthOfInteger(narrow); // not including sign
            
            // check whether we have enough bits to store the result into a long
            if(total_bits_required<63) { 
                return wide.longValue() * narrow.longValue();
            }
            
            return integerToBigInteger(wide).multiply(integerToBigInteger(narrow));
        }
        
        if(wide instanceof RationalNumber) {
            
            // at this point we know, that narrow must either be rational or an integer-only type
            if(narrow instanceof RationalNumber) {
                return ((RationalNumber) wide).multiply((RationalNumber) narrow);
            }
            
            return ((RationalNumber) wide).multiply(
                    RationalNumber.ofInteger(integerToBigInteger(narrow)));
        }
        
        // at this point we know, that wide is one of {BigDecimal, Double, Float}
        
        if(wide instanceof BigDecimal) {
            
            if(narrow instanceof BigDecimal) {
                return ((BigDecimal) wide).multiply((BigDecimal) narrow, Calculus.MATH_CONTEXT);
            }
            
            if(narrow instanceof Double || narrow instanceof Float) {
                return ((BigDecimal) wide).multiply(BigDecimal.valueOf(narrow.doubleValue()), Calculus.MATH_CONTEXT);
            }
            
            if(narrow instanceof RationalNumber) {
                //TODO[220] can we do better than that, eg. by converting BigDecimal to RationalNumber 
                return ((BigDecimal) wide).multiply(((RationalNumber) narrow).bigDecimalValue());
            }
            
            // at this point we know, that 'narrow' is one of {(Atomic)Long, (Atomic)Integer, Short, Byte}
            return ((BigDecimal) wide).multiply(BigDecimal.valueOf(narrow.longValue()));
            
        }
        
        // at this point we know, that wide is one of {Double, Float}
        
        if(narrow instanceof Double || narrow instanceof Float) {
            // not converting to BigDecimal, because fractional multiplication is not sensitive to precision loss
            return wide.doubleValue() * narrow.doubleValue();
        }
        
        if(narrow instanceof RationalNumber) {
            //TODO[220] can we do better than that, eg. by converting BigDecimal to RationalNumber
            return BigDecimal.valueOf(wide.doubleValue())
                    .multiply(((RationalNumber) narrow).bigDecimalValue());
        }
        
        if(narrow instanceof BigInteger) {
            return BigDecimal.valueOf(wide.doubleValue())
                    .multiply(new BigDecimal((BigInteger) narrow));
        }
        
        // at this point we know, that 'narrow' is one of {(Atomic)Long, (Atomic)Integer, Short, Byte}
        return BigDecimal.valueOf(wide.doubleValue())
                .multiply(BigDecimal.valueOf(narrow.longValue()));              
     
    }
    
    
    private int compareWideVsNarrow(
            NumberType wideType, Number wide, 
            NumberType narrowType, Number narrow) {
        
        
        if(wideType.isIntegerOnly()) {
            // at this point we know, that narrow must also be an integer-only type
            if(wide instanceof BigInteger) {
                return ((BigInteger) wide).compareTo(integerToBigInteger(narrow));
            }
            
            // at this point we know, that 'wide' and 'narrow' are one of {(Atomic)Long, (Atomic)Integer, Short, Byte}
            return Long.compare(wide.longValue(), narrow.longValue());
        }
        
        if(wide instanceof RationalNumber) {
            
            // at this point we know, that narrow must either be rational or an integer-only type
            if(narrow instanceof RationalNumber) {
                return ((RationalNumber) wide).compareTo((RationalNumber) narrow);
            }
            
            return ((RationalNumber) wide).compareTo(
                    RationalNumber.ofInteger(integerToBigInteger(narrow)));
        }
        
        // at this point we know, that wide is one of {BigDecimal, Double, Float}
        
        if(wide instanceof BigDecimal) {
            
            if(narrow instanceof BigDecimal) {
                return ((BigDecimal) wide).compareTo((BigDecimal) narrow);
            }
            
            if(narrow instanceof Double || narrow instanceof Float) {
                return ((BigDecimal) wide).compareTo(BigDecimal.valueOf(narrow.doubleValue()));
            }
            
            if(narrow instanceof RationalNumber) {
                //TODO[220] can we do better than that, eg. by converting BigDecimal to RationalNumber
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
            //TODO[220] can we do better than that, eg. by converting BigDecimal to RationalNumber
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

    // only for non-zero sign
    private static BigInteger copySignTo(int sign, BigInteger absNumber) {
        if(sign==-1) {
            return absNumber.negate();
        }    
        return absNumber;
    }
    
    // only for non-zero sign
    private static BigDecimal copySignTo(int sign, BigDecimal absNumber) {
        if(sign==-1) {
            return absNumber.negate();
        }    
        return absNumber;
    }
    
    private static Number[] applyToArray(Number[] array, UnaryOperator<Number> operator) {
        // only ever used for length=2
        return new Number[] {
                operator.apply(array[0]),
                operator.apply(array[1])
        };
    }
    

}
