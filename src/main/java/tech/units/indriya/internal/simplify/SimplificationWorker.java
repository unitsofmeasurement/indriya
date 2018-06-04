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

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;

import javax.measure.UnitConverter;

import tech.units.indriya.AbstractConverter;

/**
 * Worker for the Simplifier.
 * 
 * @author Andi Huber
 * @version 1.1
 * @since 2.0
 */
final class SimplificationWorker {

	private final BiPredicate<AbstractConverter, AbstractConverter> simpleComposeTest;
	private final BinaryOperator<AbstractConverter> simpleComposeAction;
	
	private AbstractConverter[] arrayOfConverters;
	
	public SimplificationWorker(
			BiPredicate<AbstractConverter, AbstractConverter> simpleComposeTest,
			BinaryOperator<AbstractConverter> simpleComposeAction) {
		
		this.simpleComposeTest = simpleComposeTest;
		this.simpleComposeAction = simpleComposeAction;
	}

	/**
	 * Description of a brute-force approach:
	 * <p>
	 * Given 'conversionSteps' a list of converters, where order matters,
	 * we form a permutation group of all allowed permutations
	 * 'reachable' through 'allowed' swapping.
	 * Swapping is allowed for 2 consecutive converters that are both
	 * multiply transformations (x.isLinear()==true).
	 * We search this permutation group for any sequence of converters, that allows simplification:
	 * <br><br>
	 * For every pair of consecutive converters within a sequence, 
	 * check whether a simplification is possible (a.isSimpleCompositionWith(b)), 
	 * then apply simplification and start over.
	 * <br><br>
	 * Finally sort according to normal-form order.
	 * </p>
	 * @param conversionSteps
	 * @return
	 */
	public AbstractConverter simplify(List<? extends UnitConverter> conversionSteps) {

		arrayOfConverters = conversionSteps.toArray(new AbstractConverter[]{});
		
		sortToNormalFormOrder(arrayOfConverters);
		
		while(trySimplify()>0){
			sortToNormalFormOrder(arrayOfConverters);
		}
		
		return sequenceToConverter(arrayOfConverters);
	}
	
	private int trySimplify() {
		
		final ArrayAdapter<AbstractConverter> adapter = ArrayAdapter.of(arrayOfConverters);
		int simplificationCount = adapter.visitSequentialPairsAndSimplify((a, b)->{
			if(a.isIdentity()) {
				return b;
			}
			if(b.isIdentity()) {
				return a;
			}
			return simpleComposeTest.test(a, b) ? simpleComposeAction.apply(a, b) : null;
		});
		if(simplificationCount>0) {
			arrayOfConverters = adapter.removeNulls(simplificationCount);
		}
		return simplificationCount;
	}

	private static void sortToNormalFormOrder(AbstractConverter[] arrayOfConverters) {
		final BitScanner bitScanner = BitScanner.of(arrayOfConverters, UnitConverter::isLinear);
		// a bit-set 0-0-1-1-1-0 would indicate 3rd to 5th position are allowed to be put in arbitrary order
		// we sort this sub-sequences of '1's according to normal-form-order
		bitScanner.visitBitSequences((fromIndex, toIndex) -> {
			Arrays.sort(arrayOfConverters, fromIndex, toIndex, (a, b)->{
				
				if(a.isIdentity()) {
					if(b.isIdentity()) {
						return Simplifier.isNormalFormOrderWhenIdentity(a, b) ? -1 : 1;
					}
					return -1;
				}
				if(b.isIdentity()) {
					return 1;
				}
				
				return Simplifier.isNormalFormOrderWhenCommutative(a, b) ? -1 : 1;
				
			});
		});
	}

//	private static AbstractConverter sequenceToConverter(List<AbstractConverter> sequence) {
//		if(sequence.isEmpty()) {
//			return AbstractConverter.IDENTITY;
//		}
//		if(sequence.size()==1) {
//			final AbstractConverter singleton = sequence.get(0);
//			return singleton;
//		}
//		// fold the sequence into a binary tree
//		final AbstractConverter start = new AbstractConverter.Pair(sequence.get(0), sequence.get(1));
//		return sequence.stream()
//		.skip(2)
//		.reduce(start, (tree, next)->new AbstractConverter.Pair(tree, next));
//	}
	
	private static AbstractConverter sequenceToConverter(AbstractConverter[] sequence) {
		if(sequence==null || sequence.length==0) {
			return AbstractConverter.IDENTITY;
		}
		if(sequence.length==1) {
			final AbstractConverter singleton = sequence[0];
			return singleton;
		}
		// fold the sequence into a binary tree
		final AbstractConverter start = new AbstractConverter.Pair(sequence[0], sequence[1]);
		return Arrays.stream(sequence)
		.skip(2)
		.reduce(start, (tree, next)->new AbstractConverter.Pair(tree, next));
	}
	
	
}