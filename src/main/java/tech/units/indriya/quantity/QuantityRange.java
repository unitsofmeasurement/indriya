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
package tech.units.indriya.quantity;

import java.util.Objects;

import javax.measure.Quantity;

import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.spi.Range;

/**
 * A Quantity Range is a pair of {@link Quantity} items that represent a range of values.
 * <p>
 * Range limits MUST be presented in the same scale and have the same unit as measured data values.<br>
 * Subclasses of QuantityRange should be immutable.
 * 
 * @param <Q>
 *          The value of the range.
 * 
 * @author <a href="mailto:werner@units.tech">Werner Keil</a>
 * @version 1.0, Sep 27, 2020
 * @see <a href= "http://www.botts-inc.com/SensorML_1.0.1/schemaBrowser/SensorML_QuantityRange.html"> SensorML: QuantityRange</a>
 */
public class QuantityRange<Q extends Quantity<Q>> extends Range<Quantity<Q>> {

  protected QuantityRange(Quantity<Q> min, Quantity<Q> max, Quantity<Q> resolution) {
    super(min, max, resolution);
  }

  protected QuantityRange(Quantity<Q> min, Quantity<Q> max) {
    super(min, max);
  }

  /**
   * Returns an {@code QuantityRange} with the specified values.
   *
   * @param minimum
   *          The minimum value for the quantity range.
   * @param maximum
   *          The maximum value for the quantity range.
   * @param resolution
   *          The resolution of the quantity range.
   * @return an {@code QuantityRange} with the given values
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static QuantityRange of(Quantity minimum, Quantity maximum, Quantity resolution) {
    if (!isCompatibleQuantityTriple(minimum, maximum, resolution)) {
      throw new IllegalArgumentException();
    }
    return new QuantityRange(minimum, maximum, resolution);
  }

  /**
   * Returns an {@code QuantityRange} with the specified values.
   *
   * @param minimum
   *          The minimum value for the quantity range.
   * @param maximum
   *          The maximum value for the quantity range.
   * @return a {@code QuantityRange} with the given values
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static QuantityRange of(Quantity minimum, Quantity maximum) {
    if (!isCompatibleQuantityPair(minimum, maximum)) {
      throw new IllegalArgumentException();
    }
    return new QuantityRange(minimum, maximum);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static boolean isCompatibleQuantityPair(final Quantity q1, final Quantity q2) {
    return q1 == null || q2 == null || q1.getUnit().isCompatible(q2.getUnit());
  }

  @SuppressWarnings("rawtypes")
  private static boolean isCompatibleQuantityTriple(final Quantity q1, final Quantity q2, final Quantity q3) {
    return isCompatibleQuantityPair(q1, q2) && isCompatibleQuantityPair(q1, q3) && isCompatibleQuantityPair(q2, q3);
  }

  private boolean isAboveMinimum(final Quantity<Q> q) {
    if (q instanceof ComparableQuantity) return ((ComparableQuantity<Q>) q).isGreaterThanOrEqualTo(getMinimum());

    final Quantity<Q> qConverted = q.to(getMinimum().getUnit());
    return qConverted.getValue().doubleValue() >= getMinimum().getValue().doubleValue();
  }

  private boolean isBelowMaximum(final Quantity<Q> q) {
    if (q instanceof ComparableQuantity) return ((ComparableQuantity<Q>) q).isLessThanOrEqualTo(getMaximum());

    final Quantity<Q> qConverted = q.to(getMaximum().getUnit());
    return qConverted.getValue().doubleValue() <= getMaximum().getValue().doubleValue();
  }

  private boolean fulfillsMaximumConstraint(final Quantity<Q> q) {
    return !hasMaximum() || isBelowMaximum(q);
  }

  private boolean fulfillsMinimumConstraint(final Quantity<Q> q) {
    return !hasMinimum() || isAboveMinimum(q);
  }

  @Override
  public boolean contains(final Quantity<Q> q) {
	Objects.requireNonNull(q);
    return q.getValue() != null && q.getUnit() != null && fulfillsMinimumConstraint(q) && fulfillsMaximumConstraint(q);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof QuantityRange<?>) {
      @SuppressWarnings("unchecked")
      final QuantityRange<Q> other = (QuantityRange<Q>) obj;
      return Objects.equals(getMinimum(), other.getMinimum()) && Objects.equals(getMaximum(), other.getMaximum())
          && Objects.equals(getResolution(), other.getResolution());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getMinimum(), getMaximum(), getResolution());
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder().append("min= ").append(getMinimum()).append(", max= ").append(getMaximum());
    if (getResolution() != null) {
      sb.append(", res= ").append(getResolution());
    }
    return sb.toString();
  }
}
