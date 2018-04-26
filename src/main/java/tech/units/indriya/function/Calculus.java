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
import java.util.logging.Logger;

/**
 * Mathematical helper class
 * @author Andi Huber
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
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isLessThanOne(Number number) {
		Objects.requireNonNull(number, MSG_NUMBER_NON_NULL);
		if(number instanceof BigInteger) {
			return ((BigInteger) number).compareTo(BigInteger.ONE) == -1;
		}
		if(number instanceof BigDecimal) {
			return ((BigDecimal) number).compareTo(BigDecimal.ONE) == -1;
		}
		if(number instanceof Double) {
			return ((double)number) < 1.0;
		}
		if(number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte) {
			return number.longValue() < 1L;
		}
		logger.fine(()->String.format(
				"WARNING: possibly loosing precision, when converting from Number type '%s' to double.",
				number.getClass().getName()));
		return number.doubleValue() < 1.0;
	}
}
