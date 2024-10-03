/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2024, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
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
package tech.units.indriya.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static javax.measure.MetricPrefix.*;
import static tech.units.indriya.unit.Units.*;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.measure.Unit;
import javax.measure.format.UnitFormat;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:werner@units.tech">Werner Keil</a>
 *
 */
// TODO put those tests on a separate tag like "slow", "heavy", etc.
@Tag("l10n")
public class LocalUnitFormatTest {
	private static final Logger logger = Logger.getLogger(SimpleUnitFormatTest.class.getName());
	private static final Level LOG_LEVEL = Level.FINER;

	@Test
	public void testFormatPrefixGmps() {
		final UnitFormat format = LocalUnitFormat.getInstance();
		logger.log(LOG_LEVEL, format.format(GIGA(METRE_PER_SECOND)));
	}

	@Test
	public void testPrefixKm() {
		final UnitFormat format = LocalUnitFormat.getInstance();
		Unit<?> u = format.parse("km");
		assertEquals(KILO(METRE), u);
		assertEquals("km", u.toString());
	}

	@Test
	public void testFormatKm() {
		final UnitFormat format = LocalUnitFormat.getInstance();
		String s = format.format(KILO(METRE));
		assertEquals("km", s);
	}
	
	@Test
	public void testFormatKmCn() {
		final UnitFormat localFormat = LocalUnitFormat.getInstance(new Locale("cn"));
		String s = localFormat.format(KILO(METRE));
		assertEquals("千米", s);
	}

	@Test
	public void testFormatN() {
		final UnitFormat format = LocalUnitFormat.getInstance();
		assertEquals("N", format.format(NEWTON));
	}
	
	@Test
	public void testFormatMm() {
		final UnitFormat format = LocalUnitFormat.getInstance();
		final String s = format.format(MILLI(METRE));
		assertEquals("mm", s);
	}

	@Test
	public void testFormatmN() {
		final UnitFormat format = LocalUnitFormat.getInstance();
		final String s = format.format(MILLI(NEWTON));
		assertEquals("mN", s);
	}
	
	@Test
	public void testParseIrregularStringLocal() {
		assertThrows(IllegalArgumentException.class, () -> { // TODO should behave like EBNFUnitFormat (throwing a
																// MeasurementParseException)
			final UnitFormat format = LocalUnitFormat.getInstance();
			@SuppressWarnings("unused")
			Unit<?> u = format.parse("bl//^--1a");
		});
	}
	
	@Test
	public void testFormatKmHDe() {
		final UnitFormat format = LocalUnitFormat.getInstance(Locale.GERMAN);
		String s = format.format(KILOMETRE_PER_HOUR);
		assertEquals("km/h", s);
	}
	
	@Test
	public void testFormatKmHSv() {
		final UnitFormat format = LocalUnitFormat.getInstance(new Locale("sv"));
		String s = format.format(KILOMETRE_PER_HOUR);
		assertEquals("km/t", s);
	}
	
	@Test
	public void testFormatKmHNbNO() {
		final UnitFormat format = LocalUnitFormat.getInstance(new Locale("nb", "NO"));
		String s = format.format(KILOMETRE_PER_HOUR);
		assertEquals("km/t", s);
	}
	
	@Test
	public void testFormatKmHNoNO() {
		final UnitFormat format = LocalUnitFormat.getInstance(new Locale("no", "NO"));
		String s = format.format(KILOMETRE_PER_HOUR);
		assertEquals("km/t", s);
	}
	
	@Test
	public void testFormatKmHIN() {
		final UnitFormat format = LocalUnitFormat.getInstance(new Locale("en", "IN"));
		String s = format.format(KILOMETRE_PER_HOUR);
		assertEquals("kmph", s);
	}
	
	@Test
	public void testFormatKmHID() {
		final UnitFormat format = LocalUnitFormat.getInstance(new Locale("en", "ID"));
		String s = format.format(KILOMETRE_PER_HOUR);
		assertEquals("km/j", s);
	}
	
	@Test
	public void testFormatKmHMY() {
		final UnitFormat format = LocalUnitFormat.getInstance(new Locale("en", "MY"));
		String s = format.format(KILOMETRE_PER_HOUR);
		assertEquals("km/j", s);
	}
	
	@Test
	public void testFormatKmHTH() {
		final UnitFormat format = LocalUnitFormat.getInstance(new Locale("th", "TH"));
		String s = format.format(KILOMETRE_PER_HOUR);
		assertEquals("กม./ชม.", s);
	}
	
	@Test
	public void testFormatKmHAr() {
		final UnitFormat format = LocalUnitFormat.getInstance(new Locale("ar"));
		String s = format.format(KILOMETRE_PER_HOUR);
		assertEquals("كم/س", s);
	}
	
	@Test
	public void testFormatDayDe() {
		final UnitFormat format = LocalUnitFormat.getInstance(Locale.GERMAN);
		String s = format.format(DAY);
		assertEquals("tag", s);
	}
	
	@Test
	public void testFormatWeekDe() {
		final UnitFormat format = LocalUnitFormat.getInstance(Locale.GERMAN);
		String s = format.format(WEEK);
		assertEquals("wo", s);
	}
	
	@Test
	public void testFormatYearDe() {
		final UnitFormat format = LocalUnitFormat.getInstance(Locale.GERMAN);
		String s = format.format(YEAR);
		assertEquals("jr", s);
	}
}
