/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2018, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

package tech.units.indriya.internal.simplify;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;

import tech.units.indriya.AbstractConverter;
import tech.units.indriya.function.AddConverter;
import tech.units.indriya.function.ExpConverter;
import tech.units.indriya.function.LogConverter;
import tech.units.indriya.function.MultiplyConverter;
import tech.units.indriya.function.PowerOfIntConverter;
import tech.units.indriya.function.PowerOfPiConverter;
import tech.units.indriya.function.RationalConverter;

/**
 * Simplifier for UnitConverter composition yielding a normal-form.
 * A normal-form is required to decide whether two UnitConverters are equivalent.
 * 
 * @author Andi Huber
 * @version 1.0
 * @since 2.0
 */
public final class Simplifier {
	
	final static Map<Class<?>, Integer> normalFormOrder = new HashMap<>(6);
	static {
		normalFormOrder.put(AbstractConverter.IDENTITY.getClass(), 0);
		normalFormOrder.put(PowerOfIntConverter.class, 1); 
		normalFormOrder.put(RationalConverter.class, 2); 
		normalFormOrder.put(PowerOfPiConverter.class, 3);
		normalFormOrder.put(MultiplyConverter.class, 4);
		normalFormOrder.put(AddConverter.class, 5);
		normalFormOrder.put(LogConverter.class, 6); 
		normalFormOrder.put(ExpConverter.class, 7);
		normalFormOrder.put(AbstractConverter.Pair.class, 99);
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @param simpleComposeTest
	 * @param simpleComposeAction
	 * @return normal-form
	 */
	public static AbstractConverter compose(
			AbstractConverter a, 
			AbstractConverter b,
			BiPredicate<AbstractConverter, AbstractConverter> simpleComposeTest,
			BinaryOperator<AbstractConverter> simpleComposeAction
			) {
		
		if(a.isIdentity()) {
			if(b.isIdentity()) {
				return isNormalFormOrderWhenIdentity(a, b) ? a : b;
			}
			return b;
		}
		if(b.isIdentity()) {
			return a;
		}
		
		if(simpleComposeTest.test(a, b)) {
			return simpleComposeAction.apply(a, b);
		}
		
		final boolean commutative = a.isLinear() && b.isLinear(); 
		final boolean swap = commutative && !isNormalFormOrderWhenCommutative(a, b);
		
		final AbstractConverter.Pair nonSimplifiedForm = swap 
				? new AbstractConverter.Pair(b, a) 
				: new AbstractConverter.Pair(a, b); 
		
		return new SimplificationWorker(
				simpleComposeTest,
				simpleComposeAction	)
				.simplify(nonSimplifiedForm.getConversionSteps());
	}


	static boolean isNormalFormOrderWhenIdentity(AbstractConverter a, AbstractConverter b) {
		if(a.getClass().equals(b.getClass())) {
			return true;
		}
		return normalFormOrder.get(a.getClass()) <= normalFormOrder.get(b.getClass());
	}
	
	static boolean isNormalFormOrderWhenCommutative(AbstractConverter a, AbstractConverter b) {
		if(a.getClass().equals(b.getClass())) {
			if(a instanceof PowerOfIntConverter) {
				return  ((PowerOfIntConverter)a).getBase() <= ((PowerOfIntConverter)b).getBase();
			}
//			if(a instanceof LogConverter) {
//				return  ((LogConverter)a).getBase() <= ((LogConverter)b).getBase();
//			}
//			if(a instanceof ExpConverter) {
//				return  ((ExpConverter)a).getBase() <= ((ExpConverter)b).getBase();
//			}
			return true;
		}
		
		Integer orderA = Objects.requireNonNull(normalFormOrder.get(a.getClass()), 
				()->String.format("no normal-form order defined for class '%s'", a.getClass().getName()));
		Integer orderB = Objects.requireNonNull(normalFormOrder.get(b.getClass()), 
				()->String.format("no normal-form order defined for class '%s'", b.getClass().getName()));
		
		return orderA <= orderB;
	}
}