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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

import javax.measure.UnitConverter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import tech.units.indriya.AbstractConverter;

@DisplayName("Testing Composition of UnitConverters")
public class CompositionEquivalenceTest {

	private Random random = new Random(0); // seed = 0, to make tests reproducible
	private final static int RANDOM_VALUES_REPEAT_COUNT = 100;

	public static enum ConverterType {
		
		ID(AbstractConverter.class, 
				()->AbstractConverter.IDENTITY,
				AbstractConverter.IDENTITY, 
				AbstractConverter.IDENTITY	),
		POWER(PowerOfIntConverter.class, 
				()->PowerOfIntConverter.of(1, 0),
				PowerOfIntConverter.of(3, 7), 
				PowerOfIntConverter.of(7, -3)	),
		RATIONAL(RationalConverter.class, 
				()->RationalConverter.of(1, 1),
				RationalConverter.of(17, 13),
				RationalConverter.of(-34, 17)	),
		MULTIPLY(MultiplyConverter.class, 
				()->new MultiplyConverter(1.),
				new MultiplyConverter(17.23),
				new MultiplyConverter(-0.333) ),
		ADD(AddConverter.class, 
				()->new AddConverter(0.),
				new AddConverter(-4.5),
				new AddConverter(0.315) ),
		LOG(LogConverter.class, 
				null, // log has no identity variant
				new LogConverter(4.5),
				new LogConverter(0.1) ),
		EXP(ExpConverter.class, 
				null, // exp has no identity variant
				new ExpConverter(4.5),
				new ExpConverter(0.1) ),
		PI(PowersOfPiConverter.class, 
				()->PowersOfPiConverter.of(0), // log has no identity variant
				PowersOfPiConverter.of(1),
				PowersOfPiConverter.of(-1) ),
		// when adding entries, also increment the typeCount!
		;

		public static final int typeCount = 8; // should be equal to ConverterType.values().length 
		public static final int candidatesPerType = 2;
		public static final int candidateCount = typeCount * candidatesPerType;

		private final Class<? extends UnitConverter> type;
		private final UnitConverter[] candidates;
		private Supplier<? extends UnitConverter> identitySupplier;

		public Class<? extends UnitConverter> getType() { return type; }
		public UnitConverter[] getCandidates() { return candidates; }

		@SafeVarargs
		private <T extends UnitConverter> ConverterType(
				Class<T> type,
				Supplier<T> identitySupplier,
				T ... instances) {
			this.type = type;
			this.identitySupplier = identitySupplier;
			this.candidates = instances;
		}

		public boolean hasIdentity() {
			return identitySupplier!=null;
		}

		public UnitConverter getIdentity() {
			return identitySupplier.get();
		}

	}
	
	@Test
	@DisplayName("Setup for tests should include all converter types.")
	public void setupForTestShouldIncludeAllTypes() throws Exception {
		assertEquals(ConverterType.values().length, ConverterType.typeCount);
	}

	@Nested
	@DisplayName("Any converter type should ...")
	@ExtendWith(ConverterTypesForTests.class)
	public class ConverterTypeTests {

		@RepeatedTest(
				value = ConverterType.typeCount, 
				name = "{currentRepetition} of {totalRepetitions} candidates")
		@DisplayName("(if has identity) provide identity")
		public void testIdentityByConstruction(ConverterType c0) {

			String msg = String.format("testing %s", c0);

			if(c0.hasIdentity()) {

				final UnitConverter _I;

				try {
					_I = c0.getIdentity(); // get identity by construction
				} catch (Exception e) {
					fail(msg+": "+e.getMessage());
					return;
				}

				assertTrue(_I.isIdentity(), msg);
				assertTrue(_I.isLinear(), msg);  // identity must always be linear
				assertTrue(_I.concatenate(_I).isIdentity(), msg);

				assertIdentityCalculus(_I, RANDOM_VALUES_REPEAT_COUNT);
			}

		}		

	}


	@Nested
	@DisplayName("Any converter should ...")
	@ExtendWith(UnitConverterForCompositionTests.class)
	public class CompositionTests {

		@RepeatedTest(
				value = ConverterType.candidateCount, 
				name = "{currentRepetition} of {totalRepetitions} candidates")
		@DisplayName("compose with inverse to identity, commute with itself and with identity")
		public void testIdentityByComposition(UnitConverter u0) {

			String msg = String.format("testing %s", u0);

			UnitConverter _I = identityOf(u0); // get identity by composition

			assertTrue(_I.isIdentity(), msg);
			assertTrue(_I.isLinear(), msg);  // identity must always be linear
			assertTrue(_I.concatenate(_I).isIdentity(), msg);

			assertTrue(commutes(u0, u0), msg);
			assertTrue(commutes(u0, _I), msg);
			assertTrue(commutes(_I, u0), msg);
		}

		@RepeatedTest(
				value = ConverterType.candidateCount, 
				name = "{currentRepetition} of {totalRepetitions} candidates")
		@DisplayName("(if identity) calculate like identity")
		public void testIdentityCalculus(UnitConverter u0) {
			UnitConverter _I = identityOf(u0);
			assertIdentityCalculus(_I, RANDOM_VALUES_REPEAT_COUNT); 
		}


		@RepeatedTest(value = ConverterType.candidateCount * ConverterType.candidateCount)
		@DisplayName("(if scaling) commute with any other that is scaling")
		public void commuteWithScaling(UnitConverter u1, UnitConverter u2) {
			if(u1.isLinear() && u2.isLinear()) {
				assertTrue(commutes(u1, u2), String.format("testing %s %s", u1, u2));
				assertCommutingCalculus(u1, u2, RANDOM_VALUES_REPEAT_COUNT); 
			}
		}

	}


