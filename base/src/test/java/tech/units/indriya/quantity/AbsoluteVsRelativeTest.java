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

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/**
 * @author Werner Keil
 */
class AbsoluteVsRelativeTest {

	@Test
	@DisplayName("Adding or substracting two RELATIVE quantities should result in a relative quantity")
	public void addRelativesTest() {
		final Quantity<Length> utrechtSeaLevel = Quantities.getQuantity(5, Units.METRE, Quantity.Scale.RELATIVE);
		final Quantity<Length> munichSeaLevel = Quantities.getQuantity(519, Units.METRE, Quantity.Scale.RELATIVE);
		final Quantity<Length> combinedSeaLevel = utrechtSeaLevel.add(munichSeaLevel);
		assertEquals(Quantity.Scale.RELATIVE, combinedSeaLevel.getScale());
	}
	
	@Test
	void addingHoursToDays(){
		Quantity<Time> relTime = Quantities.getQuantity(1d,Units.HOUR,RELATIVE);
		Quantity<Time> absTime = Quantities.getQuantity(1d,Units.DAY,ABSOLUTE);
		Quantity<Time> addedTime = relTime.add(absTime);
		Quantity<Time> expectedTime = Quantities.getQuantity(25d,Units.HOUR,ABSOLUTE);
		assertNumberEquals(expectedTime.getValue(), addedTime.getValue(), 1E-12);
	}
}
