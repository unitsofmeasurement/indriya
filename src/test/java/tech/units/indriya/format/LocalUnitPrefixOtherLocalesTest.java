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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static javax.measure.BinaryPrefix.EXBI;
import static javax.measure.BinaryPrefix.GIBI;
import static javax.measure.BinaryPrefix.KIBI;
import static javax.measure.BinaryPrefix.MEBI;
import static javax.measure.BinaryPrefix.PEBI;
import static javax.measure.BinaryPrefix.TEBI;
import static javax.measure.BinaryPrefix.YOBI;
import static javax.measure.BinaryPrefix.ZEBI;
import static javax.measure.MetricPrefix.*;
import static tech.units.indriya.NumberAssertions.assertNumberEquals;
import static tech.units.indriya.unit.Units.*;

import java.util.Locale;

import javax.measure.Quantity;
import javax.measure.format.UnitFormat;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/**
 * @author <a href="mailto:werner@units.tech">Werner Keil</a>
 *
 */
// TODO put those tests on a separate tag like "slow", "heavy", etc.
@Tag("l10n")
public class LocalUnitPrefixOtherLocalesTest {
	
	private static UnitFormat format;
	
	@BeforeAll
	static void init() {
		// TODO remove like LocalUnitFormatTest
		format = LocalUnitFormat.getInstance();
	}
	
	@AfterAll
	static void deInit() {
		format = null;
	}
		
	@Test
	public void testKilo() {
		assertEquals(format.format(KILOGRAM), format.format(KILO(GRAM)));
	}

	@Test
	public void testMegaCn() {
		final UnitFormat localFormat = LocalUnitFormat.getInstance(new Locale("cn"));
		Quantity<Mass> m1 = Quantities.getQuantity(1.0, MEGA(Units.GRAM));
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("\u5146\u514B", localFormat.format(m1.getUnit()));
	}
	
	@Test
	public void testCentiCn() {
		final UnitFormat localFormat = LocalUnitFormat.getInstance(new Locale("cn"));
		Quantity<Volume> m1 = Quantities.getQuantity(1.0, LITRE);
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("体积", localFormat.format(m1.getUnit()));

		Quantity<Volume> m2 = m1.to(CENTI(LITRE));
		assertNumberEquals(100, m2.getValue(), 1E-12);
		assertEquals("\u5398体积", localFormat.format(m2.getUnit()));
	}
	
	@Test
	public void testDeciCn() {
		final UnitFormat localFormat = LocalUnitFormat.getInstance(new Locale("cn"));
		Quantity<Volume> m1 = Quantities.getQuantity(1.0, LITRE);
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("体积", localFormat.format(m1.getUnit()));

		Quantity<Volume> m2 = m1.to(DECI(LITRE));
		assertNumberEquals(10, m2.getValue(), 1E-12);
		assertEquals("\u5206体积", localFormat.format(m2.getUnit()));
	}

	@Test
	public void testMilliCn() {
		final UnitFormat localFormat = LocalUnitFormat.getInstance(new Locale("cn"));
		Quantity<Mass> m1 = Quantities.getQuantity(1.0, MILLI(Units.GRAM));
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("毫\u514B", localFormat.format(m1.getUnit()));
	}

	@Test
	public void testMilli2Cn() {
		final UnitFormat localFormat = LocalUnitFormat.getInstance(new Locale("cn"));
		Quantity<Volume> m1 = Quantities.getQuantity(10, MILLI(LITRE));
		assertNumberEquals(10, m1.getValue(), 1E-12);
		assertEquals("毫体积", localFormat.format(m1.getUnit()));
	}

	@Test
	public void testMilli3() {
		Quantity<Volume> m1 = Quantities.getQuantity(1.0, LITRE);
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("l", format.format(m1.getUnit()));

		Quantity<Volume> m2 = m1.to(MILLI(LITRE));
		assertNumberEquals(1000L, m2.getValue(), 1E-12);
		assertEquals("ml", format.format(m2.getUnit()));
	}

	@Test
	public void testMilli4() {
		Quantity<Volume> m1 = Quantities.getQuantity(1.0, MILLI(LITRE));
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("ml", format.format(m1.getUnit()));

		Quantity<Volume> m2 = m1.to(LITRE);
		assertNumberEquals(0.001d, m2.getValue(), 1E-12);
		assertEquals("l", format.format(m2.getUnit()));
	}

	@Test
	public void testMicro2() {
		Quantity<Length> m1 = Quantities.getQuantity(1.0, Units.METRE);
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("m", format.format(m1.getUnit()));

		final Quantity<Length> m2 = m1.to(MICRO(Units.METRE));
		assertNumberEquals(1000_000L, m2.getValue(), 1E-12);
		assertEquals("µm", format.format(m2.getUnit()));
	}

	@Test
	public void testNano() {
		Quantity<Mass> m1 = Quantities.getQuantity(1.0, Units.GRAM);
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("g", format.format(m1.getUnit()));

		final Quantity<Mass> m2 = m1.to(NANO(Units.GRAM));
		assertNumberEquals(1000_000_000L, m2.getValue(), 1E-12);
		assertEquals("ng", format.format(m2.getUnit()));
	}

	@Test
	public void testNano2() {
		Quantity<Length> m1 = Quantities.getQuantity(1.0, Units.METRE);
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("m", format.format(m1.getUnit()));

		final Quantity<Length> m2 = m1.to(NANO(Units.METRE));
		assertNumberEquals(1000000000L, m2.getValue(), 1E-12);
		assertEquals("nm", format.format(m2.getUnit()));
	}

	@Test
	public void testHashMapAccessingMap() {
		assertThat(LITRE.toString(), is("l"));
		assertThat(MILLI(LITRE).toString(), is("ml"));
		assertThat(MILLI(GRAM).toString(), is("mg"));
	}

	@Test
	public void testKibi() {
		final String s = format.format(KIBI(METRE));
		assertEquals("Kim", s);
	}
	
	@Test
	public void testKibiL() {
		final String s = format.format(KIBI(LITRE));
		assertEquals("Kil", s);
	}
	
	@Test
	public void testKibiG() {
		final String s = format.format(KIBI(GRAM));
		assertEquals("Kig", s);
	}

	@Test
	public void testMebi() {
		assertEquals("Mim", format.format(MEBI(METRE)));
	}

	@Test
	public void testGibi() {
		assertEquals("Gim", format.format(GIBI(METRE)));
	}

	@Test
	public void testTebi() {
		assertEquals("Til", format.format(TEBI(LITRE)));
	}

	@Test
	public void testPebi() {
		assertEquals("Pil", format.format(PEBI(LITRE)));
	}

	@Test
	public void testExbi() {
		assertEquals("Eig", format.format(EXBI(GRAM)));
	}

	@Test
	public void testZebi() {
		assertEquals("Zig", format.format(ZEBI(GRAM)));	
	}

	@Test
	public void testYobi() {
		assertEquals("Yig", format.format(YOBI(GRAM)));	
	}
	
	@Test
	public void testQuetta() {
		assertEquals("Ql", format.format(QUETTA(LITRE)));	
	}
	
	@Test
	public void testRonto() {
		assertEquals("rg", format.format(RONTO(GRAM)));	
	}
}
