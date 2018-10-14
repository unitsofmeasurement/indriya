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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class PowerOfPiConverterTest {

	// for reference
	protected final static String HUNDRED_DIGITS_OF_PI =
			"3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117068";

	//	@BeforeEach
	//	public void setUp() throws Exception {
	//		
	//	}

	@AfterEach
	public void reset() throws Exception {
		Calculus.MATH_CONTEXT = Calculus.DEFAULT_MATH_CONTEXT;
	}

	@Test
	public void testConvertMethod() {
		PowerOfPiConverter converter = new PowerOfPiConverter(-1);
		Calculus.MATH_CONTEXT = MathContext.DECIMAL32;

		assertEquals(1000, converter.convert(3141), 0.2);
		assertEquals(0, converter.convert(0));
		assertEquals(-1000, converter.convert(-3141), 0.2);
	}

	@Test
	public void testConvertBigDecimalMethod() {
		PowerOfPiConverter converter = new PowerOfPiConverter(-1);
		Calculus.MATH_CONTEXT = MathContext.DECIMAL32;

		assertEquals(1000, converter.convert(new BigDecimal("3141")).doubleValue(), 0.2);
		assertEquals(0, converter.convert(BigDecimal.ZERO).doubleValue());
		assertEquals(-1000, converter.convert(new BigDecimal("-3141")).doubleValue(), 0.2);
	}

	@Test
	public void testEquality() {
		PowerOfPiConverter a = new PowerOfPiConverter(-1);
		PowerOfPiConverter b = new PowerOfPiConverter(-1);
		PowerOfPiConverter c = PowerOfPiConverter.ONE;
		assertTrue(a.equals(b)); 
		assertFalse(a.equals(c));
	}

	@Test
	public void isLinear() {
		PowerOfPiConverter converter = PowerOfPiConverter.of(-1);
		assertTrue(converter.isLinear());
	}

	@Test
	public void piSquaredBigDecimalDefaultPrecision() {
		PowerOfPiConverter converter = new PowerOfPiConverter(2);
		BigDecimal value = (BigDecimal) converter.convert(BigDecimal.valueOf(0.1));
		assertEquals("0.9869604401089358618834490999876151", value.toPlainString());
	}

	@Test
	public void piBigDecimalDefaultPrecision() {
		PowerOfPiConverter converter = new PowerOfPiConverter(1);
		Calculus.MATH_CONTEXT = MathContext.UNLIMITED;
		assertThrows(ArithmeticException.class, ()->converter.convert(BigDecimal.valueOf(1.0)));
	}

	@Test
	public void piBigDecimalExtendedPrecision() {
		PowerOfPiConverter converter = PowerOfPiConverter.ONE;
		Calculus.MATH_CONTEXT = new MathContext(MathContext.DECIMAL128.getPrecision() * 2);
		BigDecimal value = (BigDecimal) converter.convert(BigDecimal.valueOf(1.));
		//[ahuber] last digit should actually round to '2' instead of '0', 
		// but I suppose this is within margin of error
		assertEquals(
				"3.14159265358979323846264338327950288419716939937510582097494459230780", 
				value.toPlainString());
	}

	@Test
	public void toStringTest() {
		PowerOfPiConverter converter = new PowerOfPiConverter(2);
		assertEquals("PowerOfPi(x -> x * π^2)", converter.toString());
	}
}
