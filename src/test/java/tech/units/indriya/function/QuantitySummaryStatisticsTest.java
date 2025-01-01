/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2025, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tech.units.indriya.NumberAssertions.assertNumberEquals;
import static tech.units.indriya.function.QuantityStreams.summarizeQuantity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import javax.measure.spi.QuantityFactory;
import javax.measure.spi.ServiceProvider;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public class QuantitySummaryStatisticsTest {
    
  private final static double PRECISION_GOAL = 1E-30;

  @Test
  public void shouldBeEmpty() {
    QuantitySummaryStatistics<Time> summary = new QuantitySummaryStatistics<>(Units.DAY);
    assertEquals(0L, summary.getCount());
    assertEquals(0L, summary.getMin().getValue().longValue());
    assertEquals(0L, summary.getMax().getValue().longValue());
    assertEquals(0L, summary.getSum().getValue().longValue());
    assertEquals(0L, summary.getAverage().getValue().longValue());

  }

  @Test
  public void shouldErrorWhenIsNull() {
	  assertThrows(NullPointerException.class, () -> {
		  QuantitySummaryStatistics<Time> summary = new QuantitySummaryStatistics<>(Units.DAY);
		  summary.accept(null);
	  });
  }

  @Test
  public void shouldBeSameValueWhenOneMonetaryIsAdded() {
    QuantitySummaryStatistics<Time> summary = new QuantitySummaryStatistics<>(Units.DAY);

    summary.accept(Quantities.getQuantity(10, Units.DAY));
    
    assertEquals(1L, summary.getCount());
    
    assertEquals(10L, summary.getMin().getValue().longValue());
    assertEquals(10L, summary.getMax().getValue().longValue());
    assertEquals(10L, summary.getSum().getValue().longValue());
    assertEquals(10L, summary.getAverage().getValue().longValue());

    assertEquals(240L, summary.getMin(Units.HOUR).getValue().longValue());
    assertEquals(240L, summary.getMax(Units.HOUR).getValue().longValue());
    assertEquals(240L, summary.getSum(Units.HOUR).getValue().longValue());
    assertEquals(240L, summary.getAverage(Units.HOUR).getValue().longValue());
  }

  @Test
  public void shouldBeSameEquivalentValueWhenisConverted() {
    QuantitySummaryStatistics<Time> summary = new QuantitySummaryStatistics<>(Units.DAY);

    summary.accept(Quantities.getQuantity(10, Units.DAY));

    assertEquals(240L, summary.getMin(Units.HOUR).getValue().longValue());
    assertEquals(240L, summary.getMax(Units.HOUR).getValue().longValue());
    assertEquals(240L, summary.getSum(Units.HOUR).getValue().longValue());
    assertEquals(240L, summary.getAverage(Units.HOUR).getValue().longValue());
    
    assertNumberEquals(240, summary.getMin(Units.HOUR).getValue(), PRECISION_GOAL);
    assertNumberEquals(240, summary.getMax(Units.HOUR).getValue(), PRECISION_GOAL);
    assertNumberEquals(240, summary.getSum(Units.HOUR).getValue(), PRECISION_GOAL);
    assertNumberEquals(240, summary.getAverage(Units.HOUR).getValue(), PRECISION_GOAL);
    
  }

  @Test
  public void convertSummaryTest() {

    QuantitySummaryStatistics<Time> summary = new QuantitySummaryStatistics<>(Units.DAY);

    summary.accept(Quantities.getQuantity(10, Units.DAY));
    QuantitySummaryStatistics<Time> summaryHour = summary.to(Units.HOUR);
    
    assertEquals(240L, summaryHour.getMin().getValue().longValue());
    assertEquals(240L, summaryHour.getMax().getValue().longValue());
    assertEquals(240L, summaryHour.getSum().getValue().longValue());
    assertEquals(240L, summaryHour.getAverage().getValue().longValue());
    
    assertNumberEquals(240, summaryHour.getMin().getValue(), PRECISION_GOAL);
    assertNumberEquals(240, summaryHour.getMax().getValue(), PRECISION_GOAL);
    assertNumberEquals(240, summaryHour.getSum().getValue(), PRECISION_GOAL);
    assertNumberEquals(240, summaryHour.getAverage().getValue(), PRECISION_GOAL);
  }

  @Test
  public void addTest() {
    QuantitySummaryStatistics<Time> summary = createSummaryTime();
    
    assertEquals(3L, summary.getCount());
    
    assertEquals(1L, summary.getMin().getValue().longValue());
    assertEquals(9L, summary.getMax().getValue().longValue());
    assertEquals(12L, summary.getSum().getValue().longValue());
    //assertEquals(4L, summary.getAverage().getValue().longValue()); //well, that does not work, BigDecimal.longValue() rounding rounds to floor  
    
    assertNumberEquals(1, summary.getMin().getValue(), PRECISION_GOAL);
    assertNumberEquals(9, summary.getMax().getValue(), PRECISION_GOAL);
    assertNumberEquals(12, summary.getSum().getValue(), PRECISION_GOAL);
    assertNumberEquals(4, summary.getAverage().getValue(), PRECISION_GOAL);
  }

  @Test
  public void combineTest() {
    QuantitySummaryStatistics<Time> summaryA = createSummaryTime();
    QuantitySummaryStatistics<Time> summaryB = createSummaryTime();
    QuantitySummaryStatistics<Time> summary = summaryA.combine(summaryB);

    assertEquals(6L, summary.getCount());
    
    assertEquals(1L, summary.getMin().getValue().longValue());
    assertEquals(9L, summary.getMax().getValue().longValue());
    assertEquals(24L, summary.getSum().getValue().longValue());
    assertEquals(4L, summary.getAverage().getValue().longValue());
    
    assertNumberEquals(1, summary.getMin().getValue(), PRECISION_GOAL);
    assertNumberEquals(9, summary.getMax().getValue(), PRECISION_GOAL);
    assertNumberEquals(24, summary.getSum().getValue(), PRECISION_GOAL);
    assertNumberEquals(4, summary.getAverage().getValue(), PRECISION_GOAL);
    
  }
  
  @Test
  public void summaryTest() {
    List<Quantity<Time>> times = createTimes();
    QuantitySummaryStatistics<Time> summary = times.stream().collect(summarizeQuantity(Units.HOUR));

    assertEquals(4, summary.getCount());
    assertNotNull(summary.getAverage());
    assertNotNull(summary.getCount());
    assertNotNull(summary.getMax());
    assertNotNull(summary.getMin());
    assertNotNull(summary.getSum());
  }

  // -- HELPER
  
  private static QuantitySummaryStatistics<Time> createSummaryTime() {
    QuantitySummaryStatistics<Time> summary = new QuantitySummaryStatistics<>(Units.DAY);

    summary.accept(Quantities.getQuantity(9, Units.DAY));
    summary.accept(Quantities.getQuantity(48, Units.HOUR));
    summary.accept(Quantities.getQuantity(1440, Units.MINUTE));
    return summary;
  }
  
  private List<Quantity<Time>> createTimes() {
      ServiceProvider provider = ServiceProvider.current();
      QuantityFactory<Time> timeFactory = provider.getQuantityFactory(Time.class);
      Quantity<Time> minutes = timeFactory.create(BigDecimal.valueOf(15), Units.MINUTE);
      Quantity<Time> hours = timeFactory.create(BigDecimal.valueOf(18), Units.HOUR);
      Quantity<Time> day = timeFactory.create(BigDecimal.ONE, Units.DAY);
      Quantity<Time> seconds = timeFactory.create(BigDecimal.valueOf(100), Units.SECOND);
      List<Quantity<Time>> times = new ArrayList<>();
      times.add(day);
      times.add(hours);
      times.add(minutes);
      times.add(seconds);
      return times;
    }

}
