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
package tech.units.indriya.format;

import static javax.measure.MetricPrefix.CENTI;
import static javax.measure.MetricPrefix.KILO;
import static javax.measure.MetricPrefix.MEGA;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static tech.units.indriya.NumberAssertions.assertNumberEquals;
import static tech.units.indriya.unit.Units.HERTZ;
import static tech.units.indriya.unit.Units.KILOGRAM;
import static tech.units.indriya.unit.Units.METRE;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Locale;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.format.MeasurementParseException;
import javax.measure.format.QuantityFormat;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.units.indriya.function.RationalNumber;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/**
 * @author <a href="mailto:werner@units.tech">Werner Keil</a>
 * @author Andi Huber
 *
 */
public class QuantityFormatTest {
    private Quantity<Length> sut;
    private SimpleQuantityFormat format;

    @BeforeEach
    public void init() {
        // sut =
        // DefaultQuantityFactoryService.getQuantityFactory(Length.class).create(10,
        // METRE);
        sut = Quantities.getQuantity(10, METRE);
        format = SimpleQuantityFormat.getInstance();
    }

    @Test
    public void testFormat() {
        Unit<Frequency> hz = HERTZ;
        Quantity<Frequency> tenHz = Quantities.getQuantity(10, hz);
        assertEquals("10 Hz", format.format(tenHz));
    }

    @Test
    public void testFormat2() {
        Unit<Frequency> mhz = MEGA(HERTZ);
        Quantity<Frequency> oneMHz = Quantities.getQuantity(1, mhz);
        assertEquals("1 MHz", oneMHz.toString());
    }

    @Test
    public void testFormat3() {
        Unit<Frequency> khz = KILO(HERTZ);
        Quantity<Frequency> fiveKhz = Quantities.getQuantity(5, khz);
        assertEquals("5 kHz", format.format(fiveKhz));
    }

