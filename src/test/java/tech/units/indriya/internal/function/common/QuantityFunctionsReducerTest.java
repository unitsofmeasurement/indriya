/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2022, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import java.util.Arrays;
import java.util.List;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import javax.measure.spi.QuantityFactory;
import javax.measure.spi.ServiceProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.units.indriya.unit.Units;
import tech.uom.lib.common.function.QuantityFunctions;

public class QuantityFunctionsReducerTest {

  private QuantityFactory<Time> timeFactory;
  private Quantity<Time> day;
  private Quantity<Time> hours;
  private Quantity<Time> minutes;
  private Quantity<Time> seconds;

  @BeforeEach
  public void init() {
    ServiceProvider provider = ServiceProvider.current();
    timeFactory = provider.getQuantityFactory(Time.class);
    minutes = timeFactory.create(15, Units.MINUTE);
    hours = timeFactory.create(18, Units.HOUR);
    day = timeFactory.create(1, Units.DAY);
    seconds = timeFactory.create(100, Units.SECOND);
  }

  @Test
  public void minTest() {
    List<Quantity<Time>> times = getTimes();
    Quantity<Time> quantity = times.stream().reduce(QuantityFunctions.min()).get();
    assertEquals(seconds, quantity);

    List<Quantity<Time>> secondsList = Arrays.asList(timeFactory.create(300, Units.SECOND), timeFactory.create(130, Units.SECOND), seconds,
        timeFactory.create(10000, Units.SECOND));
    Quantity<Time> minSeconds = secondsList.stream().reduce(QuantityFunctions.min()).get();
    assertEquals(seconds, minSeconds);
  }

  @Test
  public void maxTest() {
    List<Quantity<Time>> times = getTimes();
    Quantity<Time> quantity = times.stream().reduce(QuantityFunctions.max()).get();
    assertEquals(day, quantity);
    Quantity<Time> max = timeFactory.create(20, Units.DAY);
    List<Quantity<Time>> dayList = Arrays.asList(timeFactory.create(3, Units.DAY), timeFactory.create(5, Units.DAY), max);
    Quantity<Time> maxDay = dayList.stream().reduce(QuantityFunctions.max()).get();
    assertEquals(max, maxDay);
  }

  @Test
  public void sumTest() {
    List<Quantity<Time>> dayList = Arrays.asList(timeFactory.create(3, Units.DAY), timeFactory.create(5, Units.DAY),
        timeFactory.create(20, Units.DAY));
    Quantity<Time> sumDay = dayList.stream().reduce(QuantityFunctions.sum()).get();
    assertEquals(Double.valueOf(sumDay.getValue().doubleValue()), Double.valueOf(28));
    assertEquals(sumDay.getUnit(), Units.DAY);
  }

  @Test
  public void shouldSumWhenHasDifferentTimeUnits() {
    List<Quantity<Time>> dayList = Arrays.asList(timeFactory.create(48, Units.HOUR), timeFactory.create(5, Units.DAY),
        timeFactory.create(1440, Units.MINUTE));
    Quantity<Time> sumHour = dayList.stream().reduce(QuantityFunctions.sum()).get();
    assertEquals(Double.valueOf(sumHour.getValue().doubleValue()), Double.valueOf(192));
    assertEquals(sumHour.getUnit(), Units.HOUR);
  }

  @Test
  public void sumWithConvertTest() {

    List<Quantity<Time>> dayList = Arrays.asList(timeFactory.create(48, Units.HOUR), timeFactory.create(5, Units.DAY),
        timeFactory.create(1440, Units.MINUTE));

    Quantity<Time> sumHour = dayList.stream().reduce(QuantityFunctions.sum(Units.HOUR)).get();
    Quantity<Time> sumDay = dayList.stream().reduce(QuantityFunctions.sum(Units.DAY)).get();
    Quantity<Time> sumMinute = dayList.stream().reduce(QuantityFunctions.sum(Units.MINUTE)).get();
    Quantity<Time> sumSecond = dayList.stream().reduce(QuantityFunctions.sum(Units.SECOND)).get();

    assertEquals(Double.valueOf(sumHour.getValue().doubleValue()), Double.valueOf(192));
    assertEquals(sumHour.getUnit(), Units.HOUR);

    assertEquals(Double.valueOf(sumDay.getValue().doubleValue()), Double.valueOf(8));
    assertEquals(sumDay.getUnit(), Units.DAY);

    assertEquals(Double.valueOf(sumMinute.getValue().doubleValue()), Double.valueOf(11520));
    assertEquals(sumMinute.getUnit(), Units.MINUTE);

    assertEquals(Double.valueOf(sumSecond.getValue().doubleValue()), Double.valueOf(691200));
    assertEquals(sumSecond.getUnit(), Units.SECOND);
  }

  private List<Quantity<Time>> getTimes() {
    return Arrays.asList(day, hours, minutes, seconds);
  }
}
