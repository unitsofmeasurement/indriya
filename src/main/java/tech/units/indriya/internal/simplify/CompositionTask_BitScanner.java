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

import java.util.BitSet;
import java.util.function.Predicate;

/**
 * Package private bit mask utility for CompositionTask.
 * 
 * @author Andi Huber
 * @version 1.0
 * @since 2.0
 */
final class CompositionTask_BitScanner {

  @FunctionalInterface
  public static interface BiIntConsumer {
    public void accept(int i, int j); 
  }

  private final BitSet bitSet;

  private CompositionTask_BitScanner(BitSet bitSet) {
    this.bitSet = bitSet;
  }

  /**
   * @param array
   * @param bitTest
   * @return BitScanner, that holds internally a BitSet, that represents with zeros 
   * and ones, whether the specified predicate {@code bitTest} is false or true 
   * with respect to the elements of the specified {@code array}  
   */
  public static <T> CompositionTask_BitScanner of(T[] array, Predicate<T> bitTest) {
    final BitSet mask = new BitSet(array.length);
    int bitIndex = 0;
    for(T element : array) {
      if(bitTest.test(element)) {
        mask.set(bitIndex);
      }
      bitIndex++;
    }
    return new CompositionTask_BitScanner(mask);
  }

  /**
   * This BitScanner holds internally a BitSet. The specified {@code visitor} is called 
   * for each sequence of consecutive ones, where each such call passes over 2 int parameters
   * i, j.<p>
   * i .. zero based start index of the processed one-sequence<br/>
   * j .. length of the processed one-sequence<br/>
   * <p>
   * Eg. given an internal BitSet represented by eg. 0-0-1-0-1-1-1-0, the resulting visitor
   * calls would be:<br/>
   * visitor.accept(2, 1) - start at 2, length = 1<br/>
   * visitor.accept(4, 3) - start at 4, length = 3<br/>
   * 
   * @param visitor
   */
  public void visitBitSequences(BiIntConsumer visitor) {
    int scanPointer = 0;
    int nextSetBit;
    while((nextSetBit = bitSet.nextSetBit(scanPointer))>-1) {
      int nextClearBit = bitSet.nextClearBit(nextSetBit);
      if(nextClearBit==-1) {
        // only '1's till the end
        visitor.accept(nextSetBit, bitSet.size()); 	
        return;
      }
      scanPointer = nextClearBit;
      visitor.accept(nextSetBit, nextClearBit);
    }
  }
}