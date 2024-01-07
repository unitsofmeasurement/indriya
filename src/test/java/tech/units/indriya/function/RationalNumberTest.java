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
package tech.units.indriya.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.units.indriya.NumberAssertions.assertNumberEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class RationalNumberTest {

    @Test
    public void equalityByValue() {
        RationalNumber rational_1 = RationalNumber.of(5, 3);   
        RationalNumber rational_2 = RationalNumber.of(10, 6);
        
        assertEquals(rational_1, rational_2);
        
    }
    
    @Test
    public void doubleNumberRepresentationWhenSmall() {
        
        RationalNumber rational_1 = RationalNumber.of(
                BigInteger.valueOf(1660538782L), 
                new BigInteger("1000000000000000000000000000000000000"));
        
        RationalNumber rational_2 = RationalNumber.of(1.660538782E-27);
        
        assertEquals(rational_1, rational_2);
        
    }
    
    @Test
    public void doubleNumberRepresentationWhenLarge() {
        
        RationalNumber rational_1 = RationalNumber.ofInteger(
                new BigInteger("123456789000000000000000000000000000"));
        
        RationalNumber rational_2 = RationalNumber.of(1.23456789E35);
        
        assertEquals(rational_1, rational_2);
        
    }
    
    @Test
    public void doubleNumberRepresentationWhenIntegerUnscaled() {
        
        RationalNumber rational_1 = RationalNumber.ofInteger(3);
        
        RationalNumber rational_2 = RationalNumber.of(3.);
        
        assertEquals(rational_1, rational_2);
        
    }
    
    
    @Test
    public void veryLargeNumberRepresentation() {
        
        RationalNumber veryLargeRational= RationalNumber.of(Double.MAX_VALUE);
        
        double actual = veryLargeRational
                .divide(RationalNumber.ofInteger(Long.MAX_VALUE))
                .doubleValue();
        
        double expected = Double.MAX_VALUE / Long.MAX_VALUE;
        
        assertNumberEquals(expected, actual, 1E-12);
        
    }
    
    
}
