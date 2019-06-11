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
package tech.units.indriya.unit;

import java.util.Set;
import java.util.logging.Logger;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Power;
import javax.measure.quantity.Time;
import javax.measure.spi.SystemOfUnits;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.quantity.QuantityDimension;
import tech.units.indriya.unit.TransformedUnit;
import tech.units.indriya.unit.Units;

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
public class UnitsTest {
    static final Logger logger = Logger.getLogger(UnitsTest.class.getName());

    private SystemOfUnits sou;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @BeforeEach
    public void setUp() throws Exception {
        sou = Units.getInstance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @AfterEach
    public void tearDown() throws Exception {
        sou = null;
    }

    @Test
    public void testByClassTime() {
        Unit<?> result = sou.getUnit(Time.class);
        assertNotNull(result);
        assertEquals("s", result.toString());
    }

    @Test
    public void testByStringM() {
        final Unit<?> u = sou.getUnit("m");
        assertNotNull(u);
        assertEquals(METRE, u);
    }

    @Test
    public void testByStringG() {
        final Unit<?> u = sou.getUnit("g");
        assertNotNull(u);
        assertEquals(GRAM, u);
    }

    @Test
    public void testByStringKg() {
        final Unit<?> u = sou.getUnit("kg");
        assertNotNull(u);
        assertEquals(KILOGRAM, u);
    }

    @Test
    public void testByStringW() {
        final Unit<?> u = sou.getUnit("W");
        assertNotNull(u);
        assertEquals(WATT, u);
    }
    
	@Test
	public void testGetByDimensionAoS() {
		testGetByDimension(QuantityDimension.AMOUNT_OF_SUBSTANCE, 1);
	}
	
	@Test
	public void testGetByDimensionElCurrent() {
		testGetByDimension(QuantityDimension.ELECTRIC_CURRENT, 1);
	}

	@Test
	public void testGetByDimensionLen() {
		testGetByDimension(QuantityDimension.LENGTH, 1);
	}
	
	@Test
	public void testGetByDimensionLumInt() {
		testGetByDimension(QuantityDimension.LUMINOUS_INTENSITY, 2);
	}

	@Test
	public void testGetByDimensionMass() {
		testGetByDimension(QuantityDimension.MASS, 2);
	}
	
	@Test
	public void testGetByDimensionNone() {
		testGetByDimension(QuantityDimension.NONE, 4);
	}

	@Test
	public void testGetByDimensionTemperature() {
		testGetByDimension(QuantityDimension.TEMPERATURE, 2);
	}
	
	@Test
	public void testGetByDimensionTime() {
		testGetByDimension(QuantityDimension.TIME, 6);
	}
	
	private void testGetByDimension(final Dimension dim, int expectedSize) {
		Set<? extends Unit<?>> units = sou.getUnits(dim);
		assertNotNull(units);
		assertEquals(expectedSize, units.size());
	}
}