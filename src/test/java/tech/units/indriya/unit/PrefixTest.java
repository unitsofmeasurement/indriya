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
package tech.units.indriya.unit;

import static javax.measure.BinaryPrefix.EXBI;
import static javax.measure.BinaryPrefix.GIBI;
import static javax.measure.BinaryPrefix.KIBI;
import static javax.measure.BinaryPrefix.MEBI;
import static javax.measure.BinaryPrefix.PEBI;
import static javax.measure.BinaryPrefix.TEBI;
import static javax.measure.BinaryPrefix.YOBI;
import static javax.measure.BinaryPrefix.ZEBI;
import static javax.measure.MetricPrefix.CENTI;
import static javax.measure.MetricPrefix.DECI;
import static javax.measure.MetricPrefix.EXA;
import static javax.measure.MetricPrefix.GIGA;
import static javax.measure.MetricPrefix.KILO;
import static javax.measure.MetricPrefix.MEGA;
import static javax.measure.MetricPrefix.MICRO;
import static javax.measure.MetricPrefix.MILLI;
import static javax.measure.MetricPrefix.NANO;
import static javax.measure.MetricPrefix.PETA;
import static javax.measure.MetricPrefix.TERA;
import static javax.measure.MetricPrefix.YOTTA;
import static javax.measure.MetricPrefix.ZETTA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tech.units.indriya.NumberAssertions.assertNumberEquals;
import static tech.units.indriya.unit.Units.GRAM;
import static tech.units.indriya.unit.Units.KILOGRAM;
import static tech.units.indriya.unit.Units.LITRE;
import static tech.units.indriya.unit.Units.METRE;

import java.math.BigInteger;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import tech.units.indriya.ComparableUnit;
import tech.units.indriya.function.RationalNumber;
import tech.units.indriya.quantity.Quantities;

public class PrefixTest {
	@Test
	public void testKilo() {
		// TODO how to handle equals for units?
		// assertEquals(KILOGRAM.getSymbol(), KILO(GRAM).getSymbol());
		assertEquals(KILOGRAM.toString(), KILO(GRAM).toString());
	}

	@Test
	public void testMega() {
		Quantity<Mass> m1 = Quantities.getQuantity(1.0, MEGA(Units.GRAM));
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("Mg", m1.getUnit().toString());
	}

	@Test
	public void testDeci() {
		Quantity<Volume> m1 = Quantities.getQuantity(1.0, LITRE);
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("l", m1.getUnit().toString());

		Quantity<Volume> m2 = m1.to(DECI(LITRE));
		assertNumberEquals(10, m2.getValue(), 1E-12);
		assertEquals("dl", m2.getUnit().toString());
	}

	@Test
	public void testMilli() {
		Quantity<Mass> m1 = Quantities.getQuantity(1.0, MILLI(Units.GRAM));
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("mg", m1.getUnit().toString());
	}

	@Test
	public void testMilli2() {
		Quantity<Volume> m1 = Quantities.getQuantity(10, MILLI(LITRE));
		assertEquals(10, m1.getValue());
		assertEquals("ml", m1.getUnit().toString());
	}

	@Test
	public void testMilli3() {
		Quantity<Volume> m1 = Quantities.getQuantity(1.0, LITRE);
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("l", m1.getUnit().toString());

		Quantity<Volume> m2 = m1.to(MILLI(LITRE));
		assertNumberEquals(1000L, m2.getValue(), 1E-12);
		assertEquals("ml", m2.getUnit().toString());
	}

	@Test
	public void testMilli4() {
		Quantity<Volume> m1 = Quantities.getQuantity(1.0, MILLI(LITRE));
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("ml", m1.getUnit().toString());

		Quantity<Volume> m2 = m1.to(LITRE);
		assertNumberEquals(0.001d, m2.getValue(), 1E-12);
		assertEquals("l", m2.getUnit().toString());
	}

	@Test
	public void testMicro2() {
		Quantity<Length> m1 = Quantities.getQuantity(1.0, Units.METRE);
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("m", m1.getUnit().toString());

		Quantity<Length> m2 = m1.to(MICRO(Units.METRE));
		assertNumberEquals(1000_000L, m2.getValue(), 1E-12);
		assertEquals("μm", m2.getUnit().toString());
	}

	@Test
	public void testNano() {
		Quantity<Mass> m1 = Quantities.getQuantity(1.0, Units.GRAM);
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("g", m1.getUnit().toString());

		Quantity<Mass> m2 = m1.to(NANO(Units.GRAM));
		assertNumberEquals(1000_000_000L, m2.getValue(), 1E-12);
		assertEquals("ng", m2.getUnit().toString());
	}

	@Test
	public void testNano2() {
		Quantity<Length> m1 = Quantities.getQuantity(1.0, Units.METRE);
		assertNumberEquals(1d, m1.getValue(), 1E-12);
		assertEquals("m", m1.getUnit().toString());

		Quantity<Length> m2 = m1.to(NANO(Units.METRE));
		assertNumberEquals(1000000000L, m2.getValue(), 1E-12);
		assertEquals("nm", m2.getUnit().toString());
	}

	@Test
	public void testHashMapAccessingMap() {
		assertThat(LITRE.toString(), is("l"));
		assertThat(MILLI(LITRE).toString(), is("ml"));
		assertThat(MILLI(GRAM).toString(), is("mg"));
	}

