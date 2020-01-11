/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2020, Units of Measurement project.
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
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.function.AbstractConverter;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.TransformedUnit;
import static org.junit.jupiter.api.Assertions.*;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.GRAM;
import static tech.units.indriya.unit.Units.KILOGRAM;
import static tech.units.indriya.unit.Units.WATT;
import static javax.measure.MetricPrefix.*;

/**
 *
 * @author Werner Keil
 */
public class UnitOneTest {
    static final Logger logger = Logger.getLogger(UnitOneTest.class.getName());

    private Unit<Dimensionless> one;
 
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
    public void testPowOnOne() {
        Unit<?> result = one.pow(10);
        assertEquals("one", result.toString());
    }

    /**
     * Test method for {@link javax.measure.Unit#pow(int)}.
     */
    @Test
    public void testPow() {
        Unit<?> result = WATT.pow(10);
        assertEquals("W^10", result.toString());
    }

    @Test
    public void testKiloIsAThousand() {
        ComparableQuantity<Power> w2000 = Quantities.getQuantity(2000, WATT);
        Quantity<Power> kW2 = Quantities.getQuantity(2, KILO(WATT));
        assertTrue(w2000.isEquivalentTo(kW2));
    }

    @Test
    public void testOf() {
        assertEquals(KILO(GRAM).toString(), AbstractUnit.parse("kg").toString());
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
        assertEquals("kg", KILO(GRAM).toString());
    }

    @Test
    public void testGetSymbol() {
        // TODO see https://github.com/unitsofmeasurement/uom-se/issues/54 /
        assertEquals("kg", KILOGRAM.getSymbol());
        assertNull(GRAM.getSymbol());
    }

    @Test
    public void testGetParentUnit() {
        assertEquals("TransformedUnit", GRAM.getClass().getSimpleName());
        assertEquals("kg", ((TransformedUnit<Mass>) GRAM).getParentUnit().getSymbol());
    }
}