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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Power;
import javax.measure.quantity.Time;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractConverter;
import tech.units.indriya.AbstractUnit;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.MetricPrefix;
import tech.units.indriya.unit.TransformedUnit;
import tech.units.indriya.unit.Units;

import static org.junit.jupiter.api.Assertions.*;
import static tech.units.indriya.unit.MetricPrefix.KILO;
import static tech.units.indriya.unit.Units.GRAM;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.WATT;

/**
 *
 * @author Werner Keil
 */
public class UnitsTest {
  static final Logger logger = Logger.getLogger(UnitsTest.class.getName());

  Unit<Dimensionless> one;

  public UnitsTest() {
  }

  @BeforeAll
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @BeforeEach
  public void setUp() throws Exception {
    // super.setUp();
    one = AbstractUnit.ONE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @After
  public void tearDown() throws Exception {
    // super.tearDown();
    one = null;
  }

  /**
   * Test method for {@link javax.measure.Unit#toMetric()}.
   */
  // @Test
  // public void testToMetric() {
  // AbstractUnit<? extends QuantityAmount> su = (AbstractUnit<? extends
  // QuantityAmount>) one.toMetric();
  // assertTrue(su.isUnscaledMetric());
  // }

  /**
   * Test method for {@link javax.measure.Unit#getConverterTo}.
   */
  @Test
  public void testConverterToSI() {
    Double factor = 10.0;
    UnitConverter converter = one.getConverterTo(one);
    Double result = converter.convert(factor.doubleValue());
    assertEquals(result, factor);
    logger.log(Level.FINER, result.toString());
  }

  /**
   * Test method for {@link javax.measure.Unit#isMetric()}.
   */
  // @Test
  // public void testIsMetric() {
  // boolean standard = one.isMetric();
  // assertTrue(standard);
  // }

  /**
   * Test method for {@link javax.measure.Unit#asType(java.lang.Class)}.
   */
  @Test
  public void testAsType() {
    one.asType(Dimensionless.class);
    try {
      METRE.asType(Dimensionless.class);
      fail("Should have raised ClassCastException");
    } catch (ClassCastException e) {
      assertTrue(true);
    }
  }

  /**
   * Test method for {@link javax.measure.Unit#getDimension()}.
   */
  @Test
  public void testGetDimension() {
    one.getDimension();
  }

  /**
   * Test method for {@link javax.measure.Unit#alternate(java.lang.String)}.
   */
  @Test
  public void testAlternate() {
    Unit<?> alternate = one.alternate(null);
    assertNotNull(alternate);
  }

  /**
   * Test method for {@link javax.measure.Unit#compound(javax.measure.Unit)}.
   */
  /*
   * public void testCompound() { Unit<? extends Quantity> compound =
   * one.compound(one); assertNotNull(compound); }
   */

  /**
   * Test method for {@link javax.measure.Unit#transform}.
   */
  @Test
  public void testTransform() {
    Unit<?> result = one.transform(AbstractConverter.IDENTITY);
    assertEquals(result, one);
  }

  /**
   * Test method for {@link javax.measure.Unit#shift(double)}.
   */
  @Test
  public void testShift() {
    Unit<?> result = one.shift(10);
    assertNotSame(result, one);
  }

  /**
   * Test method for {@link javax.measure.Unit#multiply(long)}.
   */
  @Test
  public void testMultiplyLong() {
    Unit<?> result = one.multiply(2L);
    assertNotSame(result, one);
  }

  /**
   * Test method for {@link javax.measure.Unit#multiply(double)}.
   */
  @Test
  public void testMultiplyDouble() {
    Unit<?> result = one.multiply(2.1);
    assertNotSame(result, one);
  }

  /**
   * Test method for {@link javax.measure.Unit#multiply(javax.measure.Unit)}.
   */
  @Test
  public void testMultiplyUnitOfQ() {
    AbstractUnit<?> result = (AbstractUnit<?>) one.multiply(one);
    assertEquals(result, one);
  }

  /**
   * Test method for {@link javax.measure.Unit#inverse()}.
   */
  @Test
  public void testInverse() {
    Unit<?> result = one.inverse();
    assertEquals(result, one);
  }

  /**
   * Test method for {@link javax.measure.Unit#divide(long)}.
   */
  @Test
  public void testDivideLong() {
    Unit<?> result = one.divide(2L);
    assertNotSame(result, one);
  }

  /**
   * Test method for {@link javax.measure.Unit#divide(double)}.
   */
  @Test
  public void testDivideDouble() {
    Unit<?> result = one.divide(3.2);
    assertNotSame(result, one);
  }

  /**
   * Test method for {@link javax.measure.Unit#divide(javax.measure.Unit)}.
   */
  @Test
  public void testDivideUnitOfQ() {
    Unit<?> result = one.divide(one);
    assertEquals(result, one);
  }

  /**
   * Test method for {@link javax.measure.Unit#root(int)}.
   */
  @Test
  public void testRoot() {
    Unit<?> result = one.root(2);
    assertEquals(result, one);
  }

  /**
   * Test method for {@link javax.measure.Unit#pow(int)}.
   */
  @Test
  public void testPow() {
    Unit<?> result = one.pow(10);
    assertEquals(result, one);
  }

  @Test
  public void testKiloIsAThousand() {
    // FIXME: Need to find the org.hamcrest assertion libs
    Quantity<Power> w2000 = Quantities.getQuantity(2000, WATT);
    Quantity<Power> kW2 = Quantities.getQuantity(2, MetricPrefix.KILO(WATT));
    // assertThat(w2000, is(kW2));
  }

  @Test
  public void testOf() {
    assertEquals(MetricPrefix.KILO(Units.GRAM).toString(), AbstractUnit.parse("kg").toString());
  }

  @Test
  public void testParse() {
    assertEquals(KILO(GRAM).toString(), AbstractUnit.parse("kg").toString()); // TODO: Problem
    // with kg...?
  }

  @Test
  public void testParse2() {
    assertEquals(KILO(METRE), AbstractUnit.parse("km"));
  }

  @Test
  public void testToString() {
    assertEquals("kg", MetricPrefix.KILO(Units.GRAM).toString());
  }

  @Test
  public void testGetConverterToSystemUnit() {
  }

  @Test
  public void testGetSymbol() {
    // TODO see https://github.com/unitsofmeasurement/uom-se/issues/54 /
    // https://java.net/jira/browse/UNITSOFMEASUREMENT-109
    assertEquals("kg", Units.KILOGRAM.getSymbol());
    // assertEquals("kg", Units.GRAM.getSymbol()); //"g"
    // assertEquals("kg", UCUM.POUND.getSymbol()); //"lb"
    // assertEquals("kg", UCUM.OUNCE.getSymbol());//"oz"
    // assertEquals("kg", MetricPrefix.KILO(Units.GRAM).getSymbol());
    // assertEquals("kg", UCUM.GRAM.getSymbol()); //"g"
    // assertEquals("kg", US.POUND.getSymbol()); //"lb"
    // assertNull(Units.GRAM.getSymbol());
    // assertNull(UCUM.OUNCE.getSymbol());
  }

  @Test
  public void testGetParentUnit() {
    assertEquals("TransformedUnit", Units.GRAM.getClass().getSimpleName());
    assertEquals("kg", ((TransformedUnit<Mass>) Units.GRAM).getParentUnit().getSymbol());
    // assertEquals("kg", UCUM.POUND.getSymbol()); //"lb"
    // assertEquals("kg", UCUM.OUNCE.getSymbol());//"oz"
    // assertEquals("kg", MetricPrefix.KILO(UCUM.GRAM).getSymbol());
    // assertEquals("kg", UCUM.GRAM.getSymbol()); //"g"
    // assertEquals("kg", US.POUND.getSymbol()); //"lb"
  }

  // TODO below are empty

  @Test
  public void testAnnotate() {
  }

  @Test
  public void testGetAnnotation() {
  }

  @Test
  public void testGetUnannotatedUnit() {
  }

  @Test
  public void testIsSystemUnit() {
  }

  @Test
  public void testGetSystemUnit() {
  }

  @Test
  public void testGetProductUnits() {
  }

  @Test
  public void testIsCompatible() {
  }

  @Test
  public void testGetConverterTo() {
  }

  @Test
  public void testGetConverterToAny() {
  }

  @Test
  public void testAdd() {
  }

  @Test
  public void testMultiply_double() {
  }

  @Test
  public void testMultiply_ErrorType() {
  }

  @Test
  public void testDivide_double() {
  }

  @Test
  public void testDivide_ErrorType() {
  }

  @Test
  public void testHashCode() {
  }

  @Test
  public void testEquals() {
  }

  @Test
  public void testByClassTime() {
    Unit result = Units.getInstance().getUnit(Time.class);
    assertNotNull(result);
    assertEquals("s", result.toString());
  }
}