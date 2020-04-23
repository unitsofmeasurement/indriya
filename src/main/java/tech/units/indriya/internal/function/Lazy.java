/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2020, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
import java.util.function.Supplier;

/**
 * Holder of an instance of type T, supporting the <em>compute-if-absent</em> idiom in a thread-safe manner.
 * <p>
 * Not serializable!     
 * 
 * @author Andi Huber
 * @since 2.0.3
 */
public class Lazy<T> {
    private final Supplier<? extends T> supplier;
    private T value;
    private boolean memorized;

    public Lazy(Supplier<? extends T> supplier) {
        this.supplier = Objects.requireNonNull(supplier, "supplier is required");
    }

    public boolean isMemorized() {
        synchronized (this) {
            return memorized;    
        }
    }

    public void clear() {
        synchronized (this) {
            this.memorized = false;
            this.value = null;
        }
    }

    public T get() {
        synchronized (this) {
            if(memorized) {
                return value;
            }
            memorized = true;
            return value = supplier.get();    
        }
    }
    
    public void set(T value) {
        synchronized (this) {
            if(memorized) {
                throw new IllegalStateException(
                        String.format("cannot set value '%s' on Lazy that has already memoized a value", ""+value));
            }
            memorized = true;
            this.value = value;
        }
    }

}