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
package tech.units.indriya.unit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import tech.units.indriya.function.RationalConverter;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;

import static javax.measure.BinaryPrefix.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static javax.measure.MetricPrefix.*;
import static tech.units.indriya.unit.Units.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class PrefixTest {
  @Test
  public void testKilo() {
    // TODO how to handle equals for units?
    // assertEquals(KILOGRAM.getSymbol(), KILO(GRAM).getSymbol());
    assertEquals(KILOGRAM.toString(), KILO(GRAM).toString());
  }

  @Test
  public void testMega() {
    Quantity<Mass> m1 = Quantities.getQuantity(1.0, MEGA(Units.GRAM));
    assertEquals(1d, m1.getValue());
    assertEquals("Mg", m1.getUnit().toString());
  }

  @Test
  public void testDeci() {
    Quantity<Volume> m1 = Quantities.getQuantity(1.0, LITRE);
    assertEquals(1d, m1.getValue());
    assertEquals("l", m1.getUnit().toString());

    Quantity<Volume> m2 = m1.to(DECI(LITRE));
    assertEquals(10.0d, m2.getValue());
    assertEquals("dl", m2.getUnit().toString());
  }

  @Test
  public void testMilli() {
    Quantity<Mass> m1 = Quantities.getQuantity(1.0, MILLI(Units.GRAM));
    assertEquals(1d, m1.getValue());
    assertEquals("mg", m1.getUnit().toString());
  }

  @Test
  public void testMilli2() {
    Quantity<Volume> m1 = Quantities.getQuantity(10, MILLI(LITRE));
    assertEquals(10, m1.getValue());
    assertEquals("ml", m1.getUnit().toString());
  }

  @Test
  public void testMilli3() {
    Quantity<Volume> m1 = Quantities.getQuantity(1.0, LITRE);
    assertEquals(1d, m1.getValue());
    assertEquals("l", m1.getUnit().toString());

    Quantity<Volume> m2 = m1.to(MILLI(LITRE));
    assertEquals(1000.0d, m2.getValue());
    assertEquals("ml", m2.getUnit().toString());
  }

  @Test
  public void testMilli4() {
    Quantity<Volume> m1 = Quantities.getQuantity(1.0, MILLI(LITRE));
    assertEquals(1d, m1.getValue());
    assertEquals("ml", m1.getUnit().toString());

    Quantity<Volume> m2 = m1.to(LITRE);
    assertEquals(0.001d, m2.getValue());
    assertEquals("l", m2.getUnit().toString());
  }

  @Test
  public void testMicro2() {
    Quantity<Length> m1 = Quantities.getQuantity(1.0, Units.METRE);
    assertEquals(1d, m1.getValue());
    assertEquals("m", m1.getUnit().toString());

    Quantity<Length> m2 = m1.to(MICRO(Units.METRE));
    assertEquals(1000000.0d, m2.getValue());
    assertEquals("µm", m2.getUnit().toString());
  }

  @Test
  public void testNano() {
    Quantity<Mass> m1 = Quantities.getQuantity(1.0, Units.GRAM);
    assertEquals(1d, m1.getValue());
    assertEquals("g", m1.getUnit().toString());

    Quantity<Mass> m2 = m1.to(NANO(Units.GRAM));
    assertEquals(1000000000.0d, m2.getValue());
    assertEquals("ng", m2.getUnit().toString());
  }

  @Test
  public void testNano2() {
    Quantity<Length> m1 = Quantities.getQuantity(1.0, Units.METRE);
    assertEquals(1d, m1.getValue());
    assertEquals("m", m1.getUnit().toString());

    Quantity<Length> m2 = m1.to(NANO(Units.METRE));
    assertEquals(1000000000.0d, m2.getValue());
    assertEquals("nm", m2.getUnit().toString());
  }

  @Test
  public void testHashMapAccessingMap() {
    assertThat(LITRE.toString(), is("l"));
    assertThat(MILLI(LITRE).toString(), is("ml"));
    assertThat(MILLI(GRAM).toString(), is("mg"));
  }

  @Test
  public void testSingleOperation() {
    assertEquals(MICRO(GRAM), GRAM.divide(1_000_000));
  }

  @Test
  @Disabled("This is research for https://github.com/unitsofmeasurement/uom-se/issues/164")
  public void testNestedOperationsShouldBeSame() {
    Unit<Mass> m1 = MICRO(GRAM);
    Unit<Mass> m2 = GRAM.divide(1000).divide(1000);
    UnitConverter c1 = m1.getConverterTo(m2);
    List steps1 = c1.getConversionSteps();
    UnitConverter c2 = m2.getConverterTo(m1);
    List steps2 = c2.getConversionSteps();
    assertEquals(c1, c2);
    assertEquals(m1, m2);
  }

  @Test
  public void testNestedOperationsNotTheSame() {
    Unit<Mass> m1 = MICRO(GRAM);
    Unit<Mass> m2 = GRAM.divide(1000).divide(2000);
    UnitConverter c1 = m1.getConverterTo(m2);
    List steps1 = c1.getConversionSteps();
    UnitConverter c2 = m2.getConverterTo(m1);
    List steps2 = c2.getConversionSteps();
    assertNotEquals(c1, c2);
    assertNotEquals(m1, m2);
  }
  
  @Test
  public void testKibi() {
    final Rational rational = Rational.of(128, 125);
    final UnitConverter converter = KIBI(METRE).getConverterTo(KILO(METRE));
    assertEquals("Ki", KIBI.getSymbol());
    assertEquals(rational.dividend, converter.convert(rational.divisor));
    assertEquals(rational.doubleValue(), converter.convert(1.), 1E-12);
  }

  @Test
  public void testMebi() {
	final Rational rational = Rational.of(16384, 15625);
    final UnitConverter converter = MEBI(METRE).getConverterTo(MEGA(METRE));
    assertEquals(rational.dividend, converter.convert(rational.divisor));
    assertEquals(rational.doubleValue(), converter.convert(1.), 1E-12);
  }

  @Test
  public void testGibi() {
	final BigInteger exactFactor = BigInteger.valueOf(GIBI.getBase()).pow(GIBI.getExponent());
	assertEquals(1073741824L, exactFactor.longValue());
	
    final Rational rational = Rational.of(2097152, 1953125);
    final UnitConverter converter = GIBI(METRE).getConverterTo(GIGA(METRE));
    assertEquals(rational.dividend, converter.convert(rational.divisor));
    assertEquals(rational.doubleValue(), converter.convert(1.), 1E-12);
  }

  @Test
  public void testTebi() {
	final Rational rational = Rational.of(268435456L, 244140625L);
    final UnitConverter converter = TEBI(LITRE).getConverterTo(TERA(LITRE));
    assertEquals(rational.dividend, converter.convert(rational.divisor));
    assertEquals(rational.doubleValue(), converter.convert(1.), 1E-12);
  }

  @Test
  public void testPebi() {
	final Rational rational = Rational.of(34359738368L, 30517578125L);
    final UnitConverter converter = PEBI(LITRE).getConverterTo(PETA(LITRE));
    assertEquals(rational.dividend, converter.convert(rational.divisor));
    assertEquals(rational.doubleValue(), converter.convert(1.), 1E-12);
  }
  
  @Test
  public void testZebi() {
	final Rational rational = Rational.of(1, 6835937500000000000L);
    final UnitConverter converter = ZEBI(GRAM).getConverterTo(ZETTA(GRAM));
    assertEquals(rational.dividend, converter.convert(rational.divisor));
    assertEquals(rational.doubleValue(), converter.convert(1.), 1E-12);
  }
  
  @Test
  public void testYobi() {
	final Rational rational = Rational.of(BigInteger.ONE, BigInteger.TEN.pow(17).multiply(BigInteger.valueOf(78125))); 
    final UnitConverter converter = YOBI(GRAM).getConverterTo(YOTTA(GRAM));
    assertEquals(rational.dividend, converter.convert(rational.divisor));
    assertEquals(rational.doubleValue(), converter.convert(1.), 1E-12);
  }
  
  // -- HELPER
  
  private static class Rational {
	  static Rational of(long dividend, long divisor) {
		  return of(BigInteger.valueOf(dividend), BigInteger.valueOf(divisor));
	  }
	  static Rational of(BigInteger dividend, BigInteger divisor) {
		  return new Rational(dividend, divisor);
	  }
	  private Rational(BigInteger dividend, BigInteger divisor) {
		  this.dividend = dividend;
		  this.divisor = divisor;
	  }
	  BigInteger dividend; 
	  BigInteger divisor;
	  public boolean isEqualTo(Rational other) {
		  // a/b =?= c/d  is equivalent to a*d =?= c*b; 

		  BigInteger a = dividend;
		  BigInteger b = divisor;
		  BigInteger c = other.dividend;
		  BigInteger d = other.divisor;
		  return a.multiply(d).equals(c.multiply(b));
	  }
	  @Override
	  public boolean equals(Object obj) {
		  if(obj instanceof Rational) {
			  return isEqualTo((Rational) obj);
		  }
		  return super.equals(obj);
	  }
	  public double doubleValue() {
		  return new BigDecimal(dividend).divide(new BigDecimal(divisor)).doubleValue();
	  }
  }
  
  
}
