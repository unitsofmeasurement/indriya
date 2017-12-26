/*
 * Next Generation Units of Measurement Implementation
 * Copyright (c) 2005-2017, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
 * 3. Neither the name of JSR-363, Indriya nor the names of their contributors may be used to endorse or promote products
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

import java.math.BigDecimal;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.Assert;
import org.junit.Test;

import tec.units.indriya.quantity.Quantities;
import tec.units.indriya.unit.Units;

public class DecimalQuantityTest {

  @Test
  public void divideTest() {
    Quantity<Length> metre = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    Quantity<Length> result = metre.divide(10D);
    Assert.assertTrue(result.getValue().intValue() == 1);
    Assert.assertEquals(result.getUnit(), Units.METRE);

    Quantity<Time> day = Quantities.getQuantity(BigDecimal.TEN, Units.DAY);
    Quantity<Time> dayResult = day.divide(BigDecimal.valueOf(2.5D));
    Assert.assertTrue(dayResult.getValue().intValue() == 4);
    Assert.assertEquals(dayResult.getUnit(), Units.DAY);
  }

  @Test
  public void addTest() {
    Quantity<Length> m = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    Quantity<Length> m2 = Quantities.getQuantity(BigDecimal.valueOf(12.5), Units.METRE);
    Quantity<Length> m3 = Quantities.getQuantity(2.5, Units.METRE);
    Quantity<Length> m4 = Quantities.getQuantity(5L, Units.METRE);
    Quantity<Length> result = m.add(m2).add(m3).add(m4);
    Assert.assertTrue(result.getValue().doubleValue() == 30.0);
    Assert.assertEquals(result.getUnit(), Units.METRE);
  }

  @Test
  public void addQuantityTest() {
    Quantity<Time> day = Quantities.getQuantity(BigDecimal.ONE, Units.DAY);
    Quantity<Time> hours = Quantities.getQuantity(BigDecimal.valueOf(12), Units.HOUR);
    Quantity<Time> result = day.add(hours);
    Assert.assertTrue(result.getValue().doubleValue() == 1.5);
    Assert.assertEquals(result.getUnit(), Units.DAY);
  }

  @Test
  public void subtractTest() {
    Quantity<Length> m = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    Quantity<Length> m2 = Quantities.getQuantity(12.5, Units.METRE);
    Quantity<Length> result = m.subtract(m2);
    Assert.assertTrue(result.getValue().doubleValue() == -2.5);
    Assert.assertEquals(result.getUnit(), Units.METRE);
  }

  @Test
  public void subtractQuantityTest() {
    Quantity<Time> day = Quantities.getQuantity(BigDecimal.ONE, Units.DAY);
    Quantity<Time> hours = Quantities.getQuantity(BigDecimal.valueOf(12), Units.HOUR);
    Quantity<Time> result = day.subtract(hours);
    Assert.assertTrue(result.getValue().doubleValue() == 0.5);
    Assert.assertEquals(result.getUnit(), Units.DAY);
  }

  @Test
  public void multiplyTest() {
    Quantity<Length> metre = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    Quantity<Length> result = metre.multiply(10D);
    Assert.assertTrue(result.getValue().intValue() == 100);
    Assert.assertEquals(result.getUnit(), Units.METRE);
    @SuppressWarnings("unchecked")
    Quantity<Length> result2 = (Quantity<Length>) metre.multiply(Quantities.getQuantity(BigDecimal.TEN, Units.METRE));
    Assert.assertTrue(result2.getValue().intValue() == 100);
  }

  @Test
  public void toTest() {
    Quantity<Time> day = Quantities.getQuantity(BigDecimal.ONE, Units.DAY);
    Quantity<Time> hour = day.to(Units.HOUR);
    Assert.assertEquals(hour.getValue().intValue(), 24);
    Assert.assertEquals(hour.getUnit(), Units.HOUR);

    Quantity<Time> dayResult = hour.to(Units.DAY);
    Assert.assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
    Assert.assertEquals(dayResult.getValue().intValue(), day.getValue().intValue());
  }

  @Test
  public void inverseTestLength() {
    @SuppressWarnings("unchecked")
    Quantity<Length> metre = (Quantity<Length>) Quantities.getQuantity(BigDecimal.TEN, Units.METRE).inverse();
    Assert.assertEquals(BigDecimal.valueOf(0.1d), metre.getValue());
    Assert.assertEquals("1/m", String.valueOf(metre.getUnit()));
  }

  @Test
  public void inverseTestTime() {
    Quantity<?> secInv = Quantities.getQuantity(BigDecimal.valueOf(2d), Units.SECOND).inverse();
    Assert.assertEquals(BigDecimal.valueOf(0.5d), secInv.getValue());
    Assert.assertEquals("1/s", String.valueOf(secInv.getUnit()));
  }

  @Test
  public void testEquality() throws Exception {
    Quantity<Length> value = Quantities.getQuantity(BigDecimal.valueOf(10.0), Units.METRE);
    Quantity<Length> anotherValue = Quantities.getQuantity(BigDecimal.TEN, Units.METRE);
    Assert.assertEquals(value, anotherValue);
  }

}
