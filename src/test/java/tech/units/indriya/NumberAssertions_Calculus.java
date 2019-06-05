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
package tech.units.indriya;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import tech.units.indriya.function.RationalNumber;
import tech.units.indriya.internal.function.calc.DefaultNumberSystem;

/**
 * Package private arithmetic helper for {@link NumberAssertions}
 * 
 * @author Andi Huber
 *
 */
final class NumberAssertions_Calculus {
    
    private static final String MSG_NUMBER_NON_NULL = "number cannot be null";

    /**
     * @implNote this is almost a copy of the private {@link DefaultNumberSystem#toBigDecimal(Number)}
     * except, that we also handle {@link RationalNumber} conversion here. 
     */
    public static BigDecimal toBigDecimal(Number number) {
        Objects.requireNonNull(number, MSG_NUMBER_NON_NULL);
        if(number instanceof BigDecimal) {
            return (BigDecimal) number;
        }
        if(number instanceof BigInteger) {
            return new BigDecimal((BigInteger) number);
        }
        if(number instanceof Long || 
                number instanceof AtomicLong ||
                number instanceof Integer || 
                number instanceof AtomicInteger ||
                number instanceof Short || 
                number instanceof Byte) {
            return BigDecimal.valueOf(number.longValue());
        }
        if(number instanceof Double || number instanceof Float) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        if(number instanceof RationalNumber) {
            // we accept possible loss of precision due to rounding or truncation here
            return ((RationalNumber) number).bigDecimalValue(); 
        }
        throw unsupportedNumberType(number);
    }
    
    // -- HELPER
    
    private static IllegalArgumentException unsupportedNumberType(Number number) {
        final String msg = String.format("Unsupported number type '%s'",
                number.getClass().getName());
        throw new IllegalArgumentException(msg);
    }
    
    
    
}
