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
package tech.units.indriya.quantity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import tech.units.indriya.unit.Units;

import javax.measure.Quantity;
import javax.measure.quantity.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;
import static javax.measure.Quantity.Scale.*;
import static tech.units.indriya.unit.Units.CELSIUS;
import static tech.units.indriya.unit.Units.PASCAL;

/**
 *
 * @author Werner Keil
 * @version 0.4
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
    assertTrue(BigInteger.class.isInstance(bigIntegerQuantity.getValue()));
    assertTrue(BigDecimal.class.isInstance(bigDecimalQuantity.getValue()));

    assertTrue(ShortQuantity.class.isInstance(shortQuantity));
    assertTrue(ByteQuantity.class.isInstance(byteQuantity));
    assertTrue(LongQuantity.class.isInstance(longQuantity));
    assertTrue(NumberQuantity.class.isInstance(intQuantity)); // workaround
    assertTrue(NumberQuantity.class.isInstance(floatQuantity)); // workaround
    assertTrue(DoubleQuantity.class.isInstance(doubleQuantity));
    assertTrue(BigIntegerQuantity.class.isInstance(bigIntegerQuantity));
    assertTrue(DecimalQuantity.class.isInstance(bigDecimalQuantity));
  }

  @Test
  public void intervalTest() {
    Quantity<Temperature> bigDecimalQuantity = Quantities.getQuantity(BigDecimal.ONE, CELSIUS, RELATIVE);
    Quantity<Temperature> bigIntegerQuantity = Quantities.getQuantity(BigInteger.ONE, CELSIUS, RELATIVE);

    Quantity<Temperature> shortQuantity = Quantities.getQuantity(Short.valueOf("2"), CELSIUS, RELATIVE);
    Quantity<Temperature> byteQuantity = Quantities.getQuantity(Byte.valueOf("2"), CELSIUS, RELATIVE);
    Quantity<Temperature> longQuantity = Quantities.getQuantity(Long.valueOf("2"), CELSIUS, RELATIVE);
    Quantity<Temperature> intQuantity = Quantities.getQuantity(Integer.valueOf("2"), CELSIUS, RELATIVE);
    Quantity<Temperature> floatQuantity = Quantities.getQuantity(Float.valueOf("2"), CELSIUS, RELATIVE);
    Quantity<Temperature> doubleQuantity = Quantities.getQuantity(Double.valueOf("2"), CELSIUS, RELATIVE);

    assertTrue(Short.class.isInstance(shortQuantity.getValue()));
    assertTrue(Byte.class.isInstance(byteQuantity.getValue()));
    assertTrue(Long.class.isInstance(longQuantity.getValue()));
    assertTrue(Integer.class.isInstance(intQuantity.getValue()));
    assertTrue(Float.class.isInstance(floatQuantity.getValue()));
    assertTrue(Double.class.isInstance(doubleQuantity.getValue()));
    assertTrue(BigInteger.class.isInstance(bigIntegerQuantity.getValue()));
    assertTrue(BigDecimal.class.isInstance(bigDecimalQuantity.getValue()));

    assertTrue(ShortQuantity.class.isInstance(shortQuantity));
    assertTrue(ByteQuantity.class.isInstance(byteQuantity));
    assertTrue(LongQuantity.class.isInstance(longQuantity));
    assertTrue(NumberQuantity.class.isInstance(intQuantity)); // workaround
    assertTrue(NumberQuantity.class.isInstance(floatQuantity)); // workaround
    assertTrue(DoubleQuantity.class.isInstance(doubleQuantity));
    assertTrue(BigIntegerQuantity.class.isInstance(bigIntegerQuantity));
    assertTrue(DecimalQuantity.class.isInstance(bigDecimalQuantity));
    
    assertEquals(RELATIVE, shortQuantity.getScale());
    assertEquals(RELATIVE, byteQuantity.getScale());
    assertEquals(RELATIVE, longQuantity.getScale());
    assertEquals(RELATIVE, intQuantity.getScale());
    assertEquals(RELATIVE, floatQuantity.getScale());
    assertEquals(RELATIVE, doubleQuantity.getScale());
    assertEquals(RELATIVE, bigIntegerQuantity.getScale());
    assertEquals(RELATIVE, bigDecimalQuantity.getScale());
  }
  
  @Test
  public void toTest() {
    Quantity<Time> minute = Quantities.getQuantity(BigDecimal.ONE, Units.YEAR);
    Quantity<Time> second = minute.to(Units.SECOND);
    BigDecimal value = (BigDecimal) second.getValue();
    value.setScale(4, RoundingMode.HALF_EVEN);
    BigDecimal expected = BigDecimal.valueOf(31556952);
    assertEquals(expected.setScale(4, RoundingMode.HALF_EVEN), value.setScale(4, RoundingMode.HALF_EVEN));
  }

  @Test
  @Disabled("equals() is broken for DecimalQuantity or all of JavaNumericQuantity")
  public void equalityValuesTest() {
   Quantity<Speed> shouldBe = Quantities.getQuantity(BigDecimal.valueOf(15.0d), Units.KILOMETRE_PER_HOUR);
   Quantity<Speed> parsedSpeed = Quantities.getQuantity("15.0 km/h").asType(Speed.class);
   assertEquals(shouldBe, parsedSpeed);
  }
}