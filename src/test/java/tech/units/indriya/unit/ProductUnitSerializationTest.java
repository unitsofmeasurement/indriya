/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2023, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
package tech.units.indriya.unit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tech.units.indriya.SerializationRoundTrip;

/**
 *
 * @author Andi Huber
 */
class ProductUnitSerializationTest {
    
    private static final ProductUnit<?> KILOGRAM_METRE = 
            (ProductUnit<?>) ProductUnit.ofProduct(Units.KILOGRAM, Units.METRE);

    @Test
    public void productUnitsShouldProperlySerialize() throws Exception {
        SerializationRoundTrip.assertProperSerializationRoundTrip(KILOGRAM_METRE);
        
        final ProductUnit<?> unitAfterRoundTrip = SerializationRoundTrip.serializationRoundTrip(KILOGRAM_METRE);
        
        assertEquals(KILOGRAM_METRE.hashCode(), unitAfterRoundTrip.hashCode());
        assertEquals(KILOGRAM_METRE.getSymbol(), unitAfterRoundTrip.getSymbol());
        assertEquals(KILOGRAM_METRE.getDimension(), unitAfterRoundTrip.getDimension());
        assertEquals(KILOGRAM_METRE.getName(), unitAfterRoundTrip.getName());
        assertEquals(KILOGRAM_METRE.getUnitCount(), unitAfterRoundTrip.getUnitCount());
        assertEquals(KILOGRAM_METRE.getBaseUnits(), unitAfterRoundTrip.getBaseUnits());
         
    }

}
