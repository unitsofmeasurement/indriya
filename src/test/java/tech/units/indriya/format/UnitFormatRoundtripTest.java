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
package tech.units.indriya.format;

import javax.measure.format.UnitFormat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import tech.units.indriya.format.UnitFormatRoundtripUtil.NonPrefixedUnits;

/**
 * Testing (almost) all built-in units and their prefixed variants
 * against different {@link UnitFormat} instances. 
 * 
 * @author Andi Huber
 */
class UnitFormatRoundtripTest {
	
	@Nested
    @DisplayName("EBNFUnitFormat") @Disabled("yet too many errors")
    public class EBNFUnitFormatTest {
	    
	  final UnitFormat format = EBNFUnitFormat.getInstance();

        /**
         * We cycle through all {@code FormatTestingUtil.NonPrefixedUnits} and 
         * for each such candidate test, whether parsing and formating are 
         * consistent with expected behavior.
         */
	    @ParameterizedTest(name = "{index} => unit=''{0}''")
        @DisplayName("should parse and format as expected")
        @EnumSource(NonPrefixedUnits.class)
        public void formatingAndParsing(NonPrefixedUnits testCandidate) {
	        testCandidate.roundtrip(format);
        }       

    }
	
	@Nested
    @DisplayName("SimpleUnitFormat")
    public class SimpleUnitFormatTest {
        
        final UnitFormat format = SimpleUnitFormat.getInstance();

        /**
         * We cycle through all {@code FormatTestingUtil.NonPrefixedUnits} and 
         * for each such candidate test, whether parsing and formating are 
         * consistent with expected behavior.
         */
        @ParameterizedTest(name = "{index} => unit=''{0}''")
        @DisplayName("should parse and format as expected")
        @EnumSource(NonPrefixedUnits.class)
        public void formatingAndParsing(NonPrefixedUnits testCandidate) {
            testCandidate.roundtrip(format);
        }       

    }

	
}