    @Test
    public void testFormatDelim() {
        final QuantityFormat format1 = new NumberDelimiterQuantityFormat.Builder()
                .setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH))
                .setUnitFormat(SimpleUnitFormat.getInstance())
                .setDelimiter("_")
                .build();
        final Unit<Length> cm = CENTI(Units.METRE);
        final Quantity<Length> l1 = Quantities.getQuantity(150, cm);
        assertEquals("150_cm", format1.format(l1));
    }

    @Test
    public void testParseSimpleTime() {
        Quantity<?> parsed1 = SimpleQuantityFormat.getInstance().parse("10 min");
        assertNotNull(parsed1);
        assertNumberEquals(BigDecimal.valueOf(10), parsed1.getValue(), 1E-24);
        assertEquals(Units.MINUTE, parsed1.getUnit());
    }

    @Test
    public void testParseSimpleLen() {
        Quantity<?> parsed1 = format.parse("60 m");
        assertNotNull(parsed1);
        assertNumberEquals(BigDecimal.valueOf(60), parsed1.getValue(), 1E-24);
        assertEquals(Units.METRE, parsed1.getUnit());
    }
    
    @Test
    public void testParseRationalLen() {
        Quantity<?> parsed1 = format.parse("5÷3 m");
        assertNotNull(parsed1);
        assertNumberEquals(RationalNumber.of(5, 3), parsed1.getValue(), 1E-24);
        assertEquals(Units.METRE, parsed1.getUnit());
    }
    
    @Test
    public void testParseRationalLenNegative() {
        Quantity<?> parsed1 = format.parse("-5÷3 m");
        assertNotNull(parsed1);
        assertNumberEquals(RationalNumber.of(-5, 3), parsed1.getValue(), 1E-24);
        assertEquals(Units.METRE, parsed1.getUnit());
    }
    

    @Test
    public void testParseAsType() {
        Quantity<Length> parsed1 = SimpleQuantityFormat.getInstance().parse("60 m").asType(Length.class);
        assertNotNull(parsed1);
        assertNumberEquals(BigDecimal.valueOf(60), parsed1.getValue(), 1E-24);
        assertEquals(Units.METRE, parsed1.getUnit());
    }

    @Test
    public void testParseSimpleMass() {
        try {
            Quantity<?> parsed1 = format.parse("5 kg");
            assertNotNull(parsed1);
            assertNumberEquals(BigDecimal.valueOf(5), parsed1.getValue(), 1E-24);
            assertNotNull(parsed1.getUnit());
            assertEquals("kg", parsed1.getUnit().getSymbol());
            assertEquals(KILOGRAM, parsed1.getUnit());
        } catch (MeasurementParseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPattern() {
        final SimpleQuantityFormat patternFormat = SimpleQuantityFormat.getInstance("n u");
        assertEquals("n u", patternFormat.getPattern());
        assertEquals("10 m", patternFormat.format(sut));
    }

    @Test
    public void testAnotherPattern() {
        final SimpleQuantityFormat patternFormat = SimpleQuantityFormat.getInstance("n_u");
        assertEquals("n_u", patternFormat.getPattern());
        assertEquals("10_m", patternFormat.format(sut));
    }
    
    @Test
    public void testAnotherPatternRational() {
        final SimpleQuantityFormat patternFormat = SimpleQuantityFormat.getInstance("n_u");
        assertEquals("n_u", patternFormat.getPattern());
        //assertEquals("-5÷3_m", patternFormat.format(Quantities.getQuantity(RationalNumber.of(-5, 3), METRE)));
        assertEquals("-1.666666666666666666666666666666667_m", patternFormat.format(Quantities.getQuantity(RationalNumber.of(-5, 3), METRE)));
    }
    
    @Test
    public void testCondensedPattern() {
        final SimpleQuantityFormat patternFormat = SimpleQuantityFormat.getInstance("nu");
        assertEquals("nu", patternFormat.getPattern());
        assertEquals("10m", patternFormat.format(sut));
    }
    
    @Test
    public void testCondensedPatternRational() {
        final SimpleQuantityFormat patternFormat = SimpleQuantityFormat.getInstance("nu");
        assertEquals("nu", patternFormat.getPattern());
        //assertEquals("-5÷3m", patternFormat.format(Quantities.getQuantity(RationalNumber.of(-5, 3), METRE)));
        assertEquals("-1.666666666666666666666666666666667m", patternFormat.format(Quantities.getQuantity(RationalNumber.of(-5, 3), METRE)));
    }

    @Test
    public void testNDFBuilder() {
        QuantityFormat quantFormat = new NumberDelimiterQuantityFormat.Builder().
			setNumberFormat(DecimalFormat.getInstance(Locale.ENGLISH)).
			setUnitFormat(SimpleUnitFormat.getInstance()).setDelimiter("_").
                build();
        final Unit<Length> cm = CENTI(Units.METRE);
        final Quantity<Length> l1 = Quantities.getQuantity(150, cm);
        assertEquals("150_cm", quantFormat.format(l1));
    }
    
    @Test
    public void testNDFBuilderNullNumFormat() {
        assertThrows(NullPointerException.class, () -> {
            @SuppressWarnings("unused")
            QuantityFormat quantFormat = new NumberDelimiterQuantityFormat.Builder().
                    setNumberFormat(null).build();
        });
    }
    
    @Test
    public void testNDFBuilderNullUnitFormat() {
        assertThrows(NullPointerException.class, () -> {
            @SuppressWarnings("unused")
            QuantityFormat quantFormat = new NumberDelimiterQuantityFormat.Builder().
                    setUnitFormat(null).build();
        });
    }

    @Test
    public void testParseDelim1() {
        QuantityFormat format1 = NumberDelimiterQuantityFormat.getInstance(DecimalFormat.getInstance(), SimpleUnitFormat.getInstance());
        Quantity<?> parsed1 = format1.parse("1 m");
        assertNumberEquals(1L, parsed1.getValue(), 1E-12);
        assertEquals(METRE, parsed1.getUnit());
    }

    @Test
    public void testParseDelim2() {
        assertThrows(IllegalArgumentException.class, () -> {
            QuantityFormat format1 = NumberDelimiterQuantityFormat.getInstance(DecimalFormat.getInstance(), SimpleUnitFormat.getInstance());
            @SuppressWarnings("unused")
            Quantity<?> parsed1 = format1.parse("1");
        });
    }

    @Test
    public void testParseDelim3() {
        assertThrows(IllegalArgumentException.class, () -> {
            QuantityFormat format1 = NumberDelimiterQuantityFormat.getInstance(DecimalFormat.getInstance(), SimpleUnitFormat.getInstance());
            @SuppressWarnings("unused")
            Quantity<?> parsed1 = format1.parse("m");
        });
    }
    
    @Test
    public void testParseAtPosition() {
        QuantityFormat format1 = NumberDelimiterQuantityFormat.getInstance(DecimalFormat.getInstance(), SimpleUnitFormat.getInstance());
        Quantity<?> parsed1 = format1.parse("1 km 2 m", new ParsePosition(5));
        assertNumberEquals(2L, parsed1.getValue(), 1E-12);
        assertEquals(METRE, parsed1.getUnit());
    }
}
