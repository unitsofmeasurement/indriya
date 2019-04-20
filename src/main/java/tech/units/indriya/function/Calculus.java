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
import java.math.MathContext;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Mathematical helper class
 * @author Andi Huber
 * @author Werner Keil
 * @since 2.0
 */
public final class Calculus {
	private static final String MSG_NUMBER_NON_NULL = "number cannot be null";
	
	private static final Logger logger = Logger.getLogger(Calculus.class.getName());

	/**
	 * The default MathContext used for BigDecimal calculus.
	 */
	public static final MathContext DEFAULT_MATH_CONTEXT = MathContext.DECIMAL128;

	/**
	 * Exposes (non-final) the MathContext used for BigDecimal calculus.
	 */
	public static MathContext MATH_CONTEXT = DEFAULT_MATH_CONTEXT;
	
	/**
	 * @param number
	 * @return whether {@code number} is one of Long, Integer, Short or Byte 
	 */
	public static boolean isPrimitiveNonFractional(Number number) {
	    Objects.requireNonNull(number, MSG_NUMBER_NON_NULL);
	    if(number instanceof Long || number instanceof Integer || 
	            number instanceof Short || number instanceof Byte) {
	        return true;
	    }
        return false;
    }
	
	public static boolean isNonFractional(Number number) {
	    if(isPrimitiveNonFractional(number) || number instanceof BigInteger) {
	        return true;
	    }
        return false;
    }
	
	public static void assertNotFractional(Number number) {
        if(!isNonFractional(number)) {
            throw new ArithmeticException("number is fractional");       
        }
    }
	
	public static void assertNotNegative(Number number) {
        if(isLessThanZero(number)) {
            throw new ArithmeticException("number is fractional");       
        }
    }
	

	/**
	 * Converts a number to {@link BigDecimal}
	 *
	 * @param number
	 *          the number to be converted
	 * @return the number converted
	 */
	public static BigDecimal toBigDecimal(Number number) {
		Objects.requireNonNull(number, MSG_NUMBER_NON_NULL);
		if(number instanceof BigDecimal) {
			return (BigDecimal) number;
		}
		if(number instanceof BigInteger) {
			return new BigDecimal((BigInteger) number);
		}
		if(number instanceof Double) {
			return BigDecimal.valueOf(number.doubleValue());
		}
		logger.fine(()->String.format(
				"WARNING: possibly loosing precision, when converting from Number type '%s' to double.",
				number.getClass().getName()));
		return BigDecimal.valueOf(number.doubleValue());
	}

	/**
	 * Converts a number to {@link BigInteger}
	 *
	 * @param number
	 *          the number to be converted
	 * @return the number converted
	 */
	public static BigInteger toBigInteger(Number number) {
		Objects.requireNonNull(number, MSG_NUMBER_NON_NULL);
		if(number instanceof BigInteger) {
			return (BigInteger) number;
		}
		if(number instanceof BigDecimal) {
			try {
				return ((BigDecimal) number).toBigIntegerExact();
			} catch (ArithmeticException e) {
				logger.fine(()->String.format(
						"WARNING: loosing precision, when converting from BigDecimal to BigInteger.",
						number.getClass().getName()));
				return ((BigDecimal) number).toBigInteger();
			}
		}
		if(number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte) {
			return BigInteger.valueOf(number.longValue());
		}
		logger.fine(()->String.format(
				"WARNING: possibly loosing precision, when converting from Number type '%s' to long.",
				number.getClass().getName()));
		return BigInteger.valueOf(number.longValue());
	}

	/**
	 * Returns the absolute value of {@code number}
	 * @param number
	 * @return 
	 */
	public static Number abs(Number number) {
		Objects.requireNonNull(number, MSG_NUMBER_NON_NULL);
		if(number instanceof BigInteger) {
			return ((BigInteger) number).abs();
		}
		if(number instanceof BigDecimal) {
			return ((BigDecimal) number).abs();
		}
		if(number instanceof Double) {
			return Math.abs((double)number);
		}
		if(number instanceof Long) {
			return Math.abs((long)number);
		}
		if(number instanceof Integer) {
			return Math.abs((int)number);
		}
		if(number instanceof Short) {
			return Math.abs((short)number);
		}
		if(number instanceof Byte) {
			return Math.abs((byte)number);
		}
		logger.fine(()->String.format(
				"WARNING: possibly loosing precision, when converting from Number type '%s' to double.",
				number.getClass().getName()));
		return Math.abs(number.doubleValue());
	}
	
