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

import javax.measure.quantity.Time;

import org.junit.Assert;
import org.junit.Test;

import tech.units.indriya.function.QuantitySummaryStatistics;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public class QuantitySummaryStatisticsTest {

  @Test
  public void shouldBeEmpty() {
    QuantitySummaryStatistics<Time> summary = new QuantitySummaryStatistics<>(Units.DAY);
    Assert.assertEquals(0L, summary.getCount());
    Assert.assertEquals(0L, summary.getMin().getValue().longValue());
    Assert.assertEquals(0L, summary.getMax().getValue().longValue());
    Assert.assertEquals(0L, summary.getSum().getValue().longValue());
    Assert.assertEquals(0L, summary.getAverage().getValue().longValue());

  }

  @Test(expected = NullPointerException.class)
  public void shouldErrorWhenIsNull() {
    QuantitySummaryStatistics<Time> summary = new QuantitySummaryStatistics<>(Units.DAY);
    summary.accept(null);
  }

  @Test
  public void shouldBeSameValueWhenOneMonetaryIsAdded() {
    QuantitySummaryStatistics<Time> summary = new QuantitySummaryStatistics<>(Units.DAY);

    summary.accept(Quantities.getQuantity(10, Units.DAY));
    Assert.assertEquals(1L, summary.getCount());
    Assert.assertEquals(10L, summary.getMin().getValue().longValue());
    Assert.assertEquals(10L, summary.getMax().getValue().longValue());
    Assert.assertEquals(10L, summary.getSum().getValue().longValue());
    Assert.assertEquals(10L, summary.getAverage().getValue().longValue());

    Assert.assertEquals(240L, summary.getMin(Units.HOUR).getValue().longValue());
    Assert.assertEquals(240L, summary.getMax(Units.HOUR).getValue().longValue());
    Assert.assertEquals(240L, summary.getSum(Units.HOUR).getValue().longValue());
    Assert.assertEquals(240L, summary.getAverage(Units.HOUR).getValue().longValue());
  }

  @Test
  public void shouldBeSameEquivalentValueWhenisConverted() {
    QuantitySummaryStatistics<Time> summary = new QuantitySummaryStatistics<>(Units.DAY);

    summary.accept(Quantities.getQuantity(10, Units.DAY));

    Assert.assertEquals(240L, summary.getMin(Units.HOUR).getValue().longValue());
    Assert.assertEquals(240L, summary.getMax(Units.HOUR).getValue().longValue());
    Assert.assertEquals(240L, summary.getSum(Units.HOUR).getValue().longValue());
    Assert.assertEquals(240L, summary.getAverage(Units.HOUR).getValue().longValue());
  }

  @Test
  public void convertSummaryTest() {

    QuantitySummaryStatistics<Time> summary = new QuantitySummaryStatistics<>(Units.DAY);

    summary.accept(Quantities.getQuantity(10, Units.DAY));
    QuantitySummaryStatistics<Time> summaryHour = summary.to(Units.HOUR);
    Assert.assertEquals(240L, summaryHour.getMin().getValue().longValue());
    Assert.assertEquals(240L, summaryHour.getMax().getValue().longValue());
    Assert.assertEquals(240L, summaryHour.getSum().getValue().longValue());
    Assert.assertEquals(240L, summaryHour.getAverage().getValue().longValue());

  }

  @Test
  public void addTest() {
    QuantitySummaryStatistics<Time> summary = createSummaryTime();
    Assert.assertEquals(3L, summary.getCount());
    Assert.assertEquals(1L, summary.getMin().getValue().longValue());
    Assert.assertEquals(9L, summary.getMax().getValue().longValue());
    Assert.assertEquals(12L, summary.getSum().getValue().longValue());
    Assert.assertEquals(4L, summary.getAverage().getValue().longValue());
  }

  @Test
  public void combineTest() {
    QuantitySummaryStatistics<Time> summaryA = createSummaryTime();
    QuantitySummaryStatistics<Time> summaryB = createSummaryTime();
    QuantitySummaryStatistics<Time> summary = summaryA.combine(summaryB);

    Assert.assertEquals(6L, summary.getCount());
    Assert.assertEquals(1L, summary.getMin().getValue().longValue());
    Assert.assertEquals(9L, summary.getMax().getValue().longValue());
    Assert.assertEquals(24L, summary.getSum().getValue().longValue());
    Assert.assertEquals(4L, summary.getAverage().getValue().longValue());
  }

  private QuantitySummaryStatistics<Time> createSummaryTime() {
    QuantitySummaryStatistics<Time> summary = new QuantitySummaryStatistics<>(Units.DAY);

    summary.accept(Quantities.getQuantity(9, Units.DAY));
    summary.accept(Quantities.getQuantity(48, Units.HOUR));
    summary.accept(Quantities.getQuantity(1440, Units.MINUTE));
    return summary;
  }

}
