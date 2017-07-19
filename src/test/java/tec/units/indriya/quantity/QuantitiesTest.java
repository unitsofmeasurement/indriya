/*
 * Next Generation Units of Measurement Implementation
 * Copyright (c) 2005-2017, Jean-Marie Dautelle, Werner Keil, V2COM.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static tec.units.indriya.unit.Units.PASCAL;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javax.measure.Quantity;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Time;

import org.junit.Assert;
import org.junit.Test;

import tec.units.indriya.quantity.DecimalQuantity;
import tec.units.indriya.quantity.DoubleQuantity;
import tec.units.indriya.quantity.NumberQuantity;
import tec.units.indriya.quantity.Quantities;
import tec.units.indriya.unit.Units;

/**
 *
 * @author Werner Keil
 * @version 0.3
 */
public class QuantitiesTest {

  @Test
  public void ofTest() {
    Quantity<Pressure> pressure = Quantities.getQuantity(BigDecimal.ONE, PASCAL);
    assertEquals(PASCAL, pressure.getUnit());
  }

  @Test
  public void getQuantityTest() {
    Quantity<Pressure> bigDecimalQuantity = Quantities.getQuantity(BigDecimal.ONE, PASCAL);
    Quantity<Pressure> bigIntegerQuantity = Quantities.getQuantity(BigInteger.ONE, PASCAL);

    Quantity<Pressure> shortQuantity = Quantities.getQuantity(Short.valueOf("2"), PASCAL);
    Quantity<Pressure> byteQuantity = Quantities.getQuantity(Byte.valueOf("2"), PASCAL);
    Quantity<Pressure> longQuantity = Quantities.getQuantity(Long.valueOf("2"), PASCAL);
    Quantity<Pressure> intQuantity = Quantities.getQuantity(Integer.valueOf("2"), PASCAL);
    Quantity<Pressure> floatQuantity = Quantities.getQuantity(Float.valueOf("2"), PASCAL);
    Quantity<Pressure> doubleQuantity = Quantities.getQuantity(Double.valueOf("2"), PASCAL);

    assertTrue(Short.class.isInstance(shortQuantity.getValue()));
    assertTrue(Byte.class.isInstance(byteQuantity.getValue()));
    assertTrue(Long.class.isInstance(longQuantity.getValue()));
    assertTrue(Integer.class.isInstance(intQuantity.getValue()));
    assertTrue(Float.class.isInstance(floatQuantity.getValue()));
    assertTrue(Double.class.isInstance(doubleQuantity.getValue()));
    assertTrue(BigDecimal.class.isInstance(bigIntegerQuantity.getValue()));
    assertTrue(BigDecimal.class.isInstance(bigDecimalQuantity.getValue()));

    assertTrue(NumberQuantity.class.isInstance(shortQuantity));
    assertTrue(NumberQuantity.class.isInstance(byteQuantity));
    assertTrue(NumberQuantity.class.isInstance(longQuantity));
    assertTrue(NumberQuantity.class.isInstance(intQuantity));
    assertTrue(NumberQuantity.class.isInstance(floatQuantity));
    assertTrue(DoubleQuantity.class.isInstance(doubleQuantity));
    assertTrue(DecimalQuantity.class.isInstance(bigIntegerQuantity));
    assertTrue(DecimalQuantity.class.isInstance(bigDecimalQuantity));

  }

  @Test
  public void toTest() {
    Quantity<Time> minute = Quantities.getQuantity(BigDecimal.ONE, Units.YEAR);
    Quantity<Time> second = minute.to(Units.SECOND);
    BigDecimal value = (BigDecimal) second.getValue();
    value.setScale(4, RoundingMode.HALF_EVEN);
    BigDecimal expected = BigDecimal.valueOf(31557816);
    Assert.assertEquals(expected.setScale(4, RoundingMode.HALF_EVEN), value.setScale(4, RoundingMode.HALF_EVEN));
  }

}