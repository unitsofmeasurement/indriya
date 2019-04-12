package tech.units.indriya.internal.radix;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tech.units.indriya.NumberAssertions;
import tech.units.indriya.internal.radix.Radix.DecimalRadix;
import tech.units.indriya.internal.radix.Radix.RationalRadix;

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

/**
*
* @author Andi Huber
*/
public class MixedRadixSupportTest {
    
    private Radix[] decimalRadices = {
      new DecimalRadix(BigDecimal.valueOf(60.)),
      new DecimalRadix(BigDecimal.valueOf(15.))
    };

    private Radix[] rationalRadices = {
      new RationalRadix(BigInteger.valueOf(60), BigInteger.ONE),
      new RationalRadix(BigInteger.valueOf(15), BigInteger.ONE)
    };

    @Test
    public void extractionArithmeticDecimal() {
        
        MixedRadixSupport mir = new MixedRadixSupport(decimalRadices);
        
        BigDecimal s_total = BigDecimal.valueOf(9*60*15 + 13*15 + 5.234);
        
        StringBuilder sb = new StringBuilder();
        
        mir.visitRadixNumbers(s_total, number->{
            sb.append(" "+number);
            sb.append(" ("+number.getClass().getSimpleName()+")");
            sb.append(",");
        });
        
        Assertions.assertEquals(
                " 5.234 (BigDecimal), 13 (BigInteger), 9 (BigInteger),", 
                sb.toString());
        
    }
    
    @Test
    public void extractionArithmeticRational() {
        
        MixedRadixSupport mir = new MixedRadixSupport(rationalRadices);
        
        BigDecimal s_total = BigDecimal.valueOf(9*60*15 + 13*15 + 5.234);
        
        StringBuilder sb = new StringBuilder();
        
        mir.visitRadixNumbers(s_total, number->{
            sb.append(" "+number);
            sb.append(" ("+number.getClass().getSimpleName()+")");
            sb.append(",");
        });
        
        Assertions.assertEquals(
                " 5.234 (BigDecimal), 13 (BigInteger), 9 (BigInteger),", 
                sb.toString());
        
    }
    
    @Test
    public void summationArithmeticDecimal() {
        
        MixedRadixSupport mir = new MixedRadixSupport(decimalRadices);
        
        Number[] values = {9, 13, 5.234};
        
        Number actual = mir.sumMostSignificant(values);
        
        BigDecimal expected = BigDecimal.valueOf(9*60*15 + 13*15 + 5.234);
        
        NumberAssertions.assertNumberEquals(expected, actual, 1E-9);
        
    }
    
    @Test
    public void summationArithmeticRational() {
        
        MixedRadixSupport mir = new MixedRadixSupport(rationalRadices);
        
        Number[] values = {9, 13, 5.234};
        
        Number actual = mir.sumMostSignificant(values);
        
        BigDecimal expected = BigDecimal.valueOf(9*60*15 + 13*15 + 5.234);
        
        NumberAssertions.assertNumberEquals(expected, actual, 1E-9);
        
    }
    

}