	// -- HELPER

	private UnitConverter identityOf(UnitConverter a) {
		return a.concatenate(a.inverse()); // a.(a^-1) == identity
	}

	private boolean commutes(UnitConverter a, UnitConverter b) {
		// a.b == (b^-1).(a^-1), must always hold
		// a.b == (a^-1).(b^-1), only holds if a and b commute (a.b == b.a)
		UnitConverter ab = a.concatenate(b);
		UnitConverter ba = b.concatenate(a);

		boolean commutes = ab.concatenate(ba.inverse()).isIdentity();
		
		if(!commutes) {
			System.out.println("Does not resolve to identity, but should!");
			System.out.println("ab: "+ab);
			System.out.println("ba: "+ba);
			System.out.println("id: "+ab.concatenate(ba.inverse()));
			System.out.println();
		}

		return commutes;
	}
	
	private double nextRandomValue() {
		double randomRange = Math.pow(10., random.nextInt(65)-32); // [10^-32..10^32]
		double randomFactor = 2.*random.nextDouble()-1.; // [-1..1]
		double randomValue = randomFactor * randomRange;
		return randomValue;
	}

	private void assertIdentityCalculus(UnitConverter a, int repeating) {
		for(int i=0; i<repeating; ++i) {
			double randomValue = nextRandomValue();
			// double calculus
			assertEquals(randomValue, a.convert(randomValue), 1E-12, 
					String.format("testing %s: identity calculus failed for double value %f", 
							a, randomValue));
			// BigDecimal calculus
			BigDecimal bdRandomValue = BigDecimal.valueOf(randomValue);
			// we assume a.convert(BigDecimal) returns BigDecimal, but this is not a strict requirement
			assertEquals(0, bdRandomValue.compareTo((BigDecimal) a.convert(bdRandomValue)), 
					String.format("testing %s: identity calculus failed for double value %f "
							+ "using BigDecimal", 
							a, randomValue));
		}
	}
	
	private void assertCommutingCalculus(UnitConverter a, UnitConverter b, int repeating) {

		UnitConverter ab = a.concatenate(b);
		UnitConverter ba = b.concatenate(a);

		for(int i=0; i<repeating; ++i) {
			double randomValue = nextRandomValue();
			
			// double calculus
			assertEquals(ab.convert(randomValue), ba.convert(randomValue), 1E-12, 
					String.format("testing %s: commuting calculus failed for double value %f", 
							a, randomValue));
			// BigDecimal calculus
			BigDecimal bdRandomValue = BigDecimal.valueOf(randomValue);
			
			// we assume AbstractConverter.convert(BigDecimal) returns BigDecimal, 
			// but this is not a strict requirement
			BigDecimal abValue = (BigDecimal) ab.convert(bdRandomValue);
			BigDecimal baValue = (BigDecimal) ba.convert(bdRandomValue);

			assertEquals(0, abValue.compareTo(baValue), 
					String.format("testing %s: commuting calculus failed for double value %f "
							+ "using BigDecimal", 
							a, randomValue));
		}
	}

	// -- HELPER - PARAMETER PROVIDER - 1  

	private static class ConverterTypesForTests implements ParameterResolver {

		private Map<String, Integer> indexByContext = new HashMap<>();

		@Override
		public boolean supportsParameter(
				ParameterContext parameterContext,
				ExtensionContext extensionContext) throws ParameterResolutionException {
			return parameterContext.getParameter().getType() == ConverterType.class;
		}

		@Override
		public Object resolveParameter(
				ParameterContext parameterContext,
				ExtensionContext extensionContext) throws ParameterResolutionException {

			String conextKey = parameterContext.getDeclaringExecutable().toString()+":"+parameterContext.getIndex();

			int next = indexByContext.compute(conextKey, (__, index)->index!=null ? index+1 : 0);
			int modulus = BigInteger.valueOf(ConverterType.candidateCount).pow(1+parameterContext.getIndex()).intValue();
			int divisor = BigInteger.valueOf(ConverterType.candidateCount).pow(parameterContext.getIndex()).intValue();

			next = (next % modulus) / divisor;

			ConverterType candidate = ConverterType.values()[next%ConverterType.typeCount];

			return candidate;
		}
	}

	// -- HELPER - PARAMETER PROVIDER - 2  

	private static class UnitConverterForCompositionTests implements ParameterResolver {

		private Map<String, Integer> indexByContext = new HashMap<>();

		@Override
		public boolean supportsParameter(
				ParameterContext parameterContext,
				ExtensionContext extensionContext) throws ParameterResolutionException {
			return parameterContext.getParameter().getType() == UnitConverter.class;
		}

		@Override
		public Object resolveParameter(
				ParameterContext parameterContext,
				ExtensionContext extensionContext) throws ParameterResolutionException {

			String conextKey = parameterContext.getDeclaringExecutable().toString()+":"+parameterContext.getIndex();

			int next = indexByContext.compute(conextKey, (__, index)->index!=null ? index+1 : 0);
			int modulus = BigInteger.valueOf(ConverterType.candidateCount).pow(1+parameterContext.getIndex()).intValue();
			int divisor = BigInteger.valueOf(ConverterType.candidateCount).pow(parameterContext.getIndex()).intValue();

			next = (next % modulus) / divisor;

			UnitConverter candidate = ConverterType.values()[next/ConverterType.candidatesPerType]
					.getCandidates()[next%ConverterType.candidatesPerType];

			return candidate;
		}
	}

}
