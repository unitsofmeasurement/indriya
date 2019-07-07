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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import javax.measure.Quantity;
import javax.measure.quantity.Temperature;
import java.math.BigDecimal;

import static javax.measure.Quantity.Scale.ABSOLUTE;
import static javax.measure.Quantity.Scale.RELATIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.units.indriya.NumberAssertions.assertNumberEquals;
import static tech.units.indriya.unit.Units.CELSIUS;
import static tech.units.indriya.unit.Units.KELVIN;

/**
 *
 * @author Werner Keil
 *
 */
class TemperatureTest {

	@Test
	void testInstantiate() {
		Quantity<Temperature> t = Quantities.getQuantity(23.0d, CELSIUS); // 23.0 °C
		assertEquals("23.0 ℃", t.toString());
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
		assertEquals(RELATIVE, relT.add(relT2).getScale());
	}

	@Test
	void addingTemperatureAbsoluteToARelative() {
		Quantity<Temperature> absT = Quantities.getQuantity(4d, CELSIUS, ABSOLUTE);
		Quantity<Temperature> relT = Quantities.getQuantity(4d, CELSIUS, RELATIVE);
		assertEquals(8d,absT.add(relT).getValue().doubleValue());
	}

	@Test
	void addingTemperatureRelativeToAbsolute() {
		Quantity<Temperature> relT = Quantities.getQuantity(3d, CELSIUS, RELATIVE);
		Quantity<Temperature> absT = Quantities.getQuantity(3d, CELSIUS, ABSOLUTE);
		assertEquals(6d,relT.add(absT).getValue().doubleValue());
	}

	@Test
	void addingAbsoluteTemperatures() {
		Quantity<Temperature> absT = Quantities.getQuantity(0d, CELSIUS, ABSOLUTE);
		Quantity<Temperature> absT2 = Quantities.getQuantity(0d, CELSIUS, ABSOLUTE);
		assertEquals(BigDecimal.valueOf(273.15d), absT.add(absT2).getValue());
		assertEquals(ABSOLUTE, absT.add(absT2).getScale());
	}

	@Test
	void addingRelativeTemperatures() {
		Quantity<Temperature> relT = Quantities.getQuantity(0d, CELSIUS, RELATIVE);
		Quantity<Temperature> relT2 = Quantities.getQuantity(0d, CELSIUS, RELATIVE);
		assertEquals(0, relT.add(relT2).getValue());
	}
	
	@Test @Disabled("Currently not working, see https://github.com/unitsofmeasurement/indriya/issues/247")
	void productOfRelativeTemperatures() {
		Quantity<Temperature> relT = Quantities.getQuantity(0d, CELSIUS, RELATIVE);
		Quantity<Temperature> relT2 = Quantities.getQuantity(0d, CELSIUS, RELATIVE);
		assertEquals(74610.9225d, relT.multiply(relT2).getValue());
	}

	@Test @Disabled("Currently not working, see https://github.com/unitsofmeasurement/indriya/issues/247")
	void productOfAbsoluteTemperatures() {
		Quantity<Temperature> relT = Quantities.getQuantity(0d, CELSIUS, ABSOLUTE);
		Quantity<Temperature> relT2 = Quantities.getQuantity(0d, CELSIUS, ABSOLUTE);
		assertEquals(74610.9225d, relT.multiply(relT2).getValue());
	}

	@Test
	void testToCelsius() {
		Quantity<Temperature> t = Quantities.getQuantity(Double.valueOf(2d), KELVIN);
		Quantity<Temperature> t2 = t.to(CELSIUS);
		assertNumberEquals(-271.15d, t2.getValue(), 1E-12);
	}
}
