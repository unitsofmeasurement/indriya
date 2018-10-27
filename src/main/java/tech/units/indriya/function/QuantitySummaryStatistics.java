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
package tech.units.indriya.function;

import static tech.uom.lib.common.function.QuantityFunctions.*;

import java.util.Objects;
import java.util.function.BinaryOperator;

import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.quantity.Quantities;

/**
 * @author Otavio
 * @author Werner
 * @version 1.1
 * @since 1.0
 * @param <Q>
 */
public class QuantitySummaryStatistics<Q extends Quantity<Q>> {

  private final Quantity<Q> empty;

  private long count;

  private Quantity<Q> min;

  private Quantity<Q> max;

  private Quantity<Q> sum;

  private Quantity<Q> average;

  private final BinaryOperator<Quantity<Q>> minFunctions = min();

  private final BinaryOperator<Quantity<Q>> maxFunctions = max();

  /**
   * Creates a new instance, targeting the given {@link javax.measure.Unit}.
   * 
   * @param unit
   *          the target unit, not null.
   */
  QuantitySummaryStatistics(Unit<Q> unit) {
    empty = Quantities.getQuantity(0, unit);
    setQuantity(empty);
  }

  /**
   * Records another value into the summary information.
   * 
   * @param quantity
   *          the input quantity value to be added, not null.
   */
  public void accept(Quantity<Q> quantity) {

    Objects.requireNonNull(quantity);

    if (isEmpty()) {
      setQuantity(quantity.to(empty.getUnit()));
      count++;
    } else {
      doSummary(quantity.to(empty.getUnit()));
    }
  }

  /**
   * Combines the state of another {@code QuantitySummaryStatistics} into this one.
   * 
   * @param quantitySummary
   *          another {@code QuantitySummaryStatistics}, not null.
   */
  public QuantitySummaryStatistics<Q> combine(QuantitySummaryStatistics<Q> quantitySummary) {
    Objects.requireNonNull(quantitySummary);

    if (!equals(quantitySummary)) {
      return this;
    }

    min = minFunctions.apply(min, quantitySummary.min.to(empty.getUnit()));
    max = maxFunctions.apply(max, quantitySummary.max.to(empty.getUnit()));
    sum = sum.add(quantitySummary.sum);
    count += quantitySummary.count;
    average = sum.divide(count);
    return this;
  }

  private void doSummary(Quantity<Q> moneraty) {
    min = minFunctions.apply(min, moneraty);
    max = maxFunctions.apply(max, moneraty);
    sum = sum.add(moneraty);
    average = sum.divide(++count);
  }

  private boolean isEmpty() {
    return count == 0;
  }

  private void setQuantity(Quantity<Q> quantity) {
    min = quantity;
    max = quantity;
    sum = quantity;
    average = quantity;
  }

  /**
   * Get the number of items added to this summary instance.
   * 
   * @return the number of summarized items, >= 0.
   */
  public long getCount() {
    return count;
  }

  /**
   * Get the minimal quantity found within this summary.
   * 
   * @return the minimal quantity
   */
  public Quantity<Q> getMin() {
    return min;
  }

  /**
   * Get the minimal quantity found within this summary converted to unit
   * 
   * @param unit
   *          to convert
   * @return the minimal quantity converted to this unit
   */
  public Quantity<Q> getMin(Unit<Q> unit) {
    return min.to(unit);
  }

  /**
   * Get the maximal amount found within this summary.
   * 
   * @return the maximal quantity
   */
  public Quantity<Q> getMax() {
    return max;
  }

  /**
   * Get the maximal amount found within this summary converted to unit
   * 
   * @param unit
   *          to convert
   * @return the maximal quantity converted to this unit
   */
  public Quantity<Q> getMax(Unit<Q> unit) {
    return max.to(unit);
  }

  /**
   * Get the sum of all amounts within this summary.
   * 
   * @return the total amount
   */
  public Quantity<Q> getSum() {
    return sum;
  }

  /**
   * Get the sum of all amounts within this summary converted to unit
   * 
   * @param unit
   *          to convert
   * @return the total amount converted to this unit
   */
  public Quantity<Q> getSum(Unit<Q> unit) {
    return sum.to(unit);
  }

  /**
   * Get the quantity average of all amounts added.
   * 
   * @return the quantity average quantity
   */
  public Quantity<Q> getAverage() {
    return average;
  }

  /**
   * Get the quantity average of all amounts added converted to unit
   * 
   * @param unit
   *          to convert
   * @return the average quantity converted to this unit
   */
  public Quantity<Q> getAverage(Unit<Q> unit) {
    return average.to(unit);
  }

  /**
   * convert the summary to this unit measure
   * 
   * @param unit
   *          to convert the summary
   * @return the summary converted to this unit
   */
  public QuantitySummaryStatistics<Q> to(Unit<Q> unit) {
    QuantitySummaryStatistics<Q> summary = new QuantitySummaryStatistics<>(unit);
    summary.average = average.to(unit);
    summary.count = count;
    summary.max = max.to(unit);
    summary.min = min.to(unit);
    summary.sum = sum.to(unit);
    return summary;
  }

  /**
   * will equals when the unit were equals
   */
  @Override
  public boolean equals(Object obj) {
    if (QuantitySummaryStatistics.class.isInstance(obj)) {
      @SuppressWarnings("rawtypes")
      QuantitySummaryStatistics other = QuantitySummaryStatistics.class.cast(obj);
      return Objects.equals(empty.getUnit(), other.empty.getUnit());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return empty.getUnit().hashCode();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("[currency: ").append(empty.getUnit()).append(",");
    sb.append("count:").append(count).append(",");
    sb.append("min:").append(min).append(",");
    sb.append("max:").append(max).append(",");
    sb.append("sum:").append(sum).append(",");
    sb.append("average:").append(average).append("]");
    return sb.toString();
  }
}
