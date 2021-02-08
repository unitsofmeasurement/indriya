/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2021, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import static javax.measure.Quantity.Scale.ABSOLUTE;
import static javax.measure.Quantity.Scale.RELATIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.NumberAssertions.assertNumberEquals;
import static tech.units.indriya.unit.Units.CELSIUS;
import static tech.units.indriya.unit.Units.KELVIN;

import javax.measure.Quantity;
import javax.measure.Quantity.Scale;
import javax.measure.quantity.Temperature;

import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractUnit;

/**
 * see <a href="https://github.com/unitsofmeasurement/unit-api/wiki/Arithmetic-rules-for-Difference-versus-Absolute-quantities">
 * Arithmetic-rules-for-Difference-versus-Absolute-quantities</a>
 * 
 * @author Werner Keil
 * @author Andi Huber
 *
 */
class TemperatureTest {

	@Test
	void testInstantiate() {
		Quantity<Temperature> t = Quantities.getQuantity(23.0d, CELSIUS); // 23.0 °C
		assertEquals("23 ℃", t.toString());
	}

	@Test
	void testTemperatureNumberQuantity() {
		Quantity<Temperature> t = Quantities.getQuantity(Double.valueOf(20d), CELSIUS);
		assertNumberEquals(20, t.getValue(), 1E-12);
	}

	@Test
	void testToKelvin() {
		Quantity<Temperature> t = Quantities.getQuantity(Double.valueOf(30d), CELSIUS);
		Quantity<Temperature> t2 = t.to(KELVIN);
		assertNumberEquals(303.15d, t2.getValue(), 1E-12);
	}

	@Test
	void testTo2() {
		Quantity<Temperature> t = Quantities.getQuantity(2d, KELVIN);
		Quantity<Temperature> t2 = t.to(CELSIUS);
		assertNumberEquals(-271.15d, t2.getValue(), 1E-12);
	}

	@Test
	void addingRelativesProducesRelative() {
		Quantity<Temperature> relT = Quantities.getQuantity(0d, KELVIN, RELATIVE);
		Quantity<Temperature> relT2 = Quantities.getQuantity(0d, KELVIN, RELATIVE);
		Quantity<Temperature> sum = relT.add(relT2);
		assertEquals(RELATIVE, sum.getScale());
		assertNumberEquals(0, sum.getValue(), 1E-12);
	}

	@Test
	void addingTemperatureAbsoluteToARelative() {
		Quantity<Temperature> absT = Quantities.getQuantity(4d, CELSIUS, ABSOLUTE);
		Quantity<Temperature> relT = Quantities.getQuantity(4d, CELSIUS, RELATIVE);
		Quantity<Temperature> sum = absT.add(relT); 
		assertCelsiusOfScale(sum, ABSOLUTE);
		assertNumberEquals(8, sum.getValue(), 1E-12);
	}

	@Test
	void addingTemperatureRelativeToAbsolute() {
		Quantity<Temperature> relT = Quantities.getQuantity(3d, CELSIUS, RELATIVE);
		Quantity<Temperature> absT = Quantities.getQuantity(3d, CELSIUS, ABSOLUTE);
		Quantity<Temperature> sum = relT.add(absT);
		assertCelsiusOfScale(sum, ABSOLUTE);
		assertNumberEquals(6, sum.getValue(), 1E-12);
	}

	@Test
	void addingAbsoluteTemperatures() {
		Quantity<Temperature> absT = Quantities.getQuantity(0d, CELSIUS, ABSOLUTE);
		Quantity<Temperature> absT2 = Quantities.getQuantity(0d, CELSIUS, ABSOLUTE);
		Quantity<Temperature> sum = absT.add(absT2);
		assertCelsiusOfScale(sum, ABSOLUTE);
		assertNumberEquals(273.15d, sum.getValue(), 1E-12);
	}

	@Test
	void addingRelativeTemperatures() {
		Quantity<Temperature> relT = Quantities.getQuantity(6, CELSIUS, RELATIVE);
		Quantity<Temperature> relT2 = Quantities.getQuantity(20, CELSIUS, RELATIVE);
		Quantity<Temperature> sum = relT.add(relT2);
        assertCelsiusOfScale(sum, RELATIVE);
		assertNumberEquals(26, sum.getValue(), 1E-12);
	}
	
	@Test
	void testToCelsius() {
	    Quantity<Temperature> t = Quantities.getQuantity(Double.valueOf(2d), KELVIN);
	    Quantity<Temperature> t2 = t.to(CELSIUS);
	    assertCelsiusOfScale(t2, ABSOLUTE);
	    assertNumberEquals(-271.15d, t2.getValue(), 1E-12);
	}
	
	// -- SCALAR PRODUCT TESTS
	
	/**
	 * Δ2°C * 3 = Δ6°C
	 */
	@Test 
    void scalarProductShouldPreserveRelativeScale() {
        Quantity<Temperature> temp = Quantities.getQuantity(2d, CELSIUS, RELATIVE);
        Quantity<Temperature> prod = temp.multiply(3);
        assertCelsiusOfScale(prod, RELATIVE);
        assertNumberEquals(6, prod.getValue(), 1E-12);
    }
	
