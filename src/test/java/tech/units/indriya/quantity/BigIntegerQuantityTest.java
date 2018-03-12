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

import java.math.BigInteger;

import javax.measure.Quantity;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.Assert;
import org.junit.Test;

import tech.units.indriya.quantity.BigIntegerQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.MetricPrefix;
import tech.units.indriya.unit.Units;

public class BigIntegerQuantityTest {

  @Test
  public void divideTest() {
    BigIntegerQuantity<ElectricResistance> quantity1 = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(3).intValue(), Units.OHM);
    BigIntegerQuantity<ElectricResistance> quantity2 = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(2).intValue(), Units.OHM);
    Quantity<?> result = quantity1.divide(quantity2);
    assertEquals(BigInteger.ONE, result.getValue());
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public void addTest() {
    BigIntegerQuantity quantity1 = new BigIntegerQuantity(Long.valueOf(1).intValue(), Units.OHM);
    BigIntegerQuantity quantity2 = new BigIntegerQuantity(Long.valueOf(2).intValue(), Units.OHM);
    Quantity<ElectricResistance> result = quantity1.add(quantity2);
    assertEquals(Short.valueOf("3").intValue(), result.getValue().intValue());
  }

  @Test
  public void subtractTest() {
    BigIntegerQuantity<ElectricResistance> quantity1 = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(1).intValue(), Units.OHM);
    BigIntegerQuantity<ElectricResistance> quantity2 = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(2).intValue(), Units.OHM);
    Quantity<ElectricResistance> result = quantity2.subtract(quantity1);
    assertEquals(Short.valueOf("1").intValue(), result.getValue().intValue());
    assertEquals(Units.OHM, result.getUnit());
  }

  @Test
  public void multiplyQuantityTest() {
    final BigIntegerQuantity<ElectricResistance> quantity1 = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(3).intValue(), Units.OHM);
    BigIntegerQuantity<ElectricResistance> quantity2 = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(2).intValue(), Units.OHM);
    Quantity<?> result = quantity1.multiply(quantity2);
    assertEquals(BigInteger.valueOf(6), result.getValue());
  }

  @Test
  public void longValueTest() {
    final BigIntegerQuantity<Time> day = new BigIntegerQuantity<Time>(Long.valueOf(3), Units.DAY);
    long hours = day.longValue(Units.HOUR);
    assertEquals(72L, hours);
  }

  @Test
  public void doubleValueTest() {
    BigIntegerQuantity<Time> day = new BigIntegerQuantity<Time>(Long.valueOf(3), Units.DAY);
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
    Quantity<Length> value = Quantities.getQuantity(new Long(10), Units.METRE);
    Quantity<Length> anotherValue = Quantities.getQuantity(new Long(10), Units.METRE);
    assertEquals(value, anotherValue);
  }

  @Test
  public void milliOhmTest() {
    final BigIntegerQuantity<ElectricResistance> ONE_OHM = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(1), Units.OHM);
    final BigIntegerQuantity<ElectricResistance> ONE_MILLIOHM = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(1),
        MetricPrefix.MILLI(Units.OHM));

    assertEquals(ONE_OHM, ONE_OHM.add(ONE_MILLIOHM));
    final BigIntegerQuantity<ElectricResistance> ONEOONE_MILLIOHM = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(1001),
        MetricPrefix.MILLI(Units.OHM));
    assertEquals(ONEOONE_MILLIOHM, ONE_MILLIOHM.add(ONE_OHM));
  }

  @Test
  public void yottaOhmTest() {
    final BigIntegerQuantity<ElectricResistance> ONE_OHM = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(1), Units.OHM);
    final BigIntegerQuantity<ElectricResistance> ONE_YOTTAOHM = new BigIntegerQuantity<ElectricResistance>(Long.valueOf(1),
        MetricPrefix.YOTTA(Units.OHM));
    final BigIntegerQuantity<ElectricResistance> RESULT_OHM = new BigIntegerQuantity<ElectricResistance>(new BigInteger("9223372036854775808"),
        Units.OHM);

    assertEquals(RESULT_OHM, ONE_OHM.add(ONE_YOTTAOHM));
  }
}
