/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2024, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Time;

import static javax.measure.MetricPrefix.CENTI;
import static javax.measure.Quantity.Scale.RELATIVE;
import static tech.units.indriya.unit.Units.METRE;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.unit.Units;

import static tech.units.indriya.NumberAssertions.assertNumberEquals;
import static tech.units.indriya.unit.Units.CELSIUS;
import static tech.units.indriya.unit.Units.PASCAL;

/**
 *
 * @author Werner Keil
 * @author Andi Huber
 * @version 1.0
 */
public class QuantitiesTest {
  
  @Test
  public void ofTest() {
    Quantity<Pressure> pressure = Quantities.getQuantity(BigDecimal.ONE, PASCAL);
    assertEquals(PASCAL, pressure.getUnit());
  }
  
  @Test
  public void canOnlyUseFinteAmountsTestNan() {
    assertThrows(IllegalArgumentException.class, ()->Quantities.getQuantity(Double.NaN, PASCAL));  
  }
  
  @Test
  public void canOnlyUseFinteAmountsTestInfinity() {
    assertThrows(IllegalArgumentException.class, ()->Quantities.getQuantity(Double.POSITIVE_INFINITY, PASCAL));  
    assertThrows(IllegalArgumentException.class, ()->Quantities.getQuantity(Double.NEGATIVE_INFINITY, PASCAL));
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

    assertNumberEquals(2, shortQuantity.getValue(), 1E-12);
    assertNumberEquals(2, byteQuantity.getValue(), 1E-12);
    assertNumberEquals(2, longQuantity.getValue(), 1E-12);
    assertNumberEquals(2, intQuantity.getValue(), 1E-12);
    assertNumberEquals(2, floatQuantity.getValue(), 1E-12);
    assertNumberEquals(2, doubleQuantity.getValue(), 1E-12);
    assertNumberEquals(1, bigIntegerQuantity.getValue(), 1E-12);
    assertNumberEquals(1, bigDecimalQuantity.getValue(), 1E-12);

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

    assertNumberEquals(2, shortQuantity.getValue(), 1E-12);
    assertNumberEquals(2, byteQuantity.getValue(), 1E-12);
    assertNumberEquals(2, longQuantity.getValue(), 1E-12);
    assertNumberEquals(2, intQuantity.getValue(), 1E-12);
    assertNumberEquals(2, floatQuantity.getValue(), 1E-12);
    assertNumberEquals(2, doubleQuantity.getValue(), 1E-12);
    assertNumberEquals(1, bigIntegerQuantity.getValue(), 1E-12);
    assertNumberEquals(1, bigDecimalQuantity.getValue(), 1E-12);

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
    assertNumberEquals(31556952, second.getValue(), 1E-12);
  }

  @Test
  public void quantityEquivalentTest() {
	  Locale.setDefault(Locale.ROOT);
      ComparableQuantity<Speed> shouldBe = Quantities.getQuantity(15, Units.KILOMETRE_PER_HOUR);
      Quantity<Speed> parsedSpeed = Quantities.getQuantity("15.0 km/h").asType(Speed.class);
      assertTrue(shouldBe.isEquivalentTo(parsedSpeed));
  }
  
  @Test
  public void quantityMixedTest() {	
	  final Number[] values = new Number[] {1, 70};
	  @SuppressWarnings("rawtypes")
	  final Unit[] units = new Unit[] {METRE, CENTI(METRE)};
      @SuppressWarnings("unchecked")
      MixedQuantity<Length> expected = Quantities.getMixedQuantity(values, units);
	  Quantity<Length> parsed = Quantities.getQuantity("1 m 70 cm").asType(Length.class);
      assertTrue(expected.to(METRE).isEquivalentTo(parsed));
  }
  
	@Test
	public void parseNoUnitTest() {
		assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings({ "unused" })
			Quantity<?> result = Quantities.getQuantity("170").asType(Dimensionless.class);
		}); 		
	}
}