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
package tech.units.indriya.spi;

import tech.uom.lib.common.function.MaximumSupplier;
import tech.uom.lib.common.function.MinimumSupplier;

/**
 * A Range is a pair of <code>T</code> items that represent a range of values.
 * <p>
 * 
 * <dl>
 * <dt><span class="strong">API Note:</span></dt><dd>This interface places no restrictions on the mutability of
 *          implementations, however immutability is strongly recommended. All
 *          implementations should be {@link Comparable}.</dd>
 * </dl>
 * 
 * @param <T>
 *          The value of the range.
 * 
 * @author <a href="mailto:werner@units.tech">Werner Keil</a>
 * @version 2.0, August 23, 2023
 * @since 1.0
 * @see <a href="http://en.wikipedia.org/wiki/Range">Wikipedia: Range</a>
 */
public interface Range<T> extends MinimumSupplier<T>, MaximumSupplier<T> {
  // XXX do we keep null for min and max to represent infinity?

  /**
   * Returns the smallest value of the range. The value is the same as that given as the constructor parameter for the smallest value.
   * 
   * @return the minimum value
   */
  public T getMinimum();

  /**
   * Returns the largest value of the range. The value is the same as that given as the constructor parameter for the largest value.
   * 
   * @return the maximum value
   */
  public T getMaximum();

  /**
   * Returns the resolution of the range. The value is the same as that given as the constructor parameter for the largest value.
   * 
   * @return resolution of the range, the value is the same as that given as the constructor parameter for the resolution
   */
  public T getResolution();

  /**
   * Method to easily check if {@link #getMinimum()} is not {@code null}.
   * 
   * @return {@code true} if {@link #getMinimum()} is not {@code null} .
   */
  public boolean hasMinimum();

  /**
   * Method to easily check if {@link #getMaximum()} is not {@code null}.
   * 
   * @return {@code true} if {@link #getMaximum()} is not {@code null}.
   */
  public boolean hasMaximum();

  /**
   * Checks whether the given <code>T</code> is within this range
   * 
   * @param t
   * @return true if the value is within the range
   */
  public abstract boolean contains(T t);
}
