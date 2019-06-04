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

import tech.units.indriya.internal.function.calc.DefaultNumberSystem;

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
     * The default NumberSystem used for Number arithmetic.
     */
    public static final NumberSystem DEFAULT_NUMBER_SYSTEM = new DefaultNumberSystem();

    /**
     * Exposes (non-final) the NumberSystem used for Number arithmetic.
     */
    public static NumberSystem NUMBER_SYSTEM = DEFAULT_NUMBER_SYSTEM;
	
	
	/**
	 * Converts a number to {@link BigDecimal}
	 *
	 * @param number
	 *          the number to be converted
	 * @return the number converted
	 */
	@Deprecated //TODO[220] use NUMBER_SYSTEM instead
	public static BigDecimal toBigDecimal(Number number) {
		Objects.requireNonNull(number, MSG_NUMBER_NON_NULL);
		if(number instanceof BigDecimal) {
			return (BigDecimal) number;
		}
		if(number instanceof BigInteger) {
			return new BigDecimal((BigInteger) number);
		}
		if(number instanceof Double || number instanceof Float) {
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
	@Deprecated //TODO[220] use NUMBER_SYSTEM instead
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

	// -- DOUBLE TO LONG
	
	private final static double upperBoundForLong = (double) Long.MAX_VALUE;
	private final static double lowerBoundForLong = (double) Long.MIN_VALUE;
	
	private static boolean canBeRoundedToLong(double x) {
	    return (lowerBoundForLong<x) && (x<upperBoundForLong);   
	}
	
	private static Number toLongIfCanBeRoundedToLong(double x) {
	    return canBeRoundedToLong(x)
	            ? (long) x
	                    : x;
    }

	// -- ROUNDING TOWARDS ZERO
	
//	/**
//	 * 
//	 * @param number
//	 * @return
//	 */
//    public static Number roundTowardsZero(Number number) {
//        Objects.requireNonNull(number, MSG_NUMBER_NON_NULL);
//        if(number instanceof BigInteger || number instanceof Long || 
//                number instanceof Integer || number instanceof Short || 
//                number instanceof Byte) {
//            return number;
//        }
//        if(number instanceof BigDecimal) {
//            return ((BigDecimal) number).toBigInteger();
//        }
//        if(number instanceof Double) {
//            return roundTowardsZero((double)number);
//        }
//        logger.fine(()->String.format(
//                "WARNING: possibly loosing precision, when converting from Number type '%s' to double.",
//                number.getClass().getName()));
//        return roundTowardsZero(number.doubleValue());
//    }
    
    private final static double roundTowardsZero(double value) {
        if(Double.isNaN(value) || Double.isInfinite(value)) {
            return value;
        }
        return value<0.
                ?  Math.ceil(value)
                        : Math.floor(value);
    }
    
    // -- ROUNDING TOWARDS ZERO WITH REMAINDER 
    
    // TODO[220] might be used when refactoring Radix implementations ...
    
    private static class IntegerAndFraction {
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
    
    private static IntegerAndFraction roundTowardsZeroWithRemainder(Number number) {
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
