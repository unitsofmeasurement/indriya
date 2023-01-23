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
package tech.units.indriya.internal.function.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import javax.measure.spi.QuantityFactory;
import javax.measure.spi.ServiceProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.units.indriya.unit.Units;
import tech.uom.lib.common.function.QuantityFunctions;

public class QuantityFunctionsFilterTest {

  private QuantityFactory<Time> timeFactory;
  private Quantity<Time> day;
  private Quantity<Time> hours;
  private Quantity<Time> minutes;
  private Quantity<Time> seconds;

  @BeforeEach
  public void init() {
    ServiceProvider factoryService = ServiceProvider.current();
    timeFactory = factoryService.getQuantityFactory(Time.class);
    minutes = timeFactory.create(15, Units.MINUTE);
    hours = timeFactory.create(18, Units.HOUR);
    day = timeFactory.create(1, Units.DAY);
    seconds = timeFactory.create(100, Units.SECOND);
  }

  @Test
  public void filterByUnitTest() {
    List<Quantity<Time>> times = new ArrayList<>(getTimes());
    times.add(timeFactory.create(30, Units.HOUR));
    List<Quantity<Time>> list = times.stream().filter(QuantityFunctions.fiterByUnit(Units.HOUR)).collect(Collectors.toList());
    assertEquals(Integer.valueOf(2), Integer.valueOf(list.size()));
  }

  @Test
  public void shouldReturnAllWhenUnitEmpty() {
    List<Quantity<Time>> times = new ArrayList<>(getTimes());
    times.add(timeFactory.create(30, Units.HOUR));
    List<Quantity<Time>> list = times.stream().filter(QuantityFunctions.fiterByUnit()).collect(Collectors.toList());
    assertEquals(Integer.valueOf(5), Integer.valueOf(list.size()));
  }

  @Test
  public void filterByNotUnitTest() {
    List<Quantity<Time>> times = new ArrayList<>(getTimes());
    times.add(timeFactory.create(30, Units.HOUR));
    List<Quantity<Time>> list = times.stream().filter(QuantityFunctions.fiterByExcludingUnit(Units.HOUR)).collect(Collectors.toList());
    assertEquals(Integer.valueOf(3), Integer.valueOf(list.size()));
  }

  @Test
  public void shouldReturnAllWhenNotUnitEmpty() {
    List<Quantity<Time>> times = new ArrayList<>(getTimes());
    times.add(timeFactory.create(30, Units.HOUR));
    List<Quantity<Time>> list = times.stream().filter(QuantityFunctions.fiterByExcludingUnit()).collect(Collectors.toList());
    assertEquals(Integer.valueOf(5), Integer.valueOf(list.size()));
  }

  @Test
  public void filterByContainsUnitsTest() {
    List<Quantity<Time>> times = new ArrayList<>(getTimes());
    times.add(timeFactory.create(30, Units.HOUR));
    List<Quantity<Time>> list = times.stream().filter(QuantityFunctions.fiterByUnit(Units.HOUR, Units.MINUTE)).collect(Collectors.toList());
    assertEquals(Integer.valueOf(3), Integer.valueOf(list.size()));
  }

  @Test
  public void isGreaterThanTest() {
    List<Quantity<Time>> times = new ArrayList<>(getTimes());
    times.add(timeFactory.create(30, Units.HOUR));
    List<Quantity<Time>> list = times.stream().filter(QuantityFunctions.isGreaterThan(15)).collect(Collectors.toList());
    assertEquals(Integer.valueOf(3), Integer.valueOf(list.size()));

  }

  @Test
  public void isGreaterThanQuantityTest() {
    List<Quantity<Time>> times = new ArrayList<>();
    times.add(timeFactory.create(30, Units.HOUR));
    times.add(timeFactory.create(24, Units.HOUR));
    times.add(timeFactory.create(1440, Units.MINUTE));
    List<Quantity<Time>> list = times.stream().filter(QuantityFunctions.isGreaterThan(timeFactory.create(1, Units.DAY))).collect(Collectors.toList());
    assertEquals(Integer.valueOf(1), Integer.valueOf(list.size()));

  }

