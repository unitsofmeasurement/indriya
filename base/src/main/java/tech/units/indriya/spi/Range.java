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
package tech.units.indriya.spi;

import java.util.Objects;

import tech.uom.lib.common.function.MaximumSupplier;
import tech.uom.lib.common.function.MinimumSupplier;

/**
 * A Range is a pair of <code>T</code> items that represent a range of values.
 * <p>
 * Subclasses of Range should be immutable.
 * 
 * @param <T>
 *          The value of the range.
 * 
 * @author <a href="mailto:werner@units.tech">Werner Keil</a>
 * @version 1.1, August 21, 2019
 * @since 1.0
 * @see <a href="http://en.wikipedia.org/wiki/Range">Wikipedia: Range</a>
 */
public abstract class Range<T> implements MinimumSupplier<T>, MaximumSupplier<T> {
  // XXX do we keep null for min and max to represent infinity?
  // Java 8 Optional was evaluated, but caused conflict with the type-safe
  // Quantity feature of this API
  private final T min;
  private final T max;
  private T res;

  /**
   * Construct an instance of Range with a min, max and res value.
   *
   * @param min
   *          The minimum value for the range.
   * @param max
   *          The maximum value for the range.
   * @param res
   *          The resolution of the range.
   */
  protected Range(T min, T max, T res) {
    this.min = min;
    this.max = max;
    this.res = res;
  }

  /**
   * Construct an instance of Range with a min and max value.
   *
   * @param min
   *          The minimum value for the range.
   * @param max
   *          The maximum value for the range.
   */
  protected Range(T min, T max) {
    this.min = min;
    this.max = max;
  }

  /**
   * Returns the smallest value of the range. The value is the same as that given as the constructor parameter for the smallest value.
   * 
   * @return the minimum value
   */
  public T getMinimum() {
    return min;
  }

  /**
   * Returns the largest value of the range. The value is the same as that given as the constructor parameter for the largest value.
   * 
   * @return the maximum value
   */
  public T getMaximum() {
    return max;
  }

  /**
   * Returns the resolution of the range. The value is the same as that given as the constructor parameter for the largest value.
   * 
   * @return resolution of the range, the value is the same as that given as the constructor parameter for the resolution
   */
  public T getResolution() {
    return res;
  }

  /**
   * Method to easily check if {@link #getMinimum()} is not {@code null}.
   * 
   * @return {@code true} if {@link #getMinimum()} is not {@code null} .
   */
  public boolean hasMinimum() {
    return min != null;
  }

  /**
   * Method to easily check if {@link #getMaximum()} is not {@code null}.
   * 
   * @return {@code true} if {@link #getMaximum()} is not {@code null}.
   */
  public boolean hasMaximum() {
    return max != null;
  }

  /**
   * Checks whether the given <code>T</code> is within this range
   * 
   * @param t
   * @return true if the value is within the range
   */
  public abstract boolean contains(T t);

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals()
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Range<?>) {
      @SuppressWarnings("unchecked")
      final Range<T> other = (Range<T>) obj;
      return Objects.equals(getMinimum(), other.getMinimum()) && Objects.equals(getMaximum(), other.getMaximum())
          && Objects.equals(getResolution(), other.getResolution());
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hash(min, max, res);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder().append("min= ").append(getMinimum()).append(", max= ").append(getMaximum());
    if (res != null) {
      sb.append(", res= ").append(getResolution());
    }
    return sb.toString();
  }
}