	@Test @Disabled("both Units have different UnitConverters, check for equality will always fail")
	public void testEquals() {
		assertEquals(MICRO(GRAM), GRAM.divide(1_000_000));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testEquivalence() {
		final ComparableUnit a = (ComparableUnit) MICRO(GRAM);
		final ComparableUnit b = (ComparableUnit) GRAM.divide(1_000_000);
		assertEquals(true, a.isEquivalentTo(b));
		assertEquals(true, b.isEquivalentTo(a));
	}

	@Test
	@Disabled("This is research for https://github.com/unitsofmeasurement/uom-se/issues/164")
	public void testNestedOperationsShouldBeSame() {
		Unit<Mass> m1 = MICRO(GRAM);
		Unit<Mass> m2 = GRAM.divide(1000).divide(1000);
		UnitConverter c1 = m1.getConverterTo(m2);
		UnitConverter c2 = m2.getConverterTo(m1);
		assertEquals(c1, c2);
		assertEquals(m1, m2);
	}

	@Test
	public void testNestedEquivalence() {
		ComparableUnit<Mass> a = (ComparableUnit<Mass>) MICRO(GRAM);
		ComparableUnit<Mass> b = (ComparableUnit<Mass>) GRAM.divide(1000).divide(1000);
		assertEquals(true, a.isEquivalentTo(b));
		assertEquals(true, b.isEquivalentTo(a));
	}

	@Test
	public void testNestedOperationsNotTheSame() {
		Unit<Mass> m1 = MICRO(GRAM);
		Unit<Mass> m2 = GRAM.divide(1000).divide(2000);
		UnitConverter c1 = m1.getConverterTo(m2);
		UnitConverter c2 = m2.getConverterTo(m1);
		assertNotEquals(c1, c2);
		assertNotEquals(m1, m2);
	}

	@Test
	public void testKibi() {
		assertEquals("Kim", KIBI(METRE).toString());
		final RationalNumber rational = RationalNumber.of(128, 125);
		final UnitConverter converter = KIBI(METRE).getConverterTo(KILO(METRE));
		assertEquals("Ki", KIBI.getSymbol());
		assertNumberEquals(rational.getDividend(), converter.convert(rational.getDivisor()), 1E-12);
		assertNumberEquals(rational.doubleValue(), converter.convert(1.), 1E-12);
	}

	@Test
	public void testMebi() {
		assertEquals("Mim", MEBI(METRE).toString());
		final RationalNumber rational = RationalNumber.of(16384, 15625);
		final UnitConverter converter = MEBI(METRE).getConverterTo(MEGA(METRE));
		assertNumberEquals(rational.getDividend(), converter.convert(rational.getDivisor()), 1E-12);
		assertNumberEquals(rational.doubleValue(), converter.convert(1.), 1E-12);
	}

	@Test
	public void testGibi() {
		assertEquals("Gim", GIBI(METRE).toString());
		final BigInteger exactFactor = BigInteger.valueOf(
				GIBI.getValue().longValue()).pow(GIBI.getExponent());
		assertEquals(1073741824L, exactFactor.longValue());

		final RationalNumber rational = RationalNumber.of(2_097_152L, 1_953_125L);
		final UnitConverter converter = GIBI(METRE).getConverterTo(GIGA(METRE));
		assertNumberEquals(rational.getDividend(), converter.convert(rational.getDivisor()), 1E-12);
		assertNumberEquals(rational.doubleValue(), converter.convert(1.), 1E-12);
	}

	@Test
	public void testTebi() {
		assertEquals("Til", TEBI(LITRE).toString());
		final RationalNumber rational = RationalNumber.of(268_435_456L, 244_140_625L);
		final UnitConverter converter = TEBI(LITRE).getConverterTo(TERA(LITRE));
		assertNumberEquals(rational.getDividend(), converter.convert(rational.getDivisor()), 1E-12);
		assertNumberEquals(rational.doubleValue(), converter.convert(1.), 1E-12);
	}

	@Test
	public void testPebi() {
		assertEquals("Pil", PEBI(LITRE).toString());
		final RationalNumber rational = RationalNumber.of(34_359_738_368L, 30_517_578_125L);
		final UnitConverter converter = PEBI(LITRE).getConverterTo(PETA(LITRE));
		assertNumberEquals(rational.getDividend(), converter.convert(rational.getDivisor()), 1E-12);
		assertNumberEquals(rational.doubleValue(), converter.convert(1.), 1E-12);
	}

	@Test
	public void testExbi() {
		assertEquals("Eig", EXBI(GRAM).toString());
		final RationalNumber rational = RationalNumber.of(4_398_046_511_104L, 3_814_697_265_625L);
		final UnitConverter converter = EXBI(GRAM).getConverterTo(EXA(GRAM));
		assertNumberEquals(rational.getDividend(), converter.convert(rational.getDivisor()), 1E-12);
		assertNumberEquals(rational.doubleValue(), converter.convert(1.), 1E-12);
	}

	@Test
	public void testZebi() {
		assertEquals("Zig", ZEBI(GRAM).toString());
		final RationalNumber rational = RationalNumber.of(562_949_953_421_312L, 47_683_715_8203_125L);
		final UnitConverter converter = ZEBI(GRAM).getConverterTo(ZETTA(GRAM));
		assertNumberEquals(rational.getDividend(), converter.convert(rational.getDivisor()), 1E-12);
		assertNumberEquals(rational.doubleValue(), converter.convert(1.), 1E-12);
	}

	@Test
	public void testYobi() {
		assertEquals("Yig", YOBI(GRAM).toString());
		final RationalNumber rational = RationalNumber.of(72_057_594_037_927_936L, 59_604_644_775_390_625L);
		final UnitConverter converter = YOBI(GRAM).getConverterTo(YOTTA(GRAM));
		assertNumberEquals(rational.getDividend(), converter.convert(rational.getDivisor()), 1E-12);
		assertNumberEquals(rational.doubleValue(), converter.convert(1.), 1E-12);
	}

	@Test
	public void testPrefixMethod() {
		assertEquals(CENTI(METRE), METRE.prefix(CENTI));
	}
}