  @Test
  public void isGreaterThanOrEqualToTest() {
    List<Quantity<Time>> times = new ArrayList<>(getTimes());
    times.add(timeFactory.create(30, Units.HOUR));
    List<Quantity<Time>> list = times.stream().filter(QuantityFunctions.isGreaterThanOrEqualTo(15)).collect(Collectors.toList());
    assertEquals(Integer.valueOf(4), Integer.valueOf(list.size()));

  }

  @Test
  public void isGreaterThanOrEqualToQuantityTest() {
    List<Quantity<Time>> times = createTimesToFilter();
    Quantity<Time> filter = timeFactory.create(1, Units.DAY);
    List<Quantity<Time>> list = times.stream().filter(QuantityFunctions.isGreaterThanOrEqualTo(filter)).collect(Collectors.toList());
    assertEquals(Integer.valueOf(3), Integer.valueOf(list.size()));

  }

  @Test
  public void isLesserThanTest() {
    List<Quantity<Time>> times = new ArrayList<>(getTimes());
    times.add(timeFactory.create(30, Units.HOUR));
    List<Quantity<Time>> list = times.stream().filter(QuantityFunctions.isLessThan(15)).collect(Collectors.toList());
    assertEquals(Integer.valueOf(1), Integer.valueOf(list.size()));

  }

  @Test
  public void isLesserThanQuantityTest() {
    List<Quantity<Time>> times = createTimesToFilter();
    Quantity<Time> filter = timeFactory.create(1, Units.DAY);
    List<Quantity<Time>> list = times.stream().filter(QuantityFunctions.isLessThan(filter)).collect(Collectors.toList());
    assertEquals(Integer.valueOf(1), Integer.valueOf(list.size()));

  }

  @Test
  public void isLesserThanOrEqualToTest() {
    List<Quantity<Time>> times = new ArrayList<>(getTimes());
    times.add(timeFactory.create(30, Units.HOUR));
    List<Quantity<Time>> list = times.stream().filter(QuantityFunctions.isLessThanOrEqualTo(15)).collect(Collectors.toList());
    assertEquals(Integer.valueOf(2), Integer.valueOf(list.size()));

  }

  @Test
  public void isLesserThanOrEqualToQuantityTest() {
    List<Quantity<Time>> times = createTimesToFilter();
    Quantity<Time> filter = timeFactory.create(1, Units.DAY);
    List<Quantity<Time>> list = times.stream().filter(QuantityFunctions.isLessThanOrEqualTo(filter)).collect(Collectors.toList());
    assertEquals(Integer.valueOf(3), Integer.valueOf(list.size()));

  }

  @Test
  public void isBetweenTest() {
    List<Quantity<Time>> times = new ArrayList<>(getTimes());
    times.add(timeFactory.create(30, Units.HOUR));
    List<Quantity<Time>> list = times.stream().filter(QuantityFunctions.isBetween(15, 30)).collect(Collectors.toList());
    assertEquals(Integer.valueOf(3), Integer.valueOf(list.size()));

  }

  @Test
  public void isBetweenQuantityTest() {
    List<Quantity<Time>> times = new ArrayList<>(getTimes());
    times.add(timeFactory.create(30, Units.HOUR));
    times.add(timeFactory.create(14, Units.HOUR));
    times.add(timeFactory.create(10, Units.HOUR));
    Quantity<Time> min = timeFactory.create(12, Units.HOUR);
    Quantity<Time> max = timeFactory.create(1, Units.DAY);
    List<Quantity<Time>> list = times.stream().filter(QuantityFunctions.isBetween(min, max)).collect(Collectors.toList());
    assertEquals(Integer.valueOf(3), Integer.valueOf(list.size()));

  }

  private List<Quantity<Time>> getTimes() {
    return Arrays.asList(day, hours, minutes, seconds);
  }

  private List<Quantity<Time>> createTimesToFilter() {
    List<Quantity<Time>> times = new ArrayList<>();
    times.add(timeFactory.create(30, Units.HOUR));
    times.add(timeFactory.create(24, Units.HOUR));
    times.add(timeFactory.create(1440, Units.MINUTE));
    times.add(timeFactory.create(0.5, Units.DAY));
    return times;
  }
}
