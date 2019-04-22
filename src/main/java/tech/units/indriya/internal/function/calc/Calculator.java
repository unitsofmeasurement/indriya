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
package tech.units.indriya.internal.function.calc;

import java.util.Objects;

import tech.units.indriya.function.Calculus;

/**
 * Provides basic arithmetic on Java {@link Number}s.   
 * 
 * @author Andi Huber
 * @since 2.0
 */
public class Calculator {

    public static Calculator getDefault() {
        return new Calculator(Calculus.NUMBER_SYSTEM);
    }

    public static Calculator loadDefault(Number number) {
        return getDefault().load(number);
    }

    private final NumberSystem ns;
    private Number acc = 0;
    
    private Calculator(NumberSystem ns) {
        this.ns = ns;
    }

    public Calculator load(Number number) {
        Objects.requireNonNull(number);
        this.acc = number;
        return this;
    }
    
    public Calculator add(Number number) {
        Objects.requireNonNull(number);
        acc = ns.add(acc, number);    
        return this;
    }
    
    public Calculator subtract(Number number) {
        Objects.requireNonNull(number);
        acc = ns.subtract(acc, number);
        return this;
    }
    
    public Calculator multiply(Number number) {
        acc = ns.multiply(acc, number);    
        return this;
    }

    public Calculator divide(Number number) {
        acc = ns.divide(acc, number);    
        return this;
    }

    public Calculator negate() {
        acc = ns.negate(acc);
        return this;
    }

    public Calculator reciprocal() {
        acc = ns.reciprocal(acc);
        return this;
    }
    
    public Number peek() {
        return ns.narrow(acc);
    }
  
    
}