	/**
     * 0°C * 2 = 273.15°C
     */
	@Test 
    void scalarProductShouldPreserveAbsoluteScale() {
        Quantity<Temperature> temp = Quantities.getQuantity(0d, CELSIUS, ABSOLUTE);
        Quantity<Temperature> prod = temp.multiply(2);
        assertCelsiusOfScale(prod, ABSOLUTE);
        assertNumberEquals(273.15, prod.getValue(), 1E-12);
    }
	
	// -- DIVISION TESTS
	
	/**
     * Δ3°C / Δ2°C = 1.5
     */
	@Test 
    void relRelDivisionShouldYieldScalar() {
        Quantity<Temperature> temp1 = Quantities.getQuantity(3d, CELSIUS, RELATIVE);
        Quantity<Temperature> temp2 = Quantities.getQuantity(2d, CELSIUS, RELATIVE);
        Quantity<?> div = temp1.divide(temp2);
        assertDimensionLess(div);
        assertNumberEquals(1.5, div.getValue(), 1E-12);
    }
	
	/**
     * Δ3°C / 2°C = 3 / (2 + 273.15)
     */
	@Test 
    void relAbsDivisionShouldYieldScalar() {
        Quantity<Temperature> temp1 = Quantities.getQuantity(3d, CELSIUS, RELATIVE);
        Quantity<Temperature> temp2 = Quantities.getQuantity(2d, CELSIUS, ABSOLUTE);
        Quantity<?> div = temp1.divide(temp2);
        assertDimensionLess(div);
        assertNumberEquals(3./(2. + 273.15), div.getValue(), 1E-12);
    }
	
	/**
     * 0°C / Δ2°C = 273.15 / 2
     */
	@Test 
    void absRelDivisionShouldYieldScalar() {
        Quantity<Temperature> temp1 = Quantities.getQuantity(0d, CELSIUS, ABSOLUTE);
        Quantity<Temperature> temp2 = Quantities.getQuantity(2d, CELSIUS, RELATIVE);
        Quantity<?> div = temp1.divide(temp2);
        assertDimensionLess(div);
        assertNumberEquals(273.15/2., div.getValue(), 1E-12);
    }
	
	// -- PRODUCT TESTS
	
    /**
     * Δ2°C * Δ3°C = 6 K²
     */
	@Test 
	void relRelMultiplicationShouldYieldKelvinSquared() {
		Quantity<Temperature> temp1 = Quantities.getQuantity(2d, CELSIUS, RELATIVE);
		Quantity<Temperature> temp2 = Quantities.getQuantity(3d, CELSIUS, RELATIVE);
		Quantity<?> prod = temp1.multiply(temp2);
		assertKelvinSquared(prod);
		assertNumberEquals(6, prod.getValue(), 1E-12);
	}

	/**
     * 2°C * 3°C = (2 + 273.15) * (3 + 273.15) K²
     */
	@Test // see https://github.com/unitsofmeasurement/indriya/issues/247
	void absAbsMultiplicationShouldYieldKelvinSquared() {
	    Quantity<Temperature> temp1 = Quantities.getQuantity(2d, CELSIUS, ABSOLUTE);
        Quantity<Temperature> temp2 = Quantities.getQuantity(3d, CELSIUS, ABSOLUTE);
        Quantity<?> prod = temp1.multiply(temp2);
        assertKelvinSquared(prod);
		assertNumberEquals((2. + 273.15) * (3. + 273.15), prod.getValue(), 1E-9);
	}
	
	/**
     * Δ2°C * 3°C = 2 * (3 + 273.15) K²
     */
	@Test // see https://github.com/unitsofmeasurement/indriya/issues/247
    void relAbsMultiplicationShouldYieldKelvinSquared() {
	    Quantity<Temperature> temp1 = Quantities.getQuantity(2d, CELSIUS, RELATIVE);
        Quantity<Temperature> temp2 = Quantities.getQuantity(3d, CELSIUS, ABSOLUTE);
        Quantity<?> prod1 = temp1.multiply(temp2);
        Quantity<?> prod2 = temp2.multiply(temp1);
        assertKelvinSquared(prod1);
        assertKelvinSquared(prod2);
        assertNumberEquals(2. * (3. + 273.15), prod1.getValue(), 1E-12);
        assertNumberEquals(2. * (3. + 273.15), prod2.getValue(), 1E-12);
    }
	
	// -- HELPER
	
	private static void assertDimensionLess(Quantity<?> x) {
	    assertTrue(x.getUnit().isCompatible(AbstractUnit.ONE)); // scalar
	    assertEquals(ABSOLUTE, x.getScale()); // should be ABSOLUTE by convention
	}
	
	private static void assertCelsiusOfScale(Quantity<?> x, Scale scale) {
        assertEquals(CELSIUS, x.getUnit());
        assertEquals(scale, x.getScale());
    }
	
	private static void assertKelvinSquared(Quantity<?> x) {
	    assertEquals("K²", x.getUnit().toString());
        assertEquals(ABSOLUTE, x.getScale()); // should be ABSOLUTE by convention
    }
	
	

}
