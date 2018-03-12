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
package tech.units.indriya.quantity;

import static org.junit.Assert.assertEquals;

import javax.measure.Quantity;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.Assert;
import org.junit.Test;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.quantity.FloatQuantity;
import tech.units.indriya.quantity.NumberQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public class FloatQuantityTest {

  @Test
  public void divideTest() {
    FloatQuantity<ElectricResistance> quantity1 = new FloatQuantity<ElectricResistance>(Float.valueOf(3).floatValue(), Units.OHM);
    FloatQuantity<ElectricResistance> quantity2 = new FloatQuantity<ElectricResistance>(Float.valueOf(2).floatValue(), Units.OHM);
    Quantity<?> result = quantity1.divide(quantity2);
    assertEquals(Float.valueOf(1.5f), result.getValue());
  }

  @Test
  public void addTest() {
    FloatQuantity quantity1 = new FloatQuantity(Float.valueOf(1).floatValue(), Units.OHM);
    FloatQuantity quantity2 = new FloatQuantity(Float.valueOf(2).floatValue(), Units.OHM);
    Quantity<ElectricResistance> result = quantity1.add(quantity2);
    assertEquals(Float.valueOf(3f), result.getValue());
  }

  @Test
  public void subtractTest() {
    FloatQuantity<ElectricResistance> quantity1 = new FloatQuantity<ElectricResistance>(Float.valueOf(1).floatValue(), Units.OHM);
    FloatQuantity<ElectricResistance> quantity2 = new FloatQuantity<ElectricResistance>(Float.valueOf(2).floatValue(), Units.OHM);
    Quantity<ElectricResistance> result = quantity2.subtract(quantity1);
    assertEquals(Float.valueOf(1), result.getValue());
    assertEquals(Units.OHM, result.getUnit());
  }

  @Test
  public void multiplyQuantityTest() {
    FloatQuantity<ElectricResistance> quantity1 = new FloatQuantity<ElectricResistance>(Float.valueOf(3).floatValue(), Units.OHM);
    FloatQuantity<ElectricResistance> quantity2 = new FloatQuantity<ElectricResistance>(Float.valueOf(2).floatValue(), Units.OHM);
    Quantity<?> result = quantity1.multiply(quantity2);
    assertEquals(Float.valueOf(6L), result.getValue());
  }

  @Test
  public void longValueTest() {
    FloatQuantity<Time> day = new FloatQuantity<Time>(3F, Units.DAY);
    long hours = day.longValue(Units.HOUR);
    assertEquals(72L, hours);
  }

  @Test
  public void doubleValueTest() {
    FloatQuantity<Time> day = new FloatQuantity<Time>(3F, Units.DAY);
    double hours = day.doubleValue(Units.HOUR);
    assertEquals(72D, hours, 0);
  }

  @Test
  public void toTest() {
    Quantity<Time> day = Quantities.getQuantity(1D, Units.DAY);
    Quantity<Time> hour = day.to(Units.HOUR);
    Assert.assertEquals(Double.valueOf(24), hour.getValue());
    Assert.assertEquals(hour.getUnit(), Units.HOUR);

    Quantity<Time> dayResult = hour.to(Units.DAY);
    Assert.assertEquals(dayResult.getValue(), day.getValue());
    Assert.assertEquals(dayResult.getUnit(), day.getUnit());
  }

  @Test
  public void inverseTest() {
    AbstractQuantity<Length> l = NumberQuantity.of(Float.valueOf(10f).floatValue(), Units.METRE);
    assertEquals(Float.valueOf(1f / 10f), l.inverse().getValue());
  }

  @Test
  public void testEquality() throws Exception {
    Quantity<Length> value = Quantities.getQuantity(new Float(10), Units.METRE);
    Quantity<Length> anotherValue = Quantities.getQuantity(new Float(10.0F), Units.METRE);
    assertEquals(value, anotherValue);
  }
}
