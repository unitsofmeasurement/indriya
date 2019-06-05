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
package tech.units.indriya.internal.function.radix;

import javax.measure.UnitConverter;

import tech.units.indriya.function.Calculus;
import tech.units.indriya.function.NumberSystem;

/**
 * Internal utility for {@link MixedRadixSupport}.
 * 
 * @author Andi Huber
 * @since 2.0
 */
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
    
    // -- RADIX IMPLEMENTATION - UnitConverterRadix

    public static class UnitConverterRadix implements Radix {
        
        private final UnitConverter unitConverter;
        private final Number radix;
        
        public UnitConverterRadix(UnitConverter unitConverter) {
            this.unitConverter = unitConverter;
            this.radix = ns().narrow(unitConverter.convert(1));
        }

        @Override
        public Number multiply(Number number) {
            return unitConverter.convert(number);
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
        
        private NumberSystem ns() {
            return Calculus.NUMBER_SYSTEM;
        }
        
    }

//TODO[220] remove once we know we don't need this anymore
//    // -- RADIX IMPLEMENTATION - DECIMAL
//
//    public static class DecimalRadix implements Radix {
//        
//        private final BigDecimal r;
//
//        public DecimalRadix(BigDecimal r) {
//            this.r = r;
//        }
//
//        @Override
//        public Number multiply(Number number) {
//            return Calculus.toBigDecimal(number).multiply(r);
//        }
//        
//        @Override
//        public Number[] divideAndRemainder(Number number, MathContext mc, boolean fractionalRemainder) {
//            Number[] result = Calculus.toBigDecimal(number).divideAndRemainder(r, mc);
//            
//            return new Number[] {
//                    toNonFractional((BigDecimal)result[0]),
//                    fractionalRemainder ? result[1] : toNonFractional((BigDecimal)result[1])};
//        }
//
//        private Number toNonFractional(BigDecimal bigDecimal) {
//            try {
//                return bigDecimal.toBigIntegerExact();
//            } catch (ArithmeticException e) {
//                return bigDecimal; // fallback
//            }
//        }
//        
//    }
//    
//    // -- RADIX IMPLEMENTATION - RATIONAL
//    
//    public static class RationalRadix implements Radix {
//        
//        private final BigInteger r_dividend;
//        private final BigInteger r_divisor;
//        private final DecimalRadix decimalRadix;
//
//        public RationalRadix(BigInteger r_dividend, BigInteger r_divisor) {
//            this.r_dividend = r_dividend;
//            this.r_divisor = r_divisor;
//            
//            BigDecimal r = Calculus.toBigDecimal(r_dividend).divide(Calculus.toBigDecimal(r_divisor));
//            this.decimalRadix = new DecimalRadix(r); 
//        }
//        
//        @Override
//        public Number multiply(Number number) {
//            
//            if(Calculus.NUMBER_SYSTEM.isInteger(number)) {
//                // multiply by this radix's rational
//                BigInteger[] divideAndRemainder = Calculus.toBigInteger(number)
//                        .multiply(r_dividend).divideAndRemainder(r_divisor);
//                
//                BigInteger remainder = divideAndRemainder[1];
//                if(BigInteger.ZERO.equals(remainder)) {
//                    return divideAndRemainder[0];
//                }
//                
//            } 
//            
//            return decimalRadix.multiply(number);
//        }
//
//        @Override
//        public Number[] divideAndRemainder(Number number, MathContext mc, boolean fractionalDivide) {
//            
//            if(Calculus.NUMBER_SYSTEM.isInteger(number)) {
//                // divide by this radix's rational, that is multiply by its reciprocal
//                return Calculus.toBigInteger(number).multiply(r_divisor).divideAndRemainder(r_dividend);
//            } 
//            
//            return decimalRadix.divideAndRemainder(number, mc, fractionalDivide);
//        }
//        
//    }

    
    
}
