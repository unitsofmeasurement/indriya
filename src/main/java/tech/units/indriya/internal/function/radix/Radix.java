/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2024, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
package tech.units.indriya.internal.function.radix;

import static org.apiguardian.api.API.Status.INTERNAL;

import java.util.Objects;

import javax.measure.UnitConverter;

import org.apiguardian.api.API;

import tech.units.indriya.function.Calculus;
import tech.units.indriya.spi.NumberSystem;

/**
 * Internal utility for {@link MixedRadixSupport}.
 * 
 * @author Andi Huber
 * @since 2.0
 */
@API(status=INTERNAL)
public interface Radix {

    /**
     * Multiply without precision loss. 
     * @param number
     * @return
     */
    Number multiply(Number number);
    
    /**
     * Returns a two-element Number array containing {number / radix, number % radix} 
     * @param number
     * @param roundRemainderTowardsZero - whether the division remainder should be rounded towards ZERO
     * @return
     */
    Number[] divideAndRemainder(Number number, boolean roundRemainderTowardsZero);
    
    // -- FACTORIES
    
    public static Radix ofNumberFactor(Number number) {
        return new NumberFactorRadix(number);
    }
    
    public static Radix ofMultiplyConverter(UnitConverter linearUnitConverter) {
        Objects.requireNonNull(linearUnitConverter, "unitConverter cannot be null");
        if(!linearUnitConverter.isLinear()) {
            throw new IllegalArgumentException("unitConverter is expected to be a linear transformation "
                    + "(a linear function without offset)");
        }
        Number radix = Calculus.currentNumberSystem().narrow(linearUnitConverter.convert(1));
        return new NumberFactorRadix(radix);
    }
    
    // -- RADIX IMPLEMENTATION
    
    //can be made private with later java versions 
    public static class NumberFactorRadix implements Radix {
        
        private final Number radix;
        
        public NumberFactorRadix(Number radix) {
            this.radix = ns().narrow(radix);
        }

        @Override
        public Number multiply(Number number) {
            Number result = ns().multiply(radix, ns().narrow(number));
            return ns().narrow(result);
        }

        @Override
        public Number[] divideAndRemainder(Number number, boolean roundRemainderTowardsZero) {
            
            Number[] result =  ns().divideAndRemainder(
                    ns().narrow(number), 
                    radix, 
                    roundRemainderTowardsZero);
            result[0] = ns().narrow(result[0]);
            result[1] = ns().narrow(result[1]);
            return result;
        }
        
        private static NumberSystem ns() {
            return Calculus.currentNumberSystem();
        }
        
    }
    
    
}
