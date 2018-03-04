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
package tec.units.indriya.quantity;

import static org.junit.Assert.assertEquals;
import javax.measure.Quantity;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.Assert;
import org.junit.Test;

import tec.units.indriya.quantity.IntegerQuantity;
import tec.units.indriya.quantity.Quantities;
import tec.units.indriya.unit.MetricPrefix;
import tec.units.indriya.unit.Units;

public class IntegerQuantityTest {

  @Test
  public void divideTest() {
    IntegerQuantity<ElectricResistance> quantity1 = new IntegerQuantity<ElectricResistance>(Long.valueOf(3).intValue(), Units.OHM);
    IntegerQuantity<ElectricResistance> quantity2 = new IntegerQuantity<ElectricResistance>(Long.valueOf(2).intValue(), Units.OHM);
    Quantity<?> result = quantity1.divide(quantity2);
    assertEquals(Double.valueOf(1.5d), result.getValue());
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public void addTest() {
    IntegerQuantity quantity1 = new IntegerQuantity(Long.valueOf(1).intValue(), Units.OHM);
    IntegerQuantity quantity2 = new IntegerQuantity(Long.valueOf(2).intValue(), Units.OHM);
    Quantity<ElectricResistance> result = quantity1.add(quantity2);
    assertEquals(Short.valueOf("3").intValue(), result.getValue().intValue());
  }

  @Test
  public void subtractTest() {
    IntegerQuantity<ElectricResistance> quantity1 = new IntegerQuantity<ElectricResistance>(Long.valueOf(1).intValue(), Units.OHM);
    IntegerQuantity<ElectricResistance> quantity2 = new IntegerQuantity<ElectricResistance>(Long.valueOf(2).intValue(), Units.OHM);
    Quantity<ElectricResistance> result = quantity2.subtract(quantity1);
    assertEquals(Short.valueOf("1").intValue(), result.getValue().intValue());
    assertEquals(Units.OHM, result.getUnit());
  }

  @Test
  public void multiplyQuantityTest() {
    IntegerQuantity<ElectricResistance> quantity1 = new IntegerQuantity<ElectricResistance>(Long.valueOf(3).intValue(), Units.OHM);
    IntegerQuantity<ElectricResistance> quantity2 = new IntegerQuantity<ElectricResistance>(Long.valueOf(2).intValue(), Units.OHM);
    Quantity<?> result = quantity1.multiply(quantity2);
    assertEquals(Integer.valueOf(6), result.getValue());
  }

  @Test
  public void longValueTest() {
    IntegerQuantity<Time> day = new IntegerQuantity<Time>(Integer.valueOf(3).intValue(), Units.DAY);
    long hours = day.longValue(Units.HOUR);
    assertEquals(72L, hours);
  }

  @Test
  public void doubleValueTest() {
    IntegerQuantity<Time> day = new IntegerQuantity<Time>(Integer.valueOf(3).intValue(), Units.DAY);
    double hours = day.doubleValue(Units.HOUR);
    assertEquals(72D, hours, 0);
  }

  @Test
  public void toTest() {
    Quantity<Time> day = Quantities.getQuantity(1D, Units.DAY);
    Quantity<Time> hour = day.to(Units.HOUR);
    Assert.assertEquals(hour.getValue().intValue(), 24);
    Assert.assertEquals(hour.getUnit(), Units.HOUR);

    Quantity<Time> dayResult = hour.to(Units.DAY);
    Assert.assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
    Assert.assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
  }

  @Test
  public void testEquality() throws Exception {
    Quantity<Length> value = Quantities.getQuantity(new Integer(10), Units.METRE);
    Quantity<Length> anotherValue = Quantities.getQuantity(new Integer(10), Units.METRE);
    assertEquals(value, anotherValue);
  }
  
  @Test
  public void milliOhmTest() {
	  final IntegerQuantity<ElectricResistance> ONE_OHM = new IntegerQuantity<ElectricResistance>(Long.valueOf(1).intValue(), Units.OHM);
	  final IntegerQuantity<ElectricResistance> ONE_MILLIOHM = new IntegerQuantity<ElectricResistance>(Long.valueOf(1).intValue(), MetricPrefix.MILLI(Units.OHM));

	  assertEquals(ONE_OHM, ONE_OHM.add(ONE_MILLIOHM));
	  final IntegerQuantity<ElectricResistance> ONEOONE_MILLIOHM = new IntegerQuantity<ElectricResistance>(Integer.valueOf(1001), MetricPrefix.MILLI(Units.OHM));
	  assertEquals(ONEOONE_MILLIOHM, ONE_MILLIOHM.add(ONE_OHM));
  }
  
}
