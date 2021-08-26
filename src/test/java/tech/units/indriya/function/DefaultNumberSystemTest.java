/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2021, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DefaultNumberSystemTest {

	DefaultNumberSystem ns;

	@BeforeEach
	void setUp() {
		ns = new DefaultNumberSystem();
	}

	// -- NUMBER COMPARING TESTS

	@ParameterizedTest
	@MethodSource("provideOneSamples")
	void one(Number x) {
		assertTrue(ns.isOne(x));
	}

	@ParameterizedTest
	@MethodSource("provideZeroSamples")
	void zero(Number x) {
		assertTrue(ns.isZero(x));
	}

	@ParameterizedTest
	@MethodSource("provideZeroSamples")
	void not_one(Number x) {
		assertFalse(ns.isOne(x));
	}

	@ParameterizedTest
	@MethodSource("provideOneSamples")
	void not_zero(Number x) {
		assertFalse(ns.isZero(x));
	}

	@ParameterizedTest
	@MethodSource("provideZeroSamples")
	void less_than_one(Number x) {
		assertTrue(ns.isLessThanOne(x));
	}

	@ParameterizedTest
	@MethodSource("provideOneSamples")
	void not_less_than_one(Number x) {
		assertFalse(ns.isLessThanOne(x));
	}

	@ParameterizedTest
	@MethodSource("provideOneSamples")
	void minus_one_is_less_than_one(Number x) {
		assertTrue(ns.isLessThanOne(ns.negate(x)));
	}

	// -- SAMPLER

	static Stream<Number> provideOneSamples() {
		return Stream.<Number>of(
				(byte) 1,
				(short) 1,
				1,
				1L,
				1.,
				1.f,
				BigInteger.ONE,
				BigDecimal.ONE,
				RationalNumber.ONE,
				new AtomicInteger(1),
				new AtomicLong(1L)
		);
	}

	static Stream<Number> provideZeroSamples() {
		return Stream.<Number>of(
				(byte) 0,
				(short) 0,
				0,
				0L,
				0.,
				0.f,
				BigInteger.ZERO,
				BigDecimal.ZERO,
				RationalNumber.ZERO,
				new AtomicInteger(0),
				new AtomicLong(0L)
		);
	}

}
