/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2019, Units of Measurement project.
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
package tech.units.indriya.internal.function.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Time;
import javax.measure.spi.QuantityFactory;
import javax.measure.spi.ServiceProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.units.indriya.unit.Units;
import tech.uom.lib.common.function.QuantityFunctions;

public class QuantityFunctionsGroupTest {

  private QuantityFactory<Time> timeFactory;
  private Quantity<Time> day;
  private Quantity<Time> hours;
  private Quantity<Time> minutes;
  private Quantity<Time> seconds;

  @BeforeEach
  public void init() {
    ServiceProvider provider = ServiceProvider.current();
    timeFactory = provider.getQuantityFactory(Time.class);
    minutes = timeFactory.create(BigDecimal.valueOf(15), Units.MINUTE);
    hours = timeFactory.create(BigDecimal.valueOf(18), Units.HOUR);
    day = timeFactory.create(BigDecimal.ONE, Units.DAY);
    seconds = timeFactory.create(BigDecimal.valueOf(100), Units.SECOND);
  }

  @Test
  public void groupByTest() {
    List<Quantity<Time>> times = createTimes();
    Map<Unit<Time>, List<Quantity<Time>>> timeMap = times.stream().collect(Collectors.groupingBy(QuantityFunctions.groupByUnit()));

    assertEquals(4, timeMap.keySet().size());
    assertEquals(1, timeMap.get(Units.MINUTE).size());
    assertEquals(1, timeMap.get(Units.HOUR).size());
    assertEquals(1, timeMap.get(Units.DAY).size());
    assertEquals(1, timeMap.get(Units.SECOND).size());

  }

  @Test
  public void groupByTest2() {
    List<Quantity<Time>> times = createTimes();
    Map<Boolean, List<Quantity<Time>>> timeMap = times.stream().collect(Collectors.partitioningBy(QuantityFunctions.fiterByUnit(Units.MINUTE)));

    assertEquals(2, timeMap.keySet().size());
    assertEquals(1, timeMap.get(Boolean.TRUE).size());
    assertEquals(3, timeMap.get(Boolean.FALSE).size());
  }

  private List<Quantity<Time>> createTimes() {
    List<Quantity<Time>> times = new ArrayList<>();
    times.add(day);
    times.add(hours);
    times.add(minutes);
    times.add(seconds);
    return times;
  }
}
