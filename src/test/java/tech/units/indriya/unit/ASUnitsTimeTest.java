/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2025, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tech.units.indriya.format.UnitStyle.NAME;
import static tech.units.indriya.format.UnitStyle.SYMBOL;
import static tech.units.indriya.unit.Units.DAY;
import static tech.units.indriya.unit.Units.HOUR;
import static tech.units.indriya.unit.Units.MINUTE;
import static tech.units.indriya.unit.Units.SECOND;
import static tech.units.indriya.unit.Units.WEEK;
import static tech.units.indriya.unit.Units.MONTH;
import static tech.units.indriya.unit.Units.SIEMENS;

import javax.measure.Unit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.units.indriya.AbstractSystemOfUnits;

/**
 * Tests the AbstractSystemOfUnits aspect of the Units class with Time units.
 * 
 * @author Werner Keil
 */
public class ASUnitsTimeTest {

	private AbstractSystemOfUnits sou;

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
	public void testByStringSec() {
		final Unit<?> u = sou.getUnit("s");
		assertNotNull(u);
		assertEquals(SECOND, u);
	}
	
	@Test
	public void testBySymbolSec() {
		final Unit<?> u = sou.getUnit("s", SYMBOL);
		assertNotNull(u);
		assertEquals(SECOND, u);
	}

	@Test
	public void testByNameSec() {
		final Unit<?> u = sou.getUnit("Second", NAME);
		assertNotNull(u);
		assertEquals(SECOND, u);
	}

	@Test
	public void testBySymbolSecIgnoreCase() {
		final Unit<?> u = sou.getUnit("S", SYMBOL, true);
		assertNotNull(u);
		assertEquals(SIEMENS, u);
		// Here we actually get SIEMENS, only in a UNIQUE system like UCUM this can be
		// avoided, otherwise it should be case-sensitive.
	}

	@Test
	public void testByNameSecIgnoreCase() {
		final Unit<?> u = sou.getUnit("second", NAME, true);
		assertNotNull(u);
		assertEquals(SECOND, u);
	}
	
	@Test
	public void testByStringMin() {
		final Unit<?> u = sou.getUnit("min");
		assertNotNull(u);
		assertEquals(MINUTE, u);
	}

	@Test
	public void testByNameMin() {
		final Unit<?> u = sou.getUnit("Minute", NAME);
		assertNotNull(u);
		assertEquals(MINUTE, u);
	}

	@Test
	public void testByNameMinIgnoreCase() {
		final Unit<?> u = sou.getUnit("minutE", NAME, true);
		assertNotNull(u);
		assertEquals(MINUTE, u);
	}
	
	@Test
	public void testByStringHour() {
		final Unit<?> u = sou.getUnit("h");
		assertNotNull(u);
		assertEquals(HOUR, u);
	}

	@Test
	public void testByNameHour() {
		final Unit<?> u = sou.getUnit("Hour", NAME);
		assertNotNull(u);
		assertEquals(HOUR, u);
	}

	@Test
	public void testByNameHourIgnoreCase() {
		final Unit<?> u = sou.getUnit("hour", NAME, true);
		assertNotNull(u);
		assertEquals(HOUR, u);
	}
	
	@Test
	public void testByStringDay() {
		final Unit<?> u = sou.getUnit("d");
		assertNotNull(u);
		assertEquals(DAY, u);
	}

	@Test
	public void testByNameDay() {
		final Unit<?> u = sou.getUnit("Day", NAME);
		assertNotNull(u);
		assertEquals(DAY, u);
	}

	@Test
	public void testByNameDayIgnoreCase() {
		final Unit<?> u = sou.getUnit("dAy", NAME, true);
		assertNotNull(u);
		assertEquals(DAY, u);
	}
	
	@Test
	public void testByStringWeek() {
		final Unit<?> u = sou.getUnit("wk");
		assertNotNull(u);
		assertEquals(WEEK, u);
	}

	@Test
	public void testBySymbolWeek() {
		final Unit<?> u = sou.getUnit("wk", SYMBOL);
		assertNotNull(u);
		assertEquals(WEEK, u);
	}
	
	@Test
	public void testByNameWeek() {
		final Unit<?> u = sou.getUnit("Week", NAME);
		assertNotNull(u);
		assertEquals(WEEK, u);
	}

	@Test
	public void testByNameWeekIgnoreCase() {
		final Unit<?> u = sou.getUnit("WeEk", NAME, true);
		assertNotNull(u);
		assertEquals(WEEK, u);
	}
	
	@Test
	public void testByNameWeekLowerCase() {
		final Unit<?> u = sou.getUnit("week", NAME, true);
		assertNotNull(u);
		assertEquals(WEEK, u);
	}
	
	@Test
	public void testByStringMon() {
		final Unit<?> u = sou.getUnit("mo");
		assertNotNull(u);
		assertEquals(MONTH, u);
	}

	@Test
	public void testBySymbolMon() {
		final Unit<?> u = sou.getUnit("wk", SYMBOL);
		assertNotNull(u);
		assertEquals(WEEK, u);
	}
	
	@Test
	public void testByNameMon() {
		final Unit<?> u = sou.getUnit("Week", NAME);
		assertNotNull(u);
		assertEquals(WEEK, u);
	}

	@Test
	public void testByNameMonIgnoreCase() {
		final Unit<?> u = sou.getUnit("WeEk", NAME, true);
		assertNotNull(u);
		assertEquals(WEEK, u);
	}
}