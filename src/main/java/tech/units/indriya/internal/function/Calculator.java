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
package tech.units.indriya.internal.function;

import java.util.Objects;

import tech.units.indriya.function.Calculus;
import tech.units.indriya.spi.NumberSystem;

/**
 * Provides arithmetic on Java {@link Number}s utilizing a provided {@link NumberSystem}.    
 * 
 * @author Andi Huber
 * @author Werner Keil
 * @version 1.1
 * @since 2.0
 */
public class Calculator {

    /**
     * Returns a new instance of a {@code Calculator} initialized with the default {@link NumberSystem}, 
     * as set at {@link Calculus#currentNumberSystem()}
     * <p>
     * This implementation is *not* thread-safe, hence threads should not share instances of this. 
     * @return a {@code Calculator} initialized with the default {@link NumberSystem} 
     */
    private static Calculator getInstance() {
        return new Calculator(Calculus.currentNumberSystem());
    }

    /**
     * Shortcut for {@code getDefault().load(number)}. See {@link #getInstance()} and {@link #load(Number)}
     * @param number
     * @return default {@code Calculator} with {@code number} loaded into its accumulator
     */
    public static Calculator of(Number number) {
        return getInstance().load(number);
    }

    private final NumberSystem ns;
    private Number acc = 0;
    
    private Calculator(NumberSystem ns) {
        this.ns = ns;
    }

    /**
     * Loads {@code number} into this {@code Calculator}´s accumulator. 
     * @param number
     * @return self
     */
    private Calculator load(Number number) {
        Objects.requireNonNull(number);
        this.acc = ns.narrow(number);
        return this;
    }
    
    /**
     * Adds {@code number} to this {@code Calculator}´s accumulator, 
     * then stores the result in the accumulator.
     * @param number
     * @return self
     */
    public Calculator add(Number number) {
        Objects.requireNonNull(number);
        acc = ns.add(acc, ns.narrow(number));    
        return this;
    }

    /**
     * Subtracts {@code number} from this {@code Calculator}´s accumulator, 
     * then stores the result in the accumulator.
     * @param number
     * @return self
     */
    public Calculator subtract(Number number) {
        Objects.requireNonNull(number);
        acc = ns.subtract(acc, ns.narrow(number));
        return this;
    }
    
    /**
     * Multiplies {@code number} with this {@code Calculator}´s accumulator, 
     * then stores the result in the accumulator.
     * @param number
     * @return self
     */
    public Calculator multiply(Number number) {
        acc = ns.multiply(acc, ns.narrow(number));    
        return this;
    }

    /**
     * Divides this {@code Calculator}´s accumulator by {@code number}, 
     * then stores the result in the accumulator.
     * @param number
     * @return self
     */
    public Calculator divide(Number number) {
        acc = ns.divide(acc, ns.narrow(number));    
        return this;
    }
    
    /**
     * Takes this {@code Calculator}´s accumulator to the integer power of {@code exponent},
     * then stores the result in the accumulator.
     * @param exponent
     * @return self
     */
    public Calculator power(int exponent) {
        acc = ns.power(acc, exponent);    
        return this;
    }
    
    /**
     * Calculates the absolute value of this {@code Calculator}´s accumulator,
     * then stores the result in the accumulator.
     * @return self
     */
    public Calculator abs() {
        acc = ns.abs(acc);
        return this;
    }

    /**
     * Calculates the additive inverse value of this {@code Calculator}´s accumulator,
     * then stores the result in the accumulator.
     * @return self
     */
    public Calculator negate() {
        acc = ns.negate(acc);
        return this;
    }

    /**
     * Calculates the multiplicative inverse value of this {@code Calculator}´s accumulator,
     * then stores the result in the accumulator.
     * @return self
     */
    public Calculator reciprocal() {
        acc = ns.reciprocal(acc);
        return this;
    }
    
    /**
     * Calculates Euler's constant taken to the power of this {@code Calculator}´s accumulator,
     * then stores the result in the accumulator.
     * @return self
     */
    public Calculator exp() {
        acc = ns.exp(acc);
        return this;
    }
    
    /**
     * Calculates the natural logarithm of this {@code Calculator}´s accumulator,
     * then stores the result in the accumulator.
     * @return self
     */
    public Calculator log() {
        acc = ns.log(acc);
        return this;
    }
    
    // -- TERMINALS
    
    /**
     * Allows to 'peek' at this {@code Calculator}´s accumulator. The {@link Number} returned is narrowed
     * to best represent the numerical value w/o loss of precision within the {@link NumberSystem} as 
     * configured for this {@code Calculator} instance.
     * @return a narrowed version of this {@code Calculator}´s accumulator
     */
    public Number peek() {
        return ns.narrow(acc);
    }
    
    /**
     * @return whether this {@code Calculator}´s accumulator is less than ONE
     */
    public boolean isLessThanOne() {
        return ns.isLessThanOne(acc);
    }
}
