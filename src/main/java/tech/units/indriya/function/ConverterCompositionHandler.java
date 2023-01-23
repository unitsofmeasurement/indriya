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
package tech.units.indriya.function;

import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;

import tech.units.indriya.internal.function.simplify.UnitCompositionHandlerYieldingNormalForm;

/**
 * Functional interface for handling the composition (concatenation) of two unit converters.
 * 
 * @author Andi Huber
 * @author Werner Keil
 * @version 1.2
 * @since 2.0
 */
public interface ConverterCompositionHandler {

    /**
     * Takes two converters {@code left}, {@code right} and returns a (not necessarily new) 
     * converter that is equivalent to the mathematical composition of these:
     * <p>
     * compose(left, right) === left o right 
     * 
     * <p>
     * Implementation Note: Instead of using AbstractConverter as parameter 
     * and result types, this could be generalized to UnitConverter, but that 
     * would require some careful changes within AbstractConverter itself.
     *  
     * @param left
     * @param right
     * @param canReduce
     * @param doReduce
     * @return
     */
    public AbstractConverter compose(
            AbstractConverter left, 
            AbstractConverter right,
            BiPredicate<AbstractConverter, AbstractConverter> canReduce,
            BinaryOperator<AbstractConverter> doReduce);
    
    // -- FACTORIES (BUILT-IN) 
    
    /**
     * @return the default built-in UnitCompositionHandler which is yielding a normal-form, 
     * required to decide whether two UnitConverters are equivalent
     */
    public static ConverterCompositionHandler yieldingNormalForm() {
        return new UnitCompositionHandlerYieldingNormalForm();
    }

}
