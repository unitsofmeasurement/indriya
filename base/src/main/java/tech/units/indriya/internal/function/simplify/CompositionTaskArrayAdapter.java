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
package tech.units.indriya.internal.function.simplify;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;

/**
 * Package private array element visitor utility for CompositionTask.
 * 
 * @author Andi Huber
 * @version 1.0
 * @since 2.0
 */
final class CompositionTaskArrayAdapter<T> {

  private final T[] array;

  public static <T> CompositionTaskArrayAdapter<T> of(T[] array){
    return new CompositionTaskArrayAdapter<T>(array);
  }

  private CompositionTaskArrayAdapter(T[] array) {
    this.array = array;
  }

  /**
   * For the underlying array visits all sequential pairs of elements.
   * @param visitor
   */
  public void visitSequentialPairs(BiConsumer<T, T> visitor) {
    if(array.length<2) {
      return;
    }
    for(int i=1; i<array.length; ++i) {
      visitor.accept(array[i-1], array[i]);
    }
  }

  /**
   * @param visitor must either return null (meaning no simplification found) or a simplification 
   * @return the number of simplifications that could be found and were applied
   */
  public int visitSequentialPairsAndSimplify(BinaryOperator<T> visitor) {
    if(array.length<2) {
      return 0;
    }
    int simplificationCount = 0;
    for(int i=1;i<array.length;++i) {
      if(array[i-1]==null) {
        continue;
      }
      T simplification = visitor.apply(array[i-1], array[i]);
      if(simplification!=null) {
        array[i-1] = simplification;
        array[i] = null;
        ++simplificationCount;
      }
    }
    return simplificationCount;
  }

  /**
   * @param nullCount since we know this number in advance, we use it to speed up this method
   * @return a new array with {@code nullCount} null-elements removed  
   */
  public T[] removeNulls(int nullCount) {
    final T[] result = Arrays.copyOf(array, array.length-nullCount);
    int j=0;
    for(int i=0;i<array.length;++i) {
      if(array[i]!=null) {
        result[j++] = array[i];	
      }
    }
    return result;
  }

}