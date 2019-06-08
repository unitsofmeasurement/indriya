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
package tech.units.indriya.function;

import java.math.MathContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import tech.units.indriya.spi.NumberSystem;

/**
 * Facade for internal number arithmetic.
 * 
 * @author Andi Huber
 * @author Werner Keil
 * @since 2.0
 */
public final class Calculus {
		
	/**
	 * The default MathContext used for BigDecimal calculus.
	 */
	public static final MathContext DEFAULT_MATH_CONTEXT = MathContext.DECIMAL128;

	/**
	 * Exposes (non-final) the MathContext used for BigDecimal calculus.
	 */
	public static MathContext MATH_CONTEXT = DEFAULT_MATH_CONTEXT;
	
    private static final String DEFAULT_NUMBER_SYSTEM = "tech.units.indriya.internal.function.calc.DefaultNumberSystem";

    /**
     * All available {@link NumberSystem NumberSystems} used for Number arithmetic.
     */
    public static List<NumberSystem> getAvailableNumberSystems() {
        List<NumberSystem> systems = new ArrayList<>();
        ServiceLoader<NumberSystem> loader = ServiceLoader.load(NumberSystem.class);
        loader.forEach(NumberSystem -> {
            systems.add(NumberSystem);
        });
        return systems;
    }

    /**
     * Returns the default {@link NumberSystem} used for Number arithmetic.
     */
    public static NumberSystem getNumberSystem() {
        return getNumberSystem(DEFAULT_NUMBER_SYSTEM);
    }

    /**
     * Returns the given {@link NumberSystem} used for Number arithmetic by (class) name.
     */
    public static NumberSystem getNumberSystem(String providerName) {
        ServiceLoader<NumberSystem> loader = ServiceLoader.load(NumberSystem.class);
        Iterator<NumberSystem> it = loader.iterator();
        while (it.hasNext()) {
            NumberSystem provider = it.next();
            if (providerName.equals(provider.getClass().getName())) {
                return provider;
            }
        }
        throw new IllegalArgumentException("NumberSystem " + providerName + " not found");
    }
    
}
