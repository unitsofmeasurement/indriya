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
package tech.units.indriya.quantity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

import javax.measure.Prefix;
import javax.measure.Quantity;
import javax.measure.Unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import tech.units.indriya.format.SamplingUtil;
import tech.units.indriya.unit.Units;

/**
 * Testing serialization round-trips for all built-in Quantity types. 
 */
class EncodingUtilTest {
    
    private EncodingRoundtrip encodingRoundtrip;
    
    @BeforeEach
    void setup() {
        encodingRoundtrip = new EncodingRoundtrip();
    }
    
    @Test
    void encodeDecodeRoundtripWhenCompound() throws IOException {
        @SuppressWarnings("unchecked")
        Quantity<?> quantity = Quantities.getQuantity(
                new Number[]{1, 2, 3}, 
                new Unit[]{Units.DAY, Units.HOUR, Units.MINUTE});
        encodingRoundtrip.test(quantity);
    } 
    

    /**
     * We cycle through all {Unit, Amount} combinations and test 
     * whether XML encoding/decoding are consistent.
     * @throws IOException 
     */
    @ParameterizedTest(name = "{index} => unit=''{0}'', amount=''{1}'' ")
    @DisplayName("XML serialization roundtrip spanning {Unit, Amount} should succeed")
    @MethodSource("provideRoundtripArgs_unit_amount")
    void encodeDecodeRoundtrip(Unit<?> unit, Number amount) throws IOException {
        Quantity<?> quantity = Quantities.getQuantity(amount, unit);
        encodingRoundtrip.test(quantity);
    }
    
    /**
     * We cycle through all {Unit, Prefix} combinations and test 
     * whether XML encoding/decoding are consistent.
     * @throws IOException 
     */
    @ParameterizedTest(name = "{index} => unit=''{0}'', prefix=''{1}'' ")
    @DisplayName("XML serialization roundtrip spanning {Unit, Prefix} should succeed")
    @MethodSource("provideRoundtripArgs_unit_prefix")
    void encodeDecodeRoundtrip(Unit<?> unit, Prefix prefix) throws IOException {
        Quantity<?> quantity = Quantities.getQuantity(1.2345, unit.prefix(prefix));
        encodingRoundtrip.test(quantity);
    }       

    // -- HELPER
    
    private static Stream<Arguments> provideRoundtripArgs_unit_amount() {
        // span a 2 dimensional finite space of {Unit, Amount} 
        return SamplingUtil.units()
        .flatMap(unit->
            SamplingUtil.numbers()
            .map(amount->Arguments.of(unit, amount))
        );
        
    }
    
    private static Stream<Arguments> provideRoundtripArgs_unit_prefix() {
        // span a 2 dimensional finite space of {Unit, Prefix} 
        return SamplingUtil.units()
        .flatMap(unit->
            SamplingUtil.prefixes()
            .map(prefix->Arguments.of(unit, prefix))
        );
        
    }
    

    private static <Q extends Quantity<? extends Q>> void assertQuantityEquals(Q q1, Q q2) {
        assertEquals(q1, q2);
    }
    
    private static class EncodingRoundtrip {
        
        public <Q extends Quantity<? extends Q>> void test(final Q quantity) throws IOException {
            assertQuantityEquals(quantity, run(quantity));
        }

        /**
         * Writes given originalQuantity as serialized XML to in-memory buffer, 
         * then reconstructs the quantity from same buffer and returns it
         * @throws IOException
         */
        @SuppressWarnings("unchecked")
        private <Q extends Quantity<? extends Q>> Q run(final Q originalQuantity) throws IOException {

            byte[] buffer;

            // write XML to buffer
            try(ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                XMLEncoder encoder = new XMLEncoder(bos);
                EncodingUtil.setupXMLEncoder(encoder);

                encoder.writeObject(originalQuantity);
                encoder.close();

                buffer = bos.toByteArray();

            } 
            
            // debug .. System.out.println(new String(buffer));

            Q reconstructedQuantity;
            
            // read XML from buffer
            try(ByteArrayInputStream bis = new ByteArrayInputStream(buffer)) {
                XMLDecoder decoder = new XMLDecoder(bis);

                reconstructedQuantity = (Q) decoder.readObject();

                decoder.close();
            } 
            
            return reconstructedQuantity;

        }
        

    }

}
