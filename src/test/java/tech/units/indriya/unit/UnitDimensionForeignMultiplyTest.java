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
package tech.units.indriya.unit;

import java.util.Map;

import javax.measure.Dimension;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 
 * Tests {@link UnitDimension#multiply(Dimension)} and 
 * {@link UnitDimension#divide(Dimension)} for when the
 * {@link Dimension} argument is not an instance of UnitDimension,
 * meaning a foreign dimension implementation, not known to this library.
 * 
 */
class UnitDimensionForeignMultiplyTest {

    @Test
    void multiplyByForeignDimension() {
        Dimension result = UnitDimension.LENGTH.multiply(new ForeingDimension());
        assertEquals(UnitDimension.LENGTH, result);
    }

    @Test
    void divideByForeignDimension() {
        Dimension result = UnitDimension.LENGTH.divide(new ForeingDimension());
        assertEquals(UnitDimension.LENGTH, result);
    }
    
    static class ForeingDimension implements Dimension {

        private Dimension delegate = UnitDimension.NONE;
        
        @Override
        public Dimension multiply(Dimension multiplicand) {
            return delegate.multiply(multiplicand);
        }

        @Override
        public Dimension divide(Dimension divisor) {
            return delegate.divide(divisor);
        }

        @Override
        public Dimension pow(int n) {
            return delegate.pow(n);
        }

        @Override
        public Dimension root(int n) {
            return delegate.root(n);
        }

        @Override
        public Map<? extends Dimension, Integer> getBaseDimensions() {
            return delegate.getBaseDimensions();
        }
        
    }
    
    
}