	/**
	 * Returns the negated value of {@code number}
	 * @param number
	 * @return -number
	 */
	public static Number negate(Number number) {
		Objects.requireNonNull(number, MSG_NUMBER_NON_NULL);
		if(number instanceof BigInteger) {
			return ((BigInteger) number).negate();
		}
		if(number instanceof BigDecimal) {
			return ((BigDecimal) number).negate();
		}
		if(number instanceof Double) {
			return -((double)number);
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
		logger.fine(()->String.format(
				"WARNING: possibly loosing precision, when converting from Number type '%s' to double.",
				number.getClass().getName()));
		return -(number.doubleValue());
	}

	/**
	 * Whether given number is &lt; 1
	 * @param number
	 * @return
	 */
	public static boolean isLessThanOne(Number number) {
		Objects.requireNonNull(number, MSG_NUMBER_NON_NULL);
		if(number instanceof BigInteger) {
			return ((BigInteger) number).compareTo(BigInteger.ONE) < 0;
		}
		if(number instanceof BigDecimal) {
			return ((BigDecimal) number).compareTo(BigDecimal.ONE) < 0;
		}
		if(number instanceof Double) {
			return ((double)number) < 1.0;
		}
		if(isPrimitiveNonFractional(number)) {
			return number.longValue() < 1L;
		}
		logger.fine(()->String.format(
				"WARNING: possibly loosing precision, when converting from Number type '%s' to double.",
				number.getClass().getName()));
		return number.doubleValue() < 1.0;
	}
	
    /**
     * Whether given number is &lt; 1
     * @param number
     * @return
     */
    public static boolean isLessThanZero(Number number) {
        Objects.requireNonNull(number, MSG_NUMBER_NON_NULL);
        if(number instanceof BigInteger) {
            return ((BigInteger) number).compareTo(BigInteger.ZERO) < 0;
        }
        if(number instanceof BigDecimal) {
            return ((BigDecimal) number).compareTo(BigDecimal.ZERO) < 0;
        }
        if(number instanceof Double) {
            return ((double)number) < 0.;
        }
        if(isPrimitiveNonFractional(number)) {
            return number.longValue() < 0L;
        }
        logger.fine(()->String.format(
                "WARNING: possibly loosing precision, when converting from Number type '%s' to double.",
                number.getClass().getName()));
        return number.doubleValue() < 0.;
    }
	
	// -- OPTIMIZED ARITHMETIC
	
	/**
     * @return number of bits in the minimal two's-complement
     *         representation of this Number, <i>excluding</i> a sign bit; 
     *         returns {@code -1}, iff the given number is a non-fractional type or {@code null} 
     */
	public static int bitLength(Number number) {
	    if(number==null) {
	        return -1;
	    }
        if(isPrimitiveNonFractional(number)) {
            
            long long_value = number.longValue(); 
            
            if(long_value == Long.MIN_VALUE) {
                return 63;
            } else {
                return Long.bitCount(Math.abs(long_value));
            }
            
        }
        if(number instanceof BigInteger) {
            return ((BigInteger) number).bitLength();
        }
        return -1;        
    }

	/**
     * Converts this number to a int, checking for lost information. If the value of this 
     * number is out of the range of the int type, then an ArithmeticException is thrown.
     * @param number
     */
	public static int intValueExact(Number number) {
	    Objects.requireNonNull(number, MSG_NUMBER_NON_NULL);
        if(number instanceof BigInteger) {
            return ((BigInteger) number).intValueExact();
        }
        if(isPrimitiveNonFractional(number)) {
            long long_value = number.longValue();
            return Math.toIntExact(long_value);
        }
        throw new ArithmeticException("Number out of int range");
	}
	
	/**
	 * Converts this number to a long, checking for lost information. If the value of this 
	 * number is out of the range of the long type, then an ArithmeticException is thrown.
	 * @param number
	 */
	public static long longValueExact(Number number) {
	    Objects.requireNonNull(number, MSG_NUMBER_NON_NULL);
        if(number instanceof BigInteger) {
            return ((BigInteger) number).longValueExact();
        }
        if(isPrimitiveNonFractional(number)) {
            return number.longValue();
        }
        throw new ArithmeticException("Number out of long range");
    }
	
	public static Number longValueIfExact(Number number) {
	    if(number==null) {
	        return null;
	    }
	    
        int total_bits_required = bitLength(number); // not including sign
        
        if(total_bits_required<63) {
            return longValueExact(number);
        }
	    
        return number;
	}
	
	/**
	 * Multiplication without loss of precision.
	 * @param x
	 * @param y
	 * @return
	 */
	public static Number multiply(Number x, Number y) {
	    
	    if(isNonFractional(x) && isNonFractional(y)) {
	        
            int total_bits_required = bitLength(x) + bitLength(y); // not including sign
            
            if(total_bits_required<31) {
                return intValueExact(x) * intValueExact(y);
            }
            
            if(total_bits_required<63) {
                return longValueExact(x) * longValueExact(y);
            }
            
            return toBigInteger(x).multiply(toBigInteger(y));
	        
	    }
	    
	    return toBigDecimal(x).multiply(toBigDecimal(y));
	    
	}
	
    /**
     * Addition without loss of precision.
     * @param x
     * @param y
     * @return
     */
	public static Number add(Number x, Number y) {
        
        if(isNonFractional(x) && isNonFractional(y)) {
            
            int total_bits_required = Math.max(bitLength(x), bitLength(y)) + 1; // +1 carry, not including sign
            
            if(total_bits_required<31) {
                return intValueExact(x) + intValueExact(y);
            }
            
            if(total_bits_required<63) {
                return longValueExact(x) + longValueExact(y);
            }
            
            return toBigInteger(x).add(toBigInteger(y));
            
        }
        
        return toBigDecimal(x).add(toBigDecimal(y));

    }
	
	// -- DOUBLE TO LONG
	
	private final static double upperBoundForLong = (double) Long.MAX_VALUE;
	private final static double lowerBoundForLong = (double) Long.MIN_VALUE;
	
	public static boolean canBeRoundedToLong(double x) {
	    return (lowerBoundForLong<x) && (x<upperBoundForLong);   
	}
	
	public static Number toLongIfCanBeRoundedToLong(double x) {
	    return canBeRoundedToLong(x)
	            ? (long) x
	                    : x;
    }

	// -- ROUNDING TOWARDS ZERO
	
	/**
	 * 
	 * @param number
	 * @return
	 */
    public static Number roundTowardsZero(Number number) {
        Objects.requireNonNull(number, MSG_NUMBER_NON_NULL);
        if(number instanceof BigInteger || number instanceof Long || 
                number instanceof Integer || number instanceof Short || 
                number instanceof Byte) {
            return number;
        }
        if(number instanceof BigDecimal) {
            return ((BigDecimal) number).toBigInteger();
        }
        if(number instanceof Double) {
            return roundTowardsZero((double)number);
        }
        logger.fine(()->String.format(
                "WARNING: possibly loosing precision, when converting from Number type '%s' to double.",
                number.getClass().getName()));
        return roundTowardsZero(number.doubleValue());
    }
    
    private final static double roundTowardsZero(double value) {
        if(Double.isNaN(value) || Double.isInfinite(value)) {
            return value;
        }
        return value<0.
                ?  Math.ceil(value)
                        : Math.floor(value);
    }
    
    // -- ROUNDING TOWARDS ZERO WITH REMAINDER
    
    public static class IntegerAndFraction {
        private final Number integer;
        private final Number fraction;
        /**
         * @param integer - null-able
         * @param fraction - null-able
         */
        public IntegerAndFraction(Number integer, Number fraction) {
            this.integer = integer;
            this.fraction = fraction;
        }
        /**
         * @return the whole number part
         */
        public Number getInteger() {
            return integer;
        }
        /**
         * @return the fractional number part
         */
        public Number getFraction() {
            return fraction;
        }
    }
    
    public static IntegerAndFraction roundTowardsZeroWithRemainder(Number number) {
        Objects.requireNonNull(number, MSG_NUMBER_NON_NULL);
        if(number instanceof BigInteger || number instanceof Long || 
                number instanceof Integer || number instanceof Short || 
                number instanceof Byte) {
            return new IntegerAndFraction(number, null);
        }
        if(number instanceof BigDecimal) {
            final BigDecimal bigDec = (BigDecimal) number;
            final BigInteger integer = bigDec.toBigInteger();
            final BigDecimal fraction = bigDec.subtract(new BigDecimal(integer), MATH_CONTEXT);
            return new IntegerAndFraction(integer, fraction);
        }
        if(number instanceof Double) {
            return roundTowardsZeroWithRemainder((double)number);
        }
        logger.fine(()->String.format(
                "WARNING: possibly loosing precision, when converting from Number type '%s' to double.",
                number.getClass().getName()));
        return roundTowardsZeroWithRemainder(number.doubleValue());
    }
    
    private final static IntegerAndFraction roundTowardsZeroWithRemainder(double value) {
        final double integer = roundTowardsZero(value);
        final double fraction = value - integer;
        return new IntegerAndFraction(toLongIfCanBeRoundedToLong(integer), fraction);
    }
   
    
    
}
