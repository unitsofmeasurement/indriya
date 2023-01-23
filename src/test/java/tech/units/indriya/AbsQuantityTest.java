/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2023, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
package tech.units.indriya;

import static javax.measure.MetricPrefix.CENTI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tech.units.indriya.unit.Units.METRE;

import java.math.BigDecimal;

import javax.measure.Quantity;
import javax.measure.format.MeasurementParseException;
import javax.measure.quantity.Dimensionless;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;

/**
 * Unit tests on the AbstractQuantity class.
 *
 */
public class AbsQuantityTest {

	@Test
	public void testParse() {
		 assertEquals(Quantities.getQuantity(BigDecimal.valueOf(0.234), CENTI(METRE)),
				 AbstractQuantity.parse("0.234 cm")); 		
	}

	@Test
	public void testParseNoUnit() {
		assertNotNull(AbstractQuantity.parse("0.234").asType(Dimensionless.class));
	}
		
	@Test
	public void testParseOnlyUnit() {
		assertThrows(MeasurementParseException.class, () -> {
			@SuppressWarnings({ "unused" })
			Quantity<?> result = AbstractQuantity.parse("m");
		});
	}
	
	@Test
	public void testParseMixed() {
		assertThrows(MeasurementParseException.class, () -> {
			@SuppressWarnings({ "unused" })
			Quantity<?> result = AbstractQuantity.parse("1 m 70 cm");
		}); 		
	}
}
