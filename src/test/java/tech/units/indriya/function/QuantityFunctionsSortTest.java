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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import javax.measure.spi.QuantityFactory;
import javax.measure.spi.ServiceProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tech.units.indriya.function.QuantityFunctions;
import tech.units.indriya.unit.Units;

public class QuantityFunctionsSortTest {

  private QuantityFactory<Time> timeFactory;
  private Quantity<Time> day;
  private Quantity<Time> hours;
  private Quantity<Time> minutes;
  private Quantity<Time> seconds;

  @Before
  public void init() {
    ServiceProvider provider = ServiceProvider.current();
    timeFactory = provider.getQuantityFactory(Time.class);
    minutes = timeFactory.create(15, Units.MINUTE);
    hours = timeFactory.create(18, Units.HOUR);
    day = timeFactory.create(1, Units.DAY);
    seconds = timeFactory.create(100, Units.SECOND);
  }

  @Test
  public void sortNumberTest() {
    List<Quantity<Time>> times = getTimes().stream().sorted(QuantityFunctions.sortNumber()).collect(Collectors.toList());

    Assert.assertEquals(day, times.get(0));
    Assert.assertEquals(minutes, times.get(1));
    Assert.assertEquals(hours, times.get(2));
    Assert.assertEquals(seconds, times.get(3));

  }

  @Test
  public void sortNumberDescTest() {
    List<Quantity<Time>> times = getTimes().stream().sorted(QuantityFunctions.sortNumberDesc()).collect(Collectors.toList());

    Assert.assertEquals(seconds, times.get(0));
    Assert.assertEquals(hours, times.get(1));
    Assert.assertEquals(minutes, times.get(2));
    Assert.assertEquals(day, times.get(3));
  }

  @Test
  public void sortSymbolTest() {
    List<Quantity<Time>> times = getTimes().stream().sorted(QuantityFunctions.sortSymbol()).collect(Collectors.toList());

    Assert.assertEquals(day, times.get(0));
    Assert.assertEquals(hours, times.get(1));
    Assert.assertEquals(minutes, times.get(2));
    Assert.assertEquals(seconds, times.get(3));

  }

  @Test
  public void sortSymbolDesctTest() {
    List<Quantity<Time>> times = getTimes().stream().sorted(QuantityFunctions.sortSymbolDesc()).collect(Collectors.toList());
    Assert.assertEquals(seconds, times.get(0));
    Assert.assertEquals(minutes, times.get(1));
    Assert.assertEquals(hours, times.get(2));
    Assert.assertEquals(day, times.get(3));
  }

  @Test
  public void sortNaturalTest() {
    List<Quantity<Time>> times = getTimes().stream().sorted(QuantityFunctions.sortNatural()).collect(Collectors.toList());
    Assert.assertEquals(seconds, times.get(0));
    Assert.assertEquals(minutes, times.get(1));
    Assert.assertEquals(hours, times.get(2));
    Assert.assertEquals(day, times.get(3));
  }

  @Test
  public void sortNaturalDescTest() {
    List<Quantity<Time>> times = getTimes().stream().sorted(QuantityFunctions.sortNaturalDesc()).collect(Collectors.toList());

    Assert.assertEquals(day, times.get(0));
    Assert.assertEquals(hours, times.get(1));
    Assert.assertEquals(minutes, times.get(2));
    Assert.assertEquals(seconds, times.get(3));
  }

  @Test
  public void sortNaturalAndSymbolTest() {
    List<Quantity<Time>> times = new ArrayList<>(getTimes());
    Quantity<Time> dayinHour = timeFactory.create(24, Units.HOUR);
    times.add(dayinHour);

    Comparator<Quantity<Time>> sortNatural = QuantityFunctions.sortNatural();
    Comparator<Quantity<Time>> sortSymbol = QuantityFunctions.sortSymbol();

    List<Quantity<Time>> result = times.stream().sorted(sortNatural.thenComparing(sortSymbol)).collect(Collectors.toList());
    Assert.assertEquals(seconds, result.get(0));
    Assert.assertEquals(minutes, result.get(1));
    Assert.assertEquals(hours, result.get(2));
    Assert.assertEquals(day, result.get(3));
    Assert.assertEquals(dayinHour, result.get(4));
  }

  private List<Quantity<Time>> getTimes() {
    return Arrays.asList(day, hours, minutes, seconds);
  }
}
