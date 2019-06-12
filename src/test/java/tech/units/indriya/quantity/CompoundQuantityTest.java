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

import static javax.measure.MetricPrefix.CENTI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tech.units.indriya.NumberAssertions.assertNumberEquals;

import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import tech.units.indriya.unit.Units;
import tech.uom.lib.common.function.QuantityConverter;

/**
 *
 * @author Werner Keil
 * @author Andi Huber
 */
public class CompoundQuantityTest {
	static final Logger logger = Logger.getLogger(CompoundQuantityTest.class.getName());

	@Test
	public void testLengthSingleValueCompositeUnit() {
		CompoundQuantity<Length> mixLen = CompoundQuantity.of(Quantities.getQuantity(1, Units.METRE));

		assertEquals("[m]", mixLen.getUnits().toString());
		assertEquals("1 m", mixLen.toString());

		Quantity<Length> length = mixLen.to(Units.METRE);
		assertNumberEquals(1, length.getValue(), 1E-28);
	}

	@Test
	public void testLengths() {

		@SuppressWarnings("unchecked")
		final Quantity<Length>[] quants = new Quantity[] { Quantities.getQuantity(1, Units.METRE),
				Quantities.getQuantity(70, CENTI(Units.METRE)) };

		CompoundQuantity<Length> mixLen = CompoundQuantity.of(quants);

		assertEquals("[m, cm]", mixLen.getUnits().toString());
		assertEquals("1 m 70 cm", mixLen.toString());

		Quantity<Length> l2 = mixLen.to(Units.METRE);
		assertNumberEquals(BigDecimal.valueOf(1.7d), l2.getValue(), 1E-12);

		Quantity<Length> l3 = mixLen.to(CENTI(Units.METRE));
		assertNumberEquals(170, l3.getValue(), 1E-12);
	}

	@Test
	public void testLengthsReverse() {
		@SuppressWarnings({ "unchecked" })
		final Quantity<Length>[] quants = new Quantity[] { Quantities.getQuantity(70, CENTI(Units.METRE)),
				Quantities.getQuantity(1, Units.METRE) };
		
		CompoundQuantity<Length> mixLen = CompoundQuantity.of(quants);
		assertEquals("[cm, m]", mixLen.getUnits().toString());
		assertEquals("70 cm 1 m", mixLen.toString());
	
		Quantity<Length> l2 = mixLen.to(Units.METRE);
		assertNumberEquals(new BigDecimal("1.7"), l2.getValue(), 1E-12);
		
		
	}

	/**
	 * Inspired by Time conversion in
	 * https://reference.wolfram.com/language/ref/MixedUnit.html
	 */
	@Test
	public void testTimes() {
		@SuppressWarnings("unchecked")
		final Quantity<Time>[] quants = new Quantity[] { Quantities.getQuantity(3, Units.DAY),
				Quantities.getQuantity(4, Units.HOUR), Quantities.getQuantity(48, Units.MINUTE) };
		final CompoundQuantity<Time> time = CompoundQuantity.of(quants);

		assertEquals("[day, h, min]", time.getUnits().toString());
		assertEquals("3 day 4 h 48 min", time.toString());

		assertNumberEquals(3.2d, time.to(Units.DAY).getValue(), 1E-28);
		assertNumberEquals(4608, time.to(Units.MINUTE).getValue(), 1E-28);
		assertNumberEquals(276480, time.to(Units.SECOND).getValue(), 1E-28);
	}

	/**
	 * Verifies that an mixed quantity is not equal to another quantity.
	 */
	@Test
	public void compoundQuantityIsNotEqualToAnotherQuantity() {
		@SuppressWarnings("unchecked")
		final Quantity<Time>[] numList = new Quantity[] { Quantities.getQuantity(2, Units.HOUR),
				Quantities.getQuantity(6, Units.MINUTE) };
		CompoundQuantity<Time> mixTime = CompoundQuantity.of(numList);
		Quantity<Time> compareTime = Quantities.getQuantity(2.5d, Units.HOUR);
		assertNotEquals(mixTime, compareTime);
		assertNotEquals(mixTime.to(Units.HOUR), compareTime);
	}

	/**
	 * Verifies that a mixed quantity is not equal to another quantity that has the
	 * same numeric value.
	 */
	@Test
	public void compoundQuantityIsEqualToAQuantityOfTheSameNumericValue() {
		@SuppressWarnings("unchecked")
		final Quantity<Time>[] numList = new Quantity[] { Quantities.getQuantity(2, Units.HOUR),
				Quantities.getQuantity(30, Units.MINUTE) };
		CompoundQuantity<Time> mixTime = CompoundQuantity.of(numList);
		Quantity<Time> compareTime = Quantities.getQuantity(2.5d, Units.HOUR);
		assertNotEquals(mixTime, compareTime);
		assertNumberEquals(mixTime.to(Units.HOUR).getValue(), compareTime.getValue(), 1E-12);
	}

	/**
	 * Verifies that a mixed quantity can be represented as QuantityConverter.
	 */
	@Test
	public void compoundQuantityAsConverter() {
		@SuppressWarnings("unchecked")
		final Quantity<Time>[] numList = new Quantity[] { Quantities.getQuantity(2, Units.HOUR),
				Quantities.getQuantity(30, Units.MINUTE) };
		QuantityConverter<Time> convTime = CompoundQuantity.of(numList);
		Quantity<Time> compareTime = Quantities.getQuantity(2.5d, Units.HOUR);
		assertNotEquals(convTime, compareTime);
		assertNumberEquals(convTime.to(Units.HOUR).getValue(), compareTime.getValue(), 1E-12);
	}
}