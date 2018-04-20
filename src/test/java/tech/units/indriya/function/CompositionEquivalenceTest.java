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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.measure.UnitConverter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

@DisplayName("Testing Composition of UnitConverters")
public class CompositionEquivalenceTest {
	
	public static enum ConverterType {
		
		POWER(PowerConverter.class,
				PowerConverter.of(3, 7), 
				PowerConverter.of(7, -3)	),
		RATIONAL(RationalConverter.class,
				RationalConverter.of(17, 13),
				RationalConverter.of(-34, 17)	),
		MULTIPLAY(MultiplyConverter.class,
				new MultiplyConverter(17.23),
				new MultiplyConverter(-0.333) ),
		ADD(AddConverter.class,
				new AddConverter(-4.5),
				new AddConverter(0.315) ),
		LOG(LogConverter.class,
				new LogConverter(4.5),
				new LogConverter(0.1) ),
		// when adding entries, also increment the typeCount!
		//TODO there are more ...
		;

		public static final int typeCount = 5; // should be equal to ConverterType.values().length 
		public static final int candidatesPerType = 2;
		public static final int candidateCount = typeCount * candidatesPerType;
		
		private final Class<? extends UnitConverter> type;
		private final UnitConverter[] candidates;
		
		public Class<? extends UnitConverter> getType() { return type; }
		public UnitConverter[] getCandidates() { return candidates; }

		private ConverterType(Class<? extends UnitConverter> type, UnitConverter ... instances) {
			this.type = type;
			this.candidates = instances;
		}

	}
	
    @Nested
    @DisplayName("When composing, any converter should ...")
    @ExtendWith(UnitConverterParameterResolver.class)
    public class Identity {
    	
    	@RepeatedTest(
    			value = ConverterType.candidateCount, 
    			name = "{currentRepetition} of {totalRepetitions} candidates")
        @DisplayName("compose with inverse to identity, commute with itself and with identity")
    	public void testIdentity(UnitConverter u0) {
    		
    		String msg = String.format("testing %s", u0);

    		UnitConverter _I = identityOf(u0);
    		
    		assertEquals(true, _I.isIdentity(), msg);
    		assertEquals(true, _I.isLinear(), msg);  // identity must always be linear
    		assertEquals(true, _I.concatenate(_I).isIdentity(), msg);
    		assertEquals(true, commutes(u0, u0), msg);
    		assertEquals(true, commutes(u0, _I), msg);
    		assertEquals(true, commutes(_I, u0), msg);
    		
    	}
    	
    	@RepeatedTest(value = ConverterType.candidateCount * ConverterType.candidateCount)
        @DisplayName("(if scaling) commute with any other that is scaling")
    	public void commuteWithScaling(UnitConverter u1, UnitConverter u2) {
    		if(u1.isLinear() && u2.isLinear()) {
    			assertEquals(true, commutes(u1, u2), String.format("testing %s %s", u1, u2));
    		}
    	}

    }

	
	// -- HELPER

	private UnitConverter identityOf(UnitConverter a) {
		return a.concatenate(a.inverse()); // a.a^-1 == identity
	}

	private boolean commutes(UnitConverter a, UnitConverter b) {
		UnitConverter ab = a.concatenate(b);
		UnitConverter ba = b.concatenate(a);
		return ab.concatenate(ba.inverse()).isIdentity();
	}

	// -- UNIT CONVERTER PARAMETER PROVIDER  
	
	private static class UnitConverterParameterResolver implements ParameterResolver {

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
