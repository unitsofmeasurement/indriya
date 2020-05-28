/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2020, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
import static tech.units.indriya.NumberAssertions.assertNumberEquals;
import static tech.units.indriya.unit.Units.CELSIUS;
import static tech.units.indriya.unit.Units.KELVIN;

import javax.measure.Quantity;
import javax.measure.quantity.Temperature;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
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
		assertEquals(ABSOLUTE, sum.getScale());
		assertNumberEquals(8, sum.getValue(), 1E-12);
	}

	@Test
	void addingTemperatureRelativeToAbsolute() {
		Quantity<Temperature> relT = Quantities.getQuantity(3d, CELSIUS, RELATIVE);
		Quantity<Temperature> absT = Quantities.getQuantity(3d, CELSIUS, ABSOLUTE);
		Quantity<Temperature> sum = relT.add(absT);
		assertEquals(ABSOLUTE, sum.getScale());
		assertNumberEquals(6, sum.getValue(), 1E-12);
	}

	@Test
	void addingAbsoluteTemperatures() {
		Quantity<Temperature> absT = Quantities.getQuantity(0d, CELSIUS, ABSOLUTE);
		Quantity<Temperature> absT2 = Quantities.getQuantity(0d, CELSIUS, ABSOLUTE);
		Quantity<Temperature> sum = absT.add(absT2);
		assertEquals(ABSOLUTE, sum.getScale());
		assertNumberEquals(273.15d, sum.getValue(), 1E-12);
	}

	@Test
	void addingRelativeTemperatures() {
		Quantity<Temperature> relT = Quantities.getQuantity(6, CELSIUS, RELATIVE);
		Quantity<Temperature> relT2 = Quantities.getQuantity(20, CELSIUS, RELATIVE);
		Quantity<Temperature> sum = relT.add(relT2);
        assertEquals(RELATIVE, sum.getScale());
		assertNumberEquals(26, sum.getValue(), 1E-12);
	}
	
	@Test @Disabled("Currently not working, see https://github.com/unitsofmeasurement/indriya/issues/247")
	void productOfRelativeTemperatures() {
		Quantity<Temperature> relT = Quantities.getQuantity(0d, CELSIUS, RELATIVE);
		Quantity<Temperature> relT2 = Quantities.getQuantity(0d, CELSIUS, RELATIVE);
		Quantity<?> prod = relT.multiply(relT2);
		assertNumberEquals(74610.9225d, prod.getValue(), 1E-12);
	}

	@Test @Disabled("Currently not working, see https://github.com/unitsofmeasurement/indriya/issues/247")
	void productOfAbsoluteTemperatures() {
		Quantity<Temperature> relT = Quantities.getQuantity(0d, CELSIUS, ABSOLUTE);
		Quantity<Temperature> relT2 = Quantities.getQuantity(0d, CELSIUS, ABSOLUTE);
		Quantity<?> prod = relT.multiply(relT2);
		assertNumberEquals(74610.9225d, prod.getValue(), 1E-12);
	}

	@Test
	void testToCelsius() {
		Quantity<Temperature> t = Quantities.getQuantity(Double.valueOf(2d), KELVIN);
		Quantity<Temperature> t2 = t.to(CELSIUS);
		assertNumberEquals(-271.15d, t2.getValue(), 1E-12);
	}
}
