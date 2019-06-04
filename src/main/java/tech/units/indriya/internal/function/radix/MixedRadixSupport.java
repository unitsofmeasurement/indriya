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

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Consumer;

import tech.units.indriya.function.Calculus;
import tech.units.indriya.function.MixedRadix;
import tech.units.indriya.internal.function.calc.Calculator;

/**
 * Internal utility class to support {@link MixedRadix}.
 * 
 * @author Andi Huber
 * @since 2.0
 */
public class MixedRadixSupport {

    private final Radix[] radices;
    private final MathContext mc;

    /**
     * 
     * @param radices - most significant first
     */
    public MixedRadixSupport(Radix[] radices) {
        this.radices = radices;
        this.mc = new MathContext(Calculus.MATH_CONTEXT.getPrecision(), RoundingMode.FLOOR);
    }
    
    /**
     * 
     * @param trailingRadixValue
     * @param numberVisitor - gets past over the extracted numbers in least significant first order
     */
    public void visitRadixNumbers(Number trailingRadixValue, Consumer<Number> numberVisitor) {
        
        Number total = trailingRadixValue;
        
        for(int i=0;i<radices.length;++i) {
            
            Radix radix = radices[invertIndex(i)];
            
            boolean fractionalRemainder = i==0;
            
            Number[] divideAndRemainder = radix.divideAndRemainder(total, mc, fractionalRemainder); 
            
            Number remainder = divideAndRemainder[1];
            
            numberVisitor.accept(remainder);

            total = divideAndRemainder[0];
            
        }
        
        numberVisitor.accept(total);
        
    }

    public Number sumMostSignificant(Number[] values) {

        int maxAllowedValueIndex = values.length - 1; 
        
        Number sum = values[0];
        
        for(int i=0;i<radices.length;++i) {
            
            sum = radices[i].multiply(sum);
            
            if(i >= maxAllowedValueIndex) {
                continue;
            }
            
            sum = Calculator.loadDefault(sum).add(values[i+1]).peek();
            
        }
        
        return sum;
        
    }
    
    // -- HELPER
    
    private int invertIndex(int index) {
        return radices.length - index - 1;
    }

}
